package io.openim.android.ouicontact.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.ui.MyFriendsActivity;
import io.openim.android.ouicore.widget.AvatarImage;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;

public class AdapterFrequentContacts extends RecyclerView.Adapter<AdapterFrequentContacts.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<FriendInfo> friendinfoList = new ArrayList<>();
    ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterFrequentContacts(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public AdapterFrequentContacts(Context context, List<FriendInfo> friendInfoList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.friendinfoList = friendInfoList;
        this.mClickListener = mClickListener;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<FriendInfo> friendInfoList) {
        // below line is to add our filtered
        // list in our course array list.
        friendinfoList = friendInfoList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_frequent_contacts, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvUsername.setText(friendinfoList.get(position).getNickname());
        holder.ivUserImage.load(friendinfoList.get(position).getFaceURL());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onContactItemClick(friendinfoList.get(position).getUserID(), friendinfoList.get(position).getNickname());
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(friendinfoList != null) {
            return friendinfoList.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        AvatarImage ivUserImage;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user);
            ivUserImage = itemView.findViewById(R.id.iv_user);

        }

    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onContactItemClick(String fromUserID, String fromNickName);
    }

}
