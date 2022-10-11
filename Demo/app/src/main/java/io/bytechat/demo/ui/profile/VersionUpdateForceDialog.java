package io.bytechat.demo.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bytechat.demo.R;

public class VersionUpdateForceDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public LinearLayout llUpdate;
    public TextView tvHeading;
    String downloadURL, version;

    public VersionUpdateForceDialog(Activity a, String downloadURL, String version) {
        super(a);
        this.c = a;
        this.downloadURL = downloadURL;
        this.version = version;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_version_update_force);

        llUpdate = (LinearLayout) findViewById(R.id.ll_update);
        tvHeading = (TextView) findViewById(R.id.tv_heading);

        tvHeading.setText(getContext().getString(io.openim.android.ouicore.R.string.force_unforce_heading_1) + " " + version +
            getContext().getString(io.openim.android.ouicore.R.string.force_unforce_heading_2));
        llUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_update:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL));
                getContext().startActivity(browserIntent);
                break;
            default:
                break;
        }
    }

//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
