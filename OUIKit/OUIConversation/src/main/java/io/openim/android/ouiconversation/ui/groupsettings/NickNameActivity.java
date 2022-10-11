package io.openim.android.ouiconversation.ui.groupsettings;

import android.os.Bundle;
import android.view.View;

import io.openim.android.ouiconversation.databinding.GroupNickNameLayoutBinding;
import io.openim.android.ouiconversation.vm.GroupChatSettingsVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;

public class NickNameActivity extends BaseActivity<GroupChatSettingsVM, GroupNickNameLayoutBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupChatSettingsVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(GroupNickNameLayoutBinding.inflate(getLayoutInflater()));

        final String loggedInUserId = LoginCertificate.getCache(this).userID;
        String groupID = getIntent().getExtras().getString("ChatID");
        String nickName = getIntent().getExtras().getString("nickNameInGroup");
        view.inputNickname.setText(nickName);

        view.btnSave.setOnClickListener(
            v -> {
                String groupNickname = view.inputNickname.getText().toString().trim();
                vm.setGroupNickName(groupID, loggedInUserId, groupNickname);

            });

        view.backBtn.setOnClickListener(v -> finish());

        vm.changeNickNameStatusMsg.observe(this, v -> {
            toast(getString(io.openim.android.ouicore.R.string.nick_name_updated));
            finish();
        });


    }
}
