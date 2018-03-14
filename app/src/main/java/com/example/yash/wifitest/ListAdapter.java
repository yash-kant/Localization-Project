package com.example.yash.wifitest;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<WifiBeacon> wifiList;

    public ListAdapter(Context context, List<WifiBeacon> wifiList) {
        this.context = context;
        this.wifiList = wifiList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        System.out.println("viewpos" + position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.dataset, null);
            holder = new Holder();
            holder.tvDetails = (TextView) view.findViewById(R.id.tvDetails);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.tvDetails.setText("SSID :: " + wifiList.get(position).scanResult.SSID
                + "\nStrength :: " + wifiList.get(position).scanResult.level
                + "\nBSSID :: " + wifiList.get(position).scanResult.BSSID);

        return view;
    }

    public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    class Holder {
        TextView tvDetails;

    }
}
