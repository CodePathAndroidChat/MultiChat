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

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<ChatMessage> mChatMessages;

    private Context mContext;
    private ViewHolder mViewHolder;

    public MessagesAdapter(Context context, List<ChatMessage> messages) {
        mChatMessages = messages;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMessage;
        public TextView tvUserName;
        public TextView tvTimeAgo;
        public TextView tvMyMessage;
        public TextView tvMyUserName;
        public TextView tvMyTimeAgo;
        public RelativeLayout rlMessage;
        public RelativeLayout rlMyMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvTimeAgo = (TextView) itemView.findViewById(R.id.tvTimeAgo);
            tvMyMessage = (TextView) itemView.findViewById(R.id.tvMyMessage);
            tvMyUserName = (TextView) itemView.findViewById(R.id.tvMyUserName);
            tvMyTimeAgo = (TextView) itemView.findViewById(R.id.tvMyTimeAgo);
            rlMessage = (RelativeLayout) itemView.findViewById(R.id.rlMessage);
            rlMyMessage = (RelativeLayout) itemView.findViewById(R.id.rlMyMessage);
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
            holder.rlMessage.setVisibility(View.INVISIBLE);
            holder.rlMyMessage.setVisibility(View.VISIBLE);
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT); // TODO set gravity to RIGHT
            // Set item views based on your views and data model
            TextView myTextView = mViewHolder.tvMyMessage;
            myTextView.setText(message.getText());
            TextView myUserName = mViewHolder.tvMyUserName;
            myUserName.setText(userId);
            TextView myTime = mViewHolder.tvMyTimeAgo;
            myTime.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));

        } else {
            holder.rlMessage.setVisibility(View.VISIBLE);
            holder.rlMyMessage.setVisibility(View.INVISIBLE);
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set item views based on your views and data model
            TextView textView = mViewHolder.tvMessage;
            textView.setText(message.getText());
            TextView userName = mViewHolder.tvUserName;
            userName.setText(userId);
            TextView time = mViewHolder.tvTimeAgo;
            time.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));
        }


    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }
}
