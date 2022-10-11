package io.openim.android.ouigroup.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.openim.android.ouigroup.R;
import io.openim.android.ouigroup.databinding.AddGroupItemBinding;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.mao.sortletterlib.SortLetterView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.ouigroup.databinding.ActivityInitiateGroupBinding;
import io.openim.android.ouigroup.databinding.ItemPsrsonSelectBinding;
import io.openim.android.ouigroup.databinding.ItemPsrsonStickyBinding;
import io.openim.android.ouigroup.entity.ExUserInfo;
import io.openim.android.ouigroup.vm.GroupVM;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.UserInfo;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

@Route(path = Routes.Group.CREATE_GROUP)
public class InitiateGroupActivity extends BaseActivity<GroupVM, ActivityInitiateGroupBinding> {
    private RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder> adapter;
    private RecyclerViewAdapter<ExUserInfo, SelectedViewHolder> adapterSelected ;
    List<ExUserInfo> allItems ;
    List<String> groupMembers ;
    String groupId ;
    boolean isAddingMembers = false ;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(GroupVM.class, true);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityInitiateGroupBinding.inflate(getLayoutInflater()));
        initView();
        vm.getAllFriend();
        listener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCacheVM();
    }


    private void initView() {
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        isAddingMembers = getIntent().getExtras().getBoolean("isAddingMembers",false);
        groupId = getIntent().getExtras().getString("groupId");
        groupMembers = getIntent().getExtras().getStringArrayList("group_members");
        System.out.println("isAddingMembers "+ isAddingMembers);

        if(isAddingMembers){
            view.title.setText(getString(io.openim.android.ouicore.R.string.friends));
        }
        view.submit.setText(getString(io.openim.android.ouicore.R.string.confirm)+"(" + 0 + "/1999）");
        view.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        view.scrollView.fullScroll(View.FOCUS_DOWN);

        view.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder>() {
            private int STICKY = 1;
            private int ITEM = 2;

            private String lastSticky = "";

            @Override
            public void setItems(List<ExUserInfo> items) {
                if(items.size() != 0 ){
                    lastSticky = items.get(0).sortLetter;
                    items.add(0, getExUserInfo());
                }

                for (int i = 0; i < items.size(); i++) {
                    ExUserInfo userInfo = items.get(i);
                    if (!lastSticky.equals(userInfo.sortLetter)) {
                        lastSticky = userInfo.sortLetter;
                        items.add(i, getExUserInfo());
                    }
                }
                super.setItems(items);
            }

            @NonNull
            private ExUserInfo getExUserInfo() {
                ExUserInfo exUserInfo = new ExUserInfo();
                exUserInfo.sortLetter = lastSticky;
                exUserInfo.isSticky = true;
                return exUserInfo;
            }

            @Override
            public int getItemViewType(int position) {
                return getItems().get(position).isSticky ? STICKY : ITEM;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == ITEM)
                    return new ItemViewHo(parent);

                return new StickyViewHo(parent);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onBindView(@NonNull RecyclerView.ViewHolder holder, ExUserInfo data, int position) {
                if (getItemViewType(position) == ITEM) {
                    ItemViewHo itemViewHo = (ItemViewHo) holder;
                    FriendInfo friendInfo = data.userInfo.getFriendInfo();
                    itemViewHo.view.avatar.load(friendInfo.getFaceURL());
                    itemViewHo.view.nickName.setText(friendInfo.getNickname());
                    itemViewHo.view.select.setChecked(data.isSelect);
                    itemViewHo.view.item.setOnClickListener(v -> {
                        data.isSelect = !data.isSelect;
                        if(data.isSelect){
                            if(getSelectNum()>= 2000){
                                return;
                            }
                            if(adapterSelected.getItems().size() == 0){
                                view.recyclerViewSelectedLayout.setVisibility(View.VISIBLE);
                            }

                            adapterSelected.getItems().add(data);
                            adapterSelected.notifyItemInserted(adapterSelected.getItems().size() - 1);
                        }
                        else{
                            int idx = adapterSelected.getItems().indexOf(data);
                            adapterSelected.getItems().remove(data);
                            adapterSelected.notifyItemRemoved(idx);
                            if(adapterSelected.getItems().size() == 0){
                                view.recyclerViewSelectedLayout.setVisibility(View.GONE);
                            }
                        }
                        notifyItemChanged(position);
                        int num = getSelectNum();
                        view.selectNum.setText("已选择：" + num + "人");
                        view.submit.setText(getString(io.openim.android.ouicore.R.string.confirm)+"(" + num + "/1999）");
                        view.submit.setEnabled(num > 0);
                        if(num>0){
                            view.submit.setBackgroundResource(R.drawable.background_gradient_confirm_btn);
                            view.submit.setTextColor(getColor(io.openim.android.ouicore.R.color.white));
                        }
                        else {
                            view.submit.setBackgroundResource(R.drawable.background_gradient_confirm_disabled_btn);
                            view.submit.setTextColor(getColor(io.openim.android.ouicore.R.color.black));
                        }
                    });
                } else {
                    StickyViewHo stickyViewHo = (StickyViewHo) holder;
                    stickyViewHo.view.title.setText(data.sortLetter);
                }

            }

        };
        view.recyclerView.setAdapter(adapter);

        adapterSelected = new RecyclerViewAdapter<ExUserInfo, SelectedViewHolder>(SelectedViewHolder.class) {
            @Override
            public void onBindView(@NonNull SelectedViewHolder holder, ExUserInfo data, int position) {
                holder.view.avatar.load(data.userInfo.getFaceURL());
            }
        };
        view.recyclerViewSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<ExUserInfo> list = new LinkedList<>();
        ExUserInfo exUserInfo = new ExUserInfo();
        exUserInfo.userInfo = new UserInfo();
        exUserInfo.userInfo.setFaceURL("http://oss.bytechat-test.com/im-oss/1659779850443804811-1874068156324778273ic_avatar_1.jpg");

        adapterSelected.setItems(list);

        view.recyclerViewSelected.setAdapter(adapterSelected);

        view.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                List<ExUserInfo> items = allItems;
                List<ExUserInfo> selectedList = new LinkedList<>();
                try {
                    for (ExUserInfo item : items) {
                        if (item.isSticky == true) continue;

                        if (item.userInfo.getFriendInfo().getNickname().toLowerCase().contains(editable.toString().toLowerCase())) {
                            selectedList.add(item);
                        }
                    }
                    adapter.setItems(selectedList);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private int getSelectNum() {
        List<FriendInfo> friendInfos = new ArrayList<>();
        int num = 0;
        for (ExUserInfo item : adapter.getItems()) {
            if (item.isSelect) {
                num++;
                friendInfos.add(item.userInfo.getFriendInfo());
            }
        }
        vm.selectedFriendInfo.setValue(friendInfos);
        return num;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void listener() {
        vm.letters.observe(this, v -> {
            if (null == v || v.isEmpty()) return;
            StringBuilder letters = new StringBuilder();
            for (String s : v) {
                letters.append(s.toLowerCase(Locale.ROOT));
            }
            char[] chars = letters.toString().toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);
            sorted = Arrays.asList(sorted.split(""))
                .stream()
                .distinct()
                .collect(Collectors.joining());

            view.sortView.setLetters(sorted);

        });

        vm.groupInviteResult.observe(this , data -> {
            if (data == false)
                return;
            toast(getString(io.openim.android.ouicore.R.string.users_added_successfully));
            System.out.println("group invite successfully ");
            finish();
        });
        vm.exUserInfo.observe(this, v -> {
            if (null == v || v.isEmpty()) return;
            allItems = v ;
            if(isAddingMembers) {
                List<ExUserInfo> list = new LinkedList<>();
                for (ExUserInfo user2 : allItems) {
                    boolean ok = true;
                    for (String user1 : groupMembers) {
                        System.out.println("user1 : "+user1 + " user2 :" + user2.userInfo.getUserID());
                        if (user1.equals(user2.userInfo.getUserID())) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        System.out.println("user : " + user2);
                        list.add(user2);
                    }
                }
                allItems = list;
            }

            adapter.setItems(allItems);
        });

        view.sortView.setOnLetterChangedListener((letter, position) -> {
            for (int i = 0; i < adapter.getItems().size(); i++) {
                ExUserInfo exUserInfo = adapter.getItems().get(i);
                if (!exUserInfo.isSticky)
                    continue;
                if (exUserInfo.sortLetter.equalsIgnoreCase(letter)) {
                    View viewByPosition = view.recyclerView.getLayoutManager().findViewByPosition(i);
                    if (viewByPosition != null) {
                        view.scrollView.smoothScrollTo(0, viewByPosition.getTop());
                    }
                    return;
                }
            }
        });
        view.submit.setOnClickListener(v -> {
            if(isAddingMembers){
                List<String> list = new LinkedList<>();
                for(ExUserInfo user : adapterSelected.getItems()){
                    list.add(user.userInfo.getFriendInfo().getUserID());
                }
                vm.groupId = groupId ;
                vm.inviteUsers(list);
            }else{
                createLauncher.launch(new Intent(this,CreateGroupActivity.class));
            }
        });
    }
    private final ActivityResultLauncher<Intent> createLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                finish();
            }
        });

    public static class ItemViewHo extends RecyclerView.ViewHolder {
        ItemPsrsonSelectBinding view;

        public ItemViewHo(@NonNull View itemView) {
            super(ItemPsrsonSelectBinding.inflate(LayoutInflater.from(itemView.getContext()), (ViewGroup) itemView, false).getRoot());
            view = ItemPsrsonSelectBinding.bind(this.itemView);
        }
    }

    public static class StickyViewHo extends RecyclerView.ViewHolder {
        ItemPsrsonStickyBinding view;

        public StickyViewHo(@NonNull View itemView) {
            super(ItemPsrsonStickyBinding.inflate(LayoutInflater.from(itemView.getContext()), (ViewGroup) itemView, false).getRoot());
            view = ItemPsrsonStickyBinding.bind(this.itemView);
        }
    }

    public static class SelectedViewHolder extends RecyclerView.ViewHolder {
        AddGroupItemBinding view;

        public SelectedViewHolder(@NonNull View itemView) {
            super(AddGroupItemBinding.inflate(LayoutInflater.from(itemView.getContext()), (ViewGroup) itemView, false).getRoot());
            view = AddGroupItemBinding.bind(this.itemView);
        }
    }
}
