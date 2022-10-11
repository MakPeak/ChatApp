package io.openim.android.ouicontact.ui.newgroups;

import android.os.Bundle;

import io.openim.android.ouicontact.databinding.ActivityNewFriendBinding;
import io.openim.android.ouicontact.databinding.ActivityNewGroupBinding;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.SinkHelper;

public class NewGroupActivity extends BaseActivity<ContactVM, ActivityNewGroupBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(ContactVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityNewGroupBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

    }

}
