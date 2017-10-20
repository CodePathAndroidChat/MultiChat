package com.example.jason.multichatapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.DateTimeUtils;
import com.example.jason.multichatapp.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<ChatMessage> mChatMessages;

    private Context mContext;
    private ViewHolder mViewHolder;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    public MessagesAdapter(Context context, List<ChatMessage> messages) {
        mChatMessages = messages;
        mContext = context;

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("message");
        mAuth = FirebaseAuth.getInstance();
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMessage;
        public TextView tvUserName;
        public TextView tvTimeAgo;
        public RelativeLayout rlMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvTimeAgo = (TextView) itemView.findViewById(R.id.tvTimeAgo);
            rlMessage = (RelativeLayout) itemView.findViewById(R.id.rlMessage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View messageView = inflater.inflate(R.layout.item_message, parent, false);

        // Return a new holder instance
        mViewHolder = new ViewHolder(messageView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        ChatMessage message = mChatMessages.get(position);

        String userId = message.getName();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user information", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String uid = sharedPreferences.getString("uid", null);
        if (uid != null && uid.equals(message.getName())) {
            userId = email;
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT); // TODO set gravity to RIGHT
        } else {
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        // Set item views based on your views and data model
        TextView textView = mViewHolder.tvMessage;
        textView.setText(message.getText());
        TextView userName = mViewHolder.tvUserName;
        userName.setText(userId);
        TextView time = mViewHolder.tvTimeAgo;
        time.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }
}
