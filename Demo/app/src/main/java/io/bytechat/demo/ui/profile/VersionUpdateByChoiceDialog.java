package io.bytechat.demo.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.bytechat.demo.R;

public class VersionUpdateByChoiceDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public TextView tvIgnore, tvLater, tvImmediately, tvSubheading, tvReleaseNotes;
    String downloadURL, versionLatest, content;

    public VersionUpdateByChoiceDialog(Activity a, String downloadURL, String versionLatest, String content) {
        super(a);
        this.c = a;
        this.downloadURL = downloadURL;
        this.versionLatest = versionLatest;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_version_update_by_choice);

        PackageManager pm = getContext().getApplicationContext().getPackageManager();
        String pkgName = getContext().getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pkgInfo.versionName;

        tvIgnore = (TextView) findViewById(R.id.tv_ignore);
        tvLater = (TextView) findViewById(R.id.tv_later);
        tvImmediately = (TextView) findViewById(R.id.tv_immediately);
        tvReleaseNotes = (TextView) findViewById(R.id.tv_release_notes);
        tvSubheading = (TextView) findViewById(R.id.tv_subheading);
        tvReleaseNotes.setText(content);
        tvSubheading.setText(getContext().getString(io.openim.android.ouicore.R.string.new_version) + " " + versionLatest +
            getContext().getString(io.openim.android.ouicore.R.string.current_version) + " " + version );
        tvIgnore.setOnClickListener(this);
        tvLater.setOnClickListener(this);
        tvImmediately.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ignore:
                c.finish();
                break;
            case R.id.tv_later:
                c.finish();
                break;
            case R.id.tv_immediately:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL));
                getContext().startActivity(browserIntent);
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
