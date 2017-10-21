package com.example.jason.multichatapp.fragments;

import android.os.Bundle;

/**
 * Created by jason on 10/15/17.
 */

public class DirectMessageFragment extends ChatRoomFragment {
    private String dbName = "bloop";

    public static DirectMessageFragment newInstance() {

        Bundle args = new Bundle();

        DirectMessageFragment fragment = new DirectMessageFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
