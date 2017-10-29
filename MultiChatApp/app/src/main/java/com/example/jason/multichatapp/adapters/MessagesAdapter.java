package com.example.jason.multichatapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.DateTimeUtils;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.models.ChatMessage;
import com.example.jason.multichatapp.models.PublicUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<ChatMessage> mChatMessages;
    private Map<String, PublicUser> mPublicUserMap;

    private FirebaseDatabase mDatabase;
    private DatabaseReference userRef;
    private String userDbName = "publicUsers";


    private Context mContext;
    private ViewHolder mViewHolder;

    private ItemClickListener mClickListener;

    public interface ItemClickListener {
        void onItemClicked(View v, ChatMessage message);
    }

    public MessagesAdapter(Context context,
        List<ChatMessage> messages,
        Map<String, PublicUser> users,
        ItemClickListener listener) {
        mChatMessages = messages;
        mContext = context;
        mPublicUserMap = users;
        mClickListener = listener;
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvMessage;
        public TextView tvUserName;
        public TextView tvTimeAgo;
        public TextView tvMyMessage;
        public TextView tvMyUserName;
        public TextView tvMyTimeAgo;
        public RelativeLayout rlMessage;
        public RelativeLayout rlMyMessage;
        public TextView tvOriginalMessage;
        public TextView tvMyOriginalMessage;
        public TextView tvOriginalLanguage;
        public TextView tvMyOriginalLanguage;
        public ImageView ivFlag;
        public ImageView ivAvatar;
        public ImageView ivMyAvatar;

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
            tvOriginalMessage = (TextView) itemView.findViewById(R.id.tvOriginalMessage);
            tvMyOriginalMessage = (TextView) itemView.findViewById(R.id.tvMyOriginalMessage);
            tvOriginalLanguage = (TextView) itemView.findViewById(R.id.tvOriginalLanguage);
            tvMyOriginalLanguage = (TextView) itemView.findViewById(R.id.tvMyOriginalLanguage);
            ivFlag = (ImageView) itemView.findViewById(R.id.ivFlag);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            ivMyAvatar = (ImageView) itemView.findViewById(R.id.ivMyAvatar);
            itemView.setOnClickListener(this);
            mDatabase = FirebaseDatabase.getInstance();
            userRef = mDatabase.getReference(userDbName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ChatMessage message = mChatMessages.get(position);
            tvOriginalMessage.setVisibility(View.VISIBLE);
            tvMyOriginalMessage.setVisibility(View.VISIBLE);
            mClickListener.onItemClicked(v, message);
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
        if( userId == null ) {
            Log.d("null", "---------");
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user information", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String uid = sharedPreferences.getString("uid", null);
        String language = sharedPreferences.getString("language", "English");
        String messageToDisplay = message.getTextByLanguage(Utils.getLanguageCode(language));
        if (messageToDisplay == null) {
            messageToDisplay = message.getText();
        }
        //current user view on the right
        if (uid != null && uid.equals(message.getName())) {
            userId = email;
            holder.rlMessage.setVisibility(View.INVISIBLE);
            holder.rlMyMessage.setVisibility(View.VISIBLE);
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT); // TODO set gravity to RIGHT
            // Set item views based on your views and data model
            TextView myTextView = holder.tvMyMessage;
            myTextView.setText(messageToDisplay);
            TextView myUserName = holder.tvMyUserName;
            myUserName.setText(userId);
            TextView myTime = holder.tvMyTimeAgo;
            myTime.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));
            TextView myOriginalMessage =  holder.tvMyOriginalMessage;
            myOriginalMessage.setText(message.getText());
            TextView myOriginalLanguageView = holder.tvMyOriginalLanguage;
            myOriginalLanguageView.setText(Utils.getLanguageCode(language));
            if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                String image = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                Glide.with(mContext).load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivMyAvatar);
            } else {
                TextDrawable drawable = TextDrawable.builder()
                    .buildRound(userId.substring(0, 1).toUpperCase(), mContext.getResources().getColor(R.color.green_normal));
                holder.ivMyAvatar.setImageDrawable(drawable);
            }


        //other user view on the left
        } else {
            PublicUser user = findUserEmail(userId);
            TextView textView = holder.tvMessage;
            textView.setText(messageToDisplay);
            TextView time = holder.tvTimeAgo;
            time.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));
            TextView originalMessage =  holder.tvOriginalMessage;
            originalMessage.setText(message.getText());


            if (user == null || user.email == null) {
                final ViewHolder _holder = holder;
                Log.d("--USER-IS_NULL", "NULLLLL");
                // THIS SEEMS TO FETCH THINGS FOR US and no more null :/.. no more is needed.
                // Kinda weird... it would work, I would expect to have to fill user by hand.
                if (userId != null ) {
                    userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, String> user = (Map<String, String>) dataSnapshot.getValue();
                            PublicUser publicUser = new PublicUser().fromObject(user);
                            setUserStuff(_holder, publicUser);
                            Log.d("------!!!!!!----", "FETCHED!!!");

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.d("USERID-NULL", "NULL");

                }

            } else {
                setUserStuff(holder, user);
            }

        }
    }

    private void setUserStuff(ViewHolder holder, PublicUser user) {
        holder.rlMessage.setVisibility(View.VISIBLE);
        holder.rlMyMessage.setVisibility(View.INVISIBLE);
        holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set item views based on your views and data model

        TextView userName = holder.tvUserName;
        if (user != null) {
            userName.setText(user.email);
            TextView lang = holder.tvOriginalLanguage;
            lang.setText(user.language);
        } else {
            userName.setText("anonymous");
        }
        ImageView flagView = holder.ivFlag;
        TextDrawable drawable;
        if (user != null) {

            flagView.setImageResource(setFlagImageForUser(user));
            drawable = TextDrawable.builder()
                    .buildRound(user.email.substring(0, 1).toUpperCase()
                            , mContext.getResources().getColor(R.color.green_light));

        } else {

            flagView.setImageResource(R.drawable.us_icon);
            drawable = TextDrawable.builder()
                    .buildRound("Default", mContext.getResources().getColor(R.color.green_light));

        }

        holder.ivAvatar.setImageDrawable(drawable);
    }

    private int setFlagImageForUser(PublicUser user) {
        if (user == null) {
            return R.drawable.us_icon;
        }

        if( user.country == null ) {
            return R.drawable.us_icon; // TODO: return world icon
        }
        switch (user.country) {
            case "US":
            case "USA":
                return R.drawable.us_icon;
            case "RU":
                return R.drawable.ru_icon;
            default:
                return R.drawable.us_icon;
        }
    }

    private PublicUser findUserEmail(String userId) {
        PublicUser user = mPublicUserMap.get(userId);
//        Log.d("@@@", "Found user: " + user.toString());
        return user;
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }
}
