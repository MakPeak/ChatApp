package io.openim.android.ouiconversation.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.vm.PrivateChatSettingsVM;

public class ClearChatDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public String msg , doneMsg;
    public LinearLayout llOK, llCancel;
    public TextView dialogMsgTextview ,doneTextview ;
    public int type ;
    PrivateChatSettingsVM vm ;

    public ClearChatDialog(Activity a, PrivateChatSettingsVM vm,String msg , String doneMsg , int type) {
        super(a);
        this.c = a;
        this.vm = vm ;
        this.msg = msg ;
        this.doneMsg = doneMsg ;
        this.type = type ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_clear_chat);

        llOK = (LinearLayout) findViewById(R.id.ll_sign_out_ok);
        llCancel = (LinearLayout) findViewById(R.id.ll_sign_out_cancel);
        dialogMsgTextview =  findViewById(R.id.message_dialog);
        doneTextview =  findViewById(R.id.tv_ok);
        llOK.setOnClickListener(this);
        llCancel.setOnClickListener(this);

        dialogMsgTextview.setText(msg);
        doneTextview.setText(doneMsg);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_sign_out_ok) {
            if(type == 1){
                vm.clearChat();
            }else {
                vm.deleteFriend();
                vm.clearChat();
                vm.deleteConversation(vm.chatID.getValue());

            }
            dismiss();
        } else if (id == R.id.ll_sign_out_cancel) {
            dismiss();
        }
        dismiss();
    }

//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
