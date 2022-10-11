package io.bytechat.demo.ui.widget;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.adapter.AddGroupMemberListAdapter;
import io.bytechat.demo.oldrelease.vm.SearchVM;
import io.openim.android.ouicore.widget.AvatarImage;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;


public class VerificationGroupDialog {
    AlertDialog dialog ;
    AlertDialog.Builder builder ;
    Activity context ;
    View dialogView ;
    SearchVM vm ;
    AvatarImage avatar ;
    TextView nickname ,groupDescription ;
    EditText verificationMSG ;
    Button cancel,sendRequest  ;
    public VerificationGroupDialog(Activity context){

        builder = new AlertDialog.Builder(context);
        vm = new SearchVM();


        dialog = builder.create();
        this.context = context ;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = context.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.layout_group_verfication_dialog, null);
        dialog.setView(dialogView);

        avatar = dialogView.findViewById(R.id.avatar);
        cancel = dialogView.findViewById(R.id.cancel);
        sendRequest = dialogView.findViewById(R.id.send_request);
        nickname = dialogView.findViewById(R.id.group_name);
        verificationMSG = dialogView.findViewById(R.id.verification_msg);
        groupDescription = dialogView.findViewById(R.id.group_description);
    }
    public void showDialog(String id,String faceURL,String name,String desc){
        dialog.show();
        vm.searchContent = id;
        if(desc.isEmpty())
            groupDescription.setText("There is no description");
        else
            groupDescription.setText(desc);
        avatar.load(faceURL,true);
        nickname.setText(name);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.hail.setValue(verificationMSG.getText().toString());
                vm.addFriend(VerificationGroupDialog.this);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    public void finishLoading(){
        dialog.dismiss();
    }

}
