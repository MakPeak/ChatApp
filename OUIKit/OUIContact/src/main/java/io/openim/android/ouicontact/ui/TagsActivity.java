package io.openim.android.ouicontact.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.openim.android.ouicontact.databinding.ActivityTagsBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterTags;
import io.openim.android.ouicontact.ui.newfriends.NewFriendActivity;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.SinkHelper;

public class TagsActivity extends BaseActivity<ContactVM, ActivityTagsBinding> {

    AdapterTags adapterTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(ContactVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityTagsBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        view.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, NewTagActivity.class);
                startActivity(intent);
            }
        });

        adapterTags = new AdapterTags(this);
        view.rvTags.setAdapter(adapterTags);

    }

}
