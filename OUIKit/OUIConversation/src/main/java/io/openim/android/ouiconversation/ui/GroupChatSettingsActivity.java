package io.openim.android.ouiconversation.ui;

import static io.openim.android.ouiconversation.utils.Constant.GROUP_ANNOUNCEMENT;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_ANNOUNCEMENT_TIME;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_DESC;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_ID;
import static io.openim.android.ouiconversation.utils.Constant.GROUP_NAME;
import static io.openim.android.ouiconversation.utils.Constant.OWNER_ID;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityGroupChatSettingsBinding;
import io.openim.android.ouiconversation.databinding.AddGroupItemBinding;
import io.openim.android.ouiconversation.ui.groupsettings.GroupAnnouncementActivity;
import io.openim.android.ouiconversation.ui.groupsettings.GroupMemberDetailsActivity;
import io.openim.android.ouiconversation.ui.groupsettings.GroupQRCodeActivity;
import io.openim.android.ouiconversation.ui.groupsettings.ModifyGroupActivity;
import io.openim.android.ouiconversation.ui.groupsettings.NickNameActivity;
import io.openim.android.ouiconversation.ui.groupsettings.ViewChatHistoryActivity;
import io.openim.android.ouiconversation.ui.groupsettings.ViewGroupMembersActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouiconversation.widget.TwoButtonDialog;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;

public class GroupChatSettingsActivity  extends BaseActivity<GroupChatSettingsVM, ActivityGroupChatSettingsBinding> {

    private static String TAG="GroupChatSettingsActivity";
    String groupID, groupIDFull;
    String ownerID;
    String loggedInUserId = LoginCertificate.getCache(this).userID;
    Boolean isLoggedInUserAnOwner=false;
    int loggedInUserRole=1;
    GroupInfo groupInfo;
    List<GroupMembersInfo> groupMembersInfoList=new ArrayList<>();
    Boolean isPinned=false;
    TwoButtonDialog twoButtonDialog;
//    Boolean isNotInGroup=false;
    RecyclerViewAdapter<GroupMembersInfo, RecyclerViewItem> adapter;
    private long clickedIDTime=-1;
    String nickNameInGroup="", nickNameInGroupFull="";
    public static boolean clearChatHistory = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityGroupChatSettingsBinding.inflate(getLayoutInflater()));
        //binding = ActivityGroupChatSettingsBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        groupID = getIntent().getExtras().getString("chatID").substring(6);
        groupIDFull = getIntent().getExtras().getString("chatID");
      //  ownerID=getIntent().getExtras().getString("ownerID");
    //    isPinned=getIntent().getExtras().getBoolean("isPined");
    //    isNotInGroup = getIntent().getExtras().getBoolean("isNotInGroup");

        view.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        adapter = new RecyclerViewAdapter<GroupMembersInfo, RecyclerViewItem>(RecyclerViewItem.class) {
            @Override
            public void onBindView(@NonNull RecyclerViewItem holder, GroupMembersInfo data, int position) {
                System.out.println("Hello name : " + data.getNickname() + " faceurl :" + data.getFaceURL());
                holder.v.avatar.load(data.getFaceURL());
                holder.v.avatar.setOnClickListener(new View.OnClickListener() {
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
                        System.out.println("iddd d: " + userId);
                        startActivity(new Intent(GroupChatSettingsActivity.this ,
                            GroupMemberDetailsActivity.class)
                            .putExtra("group_nick_name", groupNickName).putExtra("join_time", joinTime)
                            .putExtra("member_user_id", userId).putExtra("face_url",faceURL));
                    }
                });
            }
        };

        init();
        view.recyclerView.setAdapter(adapter);
    }

    public void init(){

        twoButtonDialog=new TwoButtonDialog(this, vm,groupID);
        vm.getGroupMemberList(groupID);
        vm.getGroupInfo(groupID);
        vm.getDND(groupIDFull);
        vm.searchOneConversation(groupID,2);


        vm.conversationInfoResponse.observe(this, data->{
            if(data == null)return;
            view.chatOnTop.setChecked(data.isPinned());
            view.chatOnTop.jumpDrawablesToCurrentState();
            if(data.isNotInGroup())
                view.exitGroupLayout.setVisibility(View.GONE);

        });

        vm.groupsInfo.observe(this, v->{
            if(!v.isEmpty()) {
                groupInfo = v.get(0);
                ownerID=groupInfo.getOwnerUserID();
                view.groupName.setText(groupInfo.getGroupName() + " (" + groupInfo.getMemberCount() + ")");
                view.avatarImage.load(groupInfo.getFaceURL(),true);
                view.membersCount.setText(groupInfo.getMemberCount() + getString(io.openim.android.ouicore.R.string.members));
                if(groupInfo.getOwnerUserID().equalsIgnoreCase(loggedInUserId))
                    isLoggedInUserAnOwner=true;
                else
                    isLoggedInUserAnOwner=false;
                //based on status values, handle ban switch
                if(isLoggedInUserAnOwner){
                    view.allBan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        }
                    });
                    view.allBan.setChecked(groupInfo.getStatus()==3);
                    view.allBan.jumpDrawablesToCurrentState();
                    view.allBan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            vm.allBan(groupID, isChecked);
                        }
                    });
                }
                //chk if group is dismissed
                if(groupInfo.getStatus()==2)
                    view.exitGroupLayout.setVisibility(View.GONE);
            }
            view.groupId.setText(groupID);

            //Group Settings specific to Owner
            if(isLoggedInUserAnOwner) {
                view.exitGroup.setText(io.openim.android.ouicore.R.string.dissolve_group);
                view.showMembers.setVisibility(View.VISIBLE);
                view.allBanLayout.setVisibility(View.VISIBLE);
            }
            //Group Settings specific to Team member
            else {
                view.exitGroup.setText(io.openim.android.ouicore.R.string.exit_group);
                view.showMembers.setVisibility(View.GONE);
                view.allBanLayout.setVisibility(View.GONE);
            }
            view.groupData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GroupChatSettingsActivity.this , ModifyGroupActivity.class)
                        .putExtra("ChatID", groupID).putExtra("groupName", groupInfo.getGroupName())
                        .putExtra("groupDescription", groupInfo.getIntroduction())
                        .putExtra("isLoggedInUserAnOwner",isLoggedInUserAnOwner));
                }
            });

        });

        vm.faceURL.observe(this, data -> {
            view.avatarImage.load(vm.faceURL.getValue(),true);
        });
        vm.groupMembersInfo.observe(this, v-> {

            int i=0;
            List<GroupMembersInfo> tempListOfSix=new ArrayList<>();
            for(GroupMembersInfo groupMembersInfo:v){
                if(groupMembersInfo.getUserID().equalsIgnoreCase(loggedInUserId)){
                    nickNameInGroup = groupMembersInfo.getNickname() ;
                    if(groupMembersInfo.getNickname().length()>12){
                        nickNameInGroupFull=groupMembersInfo.getNickname();
                        nickNameInGroup = groupMembersInfo.getNickname().substring(0,12)+"...";
                    }
                    view.nickname.setText(nickNameInGroup);
                    //check role level of logged in user
                    loggedInUserRole = groupMembersInfo.getRoleLevel();
                }
                groupMembersInfoList = v;
                //for showing only 6 members
                if(i<6){
                    tempListOfSix.add(groupMembersInfo);
                    i++;
                }
            }
            adapter.setItems(tempListOfSix);

        });

        view.addMember.setOnClickListener(v -> {
            ArrayList<String> list = new ArrayList<>() ;
            for(GroupMembersInfo user : groupMembersInfoList ){
                list.add(user.getUserID());
            }
            ARouter.getInstance().build(Routes.Group.CREATE_GROUP).withStringArrayList("group_members", list).withBoolean("isAddingMembers",true)
                .withString("groupId",groupID).navigation();
        });

        view.showMembers.setOnClickListener(v->{
            startActivity(new Intent(GroupChatSettingsActivity.this ,
                ViewGroupMembersActivity.class).putExtra("ChatID", groupID).putExtra("ownerID", ownerID).putExtra("loggedInUserRole", loggedInUserRole));
        });

        vm.changeNickNameStatusMsg.observe(this,v->
            vm.getGroupInfo(groupID)
        );

        //Group Settings - View Group Members
        view.membersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupChatSettingsActivity.this ,
                    ViewGroupMembersActivity.class).putExtra("ChatID", groupID).putExtra("ownerID", ownerID).putExtra("loggedInUserRole",loggedInUserRole));
            }
        });

        //Group Settings - Nickname in group
        view.nicknameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupChatSettingsActivity.this ,
                    NickNameActivity.class).putExtra("ChatID", groupID).putExtra("ownerID", ownerID)
                    .putExtra("nickNameInGroup",nickNameInGroupFull));
            }
        });

        view.groupAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupChatSettingsActivity.this ,
                    GroupAnnouncementActivity.class).putExtra(Constant.GROUP_ID, groupID).putExtra(Constant.OWNER_ID, ownerID)
                    .putExtra(Constant.GROUP_NAME,groupInfo.getGroupName())
                    .putExtra(Constant.GROUP_ANNOUNCEMENT_TIME,groupInfo.getNotificationUpdateTime())
                    .putExtra(Constant.GROUP_FACE_URL,groupInfo.getFaceURL())
                    .putExtra(Constant.GROUP_ANNOUNCEMENT, groupInfo.getNotification()));
            }
        });

        //Group Settings - Group QR code
        view.qrCodeLayout.setOnClickListener(v -> startActivity(new Intent(GroupChatSettingsActivity.this, GroupQRCodeActivity.class)
            .putExtra("ChatID", groupID)
            .putExtra("groupName", groupInfo.getGroupName())
            .putExtra("groupDesc", groupInfo.getIntroduction())
            .putExtra("groupURL", groupInfo.getFaceURL())));

        //Group ID
        view.groupId.setOnClickListener(v -> {
            long clickTimeRes = System.currentTimeMillis()- clickedIDTime ;
            clickTimeRes = clickTimeRes/1000;
            if( clickTimeRes <= 1 && clickedIDTime != -1){
                return;
            }
            clickedIDTime = System.currentTimeMillis();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(view.groupIDLabel.getText(), view.groupId.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(GroupChatSettingsActivity.this, io.openim.android.ouicore.R.string.id_copied, Toast.LENGTH_SHORT).show();
        });
        // View Chat History
        view.viewChatHistoryLayout.setOnClickListener(view ->
            startActivity(new Intent(GroupChatSettingsActivity.this , ViewChatHistoryActivity.class).putExtra("ChatID", groupID)));

        view.chatOnTop.setOnCheckedChangeListener((buttonView, isChecked) -> vm.changePin(isChecked, groupIDFull));

//        vm.pinGroupResponse.observe(this, v->{
//            isPinned=!isPinned;
//        });

        view.allBan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vm.allBan(groupID, isChecked);
            }
        });
        vm.allBanResponse.observe(this, data->{
            Log.d(TAG, "init: allBanResponse observable "+data);
        });

        view.noDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int status = 0 ;
                if(isChecked)
                    status = 2 ;

                vm.DND(status, groupIDFull);
            }
        });

        vm.DNDResponse.observe(this , data ->{
            if(data == null)
                return;
            if(data.size()>0) {
                view.noDisturb.setChecked(data.get(0).getResult() == 2);
                view.noDisturb.jumpDrawablesToCurrentState();
            }
        });

        //Clear Chat History
        view.clearChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twoButtonDialog.showDialog(loggedInUserId, Constant.CLEAR_CHAT_HISTORY, getString(io.openim.android.ouicore.R.string.clear_chat_history_question), getString(io.openim.android.ouicore.R.string.clear), "");
            }
        });

        vm.clearChatHistoryResponse.observe(this, data->{
            if(!data.isEmpty()) {
                toast(getString(io.openim.android.ouicore.R.string.chat_history_cleared));
                twoButtonDialog.closeDialog();
                clearChatHistory=true;

//                Intent intent = new Intent();
//                setResult(Constant.CLEAR_CHAT_HISTORY, intent);
//                finish();

//                Intent intent = null;
//                try {
//                    intent = new Intent(this, Class.forName("io.bytechat.demo.oldrelease.ui.main.MainActivity"));
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                finishAffinity();
//                startActivity(intent);
            }
        });

        //Member - Exit Group      Owner - Dismiss Group
        view.exitGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedInUserAnOwner)
                    twoButtonDialog.showDialog(loggedInUserId, Constant.DISSOLVE_GROUP,getString(io.openim.android.ouicore.R.string.are_you_sure_you_want_to_dissolve_group));
                else
                    twoButtonDialog.showDialog(loggedInUserId, Constant.EXIT_GROUP,getString(io.openim.android.ouicore.R.string.are_you_sure_you_want_to_exit_group));
            }
        });
        vm.dismissGroupResponse.observe(this, v->{
            twoButtonDialog.closeDialog();
            Log.d(TAG, "init: dismissGroupResponse v->"+v);
                if(v==null)
                    return;
                else {
                    Log.d(TAG, "init: dismissGroupResponse inside else "+v.toString());
                    toast(getString(io.openim.android.ouicore.R.string.group_dismissed));
                    try {
                        startActivity(new Intent(GroupChatSettingsActivity.this,
                            Class.forName("io.bytechat.demo.oldrelease.ui.main.MainActivity")));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });

        vm.exitGroupResponse.observe(this, data->{
            twoButtonDialog.closeDialog();
            if(data == null)
                return;
            else {
                toast(getString(io.openim.android.ouicore.R.string.exited_group_successfully));
                finish();
            }
        });

        view.backBtn.setOnClickListener(v->finish());
    }

    public static class RecyclerViewItem extends RecyclerView.ViewHolder {
        public AddGroupItemBinding v;

        public RecyclerViewItem(@NonNull View parent) {
            super((AddGroupItemBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = AddGroupItemBinding.bind(this.itemView);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vm.getGroupInfo(groupID);
        vm.getGroupMemberList(groupID);
    }
}
