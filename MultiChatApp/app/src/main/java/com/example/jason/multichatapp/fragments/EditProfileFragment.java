package com.example.jason.multichatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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

public class EditProfileFragment extends UserProfileFragment implements AuthenticationDialogFragment.OnAuthenticationCompleteListener{

    private static final String LOG_TAG = EditProfileFragment.class.getSimpleName();
    private FirebaseUser firebaseUser;
    private String newEmail;
    private String newPassword;

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
        // display dialogfragment to re-authenticate the user in order to change their information
        showAuthenticationDialog();
        // instantiate new email and password
        newEmail = email;
        newPassword = password;
    }

    private void showAuthenticationDialog() {
        FragmentManager fm = getFragmentManager();
        AuthenticationDialogFragment authenticationDialogFragment = AuthenticationDialogFragment.newInstance();
        // sets the target fragment for use later when sending results. used for when fragment is being started by another
        authenticationDialogFragment.setTargetFragment(EditProfileFragment.this, 0);
        authenticationDialogFragment.show(fm, "fragment_authentication_dialog");
    }

    @Override
    public void onAuthenticationComplete() {
        if (!firebaseUser.getEmail().equals(newEmail)) {
            firebaseUser.updateEmail(newEmail)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "email update success");
                                Utils.showSnackBar(getView(), "email update success");
                            } else {
                                Log.d(LOG_TAG, "email update failed: " + task.getException().getMessage());
                                Utils.showSnackBar(getView(), task.getException().getMessage());
                            }
                        }
                    });
        }
        firebaseUser.updatePassword(newPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "password update success");
                            Utils.showSnackBar(getView(), "password update success");

                        } else {
                            Log.d(LOG_TAG, "password update failed: " + task.getException().getMessage());
                            Utils.showSnackBar(getView(), task.getException().getMessage());
                        }
                    }
                });
    }
}
