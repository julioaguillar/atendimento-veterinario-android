package br.com.aonsistemas.appvet.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet {

    private static boolean isOnLine(Context context) {

        try {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            return netInfo != null && netInfo.isConnectedOrConnecting();

        } catch (Exception ignored) { }

        return false;

    }

}
