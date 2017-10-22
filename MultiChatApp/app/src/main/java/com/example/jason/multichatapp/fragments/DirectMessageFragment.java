package com.example.jason.multichatapp.fragments;

import android.os.Bundle;

/**
 * Created by jason on 10/15/17.
 */

public class DirectMessageFragment extends ChatRoomFragment {
    private String dbName = "bloop";

    public static DirectMessageFragment newInstance(String roomName) {

        Bundle args = new Bundle();
        args.putString("roomName", roomName);

        DirectMessageFragment fragment = new DirectMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
