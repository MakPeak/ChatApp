package io.openim.android.ouicontact.ui.newfriends;

import static io.openim.android.ouicore.utils.Constant.ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.databinding.ActivityNewFriendBinding;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Constant;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.FriendApplicationInfo;

public class NewFriendActivity extends BaseActivity<ContactVM, ActivityNewFriendBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(ContactVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityNewFriendBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

    }

}
