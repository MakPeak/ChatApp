package io.openim.android.ouiconversation.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;

public class TwoButtonDialog {

    AlertDialog dialog ;
    AlertDialog.Builder builder ;
    Activity context ;
    View dialogView;
    int calledFrom;
    TextView textViewTitle;
    String userID;
    int roleLevel;
    TextView btnYes, btnCancel;
    long seconds=0l;


    public TwoButtonDialog(Activity context, GroupChatSettingsVM groupChatSettingsVM, String groupID){

        builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        this.context = context ;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = context.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.two_button_dialog_layout, null);
        dialog.setView(dialogView);

        textViewTitle = dialogView.findViewById(R.id.tv_title);
        btnCancel = dialogView.findViewById(R.id.tv_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });

        btnYes = dialogView.findViewById(R.id.tv_ok);

        btnYes.setOnClickListener(v -> {
            if(calledFrom==1) //Transfer ownership to other member
                groupChatSettingsVM.transferOwner(groupID, userID);
            else if(calledFrom==2)//Remove member
                groupChatSettingsVM.removeMember(groupID, userID,"Remove");
            else if(calledFrom==3) //Remove member ban
                groupChatSettingsVM.muteMember(groupID, userID, 0);
            else if(calledFrom==Constant.CLEAR_CHAT_HISTORY) //Clear chat history
                groupChatSettingsVM.clearChatHistory(groupID);
            else if(calledFrom== Constant.MAKE_ADMIN)
                groupChatSettingsVM.makeAdmin(groupID, userID,Constant.ROLE_LEVEL_ADMIN);
            else if(calledFrom== Constant.REMOVE_ADMIN)
                groupChatSettingsVM.removeAdmin(groupID, userID,Constant.ROLE_LEVEL_MEMBER);
            else if(calledFrom==Constant.DISSOLVE_GROUP)
                groupChatSettingsVM.dismissGroup(groupID);
            else if(calledFrom==Constant.EXIT_GROUP)
                groupChatSettingsVM.exitGroup(groupID);
            else if(calledFrom==Constant.MUTE_MEMBER_SECONDS)
                groupChatSettingsVM.muteMember(groupID, userID, seconds);
            closeDialog();
        });
    }

    public void showDialog(String userID, int calledFrom, String title){
        this.userID=userID;
        this.calledFrom=calledFrom;
        textViewTitle.setText(title);
        dialog.show();
    }

    public void setSeconds(long seconds){
        this.seconds=seconds;
    }

    public void showDialog(String userID, int calledFrom, String title, String btnYesText, String btnCancelText){
        this.userID=userID;
        this.calledFrom=calledFrom;
        textViewTitle.setText(title);
        if(!btnYesText.isEmpty())
            btnYes.setText(btnYesText);
        if(!btnCancelText.isEmpty())
            btnCancel.setText(btnCancelText);
        dialog.show();
    }

    public void setMemberData(String uid, int rl){
        userID = uid;
        this.roleLevel= rl;
    }

    public void closeDialog(){
        dialog.cancel();
    }

}
