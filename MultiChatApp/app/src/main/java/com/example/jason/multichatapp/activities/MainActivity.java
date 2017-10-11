package com.example.jason.multichatapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils;
import com.example.jason.multichatapp.databinding.ActivityMainBinding;
import com.example.jason.multichatapp.fragments.ChatRoomFragment;
import com.example.jason.multichatapp.fragments.EditProfileFragment;
import com.example.jason.multichatapp.fragments.UsersListFragment;
import com.example.jason.multichatapp.fragments.UsersMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "@@@";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    String uid;

    private ActivityMainBinding binding;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvList;
    private ChatRoomFragment chatRoomFragment;
    private UsersMapFragment usersMapFragment;
    private UsersListFragment usersListFragment;
    private EditProfileFragment editProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Fabric.with(this, new Crashlytics());
        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("message");

        setupView(savedInstanceState);

        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                // TODO figure out how to take all stored messages (returns last one now)
//                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
//                HashMap<String, String> value = (HashMap<String, String>) dataSnapshot.getValue();
//                Log.d(TAG, "Value is: " + value);
//                if (value != null) {
//                    tvMessages.append("\n" + value.get("text"));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName(); //TODO need to set username
//            String email = currentUser.getEmail();
//            TextView tvEmail = (TextView) findViewById(R.id.tvUserName);
//            tvEmail.setText(email);
            // Check if user's email is verified
//            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = currentUser.getUid();
            Log.d(TAG, "User is logged " + uid);
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

//    @OnClick(R.id.btnSendMessage)
//    public void onSendMessageClicked() {
//        // Write a message to the database
//        DatabaseReference myRef = mDatabase.getReference("message"); // every message now overrides previous one because of the lack of Model
//        myRef.setValue(new ChatMessage(
//            new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss'Z'").format(new Timestamp(System.currentTimeMillis())).toString(),
//            etMessage.getText().toString(),
//            uid,
//            "en"));
//        Log.d(TAG, "Sending message to Firebase Database: " + etMessage.getText().toString());
//
//        etMessage.setText("");
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pass the event to ActionBarDrawerToggle to handle
        // the navigation drawer indicator touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // make sure to synchronize the state when the screen is restored
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        // called after onStart() is called
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    // restore the state when there is a new configuration change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    // initialize view
    private void setupView(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (savedInstanceState == null) {
            // if this is the first time opening this activity, initialize all the fragment
            chatRoomFragment = ChatRoomFragment.newInstance();
            usersMapFragment = UsersMapFragment.newInstance();
            usersListFragment = UsersListFragment.newInstance();
            editProfileFragment = EditProfileFragment.newInstance();
        }
        showFragment(chatRoomFragment, usersListFragment, usersListFragment, editProfileFragment);
        toolbar = (Toolbar) binding.toolbar;
        setSupportActionBar(toolbar);
        drawerLayout = binding.drawerLayout;
        nvList = binding.nvList;
        // ActionBarDrawerToggle indicates to the user when a drawer is being open/closed
        drawerToggle = setupDrawerToggle();
        setupDrawerContent(nvList);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_chat_room :
                showFragment(chatRoomFragment, usersMapFragment, usersListFragment, editProfileFragment);
                break;
            case R.id.mi_users_map :
                showFragment(usersMapFragment, chatRoomFragment, usersListFragment, editProfileFragment);
                break;
            case R.id.mi_users_in_chat :
                showFragment(usersListFragment, usersMapFragment, chatRoomFragment, editProfileFragment);
                break;
            case R.id.mi_edit_profile :
                showFragment(editProfileFragment, usersMapFragment, usersListFragment, chatRoomFragment);
                break;
            case R.id.mi_notifications :
                Utils.showSnackBar(binding.getRoot(), "Notification  clicked");
                break;
            case R.id.mi_log_out :
                Utils.showSnackBar(binding.getRoot(), "Log out clicked");
                break;
            default:
                showFragment(chatRoomFragment, usersMapFragment, usersListFragment, editProfileFragment);
        }
        setTitle(item.getTitle());
        drawerLayout.closeDrawers();
    }

    private void showFragment(Fragment show, Fragment hide1, Fragment hide2, Fragment hide3) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show.isAdded()) {
            ft.show(show);
        } else {
            ft.add(R.id.flContainer, show, show.getTag());
        }

        if (hide1.isAdded()) ft.hide(hide1);
        if (hide2.isAdded()) ft.hide(hide2);
        if (hide3.isAdded()) ft.hide(hide3);

        ft.commit();
    }
}
