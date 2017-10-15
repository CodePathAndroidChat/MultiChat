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

    public static String getPrivateChatRoomId(PublicUser user1, PublicUser user2) {
        if (user1.uid.compareTo(user2.uid) < 0) {
            return "chatRoom_ " + user1.uid.concat(user2.uid);
        } else {
            return "chatRoom_ " + user2.uid.concat(user1.uid);
        }
    }
}
