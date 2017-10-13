package com.example.jason.multichatapp.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.databinding.FragmentChatRoomBinding;
import com.example.jason.multichatapp.models.ChatMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ChatRoomFragment-
 */

public class ChatRoomFragment extends Fragment {

    private static final String LOG_TAG = ChatRoomFragment.class.getSimpleName();

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private String uid;
    private String email;

    private FragmentChatRoomBinding binding;
    private Button btnSendMessage;
    private TextView tvEmail;
    private TextView tvMessages;
    private EditText etMessages;

    private List<ChatMessage> chatMessagesList = new ArrayList<>();

    public static ChatRoomFragment newInstance() {

        Bundle args = new Bundle();

        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("message");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        getAllMessagesFromDatabase(); // add all previous messages
        getLastMessageFromDatabase(); // subscribe to listen for new messages
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserInfo();
    }
    // for activity call. get email and uid from activity::onstart
    public void getUserInfo(String email, String uid) {
        this.uid = uid;
        this.email = email;
    }

    private void setupView() {
        btnSendMessage = binding.btnSendMessage;
        tvEmail = binding.tvUserName;
        tvMessages = binding.tvMessages;
        etMessages = binding.etMessage;
    }

    private void showUserInfo() {
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Write a message to the database
                DatabaseReference myRef = mDatabase.getReference("message"); // every message now overrides previous one because of the lack of Model
                myRef.push().setValue(new ChatMessage(
                        new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss'Z'").format(new Timestamp(System.currentTimeMillis())).toString(),
                        binding.etMessage.getText().toString(),
                        uid,
                        "en"));
                Log.d(LOG_TAG, "Sending message to Firebase Database: " + binding.etMessage.getText().toString());

                etMessages.setText("");

            }
        });
        tvEmail.setText(email);
    }

    // returns all messages from Firebase
    private void getAllMessagesFromDatabase() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "dataSnapshot:" + dataSnapshot);
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Map<String, String > message = (Map<String, String>) child.getValue();
                    ChatMessage chatMessage = new ChatMessage().fromObject(message);
                    Log.d(LOG_TAG, "chatMessage" + chatMessage);
                    chatMessagesList.add(chatMessage);
                    tvMessages.append("\n" + chatMessage.getText()); //TODO remove hardcoded textview update
                    //get data, update dataset and remove listener
                }
                myRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    // listens and appends to list last message from Firebase
    private void getLastMessageFromDatabase() {
        // ready from the database. To read data and listen for changes.
        myRef.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "dataSnapshot:" + dataSnapshot);
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Map<String, String > message = (Map<String, String>) child.getValue();
                    ChatMessage chatMessage = new ChatMessage().fromObject(message);
                    Log.d(LOG_TAG, "chatMessage" + chatMessage);
                    chatMessagesList.add(chatMessage);
                    tvMessages.append("\n" + chatMessage.getText()); //TODO remove hardcoded textview update
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

}