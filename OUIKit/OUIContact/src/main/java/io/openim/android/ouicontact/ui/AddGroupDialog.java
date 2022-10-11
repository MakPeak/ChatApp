package io.openim.android.ouicontact.ui;

import static io.openim.android.ouicore.utils.Constant.GROUP_ID;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.ui.adapters.AddGroupMemberListAdapter;
import io.openim.android.ouicontact.vm.SearchVM;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.widget.AvatarImage;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;


public class AddGroupDialog {
    VerificationGroupDialog verificationGroupDialog ;
    AlertDialog dialog ;
    AlertDialog.Builder builder ;
    Activity context ;
    View dialogView ;
    SearchVM vm ;
    AvatarImage avatar ;
    TextView id  , nickname ,date ,groupMembersNumber;
    Button addGroup,sendMsg  ;
    ImageView addMembers , showMembers ;
    AddGroupMemberListAdapter adapter ;
    RecyclerView recyclerView;
    public AddGroupDialog(Activity context){
        verificationGroupDialog = new VerificationGroupDialog(context) ;
        builder = new AlertDialog.Builder(context);
        vm = new SearchVM();


        dialog = builder.create();
        this.context = context ;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = context.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.layout_add_group_dialog, null);
        dialog.setView(dialogView);

         avatar = dialogView.findViewById(R.id.avatar);
         addMembers = dialogView.findViewById(R.id.add_member);
         showMembers = dialogView.findViewById(R.id.show_members);
         recyclerView = dialogView.findViewById(R.id.recyclerView);
         id = dialogView.findViewById(R.id.id);
         groupMembersNumber = dialogView.findViewById(R.id.group_members_number);
         date = dialogView.findViewById(R.id.date);
         nickname = dialogView.findViewById(R.id.nickname);
         addGroup = dialogView.findViewById(R.id.add_group);
         sendMsg = dialogView.findViewById(R.id.send_msg);


    }
    public void showDialog(String id){
        vm.searchContent = id ;
        vm.getGroupInfo(this);
        dialog.show();
    }
    public void finishLoading(List<GroupInfo> data, List<GroupMembersInfo> memberList){

        GroupInfo groupInfo = data.get(0);
        if(memberList == null || memberList.size() == 0){
            // fake data ;
            List<GroupMembersInfo> ddata = new LinkedList<>();
            GroupMembersInfo temp = new GroupMembersInfo();
            temp.setFaceURL("http://oss.bytechat-test.com/im-oss/1659779850443804811-1874068156324778273ic_avatar_1.jpg");
            for(int i = 0 ; i < 7; i++)
                ddata.add(temp);


            adapter= new AddGroupMemberListAdapter(ddata.subList(0,6),context);

            sendMsg.setVisibility(View.GONE);
            addMembers.setVisibility(View.GONE);
            addGroup.setVisibility(View.VISIBLE);

        }else{
            List<GroupMembersInfo> ddata = new LinkedList<>();
            for(int i = 0 ; i < memberList.size(); i++) {
                ddata.add(memberList.get(i));
            }


            adapter= new AddGroupMemberListAdapter(ddata.subList(0,Math.min(ddata.size() , 6)),context);


            addGroup.setVisibility(View.GONE);
            showMembers.setVisibility(View.GONE);
            sendMsg.setVisibility(View.VISIBLE);
            addMembers.setVisibility(View.VISIBLE);
        }
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                verificationGroupDialog.showDialog(vm.searchContent, groupInfo.getFaceURL(), groupInfo.getGroupName(),groupInfo.getIntroduction());
            }
        });
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build(Routes.Conversation.CHAT)
                    .withString(GROUP_ID, groupInfo.getGroupID())
                    .withString(io.openim.android.ouicore.utils.Constant.K_NAME, groupInfo.getGroupName())
                    .navigation();

                dialog.dismiss();
            }
        });
        addMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> list = new ArrayList<>() ;
                for(GroupMembersInfo user : memberList ){
                    list.add(user.getUserID());
                    System.out.println(user.getUserID() + " " + user.getNickname());
                }
                ARouter.getInstance().build(Routes.Group.CREATE_GROUP).withStringArrayList("group_members", list).withBoolean("isAddingMembers",true)
                    .withString("groupId",groupInfo.getGroupID()).navigation();
                dialog.dismiss();
            }
        });
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(id.getText(), id.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "ID copied", Toast.LENGTH_SHORT).show();
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
        recyclerView.setAdapter(adapter);

        id.setText(groupInfo.getGroupID());
        nickname.setText(groupInfo.getGroupName());
        String dateString = new SimpleDateFormat("MMM/dd/yyyy").format(new Date(groupInfo.getCreateTime()));
        avatar.load(groupInfo.getFaceURL(),true);
//        avatar.setBackgroundResource(R.drawable.back);
        date.setText(dateString);
        groupMembersNumber.setText("Group member : "+groupInfo.getMemberCount());
    }

}
