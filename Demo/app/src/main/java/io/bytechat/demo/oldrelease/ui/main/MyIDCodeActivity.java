package io.bytechat.demo.oldrelease.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import io.bytechat.demo.databinding.ActivityMyIdcodeBinding;
import io.bytechat.demo.oldrelease.vm.ProfileViewModel;
import io.openim.android.ouicore.base.BaseActivity;

public class MyIDCodeActivity extends BaseActivity<ProfileViewModel, ActivityMyIdcodeBinding> implements ProfileViewModel.ViewAction {

    private long clickedIDTime=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(ProfileViewModel.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMyIdcodeBinding.inflate(getLayoutInflater()));
        view.setVm(vm);

        vm.getProfileData();

        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        view.ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTimeRes = System.currentTimeMillis()- clickedIDTime ;
                clickTimeRes = clickTimeRes/1000;
                if( clickTimeRes <= 1 && clickedIDTime != -1){
                    return;
                }
                clickedIDTime = System.currentTimeMillis();
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(vm.userID.getValue());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("User ID", vm.userID.getValue());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(MyIDCodeActivity.this,  getString(io.openim.android.ouicore.R.string.user_id_copied), Toast.LENGTH_SHORT).show();
            }
        });

        vm.avatar.observe(this, data->{
            Glide.with(this)
                .load(data)
                .into(view.ivUserPic);
        });

        vm.nickname.observe(this, data->{
            view.tvUserName.setText(vm.nickname.getValue());
        });

        vm.userID.observe(this, data->{
            view.tvUserId.setText(vm.userID.getValue());
        });

    }

    @Override
    public void onErr(String msg) {

    }

    @Override
    public void onSucc(Object o) {

    }
}
