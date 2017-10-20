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
import com.example.jason.multichatapp.databinding.FragmentLoginBinding;

/**
 * LoginFragment - For Login to the World app
 */

public class LoginFragment extends Fragment {
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();
    private OnLoginClickListener loginClickListener;
    public interface OnLoginClickListener {
        void onLoginClick(String email, String password);
    }

    private FragmentLoginBinding binding;
    private Button btLogin;
    private EditText etEmail;
    private EditText etPassword;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }
    // make sure activity is implementing the callback class
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginClickListener) {
            loginClickListener = (OnLoginClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement "
                    + OnLoginClickListener.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView();
    }

    private void setUpView() {
        etEmail = binding.etEmail;
        etPassword = binding.etPwd;
        btLogin = binding.btLogin;
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(view);
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Utils.showSnackBar(binding.getRoot(), "Field cannot be empty");
                } else {
                    Log.d(LOG_TAG, "email: " + email + "\tpassword: " + password);
                    loginClickListener.onLoginClick(email, password);
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
