package io.openim.android.ouicontact.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.sdk.models.GroupInfo;

public class AdapterUserCreatedJoinedGroups extends RecyclerView.Adapter<AdapterUserCreatedJoinedGroups.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<GroupInfo> groupApplicationInfoList = new ArrayList<>();
    ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterUserCreatedJoinedGroups(Context context, List<GroupInfo> groupApplicationInfoList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.groupApplicationInfoList = groupApplicationInfoList;
        this.mClickListener = mClickListener;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<GroupInfo> GroupApplicationInfo) {
        // below line is to add our filtered
        // list in our course array list.
        groupApplicationInfoList = GroupApplicationInfo;
        //2454925523
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_user_created_joined, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvGroupName.setText(groupApplicationInfoList.get(position).getGroupName());
        holder.tvGroupMembers.setText(String.valueOf(groupApplicationInfoList.get(position).getMemberCount()) +context.getString(io.openim.android.ouicore.R.string.space )+context.getString(io.openim.android.ouicore.R.string.member));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onGroupItemClick(groupApplicationInfoList.get(position).getGroupID(), groupApplicationInfoList.get(position).getGroupName());
            }
        });

//        if(createdORjoined.equalsIgnoreCase("created")){
//            holder.tvGroupName.setText("Test Group created");
//            holder.tvGroupMembers.setText("125 members");
//        } else if(createdORjoined.equalsIgnoreCase("joined")){
//            holder.tvGroupName.setText("Test Group joined");
//            holder.tvGroupMembers.setText("10 members");
//        }

//        Glide.with(context)
//            .load(R.mipmap.ic_avatar_2)
//            .into(holder.ivGroup);
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
        TextView tvGroupName, tvGroupMembers;
        ImageView ivGroup;

        ViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group);
            ivGroup = itemView.findViewById(R.id.iv_group);
            tvGroupMembers = itemView.findViewById(R.id.tv_group_members);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onGroupItemClick(String groupID, String groupName);
    }

}
