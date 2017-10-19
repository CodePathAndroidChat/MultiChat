package com.example.jason.multichatapp.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.adapters.UserListsAdapter;
import com.example.jason.multichatapp.databinding.FragmentUsersListBinding;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UsersListFragment-
 */

public class UsersListFragment extends Fragment implements
        UserListsAdapter.OnDirectMsgArrowClick {
    private FirebaseDatabase mDatabase;
    private DatabaseReference publicUsersReference;

    private FragmentUsersListBinding binding;
    private List<PublicUser> users;
    private UserListsAdapter adapter;
    private RecyclerView rvUsersList;



    public interface  LoadPrivateChatroomListener {
        public void onUserPMTapped(String loadPrivateChatRoom);
    }

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
        mDatabase = FirebaseDatabase.getInstance();
        publicUsersReference = mDatabase.getReference("publicUsers");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupView();
    }

    @Override
    public void onDirectMsgClick(View view, int position) {
        // showDirectMessge()

//        Utils.showSnackBar(view, users.get(position).email + " clicked");
        Log.d("fragment", users.get(position).email);
        ((LoadPrivateChatroomListener) getActivity()).onUserPMTapped(users.get(position).email);
    }

    // Make public interface  --> direct message clicked , on Click listener with position-->

    private void setupView() {
        // add some fake user data for now
        users = new ArrayList<PublicUser>();
        adapter = new UserListsAdapter(getContext(), users);
        adapter.setOnDirectMsgArrowClick(this);
        rvUsersList = binding.rvUsersList;
        rvUsersList.setAdapter(adapter);
        rvUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        getUsers();
    }

    private void getUsers() {
        publicUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Map<String, String > message = (Map<String, String>) child.getValue();
                    PublicUser user = new PublicUser().fromObject(message);
                    users.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
