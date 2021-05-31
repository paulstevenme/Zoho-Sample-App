package com.paulstevenme.countries.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;

public class Helpers {
    Context mContext;
    public Helpers(Context mContext) {
        this.mContext = mContext;
    }


    public Boolean isInternetConnected()  throws InterruptedException, IOException {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
