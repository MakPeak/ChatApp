package io.bytechat.demo.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bytechat.demo.R;

public class GenderDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public LinearLayout llMale, llFemale;
    GenderReturnListener genderReturnListener;

    public GenderDialog(Activity a, GenderReturnListener genderReturnListener) {
        super(a);
        this.c = a;
        this.genderReturnListener = genderReturnListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_gender);

        llMale = (LinearLayout) findViewById(R.id.ll_male);
        llFemale = (LinearLayout) findViewById(R.id.ll_female);
        llMale.setOnClickListener(this);
        llFemale.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_male:
                genderReturnListener.returnInt(2);
                dismiss();
//                c.finish();
                break;
            case R.id.ll_female:
                genderReturnListener.returnInt(1);
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
