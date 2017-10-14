package com.example.jason.multichatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * SignUpFragment- Sign up to the world app
 */

public class SignUpFragment extends UserProfileFragment {
    private static final String LOG_TAG = SignUpFragment.class.getSimpleName();
    private OnSignUpSuccessListener signUpSuccessListener;

    public interface OnSignUpSuccessListener {
        void onSignUpSuccess();
    }
    public static SignUpFragment newInstance() {
        Bundle args = new Bundle();
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private FirebaseAuth mAuth;

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
    }

    @Override
    public void changeWidgetConfig(EditText etEmail, EditText etPassword, Button btnProfile) {
        btnProfile.setText(getResources().getString(R.string.sign_up));
    }

    @Override
    public void setupUserInformation(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // go to activity and activity will navigate to main activity
                            Log.d(LOG_TAG, "sign up success");
                            signUpSuccessListener.onSignUpSuccess();
                        } else {
                            Log.d(LOG_TAG, "createUserWithEmailAndPassword:failure" + task.getException().getMessage());
                            Utils.showSnackBar(getView(), "Unable to login: " + task.getException().getMessage());
                        }
                    }
                });
    }

}
