package com.example.yash.wifitest;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class MainActivity extends AppCompatActivity {

    private WifiManager mainWifi;
    private Button btnRefresh;
    List<WifiBeacon> wifiList;
    List<ScanResult> scanResults;
    private int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 101;
    private static final String URL_NOT_AVAIL = "Sorry nothing great with this beacon!";
    private RecyclerView beaconRecyclerView;
    private BeaconAdapter beaconAdapter;
    private final static int REQUEST_ENABLE_LOCATION = 2;
    private final static int REQUEST_ENABLE_FINE_LOCATION = 3;



    private static final List<String> urls = new ArrayList<String>() {{
        add("https://www.facebook.com/");
        add("https://www.youtube.com");
        add("https://www.google.co.in");
        add("https://stackoverflow.com/users/5769505/yash-kant");
        add("https://github.com/yashkant");
        add("https://www.facebook.com/iitrsrishti/");
        add("https://www.facebook.com/ariesiitr/");
        add("https://www.facebook.com/ariesiitr/videos/1579031718852021/");


    }};

    private static final List<String> bssids = new ArrayList<String>() {{
        add("b8:86:87:45:dc:ed");
        add("86:a6:c8:29:2f:43");
        add("28:56:5a:02:9c:b7");
        add("94:44:52:da:9d:e2");
        add("78:0c:b8:6f:92:44");
        add("1a:fe:34:a5:62:a1");
        add("5e:cf:7f:ac:b3:cd");
        add("d8:5d:e2:05:2b:53");


    }};

    public static final List<String> names = new ArrayList<String>() {{

        add("Facebook Bro!");
        add("Youthub Bro!");
        add("Google kar le bro!");
        add("Coder ban ja bro!");
        add("LOL!");
        add("Welcome to Srishti");
        add("Welcome to ARIES");
        add("Workshop!");


    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        beaconRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        beaconRecyclerView.setLayoutManager(mLayoutManager);
        beaconRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        buildUrlMap();
//        checkPermissions();
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
            addToMap(bssids.get(i)+"hi", names.get(i));
        }
    }

    private void setAdapter() {

        beaconAdapter = new BeaconAdapter(wifiList, this);
        beaconRecyclerView.setAdapter(beaconAdapter);

    }

    public void launchChromeTab(String url) {

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(this.getResources()
                        .getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build();

        // This is optional but recommended
        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent);

        if(!url.equals(URL_NOT_AVAIL)){
            // This is where the magic happens...
            CustomTabsHelper.openCustomTab(this, customTabsIntent,
                    Uri.parse(url),
                    new WebViewFallback());
        }
        else {
            Toast.makeText(getApplicationContext(),url,Toast.LENGTH_SHORT).show();
        }

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
                wifiList.add(new WifiBeacon(sr,hashMap.get(sr.BSSID), hashMap.get(sr.BSSID + "hi")));
            }

            /*else {
                wifiList.add(new WifiBeacon(sr,URL_NOT_AVAIL));

            }*/

        }

    }

    private void sortList(int c) {
        if(c > scanResults.size()-1) c = scanResults.size()-1;

        Collections.sort(scanResults, new SortByLevel());
        scanResults = scanResults.subList(0,c);
        thresholdResults(-55);
        Log.i("TAG",scanResults.toString());


    }

    private void thresholdResults(int t) {

        ArrayList<ScanResult> sr = new ArrayList<>();

        for(int i = 0 ; i < scanResults.size(); i++){

            if(scanResults.get(i).level > t){
                sr.add(scanResults.get(i));
            }

        }

        scanResults = sr;
    }

    class SortByLevel implements Comparator<ScanResult>
    {

        @Override
        public int compare(ScanResult t2, ScanResult t1) {
            return t1.level - t2.level;
        }
    }

    private void seekPermissionsandScan(){

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{
            checkPermissions();
            //do something, permission was previously granted; or legacy device
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }

        else{
            Toast.makeText(getApplicationContext(),"Permission Required",Toast.LENGTH_SHORT).show();
        }

        if (requestCode != REQUEST_ENABLE_FINE_LOCATION || grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Please accept the Runtime Permission", Toast.LENGTH_SHORT).show();
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


    private void checkPermissions()
    {


        //Turn Location Settings On
        if(!isLocationEnabled(getApplicationContext()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Settings").setMessage("Please Turn on Location Settings to proceed further.").setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent ii = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(ii,REQUEST_ENABLE_LOCATION);

                }
            }).show();
        }
        else {
            //Runtime Permission
            if(Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED))
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ENABLE_FINE_LOCATION);
            }

            scanWifiList();

        }



    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case REQUEST_ENABLE_LOCATION:
            {
                if(!isLocationEnabled(getApplicationContext()))
                    Toast.makeText(this, "Location is required!", Toast.LENGTH_SHORT).show();
                else
                {
                    Toast.makeText(this, "Location is granted!", Toast.LENGTH_SHORT).show();
                    scanWifiList();
                }

            }

            default:{
                scanWifiList();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

