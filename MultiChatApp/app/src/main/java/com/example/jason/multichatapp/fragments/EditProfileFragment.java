package com.example.jason.multichatapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * EditProfileFragment-
 */

public class EditProfileFragment extends UserProfileFragment implements AuthenticationDialogFragment.OnAuthenticationCompleteListener {

    private static final String LOG_TAG = EditProfileFragment.class.getSimpleName();

    private String newEmail;
    private String newPassword;
    private String newLanguage;

    private FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference publicUsersReference;
    // saving the user info locally
    private SharedPreferences userInfoLocalPref;

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
        mDatabase = FirebaseDatabase.getInstance();
        publicUsersReference = mDatabase.getReference("publicUsers");
        userInfoLocalPref = getContext().getSharedPreferences(getString(R.string.user_info), Context.MODE_PRIVATE);
    }

    @Override
    public void changeWidgetConfig(EditText etEmail, EditText etPassword, Button btnProfile, Spinner spLanguage, EditText etLocation) {
        // change to save since we are in edit profile page
        etEmail.setText(firebaseUser.getEmail());
        btnProfile.setText(getResources().getString(R.string.save));
        // retrieve the local saved user language info from sharedpreference
        // and set the spinner to that language
        String language = userInfoLocalPref.getString(getString(R.string.s_language), null);
        setSpinnerLanguage(spLanguage, language);
    }

    @Override
    public void setupUserInformation(String email, String password, String language) {
        // display dialogfragment to re-authenticate the user in order to change their information
        showAuthenticationDialog();
        // instantiate new email, password, language
        newEmail = email;
        newPassword = password;
        newLanguage = language;
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
                                Utils.showSnackBar(getView(), "email update success");
                                updateDatabaseAndPref(newEmail, null, null);
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
                            Utils.showSnackBar(getView(), "password update success");
                            // since password will always get updated(event though user use the same password to update),
                            // and this method will always gets called changing the language and country goes here.
                            // Because, we don't know email will get updated or not.
                            updateDatabaseAndPref(null, newLanguage, "USA");
                        } else {
                            Log.d(LOG_TAG, "password update failed: " + task.getException().getMessage());
                            Utils.showSnackBar(getView(), task.getException().getMessage());
                        }
                    }
                });
    }

    // update firebase database and sharedprefereces for user information
    private void updateDatabaseAndPref(@Nullable String newEmail, @Nullable String newLanguage, @Nullable String newLocation) {
        SharedPreferences.Editor editor = userInfoLocalPref.edit();
        String uid = firebaseUser.getUid();
        if (newEmail != null) {
            // when newEmail reach here we know we need to change for sure.
            publicUsersReference.child(uid).child(getString(R.string.s_email)).setValue(newEmail);
            editor.putString(getString(R.string.s_email), newEmail);
        }
        if (newLanguage != null) {
            String currentLanguage = userInfoLocalPref.getString(getString(R.string.s_language), null);
            if (!currentLanguage.equals(newLanguage)) {
                editor.putString(getString(R.string.s_language), newLanguage);
                publicUsersReference.child(uid).child(getString(R.string.s_language)).setValue(Utils.getLanguageCode(newLanguage));
            }
        }
        if (newLocation != null) {
            String currentLocation = userInfoLocalPref.getString(getString(R.string.s_location), null);
            if (!currentLocation.equals(newLocation)) {
                editor.putString(getString(R.string.s_location), newLocation);
                publicUsersReference.child(uid).child(getString(R.string.s_location)).setValue(newLocation);
            }
        }
        editor.apply();
    }
    // for update the spinner value based on the user information that was sat in the sharedpreferences
    private void setSpinnerLanguage(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        // getCount returns the number of items in the spinner
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }
}