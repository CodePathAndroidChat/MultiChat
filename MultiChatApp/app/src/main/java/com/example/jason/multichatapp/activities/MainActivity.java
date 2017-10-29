package com.example.jason.multichatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.MapUtils;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.databinding.ActivityMainBinding;
import com.example.jason.multichatapp.fragments.ChatRoomFragment;
import com.example.jason.multichatapp.fragments.DirectMessageFragment;
import com.example.jason.multichatapp.fragments.EditProfileFragment;
import com.example.jason.multichatapp.fragments.UsersListFragment;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        UsersListFragment.LoadPrivateChatroomListener,
        ChatRoomFragment.GetAllPublicUserInfoListener,
        OnMapReadyCallback {

    private final String TAG = "@@@";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    // for setting up the views
    private ActivityMainBinding binding;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nvList;
    private ChatRoomFragment chatRoomFragment;
    private DirectMessageFragment directMessageFragment;
    private UsersListFragment usersListFragment;
    private EditProfileFragment editProfileFragment;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;

    private SharedPreferences userInfoLocal;
    // used for saving the title location in order to add marker on to the map
    private Map<String, LatLng> locationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        userInfoLocal = this.getSharedPreferences(getString(R.string.user_info), Context.MODE_PRIVATE);
        locationMap = new HashMap<>();
        // error check for google map api key
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }
        setupView(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName(); //TODO need to set username
            String email = currentUser.getEmail();
            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = currentUser.getUid();
            if (chatRoomFragment.isAdded()) {
                chatRoomFragment.getUserInfo(uid);
            }
            Log.d(TAG, "User is logged " + uid);
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

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
            directMessageFragment = DirectMessageFragment.newInstance("bloop");
            supportMapFragment = SupportMapFragment.newInstance();
            usersListFragment = UsersListFragment.newInstance();
            editProfileFragment = EditProfileFragment.newInstance();
        }
        // open the global chat room page by default
        toolbar = (Toolbar) binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.global_chat_room);
        showFragment(chatRoomFragment, new Fragment[]{usersListFragment, usersListFragment, editProfileFragment, directMessageFragment});
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
    // Implement class header --> interface for click listener for userListFragment
    // go to direct messg fragment
    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_chat_room:
                showFragment(chatRoomFragment, new Fragment[]{supportMapFragment, usersListFragment, editProfileFragment, directMessageFragment});
                break;
            case R.id.mi_users_map:
                showMapFragment(supportMapFragment, new Fragment[]{chatRoomFragment, usersListFragment, editProfileFragment});
                break;
            case R.id.mi_users_in_chat:
                showFragment(usersListFragment, new Fragment[]{directMessageFragment, supportMapFragment, chatRoomFragment, editProfileFragment});
                break;
            case R.id.mi_edit_profile:
                showFragment(editProfileFragment, new Fragment[]{directMessageFragment, supportMapFragment, usersListFragment, chatRoomFragment});
                break;
            case R.id.mi_notifications:
                Utils.showSnackBar(binding.getRoot(), "Notification  clicked");
                break;
            case R.id.mi_log_out:
                Utils.showSnackBar(binding.getRoot(), "Log out clicked");
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;
            default:
                showFragment(chatRoomFragment, new Fragment[]{directMessageFragment, supportMapFragment, usersListFragment, editProfileFragment});
        }
        // set the title of the action bar based on the page user is opening
        getSupportActionBar().setTitle(item.getTitle());
        // close the burger menu
        drawerLayout.closeDrawers();
    }

    private void showFragment(Fragment show, Fragment[] elementsToHide) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show.isAdded()) {
            ft.show(show);
        } else {
            ft.add(R.id.flContainer, show, show.getTag());
        }
        for(Fragment hideMe: elementsToHide) {
            if (hideMe.isAdded()) ft.hide(hideMe);
        }
        ft.commit();
    }

    private void showMapFragment(SupportMapFragment show, Fragment[] elementsToHide) {
        showFragment(show, elementsToHide);
        // Unique feature for map fragment. setup the callback
        if (show != null) {
            show.getMapAsync(this);
        } else {
            Log.d(TAG, show.getClass().getSimpleName() + " was null");
        }
    }

    //    called when the map is ready to be used
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called!");
        if (googleMap != null) {
            this.googleMap = googleMap;
            setupMap(googleMap);
        } else {
            Log.d(TAG, "google map is null!");
        }
    }


    public void setupMap(GoogleMap googleMap) {
        // add end user maker
        BitmapDescriptor icon = MapUtils.createBubble(MainActivity.this, IconGenerator.STYLE_DEFAULT, getString(R.string.you_are_here));
        MapUtils.addMarker(googleMap, new LatLng(userInfoLocal.getFloat(getString(R.string.latitude), 0), userInfoLocal.getFloat(getString(R.string.longitude), 0)),icon);
        Random random = new Random();
        for (Map.Entry<String, LatLng> marker : locationMap.entrySet()) {
            // obtain the string before @XOXOX.com in the user email address
            int endOfNameIndex = marker.getKey().indexOf("@");
            String userName = marker.getKey().substring(0, endOfNameIndex);
            BitmapDescriptor otherUserIcon = MapUtils.createBubble(MainActivity.this, random.nextInt(IconGenerator.STYLE_ORANGE) + IconGenerator.STYLE_WHITE, userName);
            MapUtils.addMarker(googleMap, marker.getValue(), otherUserIcon);
        }
        // setting up camera angle
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(userInfoLocal.getFloat(getString(R.string.latitude), 0), userInfoLocal.getFloat(getString(R.string.longitude), 0)));
        googleMap.moveCamera(center);
    }

    @Override
    public void onUserPMTapped(PublicUser user2) {
        String chatroomName = Utils.getPrivateChatRoomId(uid /*"me"*/, user2.uid);
        Log.d(TAG, "load chatroom with " + chatroomName);
        directMessageFragment = DirectMessageFragment.newInstance(chatroomName);
        showFragment(directMessageFragment, new Fragment[]{supportMapFragment, usersListFragment, editProfileFragment, chatRoomFragment});

    }
    // callback to getting all the public user information
    @Override
    public void onAllUserInfo(Map<String, PublicUser> usersMap) {
        for (PublicUser user : usersMap.values()) {
            Address address = Utils.getLanAndLog(this, user.country, user.states);
            locationMap.put(user.email, new LatLng(address.getLatitude(), address.getLongitude()));
        }
    }
}