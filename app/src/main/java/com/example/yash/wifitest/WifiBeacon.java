package com.example.yash.wifitest;

import android.net.wifi.ScanResult;

/**
 * Created by yash on 14/3/18.
 */

public class WifiBeacon {

    public final ScanResult scanResult;
    public final String url;

    WifiBeacon(ScanResult scanResult, String url) {

        this.scanResult = scanResult;
        this.url = url;
    }
}
