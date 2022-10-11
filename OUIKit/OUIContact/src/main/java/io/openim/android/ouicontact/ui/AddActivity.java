package io.openim.android.ouicontact.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;

import io.openim.android.ouicontact.databinding.ActivityAddBinding;
import io.openim.android.ouicontact.databinding.ActivityFriendRequestDetailBinding;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;

public class AddActivity extends BaseActivity<ContactVM, ActivityAddBinding> {
    public static final String IS_PERSON = "is_person";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(ContactVM.class);
        super.onCreate(savedInstanceState);

        bindViewDataBinding(ActivityAddBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        view.rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    intent = new Intent(AddActivity.this, Class.forName("io.bytechat.demo.oldrelease.ui.search.SearchConversActivity"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        view.rlScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    intent = new Intent(AddActivity.this, Class.forName("io.bytechat.demo.oldrelease.ui.main.ScannerActivity"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        view.rlCreateGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(Routes.Group.CREATE_GROUP).navigation();
            }
        });

        view.rlJoinGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(AddActivity.this, Class.forName("io.bytechat.demo.oldrelease.ui.search.SearchConversActivity")).putExtra(IS_PERSON, false));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
