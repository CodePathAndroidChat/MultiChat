package com.example.jason.multichatapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.example.jason.multichatapp.fragments.EditProfileFragment;
import com.example.jason.multichatapp.fragments.UsersListFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.ui.IconGenerator;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements UsersListFragment.LoadPrivateChatroomListener, OnMapReadyCallback {

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
    private UsersListFragment usersListFragment;
    private EditProfileFragment editProfileFragment;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;

    private static final int REQUEST_CODE_LOCATION = 1;
    private static final int REQUEST_CODE_LOCATION_COARSE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
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
                chatRoomFragment.getUserInfo(email, uid);
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
            supportMapFragment = SupportMapFragment.newInstance();
            usersListFragment = UsersListFragment.newInstance();
            editProfileFragment = EditProfileFragment.newInstance();
        }
        // open the global chat room page by default
        toolbar = (Toolbar) binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.global_chat_room);
        showFragment(chatRoomFragment, usersListFragment, usersListFragment, editProfileFragment);
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
                showFragment(chatRoomFragment, supportMapFragment, usersListFragment, editProfileFragment);
                break;
            case R.id.mi_users_map:
                showMapFragment(supportMapFragment, chatRoomFragment, usersListFragment, editProfileFragment);
                break;
            case R.id.mi_users_in_chat:
                showFragment(usersListFragment, supportMapFragment, chatRoomFragment, editProfileFragment);
                break;
            case R.id.mi_edit_profile:
                showFragment(editProfileFragment, supportMapFragment, usersListFragment, chatRoomFragment);
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
                showFragment(chatRoomFragment, supportMapFragment, usersListFragment, editProfileFragment);
        }
        // set the title of the action bar based on the page user is opening
        getSupportActionBar().setTitle(item.getTitle());
        // close the burger menu
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

    private void showMapFragment(SupportMapFragment show, Fragment hide1, Fragment hide2, Fragment hide3) {
        showFragment(show, hide1, hide2, hide3);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setupMap(GoogleMap googleMap) {
        Log.d(TAG, "setupGoogleMap called");
        askPermissionAndGetUserLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermissionAndGetUserLocation() {
        // ask user permission if the user hasn't accept to access his/her location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // TODO: explanation on why we need the access current location permission

                }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            // if permission is granted already, get user location
            getUserCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission granted");
                getUserCurrentLocation();
            } else {
                Log.d(TAG, "permission denied");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserCurrentLocation() {
        googleMap.setMyLocationEnabled(true);
        // this client is the main entry point for interaction with the fused location provider(location API in Google Play)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
//                        add pin here...?
                        Log.d(TAG, "lat: " + location.getLatitude() + "\tlong:" + location.getLongitude());
                        BitmapDescriptor icon = MapUtils.createBubble(MainActivity.this, IconGenerator.STYLE_ORANGE, "You are here");
                        MapUtils.addMarker(googleMap, new LatLng(location.getLatitude(), location.getLongitude()),icon);
                        CameraUpdate center=
                            CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                                location.getLongitude()));
                        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                        googleMap.moveCamera(center);
                        googleMap.animateCamera(zoom);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onUserPMTapped(String loadPrivateChatRoom) {
        Log.d(TAG, "load chatroom with" + loadPrivateChatRoom);
    }
}