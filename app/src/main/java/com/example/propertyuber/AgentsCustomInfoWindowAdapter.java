package com.example.propertyuber;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AgentsCustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private ImageView openHouseImage;
    private ImageView openHouseImage1;
    private TextView informationText;
    private TextView addressText;


    public AgentsCustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.agents_custom_layout_window, null);


    }

    private void renderWindowText(Marker marker, View view) {

        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        openHouseImage1 = view.findViewById(R.id.agentsCustomWindowImage1);
        informationText = view.findViewById(R.id.agentOpenHouseInformation);
        addressText = view.findViewById(R.id.agentAddressOpenHouse);
        tvTitle.setText("Open House!");
        addressText.setText(title);
        String snippet = marker.getSnippet();
        informationText.setText(snippet);
        getJSON("https://www.wozzytheprogrammer.com/onlineapi.php");


    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }


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
                    getImgUrl(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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


    private void getImgUrl(String json) throws JSONException, IOException {
        //creating a json array from the json string
        final JSONArray imagesArray = new JSONArray(json);

        //creating a string array for listview
        final String[] imagesUrl = new String[imagesArray.length()];


        //looping through all the elements in json array


        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {


                    for (int i = 0; i < imagesArray.length(); i++) {

                        JSONObject obj = imagesArray.getJSONObject(i);

                        imagesUrl[i] = obj.getString("imgUrl");
                        URL url = new URL(imagesUrl[i]);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        if (imagesUrl == null) {
                            openHouseImage1.setImageResource(R.drawable.pplogo);
                        } else {
                            openHouseImage1.setImageBitmap(bmp);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }


    private Context getApplication() {
        throw new RuntimeException("error!");
    }
}



