package com.example.jason.multichatapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.adapters.MessagesAdapter;
import com.example.jason.multichatapp.databinding.FragmentChatRoomBinding;
import com.example.jason.multichatapp.models.ChatMessage;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * ChatRoomFragment-
 */

public class ChatRoomFragment extends Fragment implements MessagesAdapter.ItemClickListener {
    private GetAllPublicUserInfoListener allPublicUserInfoListener;
    public interface GetAllPublicUserInfoListener {
        void onAllUserInfo(Map<String, PublicUser> userMap);

    }
    private static final String LOG_TAG = ChatRoomFragment.class.getSimpleName();

    private final String URL = "https://translation.googleapis.com/language/translate/v2";

    private FirebaseDatabase mDatabase;
    private DatabaseReference messageRef;
    private DatabaseReference userRef;
    private MessagesAdapter mAdapter;
    private ValueEventListener mEventListener;
    private ValueEventListener mUserEventListener;

    private String uid;
    private String locale;

    private FragmentChatRoomBinding binding;
    private Button btnSendMessage;
    private EditText etMessages;
    private RecyclerView rvMessages;

    private List<ChatMessage> chatMessagesList = new ArrayList<>();
    private Map<String, PublicUser> mUserList = new HashMap<>();

    private String dbName = "message";
    private String userDbName = "publicUsers";
    private String roomName = "globalRoom";

    public static ChatRoomFragment newInstance() {
        Bundle args = new Bundle();
        args.putString("roomName", "globalRoom");
        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GetAllPublicUserInfoListener) {
            allPublicUserInfoListener = (GetAllPublicUserInfoListener) context;
        } else {
            throw new ClassCastException(context.getClass().getSimpleName() +
            " must implement " + ChatRoomFragment.GetAllPublicUserInfoListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomName= getArguments().getString("roomName");
        Log.d(LOG_TAG, "roomName " + roomName);
        mDatabase = FirebaseDatabase.getInstance();
        Log.d(LOG_TAG, dbName);
        messageRef = mDatabase.getReference(dbName);
        userRef = mDatabase.getReference(userDbName);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.user_info), Context.MODE_PRIVATE);
        locale = Utils.getLanguageCode(sharedPreferences.getString(getString(R.string.s_language), "English"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false);
        rvMessages = binding.rvMessages;

        mAdapter = new MessagesAdapter(getContext(), chatMessagesList, mUserList, this);
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
        getAllMessagesFromDatabase();
        getAllUsersFromDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnClickListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageRef.removeEventListener(mEventListener);
        userRef.removeEventListener(mUserEventListener);
    }

    // for activity call. get email and uid from activity::onstart
    public void getUserInfo(String uid) {
        this.uid = uid;
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
                if (originalMessage.equals("")) {
                    return;
                }
                // Translate message to English if not in English
                if (!locale.equals("en")) {
                    Log.d(LOG_TAG, "Translating nonEnglish string before saving to Firebase");
                    getTranslationToEnglish(originalMessage);
                } else {
                    saveMessageToFirebase(originalMessage, originalMessage);
                }
                closeKeyboard();
            }
        });
    }

    private void saveMessageToFirebase(String originalMessage, String englishMessage) {
        // Write a message to the database
        String esMessage = null;
        String ruMessage = null;
        String jaMessage = null;
        switch (locale) {
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
        if (uid == null) {
            FirebaseUser firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();
            uid = firebaseUser.getUid();
        }

        myRef.push().setValue(new ChatMessage(
            roomName,
            new Timestamp(System.currentTimeMillis()).toString(),
            originalMessage,
            uid,
            locale,
            englishMessage,
            esMessage,
            ruMessage,
            jaMessage));
        Log.d(LOG_TAG, "Sending message to Firebase Database: " + binding.etMessage.getText().toString());
        etMessages.setText("");
    }

    // returns all messages from Firebase and subscribes to new events
    private void getAllMessagesFromDatabase() {
        mEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Map<String, String > message = (Map<String, String>) child.getValue();
                    ChatMessage chatMessage = new ChatMessage().fromObject(child.getKey(), message);
                    Log.d(LOG_TAG, "----------------------");
                    Log.d(LOG_TAG, "onDataChange event: " + chatMessage.toString());
                    Log.d(LOG_TAG, "chatMessage" + chatMessage);
                    Log.d(LOG_TAG, "chatMessage: room " + chatMessage.getRoom());
                    Log.d(LOG_TAG, "chatMessage: text " + chatMessage.getText());
                    if(roomName.equals(chatMessage.getRoom())) {
                        checkIfMessageInTheListAndUpdate(chatMessage);
                    }
                 }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value. ", databaseError.toException());
            }
        };
        messageRef.addValueEventListener(mEventListener);
    }

    private void getAllUsersFromDatabase() {
        mUserEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Map<String, String > user = (Map<String, String>) child.getValue();
                    PublicUser publicUser = new PublicUser().fromObject(user);
                    Log.d(LOG_TAG, publicUser.toString());
                    mUserList.put(publicUser.uid, publicUser);
                }
                // passing all the user data back to Activity
                allPublicUserInfoListener.onAllUserInfo(mUserList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "Failed to read value. ", databaseError.toException());
            }
        };
        userRef.addValueEventListener(mUserEventListener);
    }

    private void getTranslationToEnglish(final String messageToTranslate) {
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

    @Override
    public void onItemClicked(View v, ChatMessage message) {
        Log.d(LOG_TAG, "Message clicked " + message.toString()); // TODO remove if no need to interact with message list
    }

    private void checkIfMessageInTheListAndUpdate(ChatMessage chatMessage) {
        Log.d(LOG_TAG, "Checking if message already exists " + chatMessage.toString());
        for (int i=0; i < chatMessagesList.size(); i++) {
            if (chatMessage.getId().equals(chatMessagesList.get(i).getId())) {
                chatMessagesList.get(i).updateMessage(chatMessage);
                mAdapter.notifyItemChanged(i);
                Log.d(LOG_TAG, "Message was in the list.");
                return;
            }
        }
        chatMessagesList.add(chatMessage);
        Log.d(LOG_TAG, "Message is new, adding to the list.");
        mAdapter.notifyItemInserted(chatMessagesList.size() - 1);
        rvMessages.scrollToPosition(chatMessagesList.size() - 1);
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
