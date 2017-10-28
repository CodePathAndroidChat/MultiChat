package com.example.jason.multichatapp.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.databinding.FragmentUserProfileBinding;

/**
 * ProfileBaseFragment- base class for sign up and edit profile page
 */

public abstract class UserProfileFragment extends Fragment {

    private static final String LOG_TAG = UserProfileFragment.class.getSimpleName();
    private FragmentUserProfileBinding binding;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnProfile;
    private Spinner spLanguage;
    private EditText etLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail = binding.etEmail;
        etPassword = binding.etPwd;
        btnProfile = binding.btnProfile;
        spLanguage = binding.spLanguage;
        etLocation = binding.etLocation;
        changeWidgetConfig(etEmail, etPassword, btnProfile, spLanguage, etLocation);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(view);
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String language = spLanguage.getSelectedItem().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Utils.showSnackBar(binding.getRoot(), "Field cannot be empty");
                } else {
                    Log.d(LOG_TAG, "email: " + email + "\tpassword: " + password + "\tlang: " + language);
                    setupUserInformation(email, password, language);
                }

            }
        });
    }

    private void hideKeyBoard(View view) {
        // close keyboard so user can see snackbar. getSystemService will return the system-level service which in this case input manager for interacting with input devices
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // no additional operation flag so the second argument is zero
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Abstract classes for subclass to use
     */
    // for changing the widget configurations
    public abstract void changeWidgetConfig(EditText etEmail, EditText etPassword, Button btnProfile, Spinner spLanguage, EditText etLocation);
    // for creating/updating user information
    public abstract void setupUserInformation(String email, String password, String language);
}
