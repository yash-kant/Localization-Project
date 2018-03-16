package com.example.yash.wifitest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yash on 14/3/18.
 */

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder> {


    private List<WifiBeacon> wifiList;

    public BeaconAdapter(List<WifiBeacon> wifiList) {

        this.wifiList = wifiList;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_list_row,parent,false);

        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {

        WifiBeacon wifiBeacon = wifiList.get(position);

        holder.name.setText(wifiBeacon.scanResult.SSID);
        holder.url.setText(wifiBeacon.url);
//        holder.distance.setText(wifiBeacon.scanResult.level);

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

        }
    }
}
