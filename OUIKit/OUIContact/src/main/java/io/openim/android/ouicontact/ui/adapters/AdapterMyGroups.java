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

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.sdk.models.GroupApplicationInfo;

public class AdapterMyGroups extends RecyclerView.Adapter<AdapterMyGroups.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<GroupApplicationInfo> groupApplicationInfoList = new ArrayList<>();

    // data is passed into the constructor
    public AdapterMyGroups(Context context, List<GroupApplicationInfo> groupApplicationInfoList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.groupApplicationInfoList = groupApplicationInfoList;
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
        View view = mInflater.inflate(R.layout.item_my_friends, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvUsername.setText(groupApplicationInfoList.get(position).getGroupName());
        if(position == 2){
            holder.tvAcceptedOrRejectedOrRequested.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_request_sent, 0, 0, 0);
            holder.tvAcceptedOrRejectedOrRequested.setText(context.getResources().getString(io.openim.android.ouicore.R.string.request_sent));
        }
        if(position == 4){
            holder.tvAcceptedOrRejectedOrRequested.setText(context.getResources().getString(io.openim.android.ouicore.R.string.rejected));
        }

        Glide.with(context)
            .load(R.mipmap.ic_avatar_2)
            .into(holder.ivUserImage);
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
