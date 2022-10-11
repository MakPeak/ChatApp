package io.bytechat.demo.oldrelease.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.zxing.EncodeHintType;

import net.glxn.qrgen.android.QRCode;

import io.bytechat.demo.R;
import io.bytechat.demo.databinding.ActivityMainBinding;
import io.bytechat.demo.databinding.ActivityMyQrcodeBinding;
import io.bytechat.demo.oldrelease.vm.MainVM;
import io.bytechat.demo.oldrelease.vm.MyQRCodeViewModel;
import io.openim.android.ouicore.base.BaseActivity;

public class MyQRCodeActivity extends BaseActivity<MyQRCodeViewModel, ActivityMyQrcodeBinding> implements MyQRCodeViewModel.ViewAction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(MyQRCodeViewModel.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMyQrcodeBinding.inflate(getLayoutInflater()));
        view.setVm(vm);
        init();
    }

    private void init() {
        vm.avatar.observe(this, data->{
            if(this != null) {
                view.profileImg.load(data);
            }

        });
        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vm.nickname.observe(this , data->{
            view.nickname.setText(vm.nickname.getValue());
        });

        vm.userID.observe(this , data->{
            if(data.isEmpty())return;
            Bitmap myBitmap = QRCode.from("io.openim.app/addFriend/"+data).withHint(EncodeHintType.MARGIN, 0).bitmap();
            view.qrCodeImage.setImageBitmap(myBitmap);
        });

        vm.getProfileData();

        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onErr(String msg) {

    }

    @Override
    public void onSucc(Object o) {

    }
}
