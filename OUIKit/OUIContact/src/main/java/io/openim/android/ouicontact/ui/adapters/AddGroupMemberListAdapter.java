package io.openim.android.ouicontact.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.sdk.models.GroupMembersInfo;

public class AddGroupMemberListAdapter extends RecyclerView.Adapter<AddGroupMemberListAdapter.ViewHolder> {

    private List<GroupMembersInfo> list;
    Activity sView ;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            avatar =  view.findViewById(R.id.avatar);
        }
    }

    public AddGroupMemberListAdapter(List<GroupMembersInfo> dataSet, Activity view ) {
        list = dataSet;
        this.sView = view ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.add_group_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        Glide.with(sView)
            .load(list.get(position).getFaceURL())
            .into(viewHolder.avatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
