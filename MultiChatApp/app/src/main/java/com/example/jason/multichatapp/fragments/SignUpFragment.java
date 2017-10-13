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

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.databinding.FragmentSignUpBinding;

/**
 * SignUpFragment- Sign up to the world app
 */

public class SignUpFragment extends Fragment {
    private static final String LOG_TAG = SignUpFragment.class.getSimpleName();
    private OnSignUpClickListener signUpListener;
    public interface OnSignUpClickListener {
        void onSignUpClick(String email, String password);
    }

    private FragmentSignUpBinding binding;
    private EditText etEmail;
    private EditText etPassword;
    private Button btSignUp;

    public static SignUpFragment newInstance() {
        Bundle args = new Bundle();
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpClickListener) {
            signUpListener = (OnSignUpClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement "
                    + OnSignUpClickListener.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
    }

    private void setupView(){
        etEmail = binding.etEmail;
        etPassword = binding.etPwd;
        btSignUp = binding.btSignUp;
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(view);
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Utils.showSnackBar(binding.getRoot(), "Field cannot be empty");
                } else {
                    Log.d(LOG_TAG, "email: " + email + "\tpassword: " + password);
                    signUpListener.onSignUpClick(email, password);
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
}
