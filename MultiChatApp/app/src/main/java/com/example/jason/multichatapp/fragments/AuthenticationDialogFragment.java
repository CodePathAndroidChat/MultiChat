package com.example.jason.multichatapp.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.databinding.FragmentAuthenticationDialogBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * AuthenticationDialogFragment- for authenticate the user for changing the profile setting
 * references: https://firebase.google.com/docs/auth/android/manage-users#re-authenticate_a_user
 */

public class AuthenticationDialogFragment extends DialogFragment {

    public interface OnAuthenticationCompleteListener {
        void onAuthenticationComplete();
    }

    private static final String LOG_TAG = AuthenticationDialogFragment.class.getSimpleName();
    private FirebaseUser firebaseUser;
    private FragmentAuthenticationDialogBinding binding;

    public AuthenticationDialogFragment() {
        // dialog fragment require an empty constructor
    }

    public static AuthenticationDialogFragment newInstance() {
        Bundle args = new Bundle();
        AuthenticationDialogFragment fragment = new AuthenticationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get current user status
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_authentication_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setView();

    }

    private void setView() {
        // clicking the cancel icon simply close the dialog fragment
        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // simply close the dialog fragment
                dismiss();
            }
        });
        // okay button for performing the re-authentication
        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    authenticateUser(email, password);
                    OnAuthenticationCompleteListener listener = (OnAuthenticationCompleteListener) getTargetFragment();
                    listener.onAuthenticationComplete();
                    dismiss();
                } else {
                    Utils.showSnackBar(binding.getRoot(), "Field cannot be empty");
                }
            }
        });
    }
    // used to authenticate the user. call when button ok is clicked
    private void authenticateUser(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "success");
                        } else {
                            Log.d(LOG_TAG, task.getException().getMessage());
                        }
                    }
                });
    }
}
