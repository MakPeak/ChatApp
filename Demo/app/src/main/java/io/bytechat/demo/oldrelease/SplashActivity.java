package io.bytechat.demo.oldrelease;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.bytechat.demo.oldrelease.ui.login.LoginActivity;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.openim.android.ouicore.entity.LoginCertificate;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginCertificate loginCertificate = LoginCertificate.getCache(this);
        if (null == loginCertificate)
            startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
