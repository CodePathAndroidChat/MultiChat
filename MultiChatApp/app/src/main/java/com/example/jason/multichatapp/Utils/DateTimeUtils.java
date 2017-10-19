package com.example.jason.multichatapp.Utils;

import android.util.Log;

import com.example.jason.multichatapp.fragments.ChatRoomFragment;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;

public class DateTimeUtils {
    private static final String LOG_TAG = ChatRoomFragment.class.getSimpleName();

    public String getRelativeTimeAgo(String timeStamp) {
        try {
            Timestamp ts = Timestamp.valueOf(timeStamp);
            PrettyTime p = new PrettyTime();
            return p.format(ts);
        } catch(Exception e) {
            Log.e(LOG_TAG, "Error parsing timestamp" + e);
        }
        return "";
    }
}
