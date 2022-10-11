package io.bytechat.demo.ui.profile;

import android.os.Bundle;

import io.bytechat.demo.databinding.ActivityAboutUsBinding;

import io.bytechat.demo.oldrelease.vm.MainVM;
import io.openim.android.ouicore.base.BaseActivity;

public class AboutUsActivity extends BaseActivity<MainVM, ActivityAboutUsBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(MainVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityAboutUsBinding.inflate(getLayoutInflater()));
    }
}
