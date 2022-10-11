package io.openim.android.ouicontact.ui.adapters;

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

import io.openim.android.ouicontact.R;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.GroupApplicationInfo;

public class AdapterNewGroupsRequest extends RecyclerView.Adapter<AdapterNewGroupsRequest.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<GroupApplicationInfo> groupApplicationInfoList = new ArrayList<>();
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterNewGroupsRequest(Context context, List<GroupApplicationInfo> groupApplicationInfoList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.groupApplicationInfoList = groupApplicationInfoList;
        this.mClickListener = mClickListener;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<GroupApplicationInfo> GroupApplicationInfo) {
        // below line is to add our filtered
        // list in our course array list.
        groupApplicationInfoList = GroupApplicationInfo;
        //2454925523
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_new_groups_requests, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvUsername.setText(groupApplicationInfoList.get(position).getNickname());
        holder.tvUserMsg.setText(groupApplicationInfoList.get(position).getGroupName());
        holder.tvRemark.setText("Remark " + groupApplicationInfoList.get(position).getReqMsg());
        if(groupApplicationInfoList.get(position).getHandleResult() == 1){
            holder.tvAccept.setVisibility(View.GONE);
            holder.tvReject.setVisibility(View.GONE);
            holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
            holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.accepted));
        } else if(groupApplicationInfoList.get(position).getHandleResult() == -1){
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
            .load(groupApplicationInfoList.get(position).getGroupFaceURL())
            .into(holder.ivUserImage);

        holder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvAccept.setVisibility(View.GONE);
                holder.tvReject.setVisibility(View.GONE);
                holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
                holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.accepted));
                mClickListener.onItemClick(groupApplicationInfoList.get(position).getGroupID(), groupApplicationInfoList.get(position).getUserID(), 1);
            }
        });
        holder.tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvAccept.setVisibility(View.GONE);
                holder.tvReject.setVisibility(View.GONE);
                holder.tvAcceptedOrRejected.setVisibility(View.VISIBLE);
                holder.tvAcceptedOrRejected.setText(context.getResources().getString(io.openim.android.ouicore.R.string.rejected));
                mClickListener.onItemClick(groupApplicationInfoList.get(position).getGroupID(), groupApplicationInfoList.get(position).getUserID(), -1);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(groupApplicationInfoList != null) {
            return groupApplicationInfoList.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvUserMsg, tvAccept, tvReject, tvAcceptedOrRejected, tvRemark;
        ImageView ivUserImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_group_name);
            ivUserImage = itemView.findViewById(R.id.iv_user);
            tvUserMsg = itemView.findViewById(R.id.tv_group_msg);
            tvAccept = itemView.findViewById(R.id.tv_accept);
            tvReject = itemView.findViewById(R.id.tv_reject);
            tvAcceptedOrRejected = itemView.findViewById(R.id.tv_accepted_rejected);
            tvRemark = itemView.findViewById(R.id.tv_group_remark);

        }

    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(String groupID, String userID, int acceptOrReject);
    }

}
