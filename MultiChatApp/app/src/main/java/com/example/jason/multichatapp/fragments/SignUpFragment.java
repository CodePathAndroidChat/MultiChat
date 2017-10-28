package com.example.jason.multichatapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * SignUpFragment- Sign up to the world app
 */

public class SignUpFragment extends UserProfileFragment {
    private static final String LOG_TAG = SignUpFragment.class.getSimpleName();

    private OnSignUpSuccessListener signUpSuccessListener;

    public interface OnSignUpSuccessListener {

        void onSignUpSuccess();
    }

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference publicUsersReference;

    private EditText etLocation;
    private String country;
    private String states;
    private float latitude;
    private float longitude;

    private static final int REQUEST_CODE_LOCATION = 1;

    public static SignUpFragment newInstance() {
        Bundle args = new Bundle();
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpSuccessListener) {
            signUpSuccessListener = (OnSignUpSuccessListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement "
                    + OnSignUpSuccessListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        publicUsersReference = mDatabase.getReference("publicUsers");
    }

    @Override
    public void changeWidgetConfig(EditText etEmail, EditText etPassword, Button btnProfile, Spinner spLanguage, EditText etLocation) {
        btnProfile.setText(getResources().getString(R.string.sign_up));
        this.etLocation = etLocation;
        this.etLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(LOG_TAG, "clicked!");
                askPermissionAndGetUserLocation();
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "permission_granted");
                    getUserLocation();
                } else {
                    Log.d(LOG_TAG, "permission_denied");
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setupUserInformation(final String email, String password, final String language) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // go to activity and activity will navigate to main activity
                            Log.d(LOG_TAG, "sign up success");
                            signUpSuccessListener.onSignUpSuccess();

                            /////////////////
                            // currentFirebaseUser.getDisplayName()
                            // Change!!!:
                            saveUserToDatabaseAndPref(email, language, country, states, latitude, longitude);

                            Log.d(LOG_TAG, "public user created");
                        } else {
                            Log.d(LOG_TAG, "createUserWithEmailAndPassword:failure" + task.getException().getMessage());
                            Utils.showSnackBar(getView(), "Unable to sign up: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveUserToDatabaseAndPref(String email, String language, String country, String states, float latitude, float longitude) {
        // save the user information locally in shared preferences.
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.user_info), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.s_email), email);
        editor.putString(getString(R.string.s_language), language);
        editor.putString(getString(R.string.s_country), country);
        editor.putString(getString(R.string.s_states), states);
        editor.putFloat(getString(R.string.latitude), latitude);
        editor.putFloat(getString(R.string.longitude), longitude);
        editor.putString(getString(R.string.s_uid), mAuth.getCurrentUser().getUid());
        editor.apply();
        publicUsersReference.child(mAuth.getCurrentUser().getUid())
                .setValue(new PublicUser(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(),
                        Utils.getLanguageCode(language),
                        country, states)
                );
    }

    // need to be callback in order to display this....?
    private void askPermissionAndGetUserLocation() {
        // ask user permission if the user hasn't accept to access his/her country
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: explanation on why we need the access current country permission
            }
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getActivity());
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        latitude = (float) location.getLatitude();
                        longitude = (float) location.getLongitude();
                        Log.d(LOG_TAG, "successfully get country." + latitude + " " + longitude);
                        Address address = Utils.getCountryAndStates(getContext(), latitude, longitude);
                        country = address.getCountryName();
                        states = address.getAdminArea();
                        etLocation.setText(country + ", " + states);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "fail to get the country");
                    }
                });
    }

}