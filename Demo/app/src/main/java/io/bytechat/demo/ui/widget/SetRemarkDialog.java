package io.bytechat.demo.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textfield.TextInputEditText;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.ui.search.PersonDetailActivity;

public class SetRemarkDialog extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Dialog d;
    public LinearLayout llOK, llCancel;
    String remark;
    RemarkReturnListener remarkReturnListener;
    public TextInputEditText etNickname;

    public SetRemarkDialog(Activity activity, RemarkReturnListener remarkReturnListener) {
        super(activity);
        this.activity = activity;
        this.remarkReturnListener = remarkReturnListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_remark);

        llOK = (LinearLayout) findViewById(R.id.ll_remark_ok);
        llCancel = (LinearLayout) findViewById(R.id.ll_remark_cancel);
        etNickname = (TextInputEditText) findViewById(R.id.et_nickname);
        llOK.setOnClickListener(this);
        llCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_remark_ok:
                remark = etNickname.getText().toString().trim();
                remarkReturnListener.returnRemark(remark);
                dismiss();

                break;
            case R.id.ll_remark_cancel:
                remarkReturnListener.returnRemark("");
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface RemarkReturnListener {
        void returnRemark(String remark);
    }

//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
