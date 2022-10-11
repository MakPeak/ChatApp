package io.openim.android.ouicontact.ui;

import static io.openim.android.ouicore.utils.Constant.ID;

import android.os.Bundle;
import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import io.openim.android.ouicontact.databinding.ActivitySendVerifyBinding;
import io.openim.android.ouicontact.vm.SearchVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;

@Route(path = Routes.Main.SEND_VERIFY)
public class SendVerifyActivity extends BaseActivity<SearchVM, ActivitySendVerifyBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySendVerifyBinding.inflate(getLayoutInflater()));
        view.setSearchVM(vm);

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());

        vm.searchContent = getIntent().getStringExtra(ID);
        vm.isPerson = getIntent().getBooleanExtra(io.openim.android.ouicore.utils.Constant.IS_PERSON, true);
        listener();
        click();
    }

    private void click() {
        view.send.setOnClickListener(v -> {
            vm.addFriend();
        });
        view.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerifyActivity.this.finish();
            }
        });
    }

    private void listener() {
        vm.hail.observe(this, v -> {
            if (v == null)
                finish();
        });
    }
}
