package io.bytechat.demo.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bytechat.demo.R;

public class VersionUpdateUnforceDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public LinearLayout llUpdate;
    public ImageView ivClose;
    public TextView tvHeading;
    String downloadURL, version;

    public VersionUpdateUnforceDialog(Activity a, String downloadURL, String version) {
        super(a);
        this.c = a;
        this.downloadURL = downloadURL;
        this.version = version;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_version_update_unforce);

        llUpdate = (LinearLayout) findViewById(R.id.ll_update);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        tvHeading = (TextView) findViewById(R.id.tv_heading);

        tvHeading.setText(getContext().getString(io.openim.android.ouicore.R.string.force_unforce_heading_1) + " " + version +
            getContext().getString(io.openim.android.ouicore.R.string.force_unforce_heading_2));
        llUpdate.setOnClickListener(this);
        ivClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_update:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL));
                getContext().startActivity(browserIntent);
                break;
            case R.id.iv_close:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
