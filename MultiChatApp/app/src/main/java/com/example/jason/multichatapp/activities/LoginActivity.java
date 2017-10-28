package com.example.jason.multichatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.jason.multichatapp.Constants;
import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.adapters.LoginFragmentPagerAdapter;
import com.example.jason.multichatapp.databinding.ActivityLoginBinding;
import com.example.jason.multichatapp.fragments.LoginFragment;
import com.example.jason.multichatapp.fragments.SignUpFragment;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.OnLoginClickListener,
        SignUpFragment.OnSignUpSuccessListener {

    private final String TAG = "@@@";

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference publicUsersReference;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        publicUsersReference = mDatabase.getReference("publicUsers");
    }

    @Override
    public void onLoginClick(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // when login is successful, instantiate the firebaseuser to get the valid current user
                            firebaseUser = mAuth.getCurrentUser();
                            Log.d(TAG, "login success");
                            goToMainActivity();
                            updateUserInfo();
                        } else {
                            Log.d(TAG, "signInWithEmail:failure" + task.getException().getMessage());
                            Utils.showSnackBar(binding.getRoot(), "Unable to login: " + task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onSignUpSuccess() {
        goToMainActivity();
    }

    private void setupView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setSupportActionBar((Toolbar) binding.toolbar);
        getSupportActionBar().setTitle(Constants.WELCOME);
        // attach viewpager to the LoginFragmentPagerAdapter
        binding.viewPager.setAdapter(new LoginFragmentPagerAdapter(getSupportFragmentManager(), LoginActivity.this));
        // set viewpager on the tablayout to connect the pager with tabs
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    // temporary method. jump to the chat screen.
    private void goToMainActivity(){
        Intent intent = new Intent(LoginActivity.this
                , MainActivity.class);
        startActivity(intent);
    }
    // this method retrieve data from PublicUsers database and find the current user information
    // save the current user information locally
    private void updateUserInfo() {
        ValueEventListener userInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.getKey().equals(firebaseUser.getUid())) {
                        System.out.println("user.getKey(): " + user.getKey());
                        PublicUser currentUser = user.getValue(PublicUser.class);
                        if (currentUser != null) {
                            saveUserToPref(currentUser.email, currentUser.language, currentUser.country, currentUser.states);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        publicUsersReference.addListenerForSingleValueEvent(userInfoListener);
    }
    // method for saving the current user info locally using sharedpreferences
    private void saveUserToPref(String email, String language, String country, String states) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.user_info), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.s_email), email);
        editor.putString(getString(R.string.s_language), Utils.getLanguageFromCode(language));
        editor.putString(getString(R.string.s_country), country);
        editor.putString(getString(R.string.s_states), states);
        editor.putString(getString(R.string.s_uid), mAuth.getCurrentUser().getUid());
        editor.apply();
    }
}
