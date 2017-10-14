package com.example.jason.multichatapp.fragments;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * EditProfileFragment-
 */

public class EditProfileFragment extends UserProfileFragment {

    private static final String LOG_TAG = EditProfileFragment.class.getSimpleName();
    private FirebaseUser firebaseUser;

    public static EditProfileFragment newInstance() {
        Bundle args = new Bundle();
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void changeWidgetConfig(EditText etEmail, EditText etPassword, Button btnProfile) {
        etEmail.setText(firebaseUser.getEmail());
        btnProfile.setText(getResources().getString(R.string.save));
    }
    // TODO: add a dialog for re-authenticate the user. email/password update user re-authentication
    @Override
    public void setupUserInformation(String email, String password) {
        // references: https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
        // add the code to re-authenticate user


        // the following can only be updated if we re-authenticate the user
        // references: https://firebase.google.com/docs/auth/android/manage-users#update_a_users_profile
        if (!firebaseUser.getEmail().equals(email)) {
            firebaseUser.updateEmail(email)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "email update success");
                            } else {
                                Log.d(LOG_TAG, "email update failed: " + task.getException().getMessage());
                                Utils.showSnackBar(getView(), task.getException().getMessage());
                            }
                        }
                    });
        }
        firebaseUser.updatePassword(password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "password update success");
                        } else {
                            Log.d(LOG_TAG, "password update failed: " + task.getException().getMessage());
                            Utils.showSnackBar(getView(), task.getException().getMessage());
                        }
                    }
                });
    }
}
