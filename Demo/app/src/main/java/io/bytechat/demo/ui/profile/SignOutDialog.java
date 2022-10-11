package io.bytechat.demo.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import io.bytechat.demo.R;

public class SignOutDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public LinearLayout llOK, llCancel;
    GenderReturnListener genderReturnListener;

    public SignOutDialog(Activity a, GenderReturnListener genderReturnListener) {
        super(a);
        this.c = a;
        this.genderReturnListener = genderReturnListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sign_out);

        llOK = (LinearLayout) findViewById(R.id.ll_sign_out_ok);
        llCancel = (LinearLayout) findViewById(R.id.ll_sign_out_cancel);
        llOK.setOnClickListener(this);
        llCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sign_out_ok:
                genderReturnListener.returnInt(1);
                dismiss();
                break;
            case R.id.ll_sign_out_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface GenderReturnListener {
        void returnInt(int gender);
    }

//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
