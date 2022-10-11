package io.openim.android.ouiconversation.ui.groupsettings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityViewGroupMembersBinding;
import io.openim.android.ouiconversation.databinding.GroupMemberItemLayoutBinding;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouiconversation.widget.BanMemberDialog;
import io.openim.android.ouiconversation.widget.TwoButtonDialog;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.sdk.models.GroupMembersInfo;

public class ViewGroupMembersActivity extends BaseActivity<GroupChatSettingsVM, ActivityViewGroupMembersBinding> {

    public RecyclerViewAdapter<GroupMembersInfo, ViewGroupMembersActivity.RecyclerViewItem> adapter;
    BanMemberDialog banMemberDialog;
    TwoButtonDialog twoButtonDialog;
    private final String TAG="ViewGroupMemberActivity";
    String loggedInUserId ="";
    String groupID;
    String ownerID;
    int loggedInUserRole;
   // List<GroupMembersInfo> groupMembersInfoList=new LinkedList<>();

    private void init(){
        groupID = getIntent().getExtras().getString("ChatID");
        ownerID = getIntent().getExtras().getString("ownerID");
        loggedInUserRole=getIntent().getExtras().getInt("loggedInUserRole");
        loggedInUserId = LoginCertificate.getCache(this).userID;
        banMemberDialog=new BanMemberDialog(this, vm,groupID);
        twoButtonDialog=new TwoButtonDialog(this, vm,groupID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityViewGroupMembersBinding.inflate(getLayoutInflater()));

        init();

        adapter = new RecyclerViewAdapter<GroupMembersInfo, ViewGroupMembersActivity.RecyclerViewItem>(ViewGroupMembersActivity.RecyclerViewItem.class) {
            @Override
            public void onBindView(@NonNull RecyclerViewItem holder, GroupMembersInfo data, int position) {
                holder.v.groupMemberAvatar.load(data.getFaceURL());

                String nickname = data.getNickname() ;
                if(nickname.length()>12){
                    nickname = nickname.substring(0,12)+"...";
                }
                holder.v.groupMemberNickname.setText(nickname);
               // banMemberDialog.setUserId(data.getUserID());
             //  twoButtonDialog.setMemberData(data.getUserID(),data.getRoleLevel());

                Boolean isBanned=data.getMuteEndTime()>System.currentTimeMillis()/1000;
                Log.d(TAG, "onBindView: "+data.getNickname() + " " +data.getMuteEndTime() + " "+isBanned);
                if(isBanned)
                    holder.v.banMember.setBackgroundResource(R.mipmap.banned_red);
                else
                    holder.v.banMember.setBackgroundResource(R.mipmap.setting_members01);

                //HANDLING OF OWNER/ADMIN/MEMBER VIEWS OF EACH MEMBER
                if(data.getRoleLevel()==Constant.ROLE_LEVEL_OWNER){
                    //For Owners/Admin/member - always show group owner label
                    holder.v.btnGroupOwner.setVisibility(View.VISIBLE);
                    holder.v.btnGroupAdmin.setVisibility(View.GONE);
                    holder.v.ownerOptionsLayout.setVisibility(View.INVISIBLE);
                }
                //Admin view - show Owner label, admin label, options layout with 2 options
                else if(data.getRoleLevel()==Constant.ROLE_LEVEL_ADMIN){
                    //1. For members/Admins - show admin label
                    if(loggedInUserRole==Constant.ROLE_LEVEL_MEMBER || loggedInUserRole==Constant.ROLE_LEVEL_ADMIN) {
                        holder.v.btnGroupOwner.setVisibility(View.INVISIBLE);
                        holder.v.btnGroupAdmin.setVisibility(View.VISIBLE);
                        holder.v.ownerOptionsLayout.setVisibility(View.INVISIBLE);
                    }
                    //2. Owners - Show make admin in orange
                    else if(loggedInUserRole==Constant.ROLE_LEVEL_OWNER) {
                        holder.v.btnGroupOwner.setVisibility(View.INVISIBLE);
                        holder.v.btnGroupAdmin.setVisibility(View.GONE);
                        holder.v.ownerOptionsLayout.setVisibility(View.VISIBLE);
                        holder.v.removeAdmin.setVisibility(View.VISIBLE);
                        holder.v.makeAdmin.setVisibility(View.GONE);
                    }
                }else if(data.getRoleLevel()==Constant.ROLE_LEVEL_MEMBER){
                    holder.v.btnGroupOwner.setVisibility(View.INVISIBLE);
                    holder.v.btnGroupAdmin.setVisibility(View.GONE);
                    //1. Owner view of a member
                    if(loggedInUserRole==Constant.ROLE_LEVEL_OWNER){
                        holder.v.ownerOptionsLayout.setVisibility(View.VISIBLE);
                        holder.v.makeAdmin.setVisibility(View.VISIBLE);
                        holder.v.removeAdmin.setVisibility(View.GONE);
                        holder.v.transferOwnership.setVisibility(View.VISIBLE);
                    }
                    //2. Admin view of a member
                    else if(loggedInUserRole==Constant.ROLE_LEVEL_ADMIN){
                        holder.v.ownerOptionsLayout.setVisibility(View.VISIBLE);
                        holder.v.makeAdmin.setVisibility(View.GONE);
                        holder.v.removeAdmin.setVisibility(View.GONE);
                        holder.v.transferOwnership.setVisibility(View.GONE);
                    }
                    //3. Member view of a member
                    else if(loggedInUserRole==Constant.ROLE_LEVEL_MEMBER) {
                        holder.v.ownerOptionsLayout.setVisibility(View.INVISIBLE);
                    }
                }

                holder.v.makeAdmin.setOnClickListener(v ->
                    twoButtonDialog.showDialog(data.getUserID(), Constant.MAKE_ADMIN, getString(io.openim.android.ouicore.R.string.make_the_member_an_admin_question)));

                holder.v.removeAdmin.setOnClickListener(v->{
                    twoButtonDialog.showDialog(data.getUserID(), Constant.REMOVE_ADMIN,getString(io.openim.android.ouicore.R.string.remove_the_member_as_admin_quesiton));
                });

                holder.v.banMember.setOnClickListener(view -> {
                    if(isBanned)
                        twoButtonDialog.showDialog(data.getUserID(), 3, getString(io.openim.android.ouicore.R.string.remove_the_member_ban_quesiton));
                    else
                        banMemberDialog.showDialog(data.getUserID());
                });

                holder.v.transferOwnership.setOnClickListener(v -> {
                   // twoButtonDialog.setMemberData(data.getUserID(),data.getRoleLevel()) ;
                    twoButtonDialog.showDialog(data.getUserID(), 1, getString(io.openim.android.ouicore.R.string.transfer_group_owner_quesiton));
                });

                holder.v.removeMember.setOnClickListener(v ->
                    twoButtonDialog.showDialog(data.getUserID(), 2, getString(io.openim.android.ouicore.R.string.remove_the_member_quesiton)));

                holder.v.groupMemberNickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String groupNickName="";
                        String joinTime="";
                        String userId="";
                        String faceURL="";

                        if(data!=null){
                            userId=data.getUserID();
                            groupNickName=data.getNickname();
                            faceURL=data.getFaceURL();

                            //Date format
                            long millis = data.getJoinTime()*1000;
                            Date date = new Date(millis);
                            SimpleDateFormat DateFor = new SimpleDateFormat("MMMM d, yyyy");
                            joinTime = DateFor.format(date);

                        }
                        startActivity(new Intent(ViewGroupMembersActivity.this ,
                            GroupMemberDetailsActivity.class)
                            .putExtra("group_nick_name", groupNickName).putExtra("join_time", joinTime)
                            .putExtra("member_user_id", userId).putExtra("face_url",faceURL));
                    }
                });
                holder.v.groupMemberAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String groupNickName="";
                        String joinTime="";
                        String userId="";
                        String faceURL="";

                        if(data!=null){
                            userId=data.getUserID();
                            groupNickName=data.getNickname();
                            faceURL=data.getFaceURL();

                            //Date format
                            long millis = data.getJoinTime()*1000;
                            Date date = new Date(millis);
                            SimpleDateFormat DateFor = new SimpleDateFormat("MMMM d, yyyy");
                            joinTime = DateFor.format(date);

                        }
                        startActivity(new Intent(ViewGroupMembersActivity.this ,
                            GroupMemberDetailsActivity.class)
                            .putExtra("group_nick_name", groupNickName).putExtra("join_time", joinTime)
                            .putExtra("member_user_id", userId).putExtra("face_url",faceURL));
                    }
                });
            }
        };

        view.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view.recyclerView.setAdapter(adapter);
        view.swipeRefresh.setOnRefreshListener(() -> vm.getGroupMemberList(groupID));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.recyclerView.getContext(),
            DividerItemDecoration.VERTICAL);
        view.recyclerView.addItemDecoration(dividerItemDecoration);

        vm.getGroupMemberList(groupID);
        vm.groupMembersInfo.observe(this, v -> {
          //  init();
            view.swipeRefresh.setRefreshing(false);
            Log.d(TAG, "onCreate:groupMembersInfo.observe called "+ v.size());
            List<GroupMembersInfo> groupMembersInfoList = new LinkedList<>();
            for (GroupMembersInfo groupMembersInfo : v) {
                Log.d(TAG, "groupMembersInfo : "+groupMembersInfo.getNickname() + " " + groupMembersInfo.getRoleLevel()+" "+groupMembersInfo.getUserID());
                Log.d(TAG, "groupMembersInfo: groupMembersInfo.getMuteEndTime(): " + groupMembersInfo.getMuteEndTime());
                Log.d(TAG, "groupMembersInfo: System.currentTimeMillis()/1000 :" + System.currentTimeMillis()/1000);
                if(groupMembersInfo.getUserID().equalsIgnoreCase(loggedInUserId)) {
                    loggedInUserRole = groupMembersInfo.getRoleLevel();
                    ownerID = groupMembersInfo.getOperatorUserID();
                }

                groupMembersInfoList.add(groupMembersInfo);
            }
            view.title.setText(getString(io.openim.android.ouicore.R.string.group_members)+"("+groupMembersInfoList.size()+")");
          //  view.recyclerView.setAdapter(adapter);
            adapter.clearAllItems();
            adapter.setItems(groupMembersInfoList);
            adapter.notifyDataSetChanged();
        });

        vm.muteMemberStatusMsg.observe(this, v-> {
            view.statusMessageLayoutRed.setVisibility(View.VISIBLE);
            if(v.equalsIgnoreCase("0"))
                view.tvStatusMessageRed.setText(io.openim.android.ouicore.R.string.removed_ban_successfully);
            else
                view.tvStatusMessageRed.setText(io.openim.android.ouicore.R.string.banned_successfully);
            view.tvStatusMessageRed.postDelayed(() -> view.statusMessageLayoutRed.setVisibility(View.GONE),2000);
            vm.getGroupMemberList(groupID);
            banMemberDialog.closeDialog();
            twoButtonDialog.closeDialog();
        });

        vm.modifyRoleLevelMsg.observe(this, v->{
            view.statusMessageLayoutBlue.setVisibility(View.VISIBLE);
            view.tvStatusMessageBlue.setText(io.openim.android.ouicore.R.string.transfer_ownership_correct);
            view.tvStatusMessageBlue.postDelayed(() -> view.statusMessageLayoutBlue.setVisibility(View.GONE),2000);
            vm.getGroupMemberList(groupID);
            twoButtonDialog.closeDialog();
        });
        vm.makeAdminRoleMsg.observe(this, v->{
            view.statusMessageLayoutBlue.setVisibility(View.VISIBLE);
            view.tvStatusMessageBlue.setText(io.openim.android.ouicore.R.string.made_admin_correct);
            view.tvStatusMessageBlue.postDelayed(() -> view.statusMessageLayoutBlue.setVisibility(View.GONE),2000);
            vm.getGroupMemberList(groupID);
            twoButtonDialog.closeDialog();
        });
        vm.removeAdminRoleMsg.observe(this, v->{
            view.statusMessageLayoutBlue.setVisibility(View.VISIBLE);
            view.tvStatusMessageBlue.setText(io.openim.android.ouicore.R.string.remove_admin_correct);
            view.tvStatusMessageBlue.postDelayed(() -> view.statusMessageLayoutBlue.setVisibility(View.GONE),2000);
            vm.getGroupMemberList(groupID);
            twoButtonDialog.closeDialog();
        });

        vm.removeStatusMsg.observe(this, v->{
            view.statusMessageLayoutRed.setVisibility(View.VISIBLE);
            view.tvStatusMessageRed.setText(io.openim.android.ouicore.R.string.remove_user_correct);
            view.tvStatusMessageRed.postDelayed(() -> view.statusMessageLayoutRed.setVisibility(View.GONE),2000);
            vm.getGroupMemberList(groupID);
            twoButtonDialog.closeDialog();
        });

        view.backBtn.setOnClickListener(view -> finish());
        view.title.setText(getString(io.openim.android.ouicore.R.string.group_members));

    }

    public static class RecyclerViewItem extends RecyclerView.ViewHolder {
        public GroupMemberItemLayoutBinding v;

        public RecyclerViewItem(@NonNull View parent) {
            super((GroupMemberItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = GroupMemberItemLayoutBinding.bind(this.itemView);
        }
    }

}
