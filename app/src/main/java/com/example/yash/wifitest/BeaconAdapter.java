package com.example.yash.wifitest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yash on 14/3/18.
 */

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder> {


    private List<WifiBeacon> wifiList;
    private MainActivity mainActivity;

    public BeaconAdapter(List<WifiBeacon> wifiList, MainActivity mainActivity) {

        this.wifiList = wifiList;
        this.mainActivity = mainActivity;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_list_row,parent,false);

        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {

        WifiBeacon wifiBeacon = wifiList.get(position);
        holder.name.setText(wifiBeacon.eventName);
        holder.url.setText(wifiBeacon.url);
        holder.distance.setText(String.valueOf(wifiBeacon.scanResult.level));
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {
        public TextView name, url, distance;


        public BeaconViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);
            url = itemView.findViewById(R.id.url);
            distance = itemView.findViewById(R.id.dist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mainActivity.launchChromeTab(url.getText().toString());
                }
            });

        }
    }
}
