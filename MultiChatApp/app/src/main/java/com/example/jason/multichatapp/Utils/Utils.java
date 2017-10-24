package com.example.jason.multichatapp.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Utils- convenient class contains often used method
 */

public class Utils {

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
}
