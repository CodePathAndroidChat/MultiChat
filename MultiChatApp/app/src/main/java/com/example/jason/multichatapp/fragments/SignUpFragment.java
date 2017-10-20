package com.example.jason.multichatapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public void changeWidgetConfig(EditText etEmail, EditText etPassword, Button btnProfile, Spinner spLanguage) {
        btnProfile.setText(getResources().getString(R.string.sign_up));
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
                            saveUserToDatabaseAndPref(email, language, "USA");

                            Log.d(LOG_TAG, "public user created");
                        } else {
                            Log.d(LOG_TAG, "createUserWithEmailAndPassword:failure" + task.getException().getMessage());
                            Utils.showSnackBar(getView(), "Unable to sign up: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveUserToDatabaseAndPref(String email, String language, String location) {
        // save the user information locally in shared preferences.
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.user_info), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.s_email), email);
        editor.putString(getString(R.string.s_language), language);
        editor.putString(getString(R.string.s_location), location);
        editor.apply();
        publicUsersReference.child(mAuth.getCurrentUser().getUid())
                .setValue(new PublicUser(mAuth.getCurrentUser().getUid(),
                        mAuth.getCurrentUser().getEmail(),
                        Utils.getLanguageCode(language),
                        "USA")
        );
    }

}
