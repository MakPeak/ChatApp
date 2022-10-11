package io.bytechat.demo.oldrelease.ui.login;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import io.bytechat.demo.oldrelease.vm.LoginVM;

import io.bytechat.demo.databinding.ActivitySupplementInfoBinding;
import io.bytechat.demo.oldrelease.ui.main.MainActivity;
import io.openim.android.ouicore.base.BaseActivity;


public class SupplementInfoActivity extends BaseActivity<LoginVM, ActivitySupplementInfoBinding> implements LoginVM.ViewAction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindVMByCache(LoginVM.class);
        bindViewDataBinding(ActivitySupplementInfoBinding.inflate(getLayoutInflater()));
        view.setLoginVM(vm);
    }

    // 禁止返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCacheVM();
    }

    @Override
    public void jump() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
}
