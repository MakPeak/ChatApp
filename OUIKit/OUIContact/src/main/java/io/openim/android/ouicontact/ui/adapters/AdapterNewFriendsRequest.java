package io.openim.android.ouicontact.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.sdk.models.FriendApplicationInfo;

public class AdapterNewFriendsRequest extends RecyclerView.Adapter<AdapterNewFriendsRequest.ViewHolder> {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    List<FriendApplicationInfo> friendApply;

    // data is passed into the constructor
    public AdapterNewFriendsRequest(Context context, List<FriendApplicationInfo> friendApply, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendApply = friendApply;
        this.mClickListener = mClickListener;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<FriendApplicationInfo> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        friendApply = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_new_friends_requests, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvUsername.setText(friendApply.get(position).getFromNickname());
        holder.tvUserMsg.setText(friendApply.get(position).getReqMsg());
        if(friendApply.get(position).getHandleResult() == 1){
            holder.tvAccept.setVisibility(View.GONE);
            holder.tvReject.setVisibility(View.GONE);
            holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
            holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.accepted));
        } else if(friendApply.get(position).getHandleResult() == -1){
            holder.tvAccept.setVisibility(View.GONE);
            holder.tvReject.setVisibility(View.GONE);
            holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
            holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.rejected));
        } else {
            holder.tvAccept.setVisibility(View.VISIBLE);
            holder.tvReject.setVisibility(View.VISIBLE);
            holder.tvAcceptedOrRejected.setVisibility(View.GONE);
        }

        Glide.with(context)
            .load(friendApply.get(position).getFromFaceURL())
            .into(holder.ivUserImage);

        holder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvAccept.setVisibility(View.GONE);
                holder.tvReject.setVisibility(View.GONE);
                holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
                holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.accepted));
                mClickListener.onItemClick(friendApply.get(position).getFromUserID(), 1);
            }
        });
        holder.tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvAccept.setVisibility(View.GONE);
                holder.tvReject.setVisibility(View.GONE);
                holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
                holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.rejected));
                mClickListener.onItemClick(friendApply.get(position).getFromUserID(), -1);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(friendApply != null) {
            return friendApply.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvUserMsg, tvAccept, tvReject, tvAcceptedOrRejected;
        ImageView ivUserImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user_name);
            ivUserImage = itemView.findViewById(R.id.iv_user);
            tvUserMsg = itemView.findViewById(R.id.tv_user_msg);
            tvAccept = itemView.findViewById(R.id.tv_accept);
            tvReject = itemView.findViewById(R.id.tv_reject);
            tvAcceptedOrRejected = itemView.findViewById(R.id.tv_accepted_rejected);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(String fromUserID, int acceptOrReject);
    }
}
