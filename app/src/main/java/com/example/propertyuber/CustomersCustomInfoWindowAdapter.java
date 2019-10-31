package com.example.propertyuber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomersCustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private ImageView openHouseImage;

    public CustomersCustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.customers_custom_layout_window, null);


    }

    private void renderWindowText(Marker marker, View view) {

        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);
        openHouseImage = view.findViewById(R.id.customersCustomWindowImage);
        openHouseImage.setImageResource(R.drawable.pplogo);
        tvTitle.setText(title);


        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);


        tvSnippet.setText(snippet);

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
