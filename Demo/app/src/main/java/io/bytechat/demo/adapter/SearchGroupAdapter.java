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
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupInfo;

public class SearchGroupAdapter extends RecyclerView.Adapter<SearchGroupAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<GroupInfo> groupInfoList = new ArrayList<>();
    ItemClickListener mClickListener;

    // data is passed into the constructor
    public SearchGroupAdapter(Context context, List<GroupInfo> groupInfoList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.groupInfoList = groupInfoList;
        this.mClickListener = mClickListener;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_group, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvGroupName.setText(groupInfoList.get(position).getGroupName());

        Glide.with(context)
            .load(groupInfoList.get(position).getFaceURL())
            .placeholder(R.mipmap.icon_message_group)
            .into(holder.ivGroup);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onGroupItemClick(groupInfoList.get(position).getGroupID(), groupInfoList.get(position).getGroupName());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(groupInfoList != null) {
            return groupInfoList.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvGroupName;
        ImageView ivGroup;

        ViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            ivGroup = itemView.findViewById(R.id.iv_group);

        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onGroupItemClick(String groupID, String groupName);
    }
}

