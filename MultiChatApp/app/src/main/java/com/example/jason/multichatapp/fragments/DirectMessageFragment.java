package com.example.jason.multichatapp.fragments;

import android.os.Bundle;

/**
 * Created by jason on 10/15/17.
 */

public class DirectMessageFragment extends ChatRoomFragment {
//    private String dbName = "bloop";


    public static DirectMessageFragment newInstance(String msg) {

        DirectMessageFragment fragment = new DirectMessageFragment();
//        fragment.dbName = "bloop2";


        Bundle args = new Bundle(1);
        args.putString("dbName", msg);

        fragment.setArguments(args);
        return fragment;
    }


}

// Set args
// https://stackoverflow.com/questions/10450348/do-fragments-really-need-an-empty-constructor