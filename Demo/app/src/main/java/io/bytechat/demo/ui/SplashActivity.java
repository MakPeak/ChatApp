package io.bytechat.demo.ui;

import static io.openim.android.ouicore.utils.Constant.ID;

import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.bytechat.demo.databinding.ActivitySplashBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.bytechat.demo.oldrelease.utils.LocaleHelper;
import io.bytechat.demo.oldrelease.vm.LoginVM;
import io.bytechat.demo.oldrelease.vm.MainVM;
import io.bytechat.demo.ui.auth.AuthActivity;
import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.FolderHelper;
import io.openim.android.ouicore.utils.SinkHelper;

public class SplashActivity extends BaseActivity<MainVM, ActivitySplashBinding> implements LoginVM.ViewAction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.settingLanguage(SplashActivity.this);
        bindVM(MainVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySplashBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        view.setMainVM(vm);
        vm.getRecvFriendApplicationList();
        vm.getRecvGroupApplicationList();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Creating folders in internal storage
        FolderHelper.createFoldersInInternalStorage(vm.getContext());

        /* New Handler to start the Auth Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
//                    Toast.makeText(SplashActivity.this, "EXTRAS NOTIFICATION", Toast.LENGTH_LONG).show();
                    JSONObject jsonObj = null;
                    try {
//                        Toast.makeText(SplashActivity.this, jsonObj.get("from").toString(), Toast.LENGTH_LONG).show();
                        Intent mIntent = new Intent(SplashActivity.this, ChatActivity.class);
                        if(extras.get("cn.jpush.android.MESSAGE")!=null) {
                            jsonObj = new JSONObject(extras.get("cn.jpush.android.MESSAGE").toString());
                            mIntent.putExtra(ID, "single_" + jsonObj.get("from"));
                        }
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        Toast.makeText(SplashActivity.this, "EXCEPTION OF NOTIFICATION", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                LoginCertificate loginCertificate = LoginCertificate.getCache(SplashActivity.this);
                if (null == loginCertificate)
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 2000);
    }

    public static String createFolder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File[] directory = new File[0];
            directory = context.getExternalMediaDirs();
            for(int i = 0; i<directory.length; i++){
                if(directory[i].getName().contains(context.getPackageName())){
                    return directory[i].getAbsolutePath();
                }
            }
        }
        return null;
    }

    @Override
    public void jump() {

    }

    @Override
    public void err(String msg) {

    }

    @Override
    public void succ(Object o) {

    }

    @Override
    public void initDate() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        Toast.makeText(SplashActivity.this, "NEW INTENT", Toast.LENGTH_LONG).show();
    }
}
