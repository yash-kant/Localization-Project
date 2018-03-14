package com.example.yash.wifitest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager mainWifi;
    private Button btnRefresh;
    ListAdapter adapter;
    ListView lvWifiDetails;
    List wifiList;
    private int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 101;
    private HashMap<String, String> urlMap;

    private static final List<String> urls = new ArrayList<String>() {{
        add("https://www.facebook.com/");
        add("https://www.youtube.com");
        add("https://www.google.co.in");
        add("https://stackoverflow.com/users/5769505/yash-kant");
        add("https://github.com/yashkant");

    }};


    private HashMap<String, String> loadMap(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        HashMap<String, String> map= (HashMap<String, String>) pref.getAll();

        return map;
    }

    private void addToMap(String url, String ssid){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ssid,url);
        editor.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvWifiDetails = (ListView) findViewById(R.id.lvWifiDetails);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);

        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        /*receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));*/

        seekPermissionsandScan();

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                seekPermissionsandScan();


            }
        });
    }

    private void setAdapter() {
        adapter = new com.example.yash.wifitest.ListAdapter(getApplicationContext(), wifiList);
        lvWifiDetails.setAdapter(adapter);
    }

    private void scanWifiList() {
        mainWifi.startScan();
        wifiList = mainWifi.getScanResults();
        sortList();
        Log.i("TAG", "scanWifiList: " + wifiList.toString());
        setAdapter();

    }

    private void sortList() {
        Collections.sort(wifiList, new SortByLevel());
        wifiList = wifiList.subList(0,5);

    }

    class SortByLevel implements Comparator<ScanResult>
    {

        @Override
        public int compare(ScanResult t2, ScanResult t1) {
            return t1.level - t2.level;
        }
    }

    private void seekPermissionsandScan(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{
            scanWifiList();
            //do something, permission was previously granted; or legacy device
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanWifiList();
        }

        else{
            Toast.makeText(getApplicationContext(),"Permission Required",Toast.LENGTH_SHORT).show();
        }
    }




}