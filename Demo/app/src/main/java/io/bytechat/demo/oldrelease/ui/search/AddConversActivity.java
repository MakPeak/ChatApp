package io.bytechat.demo.oldrelease.ui.search;

import android.content.Intent;
import android.os.Bundle;

import io.bytechat.demo.oldrelease.vm.SearchVM;
import io.bytechat.demo.R;
import io.bytechat.demo.databinding.ActivityAddFriendBinding;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.SinkHelper;

public class AddConversActivity extends BaseActivity<SearchVM, ActivityAddFriendBinding> {


    public static final String IS_PERSON = "is_person";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class,true);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityAddFriendBinding.inflate(getLayoutInflater()));
        vm.isPerson=getIntent().getBooleanExtra(IS_PERSON,true);

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        initView();
    }

    private void initView() {
        view.input.getEditText().setHint(vm.isPerson? io.openim.android.ouicore.R.string.hint_text:io.openim.android.ouicore.R.string.hint_text_group);
        view.back.back.setOnClickListener(v->finish());
        view.input.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchConversActivity.class).putExtra(IS_PERSON,vm.isPerson));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCacheVM();
    }
}
