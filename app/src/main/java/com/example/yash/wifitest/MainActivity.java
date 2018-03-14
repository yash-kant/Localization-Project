package com.example.yash.wifitest;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    List<WifiBeacon> wifiList;
    List<ScanResult> scanResults;
    private int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 101;
    private HashMap<String, String> urlMap;

    private static final List<String> urls = new ArrayList<String>() {{
        add("https://www.facebook.com/");
        add("https://www.youtube.com");
        add("https://www.google.co.in");
        add("https://stackoverflow.com/users/5769505/yash-kant");
        add("https://github.com/yashkant");

    }};

    private static final List<String> bssids = new ArrayList<String>() {{
        add("b8:86:87:45:dc:ed");
        add("86:a6:c8:29:2f:43");
        add("28:56:5a:02:9c:b7");
        add("94:44:52:da:9d:e2");
        add("78:0c:b8:6f:92:44");

    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvWifiDetails = (ListView) findViewById(R.id.lvWifiDetails);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        buildUrlMap();

        seekPermissionsandScan();

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                seekPermissionsandScan();


            }
        });
    }

    private void buildUrlMap() {

        for(int i = 0; i< bssids.size(); i++){
            addToMap(bssids.get(i),urls.get(i));
        }
    }

    private void setAdapter() {

        adapter = new com.example.yash.wifitest.ListAdapter(getApplicationContext(), wifiList);
        lvWifiDetails.setAdapter(adapter);

        lvWifiDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiBeacon wifiBeacon = (WifiBeacon) lvWifiDetails.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(),wifiBeacon.url,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void scanWifiList() {
        mainWifi.startScan();
        scanResults = mainWifi.getScanResults();
        sortList(6);
        detectBeacons();
        Log.i("TAG", "scanWifiList: " + wifiList.toString());
        setAdapter();

    }

    private void detectBeacons() {
        //check the SSIDs which are available in the shared-prefs
        //create the list of WifiBeacons merging ScanResult and respective urls.
        HashMap<String, String> hashMap = loadMap();
        wifiList = new ArrayList<>();

        for(int i = 0; i < scanResults.size(); i++){
            ScanResult sr = scanResults.get(i);

            if(hashMap.containsKey(sr.BSSID)){
                wifiList.add(new WifiBeacon(sr,hashMap.get(sr.BSSID)));
            }
            else {
                wifiList.add(new WifiBeacon(sr,"Sorry nothing great with this beacon!"));

            }

        }

    }

    private void sortList(int c) {
        Collections.sort(scanResults, new SortByLevel());
        scanResults = scanResults.subList(0,c);

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

    private HashMap<String, String> loadMap(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        HashMap<String, String> map= (HashMap<String, String>) pref.getAll();

        return map;
    }

    private void addToMap(String bssid, String url){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(bssid,url);
        editor.commit();
    }


}