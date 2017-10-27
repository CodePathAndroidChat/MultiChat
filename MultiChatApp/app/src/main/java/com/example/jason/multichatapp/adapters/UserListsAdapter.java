package com.example.jason.multichatapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.models.PublicUser;

import java.util.List;

/**
 * UserListsAdapter-
 */

public class UserListsAdapter extends RecyclerView.Adapter<UserListsAdapter.ViewHolder> {
    // callback to handle arrow click that links to direct message screen
    private OnDirectMsgArrowClick arrowClickListener;
    public interface OnDirectMsgArrowClick {
        void onDirectMsgClick(View view, int position);
    }
    public void setOnDirectMsgArrowClick(OnDirectMsgArrowClick arrowClickListener) {
        this.arrowClickListener = arrowClickListener;
    }
    // TODO: once user class is finalized, update this class to use data binding
    private List<PublicUser> users;
    private Context context;

    public UserListsAdapter(Context context, List<PublicUser> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.item_users_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PublicUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView tvOriginalLanguage;
        TextView tvName;
        TextView tvLocation;
        ImageView ivMsgArrow;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvOriginalLanguage = itemView.findViewById(R.id.tvOriginalLanguage);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivMsgArrow = itemView.findViewById(R.id.ivMsgArrow);

        }

        public void bind(PublicUser user) {
            tvName.setText(user.email);
            tvOriginalLanguage.setText(user.language);
            tvLocation.setText(user.location);
            ivMsgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    arrowClickListener.onDirectMsgClick(view, getAdapterPosition());
                }
            });

            TextDrawable drawable = TextDrawable.builder()
                .buildRound(user.email.substring(0, 1).toUpperCase(), context.getResources().getColor(R.color.green_light));
            ivAvatar.setImageDrawable(drawable);
        }
    }
}
