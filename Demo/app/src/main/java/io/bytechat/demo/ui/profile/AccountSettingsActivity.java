package io.bytechat.demo.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.ActivityAccountSettingsBinding;
import io.bytechat.demo.databinding.ActivityMyInformationBinding;
import io.bytechat.demo.oldrelease.vm.MainVM;
import io.openim.android.ouicore.base.BaseActivity;

public class AccountSettingsActivity extends BaseActivity<MainVM, ActivityAccountSettingsBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(MainVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityAccountSettingsBinding.inflate(getLayoutInflater()));


    }
}
