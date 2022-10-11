package io.openim.android.ouicontact.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.sdk.models.FriendApplicationInfo;

public class AdapterMyFriends extends RecyclerView.Adapter<AdapterMyFriends.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<FriendApplicationInfo> friendSent;

    // data is passed into the constructor
    public AdapterMyFriends(Context context, List<FriendApplicationInfo> friendApplicationInfoList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendSent = friendApplicationInfoList;
    }

    public void filterList(List<FriendApplicationInfo> filterlist) {
        friendSent = filterlist;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_my_friends, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvUsername.setText(friendSent.get(position).getToNickname());
        if(friendSent.get(position).getHandleResult() == 0){
            holder.tvAcceptedOrRejectedOrRequested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_request_sent, 0, 0, 0);
            holder.tvAcceptedOrRejectedOrRequested.setText(context.getResources().getString(io.openim.android.ouicore.R.string.request_sent));
        } else if(friendSent.get(position).getHandleResult() == 1){
            holder.tvAcceptedOrRejectedOrRequested.setText(context.getResources().getString(io.openim.android.ouicore.R.string.accepted));
        } else if(friendSent.get(position).getHandleResult() == -1){
            holder.tvAcceptedOrRejectedOrRequested.setText(context.getResources().getString(io.openim.android.ouicore.R.string.rejected));
        }

        Glide.with(context)
            .load(friendSent.get(position).getToFaceURL())
            .into(holder.ivUserImage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(friendSent != null) {
            return friendSent.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvAcceptedOrRejectedOrRequested;
        ImageView ivUserImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user);
            ivUserImage = itemView.findViewById(R.id.iv_user);
            tvAcceptedOrRejectedOrRequested = itemView.findViewById(R.id.tv_accepted_rejected_requested);
        }

    }

}
