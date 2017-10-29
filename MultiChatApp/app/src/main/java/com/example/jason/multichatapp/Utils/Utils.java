package com.example.jason.multichatapp.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utils- convenient class contains often used method
 */

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
    // for changing to language code (i.e. "English" => "en)
    public static String getLanguageCode(String language) {
        switch (language) {
            case  "English" :
                return "en";
            case "Spanish" :
                return "es";
            case "Japanese" :
                return "ja";
            case "Russian" :
                return "ru";
            default:
                return "en";
        }
    }
    // change the language code to regular string
    public static String getLanguageFromCode(String languageCode) {
        switch (languageCode) {
            case  "en" :
                return "English";
            case "es" :
                return "Spanish";
            case "ja" :
                return "Japanese";
            case "ru" :
                return "Russian";
            default:
                return "English";
        }
    }

    public static String getPrivateChatRoomId(String user1_uid, String user2_uid) {
        if (user1_uid.compareTo(user2_uid) < 0) {
            return "chatRoom_ " + user1_uid.concat(user2_uid);
        } else {
            return "chatRoom_ " + user2_uid.concat(user1_uid);
        }
    }

    public static Address getCountryAndStates(Context context, double latitude, double longitude) {

        // Locale.getDefault() : gets the current value of the default locale for the specified category for the instance of the JVM
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.d(LOG_TAG, "fail to get the address: " + e.getMessage());
        }
        return addresses.get(0);
    }

    public static Address getLanAndLog(Context context, String county, String state) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(state + ", " + state, 1);
        } catch (IOException e) {
            Log.d(LOG_TAG, "fail to get the address: " + e.getMessage());

        }
        return addresses.get(0);
    }

}
