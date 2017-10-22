package com.example.jason.multichatapp.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.adapters.MessagesAdapter;
import com.example.jason.multichatapp.databinding.FragmentChatRoomBinding;
import com.example.jason.multichatapp.models.ChatMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * ChatRoomFragment-
 */

public class ChatRoomFragment extends Fragment {
    private static final String LOG_TAG = ChatRoomFragment.class.getSimpleName();

    private final String URL = "https://translation.googleapis.com/language/translate/v2";

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private MessagesAdapter mAdapter;

    private String uid;
    private String email;

    private FragmentChatRoomBinding binding;
    private Button btnSendMessage;
    private EditText etMessages;
    private RecyclerView rvMessages;
    private TextView tvMessages;

    private List<ChatMessage> chatMessagesList = new ArrayList<>();

    private String dbName = "message";
    private String roomName = "globalRoom";
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
        Log.d(LOG_TAG, dbName);
        myRef = mDatabase.getReference(dbName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false);
        rvMessages = binding.rvMessages;

        mAdapter = new MessagesAdapter(getContext(), chatMessagesList);
        // Attach the adapter to the recyclerview to items
        rvMessages.setAdapter(mAdapter);
        // Set layout manager to position the items
        rvMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        setOnClickListener();
    }
    // for activity call. get email and uid from activity::onstart
    public void getUserInfo(String email, String uid) {
        this.uid = uid;
        this.email = email;
    }

    private void setupView() {
        btnSendMessage = binding.btnSendMessage;
        etMessages = binding.etMessage;
    }

    private void setOnClickListener() {
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String originalMessage = binding.etMessage.getText().toString();
                // Translate message to English if not in English
                if (!Locale.getDefault().getLanguage().equals("en")) {
                    Log.d(LOG_TAG, "Translating nonEnglish string before saving to Firebase");
                    getTranslationToEnglish(originalMessage);
                } else {
                    saveMessageToFirebase(originalMessage, originalMessage);
                }
            }
        });
    }

    private void saveMessageToFirebase(String originalMessage, String englishMessage) {
        // Write a message to the database
        String esMessage = null;
        String ruMessage = null;
        String jaMessage = null;
        switch (Locale.getDefault().getLanguage()) {
            case "ru":
                ruMessage = originalMessage;
                break;
            case "es":
                esMessage = originalMessage;
                break;
            case "jp":
                jaMessage = originalMessage;
                break;
        }
        DatabaseReference myRef = mDatabase.getReference("message");
        myRef.push().setValue(new ChatMessage(
                roomName,
            new Timestamp(System.currentTimeMillis()).toString(),
            originalMessage,
            uid,
            Locale.getDefault().getLanguage(),
            englishMessage,
            esMessage,
            ruMessage,
            jaMessage));
        Log.d(LOG_TAG, "Sending message to Firebase Database: " + binding.etMessage.getText().toString());
        etMessages.setText("");
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
                    if(chatMessage.getRoomName() == roomName) {
                        chatMessagesList.add(chatMessage);
                        mAdapter.notifyDataSetChanged();
                        //get data, update dataset and remove listener
                        rvMessages.scrollToPosition(chatMessagesList.size() - 1);
                    }
                                 }
                myRef.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    // listens and appends to list new message from Firebase
    private void getLastMessageFromDatabase() {
        myRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(LOG_TAG, "dataSnapshot: " + dataSnapshot.getValue());
                Map<String, String> message = (Map<String, String>) dataSnapshot.getValue();
                ChatMessage chatMessage = new ChatMessage().fromObject(message);
                Log.d(LOG_TAG, "chatMessage: " + chatMessage);
                if(chatMessage.getRoomName() == roomName) {
                    chatMessagesList.add(chatMessage);
                    mAdapter.notifyItemInserted(chatMessagesList.size() - 1);
                    rvMessages.scrollToPosition(chatMessagesList.size() - 1);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getTranslationToEnglish(final String messageToTranslate) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("key", getString(R.string.translate_key));
        params.put("q", messageToTranslate);
        params.put("target", "en");
        Log.d(LOG_TAG, "Translating " + messageToTranslate + " before sending to Firebase");
        client.post(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(LOG_TAG, "Google translation " + response);
                try {
                    JSONObject getResponse = (JSONObject) response.getJSONObject("data")
                        .getJSONArray("translations")
                        .get(0);
                    String translation = getResponse.get("translatedText").toString();
                    saveMessageToFirebase(messageToTranslate, translation);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.toString());
                    saveMessageToFirebase(messageToTranslate, null);
                }
            }
        });
    }
}
