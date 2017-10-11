package com.example.jason.multichatapp.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.databinding.FragmentUsersListBinding;

/**
 * UsersListFragment-
 */

public class UsersListFragment extends Fragment {

    private FragmentUsersListBinding binding;

    public static UsersListFragment newInstance() {

        Bundle args = new Bundle();

        UsersListFragment fragment = new UsersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users_list, container, false);
        return binding.getRoot();
    }
}
