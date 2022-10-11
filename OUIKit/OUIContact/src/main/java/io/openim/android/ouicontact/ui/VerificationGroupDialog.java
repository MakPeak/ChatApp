package io.openim.android.ouicontact.ui;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.vm.SearchVM;
import io.openim.android.ouicore.widget.AvatarImage;


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
