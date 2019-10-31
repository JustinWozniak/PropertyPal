package com.example.propertyuber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomersCustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private ImageView openHouseImage1;
    private TextView informationText;
    private TextView openHouseAddress;

    public CustomersCustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.customers_custom_layout_window, null);


    }

    private void renderWindowText(Marker marker, View view) {

        String title = marker.getTitle();
        String snippet = marker.getSnippet();
        TextView tvTitle = view.findViewById(R.id.title);
        openHouseImage1 = view.findViewById(R.id.customersCustomWindowImage1);
        openHouseImage1.setImageResource(R.drawable.pplogo);
        informationText = view.findViewById(R.id.customersOpenHouseInformation);
        openHouseAddress = view.findViewById(R.id.customersAddressOpenHouse);
        openHouseAddress.setText(title);
        tvTitle.setText("Open House!");
        informationText.setText(snippet);



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
}
