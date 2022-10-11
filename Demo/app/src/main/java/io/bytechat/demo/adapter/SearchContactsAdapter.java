package io.bytechat.demo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.ui.search.SearchContactsFragment;
import io.openim.android.ouicontact.ui.adapters.AdapterNewFriendsRequest;
import io.openim.android.sdk.models.FriendInfo;

public class SearchContactsAdapter extends RecyclerView.Adapter<SearchContactsAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<FriendInfo> friendInfoList = new ArrayList<>();
    ItemClickListener mClickListener;
    // data is passed into the constructor
    public SearchContactsAdapter(Context context, List<FriendInfo> friendInfoList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendInfoList = friendInfoList;
        this.mClickListener = mClickListener;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_contacts, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvContactName.setText(friendInfoList.get(position).getNickname());
        holder.tvContactID.setText(String.valueOf(friendInfoList.get(position).getUserID()));

        Glide.with(context)
            .load(friendInfoList.get(position).getFaceURL())
            .into(holder.ivContact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onContactItemClick(friendInfoList.get(position).getUserID(), friendInfoList.get(position).getNickname());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(friendInfoList != null) {
            return friendInfoList.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvContactName, tvContactID;
        ImageView ivContact;

        ViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            ivContact = itemView.findViewById(R.id.iv_contact);
            tvContactID = itemView.findViewById(R.id.tv_contact_id);

        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onContactItemClick(String fromUserID, String fromNickName);
    }
}

