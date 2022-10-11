package io.bytechat.demo.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import io.bytechat.demo.R;

public class VersionUpdateLatestDialog extends Dialog {

    public Activity c;
    public Dialog d;
    public LinearLayout llUpdate;

    public VersionUpdateLatestDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_version_update_latest);


    }


//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
