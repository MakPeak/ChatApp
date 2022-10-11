package io.bytechat.demo.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import io.bytechat.demo.R;
import io.bytechat.demo.ui.SplashActivity;
import io.bytechat.demo.ui.profile.AccountLanguageSettingsFragment;
import io.openim.android.ouicore.utils.SharedPreferencesUtil;

public class LanguageChangeDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public FragmentActivity fragmentActivity;
    public Dialog d;
    public LinearLayout llOK, llCancel;
    String language;
    LanguageReturnListener languageReturnListener;

    public LanguageChangeDialog(Activity a, String language, LanguageReturnListener languageReturnListener) {
        super(a);
        this.c = a;
        this.language = language;
        this.languageReturnListener = languageReturnListener;
    }

    public LanguageChangeDialog(FragmentActivity fragmentActivity, String language, LanguageReturnListener languageReturnListener) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
        this.language = language;
        this.languageReturnListener = languageReturnListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_language_change);

        llOK = (LinearLayout) findViewById(R.id.ll_sign_out_ok);
        llCancel = (LinearLayout) findViewById(R.id.ll_sign_out_cancel);
        llOK.setOnClickListener(this);
        llCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sign_out_ok:
                languageReturnListener.returnLanguage(language);
                dismiss();
                break;
            case R.id.ll_sign_out_cancel:
                languageReturnListener.returnLanguage("");
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface LanguageReturnListener {
        void returnLanguage(String language);
    }

//    CustomDialogClass cdd = new CustomDialogClass(MainActivity.this);
//    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//    cdd.show();

}
