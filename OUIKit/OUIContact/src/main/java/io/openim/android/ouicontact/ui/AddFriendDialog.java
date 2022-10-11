package io.openim.android.ouicontact.ui;

import static io.openim.android.ouicore.utils.Constant.ID;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.vm.SearchVM;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.widget.AvatarImage;
import io.openim.android.sdk.models.UserInfo;


public class AddFriendDialog {
    AlertDialog dialog ;
    AlertDialog.Builder builder ;
    Activity context ;
    View dialogView ;
    SearchVM vm ;
    AvatarImage avatar ;
    TextView id , gender , nickname ,fullNickname ;
    Button addFriend,sendMsg ;

    public AddFriendDialog(Activity context){

        builder = new AlertDialog.Builder(context);
        vm = new SearchVM();


        dialog = builder.create();
        this.context = context ;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = context.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.layout_add_friend_dialog, null);
        dialog.setView(dialogView);

         avatar = dialogView.findViewById(R.id.avatar);
         id = dialogView.findViewById(R.id.id);
         gender = dialogView.findViewById(R.id.gender);
         nickname = dialogView.findViewById(R.id.nickname);
         fullNickname = dialogView.findViewById(R.id.full_nickname);
         addFriend = dialogView.findViewById(R.id.add_friend);
         sendMsg = dialogView.findViewById(R.id.send_msg);
    }
    public void showDialog(String id){
        vm.searchContent = id ;
        vm.searchPerson(this);
        dialog.show();
    }
    public void finishLoading(List<UserInfo> data){
        fullNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(fullNickname.getText(), fullNickname.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, context.getString(io.openim.android.ouicore.R.string.nickname_copied), Toast.LENGTH_SHORT).show();
            }
        });


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SendVerifyActivity.class).putExtra(ID, vm.searchContent));
                dialog.dismiss();
            }
        });

        sendMsg.setOnClickListener(v -> ARouter.getInstance().build(Routes.Conversation.CHAT)
            .withString(ID, vm.searchContent)
            .withString(io.openim.android.ouicore.utils.Constant.K_NAME, data.get(0).getNickname())
            .navigation());


        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(id.getText(), id.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "ID copied", Toast.LENGTH_SHORT).show();
            }
        });
        System.out.println("isFriendship : "+ data.get(0).isFriendship());
        if(data.get(0).isFriendship())
            addFriend.setVisibility(View.GONE);

        id.setText(""+data.get(0).getUserID());
        String genderString ;
        if(data.get(0).getGender() == 1)
            genderString = "male";
        else
            genderString = "female";
        avatar.load(data.get(0).getFaceURL());
        gender.setText(genderString);
        int length = Math.min(data.get(0).getNickname().length(),11);
        String shorNickname = data.get(0).getNickname().substring(0,length);
        System.out.println("short name " + shorNickname);
        nickname.setText(""+shorNickname);
        fullNickname.setText(""+data.get(0).getNickname());

    }

}
