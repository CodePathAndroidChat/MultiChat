package com.example.jason.multichatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.jason.multichatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "@@@";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    String uid;

    @BindView(R.id.etMessage) EditText etMessage;
    @BindView(R.id.tvMessages) TextView tvMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        ButterKnife.bind(this);

        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("message");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                tvMessages.append("\n" + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName(); //TODO need to set username
            String email = currentUser.getEmail();
            TextView tvEmail = (TextView) findViewById(R.id.tvUserName);
            tvEmail.setText(email);
            // Check if user's email is verified
//            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = currentUser.getUid();
            Log.d(TAG, "User is logged " + uid);
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

    @OnClick(R.id.btnSendMessage)
    public void onSendMessageClicked() {
        // Write a message to the database
        DatabaseReference myRef = mDatabase.getReference("message"); // every message now overrides previous one because of the lack of Model
        myRef.setValue(etMessage.getText().toString());
        Log.d(TAG, "Sending message to Firebase Database: " + etMessage.getText().toString());

        //TODO Create Message Model and set all the fields like:
//        FriendlyMessage friendlyMessage = new
//            FriendlyMessage(etMessage.getText().toString(),
//            mUsername,
//            language);
//        myRef.child(MESSAGES_CHILD) // define scheme for a database https://firebase.google.com/docs/database/web/structure-data
//            .push().setValue(friendlyMessage);
        etMessage.setText("");
    }
}