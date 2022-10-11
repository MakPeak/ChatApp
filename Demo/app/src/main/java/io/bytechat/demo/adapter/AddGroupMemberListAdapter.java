package io.bytechat.demo.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.bytechat.demo.R;
import io.openim.android.ouiconversation.ui.groupsettings.GroupMemberDetailsActivity;
import io.openim.android.ouicore.widget.AvatarImage;
import io.openim.android.sdk.models.GroupMembersInfo;

public class AddGroupMemberListAdapter extends RecyclerView.Adapter<AddGroupMemberListAdapter.ViewHolder> {

    private List<GroupMembersInfo> list;
    Activity sView ;
    AlertDialog dialog ;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        AvatarImage avatar;

        public ViewHolder(View view) {
            super(view);
            avatar =  view.findViewById(R.id.avatar);
        }
    }


    public AddGroupMemberListAdapter(List<GroupMembersInfo> dataSet, Activity view, AlertDialog dialog) {
        list = dataSet;
        this.sView = view ;
        this.dialog = dialog ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.add_group_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.avatar.load(list.get(position).getFaceURL());
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SimpleDateFormat DateFor = new SimpleDateFormat("MMMM d, yyyy");
                    String joinTime = DateFor.format(list.get(position).getJoinTime());
                    System.out.println("onBindViewHolder" +list.get(position).getUserID()+" "+
                        list.get(position).getNickname() + " " + list.get(position).getJoinTime()+list.get(position).getFaceURL());
                    dialog.dismiss();
                    view.getContext().startActivity(new Intent(view.getContext() ,
                        GroupMemberDetailsActivity.class)
                        .putExtra("group_nick_name", list.get(position).getNickname()).putExtra("join_time",joinTime)
                        .putExtra("member_user_id", list.get(position).getUserID()).putExtra("face_url",list.get(position).getFaceURL()));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
