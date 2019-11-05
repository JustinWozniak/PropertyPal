package com.example.propertyuber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomersMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, RoutingListener {


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private Button mLogout, mRequest, mSettings, agentsProfile;

    private LatLng pickupLocation;

    private Boolean requestBol = false;

    private Marker pickupMarker;

    private SupportMapFragment mapFragment;

    private String destination;
    private String requestService;

    private LatLng destinationLatLng;

    private LinearLayout mAgentInfo;

    private ImageView mAgentProfileImage;

    private TextView mAgentName, mAgentCar;
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomersMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mapFragment.getMapAsync(this);
        }

        destinationLatLng = new LatLng(0.0, 0.0);

        mAgentInfo = findViewById(R.id.agentInfo);

        mAgentProfileImage = findViewById(R.id.agentProfileImage);

        mAgentName = findViewById(R.id.agentName);
        mAgentCar = findViewById(R.id.agentCar);

        mLogout = findViewById(R.id.logout);
        mRequest = findViewById(R.id.request);
        mSettings = findViewById(R.id.settings);
        agentsProfile = findViewById(R.id.agentProfile);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomersMapActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol) {
//                    for(Polyline line : polylines){
//                        line.remove();
//                    }
//                    polylines.clear();

                    endRide();


                } else {

                    if (mLastLocation != null) {
                        requestBol = true;

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                        GeoFire geoFire = new GeoFire(ref);

                        geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Customers Location").icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.customer)));

                        mRequest.setText("Getting your Agent....");

                        getClosestAgent();

                        getAgentsAround();
                    }
                }
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersMapActivity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });


    }

    private int radius = 1;
    private Boolean agentFound = false;
    private String agentFoundID;

    GeoQuery geoQuery;

    private void getClosestAgent() {
        DatabaseReference agentLocation = FirebaseDatabase.getInstance().getReference().child("agentsAvailable");

        GeoFire geoFire = new GeoFire(agentLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!agentFound && requestBol) {
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (agentFound) {
                                    return;
                                }


                                agentFound = true;
                                agentFoundID = dataSnapshot.getKey();

                                DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents").child(agentFoundID).child("customerRequest");
                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap map = new HashMap();
                                map.put("customerRideId", customerId);
                                map.put("destination", destination);
                                map.put("destinationLat", destinationLatLng.latitude);
                                map.put("destinationLng", destinationLatLng.longitude);
                                driverRef.updateChildren(map);

                                getAgentLocation();
                                getAgentInfo();
                                getHasRideEnded();
                                mRequest.setText("Looking for Agents Location....");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!agentFound) {
                    radius++;
                    getClosestAgent();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private Marker mAgentMarker;
    private DatabaseReference agentLocationRef;
    private ValueEventListener agentLocationRefListener;

    private void getAgentLocation() {
        agentLocationRef = FirebaseDatabase.getInstance().getReference().child("agentsWorking").child(agentFoundID).child("l");
        agentLocationRefListener = agentLocationRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng agentLatLng = new LatLng(locationLat, locationLng);
                    if (mAgentMarker != null) {
                        mAgentMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(agentLatLng.latitude);
                    loc2.setLongitude(agentLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    float finalDistance = distance / 1000;

                    LatLng Agents = new LatLng(agentLatLng.latitude, agentLatLng.longitude);

                    if (agentFound) {

                        mMap.addMarker(new MarkerOptions().position(Agents)
                                .title("Agents Location")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.agent))
                                .snippet("Closest agent"));
                        getRouteToMarker(Agents);

                        if (distance < 100) {
                            mRequest.setText("Agent's Here!!!");
                        } else {
                            String newValue = Double.toString(Math.floor(finalDistance));
                            mRequest.setText("Agent Found: " + newValue + " Km Away");
                        }
                        mAgentMarker = mMap.addMarker(new MarkerOptions().position(agentLatLng).title("Your Agent").icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.agent)));
                        agentsProfile.setVisibility(View.VISIBLE);
                        agentsProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CustomersMapActivity.this, agentsProfileActivity.class);
                                startActivity(intent);
                                return;
                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .key("AIzaSyDRMQHpMV2u2cB27aC1q7ejEy74kCb8Y6c")
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();

    }


    private void getAgentInfo() {
        mAgentInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents").child(agentFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (dataSnapshot.child("name") != null) {
                        mAgentName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.child("car") != null) {
                        mAgentCar.setText(dataSnapshot.child("car").getValue().toString());
                    }
                    if (dataSnapshot.child("profileImageUrl") != null) {
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mAgentProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference tripHasEndedRef;
    private ValueEventListener tripHasEndedRefListener;

    private void getHasRideEnded() {
        tripHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents").child(agentFoundID).child("customerRequest").child("customerRideId");
        tripHasEndedRefListener = tripHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    if (polylines != null) {
                        erasePolylines();
                    }
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide() {
        requestBol = false;
        geoQuery.removeAllListeners();
        if (agentLocationRef != null) {
            agentLocationRef.removeEventListener(agentLocationRefListener);
            tripHasEndedRef.removeEventListener(tripHasEndedRefListener);
        }

        if (agentFoundID != null) {
            DatabaseReference agentRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Agents").child(agentFoundID).child("customerRequest");
            agentRef.removeValue();
            agentFoundID = null;

        }
        agentFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        if (mAgentMarker != null) {
            mAgentMarker.remove();
        }
        mRequest.setText("Find an Agent");

        mAgentInfo.setVisibility(View.GONE);
        mAgentName.setText("");
        mAgentCar.setText("Destination: --");
        mAgentProfileImage.setImageResource(R.mipmap.customer);
        if (mAgentMarker != null) {
            mAgentMarker.remove();
        }
        if (polylines != null) {
            erasePolylines();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng Kitchener = new LatLng(43.467831, -80.521872);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Kitchener, 12));
        mMap.setInfoWindowAdapter(new CustomersCustomInfoWindowAdapter(CustomersMapActivity.this));

//        getJSON("http://192.168.1.113/MyApi/Api.php");
        getJSON("https://www.wozzytheprogrammer.com/onlineapi.php");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }


    @Override
    public void onLocationChanged(Location location) {
        if (getApplicationContext() != null) {
            mLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomersMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    final int LOCATION_REQUEST_CODE = 1;

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);

                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    //this method is actually fetching the json string
    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    loadIntoMaps(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {


                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();


    }

    private void loadIntoMaps(String json) throws JSONException {
        //creating a json array from the json string
        JSONArray addressArray = new JSONArray(json);

        //creating a string array for listview
        String[] markerNames = new String[addressArray.length()];
        String[] addresses = new String[addressArray.length()];
        String[] latittudes = new String[addressArray.length()];
        String[] longitutes = new String[addressArray.length()];
        String[] urlString = new String[addressArray.length()];
        final String[] propertyInformation = new String[addressArray.length()];


        //looping through all the elements in json array
        for (int i = 0; i < addressArray.length(); i++) {

            JSONObject obj = addressArray.getJSONObject(i);

            markerNames[i] = obj.getString("name");
            Log.e("names", markerNames[i]);
            addresses[i] = obj.getString("address");
            latittudes[i] = obj.getString("lat");
            longitutes[i] = obj.getString("lng");
            propertyInformation[i] = obj.getString("information");
            urlString[i] = obj.getString("urlString");


            JSONObject jsonObj = addressArray.getJSONObject(i);
            final double finalLat = Double.valueOf(jsonObj.getString("lat"));
            final double finalLng = Double.valueOf(jsonObj.getString("lng"));
            mMap.addMarker(new MarkerOptions()
                    .title(jsonObj.getString("address"))
                    .snippet(propertyInformation[i])
                    .position(new LatLng(finalLat,
                            finalLng)
                    ));


            final String anotherUrl = urlString[i].toString();
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(anotherUrl));
                    startActivity(browserIntent);

                    marker.setSnippet(propertyInformation.toString());

                }
            });

        }
    }


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines != null) {
            if (polylines.size() > 0) {
                for (Polyline poly : polylines) {
                    poly.remove();
                }
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }


    List<Marker> markerList = new ArrayList<Marker>();

    private void getAgentsAround() {
        Toast.makeText(getApplicationContext(), "getAgentsAround", Toast.LENGTH_LONG).show();

        DatabaseReference agentsLocation = FirebaseDatabase.getInstance().getReference().child("Users").child("agentsAvailable");

        GeoFire geoFire = new GeoFire(agentsLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999);
        Log.e("getLongitude", String.valueOf(mLastLocation.getLongitude()));
        Log.e("getLatitude", String.valueOf(mLastLocation.getLatitude()));
        Log.e("geoQuery", String.valueOf(geoQuery));

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Toast.makeText(getApplicationContext(), "onKeyEntered" + key, Toast.LENGTH_LONG).show();
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        return;
                    }

                    LatLng agentsLocation = new LatLng(location.latitude, location.longitude);
                    Marker mAgentMarker = mMap.addMarker(new MarkerOptions().position(agentsLocation).icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.agent))
                            .snippet("Agent"));
                    mAgentMarker.setTag(key);
                    markerList.add(mAgentMarker);

                }
            }

            @Override
            public void onKeyExited(String key) {
                Toast.makeText(getApplicationContext(), "onKeyExited" + key, Toast.LENGTH_LONG).show();

                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key))
                        markerIt.remove();
                    markerList.remove(markerIt);
                    return;
                }
            }


            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Toast.makeText(getApplicationContext(), "onKeyMoved" + key, Toast.LENGTH_LONG).show();

                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
                Toast.makeText(getApplicationContext(), "onGeoQueryReady", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "onGeoQueryError" + error, Toast.LENGTH_LONG).show();

            }
        });

    }
}