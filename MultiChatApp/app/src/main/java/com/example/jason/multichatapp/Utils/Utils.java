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
}
