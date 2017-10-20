package com.example.jason.multichatapp.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.jason.multichatapp.models.PublicUser;

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

    public static String getPrivateChatRoomId(PublicUser user1, PublicUser user2) {
        if (user1.uid.compareTo(user2.uid) < 0) {
            return "chatRoom_ " + user1.uid.concat(user2.uid);
        } else {
            return "chatRoom_ " + user2.uid.concat(user1.uid);
        }
    }
}
