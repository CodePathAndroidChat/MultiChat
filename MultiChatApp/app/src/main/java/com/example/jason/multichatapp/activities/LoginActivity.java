package com.example.jason.multichatapp.activities;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements
        LoginFragment.OnLoginClickListener,
        SignUpFragment.OnSignUpClickListener {

    private final String TAG = "@@@";
    private FirebaseAuth mAuth;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onLoginClick(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "login success");
                            goToMainActivity();
                        } else {
                            Log.d(TAG, "signInWithEmail:failure" + task.getException().getMessage());
                            Utils.showSnackBar(binding.getRoot(), "Unable to login: " + task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onSignUpClick(String email, String password) {
        Log.d(TAG, "email: " + email + "\tpassword: " + password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "sign up success");
                            goToMainActivity();
                        } else {
                            Log.d(TAG, "createUserWithEmailAndPassword:failure" + task.getException().getMessage());
                            Utils.showSnackBar(binding.getRoot(), "Unable to login: " + task.getException().getMessage());
                        }
                    }
                });
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
}
