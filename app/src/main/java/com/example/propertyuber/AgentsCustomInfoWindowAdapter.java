package com.example.propertyuber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
        openHouseImage1.setImageResource(R.drawable.pplogo);
        informationText = view.findViewById(R.id.agentOpenHouseInformation);
        addressText = view.findViewById(R.id.agentAddressOpenHouse);
        tvTitle.setText("Open House!");
        addressText.setText(title);
        String snippet = marker.getSnippet();
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
