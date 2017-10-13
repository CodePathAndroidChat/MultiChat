package com.example.jason.multichatapp.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.adapters.UserListsAdapter;
import com.example.jason.multichatapp.databinding.FragmentUsersListBinding;
import com.example.jason.multichatapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * UsersListFragment-
 */

public class UsersListFragment extends Fragment implements
        UserListsAdapter.OnDirectMsgArrowClick {

    private FragmentUsersListBinding binding;
    private List<User> users;
    private UserListsAdapter adapter;
    private RecyclerView rvUsersList;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupView();
    }

    @Override
    public void onDirectMsgClick(View view, int position) {
        Utils.showSnackBar(view, users.get(position).name + " clicked");
    }

    private void setupView() {
        // add some fake user data for now
        users = addFewFakeUser(10);
        adapter = new UserListsAdapter(getContext(), users);
        adapter.setOnDirectMsgArrowClick(this);
        rvUsersList = binding.rvUsersList;
        rvUsersList.setAdapter(adapter);
        rvUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<User> addFewFakeUser(int number) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            String name = "name" + i;
            String language = "lang" + i;
            String location = "location" + i;
            userList.add(new User(name, language, location));
        }
        return userList;
    }
}
