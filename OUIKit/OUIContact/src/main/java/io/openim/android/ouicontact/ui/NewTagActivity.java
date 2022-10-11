package io.openim.android.ouicontact.ui;

import android.os.Bundle;
import android.view.View;

import io.openim.android.ouicontact.databinding.ActivityNewTagBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterTags;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.SinkHelper;

public class NewTagActivity extends BaseActivity<ContactVM, ActivityNewTagBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(ContactVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityNewTagBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
