package io.openim.android.ouiconversation.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;

public class BanMemberDialog {

    AlertDialog dialog ;
    AlertDialog.Builder builder ;
    Activity context ;
    View dialogView ;
    String userID;
    TwoButtonDialog twoButtonDialog;


    public BanMemberDialog(Activity context, GroupChatSettingsVM groupChatSettingsVM, String groupID){

        builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        this.context = context ;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = context.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.ban_member_dialog_layout, null);
        dialog.setView(dialogView);
        twoButtonDialog=new TwoButtonDialog(context, groupChatSettingsVM,groupID);

        ImageView btnClose = dialogView.findViewById(R.id.img_close);

        btnClose.setOnClickListener(view -> closeDialog());

        dialogView.findViewById(R.id.row1).setOnClickListener(v -> {
            //10minutes * 60 = 600 seconds
            Log.d("ViewMembersVMLog", "onClick: 600");

            twoButtonDialog.setSeconds(600);
            showConfirmationPopup(userID, context.getResources().getString(io.openim.android.ouicore.R.string._10_minutes));
           // groupChatSettingsVM.muteMember(groupID, userID, 600);
            closeDialog();
        });
        dialogView.findViewById(R.id.row2).setOnClickListener(v -> {
            //1 hour clicked=> 60 * 60 = 3600
            Log.d("ViewMembersVMLog", "onClick: 3600");
            twoButtonDialog.setSeconds(3600);
            showConfirmationPopup(userID, context.getResources().getString(io.openim.android.ouicore.R.string._1_hour));
            //groupChatSettingsVM.muteMember(groupID, userID, 3600);
            closeDialog();
        });
        dialogView.findViewById(R.id.row3).setOnClickListener(v -> {
            //12 hours clicked => 43200 seconds
            Log.d("ViewMembersVMLog", "onClick: 43200");
            twoButtonDialog.setSeconds(43200);
            showConfirmationPopup(userID,context.getResources().getString(io.openim.android.ouicore.R.string._12_hours));
            //groupChatSettingsVM.muteMember(groupID, userID, 43200);
            closeDialog();
        });
        dialogView.findViewById(R.id.row4).setOnClickListener(v -> {
            //1 day clicked => 86400
            Log.d("ViewMembersVMLog", "onClick: 86400");
            twoButtonDialog.setSeconds(86400);
            showConfirmationPopup(userID,context.getResources().getString(io.openim.android.ouicore.R.string._1_day));
            //groupChatSettingsVM.muteMember(groupID, userID, 86400);
            closeDialog();
        });

        dialogView.findViewById(R.id.btn_enter).setOnClickListener(v -> {
            String enteredSeconds = ((EditText)dialogView.findViewById(R.id.editText_seconds)).getText().toString();
            //customize
            if(enteredSeconds.isEmpty()) {
                Toast.makeText(context, io.openim.android.ouicore.R.string.ban_time_empty_error_message, Toast.LENGTH_SHORT).show();
                ((EditText)dialogView.findViewById(R.id.editText_seconds)).setText("");
            }
            else {
                Long timeInSeconds = Long.valueOf(enteredSeconds);
                Log.d("ViewMembersVMLog", "Custom timeInSeconds->"+enteredSeconds+" seconds");
                ((EditText)dialogView.findViewById(R.id.editText_seconds)).setText("");
                twoButtonDialog.setSeconds(timeInSeconds);
                showConfirmationPopup(userID, timeInSeconds +" seconds");
              //  groupChatSettingsVM.muteMember(groupID, userID, timeInSeconds);
                closeDialog();
            }
        });
    }


    private void showConfirmationPopup(String userID, String duration){
        twoButtonDialog.showDialog(userID, Constant.MUTE_MEMBER_SECONDS, context.getString(io.openim.android.ouicore.R.string.are_you_sure_want_to_ban_the_member)+duration+"?");
    }

    public void showDialog(String userID){
        this.userID=userID;
        dialog.show();
    }

    public void setUserId(String uid){
        userID=uid;
    }

    public void closeDialog(){
        dialog.cancel();
    }
}
