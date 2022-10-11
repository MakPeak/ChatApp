package io.bytechat.demo.oldrelease.ui.search;


import static io.bytechat.demo.oldrelease.ui.search.AddConversActivity.IS_PERSON;
import static io.openim.android.ouicore.utils.Constant.ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.oldrelease.vm.SearchVM;
import io.bytechat.demo.R;
import io.bytechat.demo.databinding.ActivitySearchPersonBinding;
import io.bytechat.demo.databinding.LayoutSearchItemBinding;
import io.bytechat.demo.ui.widget.AddFriendDialog;
import io.bytechat.demo.ui.widget.AddGroupDialog;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.UserInfo;

public class SearchConversActivity extends BaseActivity<SearchVM, ActivitySearchPersonBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivitySearchPersonBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.sink);
        initView();
    }

    private void initView() {
        view.searchView.getEditText().setFocusable(true);
        view.searchView.getEditText().setFocusableInTouchMode(true);
        view.searchView.getEditText().requestFocus();
        vm.isPerson=getIntent().getBooleanExtra(IS_PERSON,true);
        System.out.println("vm.isPerson : "+vm.isPerson);
        view.searchView.getEditText().setHint(vm.isPerson ? io.openim.android.ouicore.R.string.hint_text : io.openim.android.ouicore.R.string.hint_text_group);
        view.searchView.getEditText().setOnKeyListener((v, keyCode, event) -> {
            vm.searchContent = view.searchView.getEditText().getText().toString();
            vm.search();
            return false;
        });


        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, vm.isPerson);
        view.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view.recyclerView.setAdapter(recyclerViewAdapter);

        vm.userInfo.observe(this, v -> {
            if (vm.searchContent.isEmpty()||null==v) return;
            List<String> userIDs = new ArrayList<>();
            for (UserInfo userInfo : v) {
                userIDs.add(userInfo.getUserID());
            }
            bindDate(recyclerViewAdapter, userIDs);
        });
        vm.groupsInfo.observe(this, v -> {
            if (vm.searchContent.isEmpty()) return;
            List<String> groupIds = new ArrayList<>();
            for (GroupInfo groupInfo : v) {
                groupIds.add(groupInfo.getGroupID());
            }
            bindDate(recyclerViewAdapter, groupIds);
        });

        view.cancel.setOnClickListener(v -> finish());
    }

    private void bindDate(RecyclerViewAdapter recyclerViewAdapter, List<String> v) {

        if (null == v || v.isEmpty()) {
            view.notFind.setVisibility(View.VISIBLE);
            if(vm.isPerson)
                view.notFind.setText(io.openim.android.ouicore.R.string.not_find_user);
            else
                view.notFind.setText(io.openim.android.ouicore.R.string.not_find_group);

            view.recyclerView.setVisibility(View.GONE);
        } else {
            view.notFind.setVisibility(View.GONE);
            view.recyclerView.setVisibility(View.VISIBLE);
            recyclerViewAdapter.setUserInfoList(v);
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AViewHolder> {

        List<String> titles = new ArrayList<>();
        Context context;
        boolean isPerson;
        AddFriendDialog dialog ;
        AddGroupDialog groupDialog ;

        public RecyclerViewAdapter(Activity context, boolean isPerson) {
            this.context = context;
            this.isPerson = isPerson;
            dialog = new AddFriendDialog(context);
            groupDialog = new AddGroupDialog(context);
        }

        public void setUserInfoList(List<String> titles) {
            this.titles = titles;
        }

        @NonNull
        @Override
        public AViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AViewHolder(LayoutSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull AViewHolder holder, int position) {
            String title = titles.get(position);
            holder.view.userId.setText("" + title);

            holder.view.getRoot().setOnClickListener(v -> {
                if (isPerson)
                    dialog.showDialog(title);
                    //context.startActivity(new Intent(context, PersonDetailActivity.class).putExtra(ID, title));
                else
                    groupDialog.showDialog(title);
//                    ARouter.getInstance().build(Routes.Group.DETAIL)
//                        .withString(io.openim.android.ouicore.utils.Constant.GROUP_ID, title).navigation();

            });
        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        public static class AViewHolder extends RecyclerView.ViewHolder {
            public final LayoutSearchItemBinding view;

            public AViewHolder(LayoutSearchItemBinding viewBinding) {
                super(viewBinding.getRoot());
                view = viewBinding;
            }
        }
    }

}
