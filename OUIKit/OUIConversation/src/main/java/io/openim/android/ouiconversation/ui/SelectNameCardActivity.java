package io.openim.android.ouiconversation.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.openim.android.ouiconversation.databinding.ActivitySelectNameCardBinding;
import io.openim.android.ouiconversation.databinding.FragmentMyFriendBinding;
import io.openim.android.ouiconversation.databinding.ItemPsrsonForwardMessageBinding;
import io.openim.android.ouiconversation.databinding.ItemPsrsonStickyBinding;
import io.openim.android.ouiconversation.databinding.ItemSelectNameCardBinding;
import io.openim.android.ouiconversation.entity.ExUserInfo;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.vm.ForwardMessageVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.BusinessCard;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.Message;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class SelectNameCardActivity extends BaseActivity<ForwardMessageVM, ActivitySelectNameCardBinding> {
    private RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder> adapter;
    List<ExUserInfo> allItems ;
    List<String> groupMembers ;
    String groupId ;
    boolean isAddingMembers = false ;
    public static ChatVM chatVM ;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindVM(ForwardMessageVM.class);
        bindViewDataBinding(ActivitySelectNameCardBinding.inflate(getLayoutInflater()));


        initView();
        vm.getAllFriend();
        listener();

    }


    private void initView() {

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

                        Message msg = vm.createCardMessage(new BusinessCard(friendInfo.getFaceURL() , friendInfo.getNickname(),friendInfo.getUserID()));
                        chatVM.sendMsg(msg);

                        finish();
                    });
                } else {
                    StickyViewHo stickyViewHo = (StickyViewHo) holder;
                    stickyViewHo.view.title.setText(data.sortLetter);
                }
            }
        };
        view.recyclerView.setAdapter(adapter);

//        view.searchView.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void afterTextChanged(Editable editable) {
//                List<ExUserInfo> items = allItems;
//                List<ExUserInfo> selectedList = new LinkedList<>();
//
//                for (ExUserInfo item : items) {
//                    if (item.isSticky == true) continue;
//
//                    if (item.userInfo.getFriendInfo().getNickname().toLowerCase().contains(editable.toString().toLowerCase())) {
//                        selectedList.add(item);
//                    }
//                }
//                adapter.setItems(selectedList);
//                adapter.notifyDataSetChanged();
//            }
//        });

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
                letters.append(s);
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


        vm.exUserInfo.observe(this, v -> {
            if (null == v || v.isEmpty()) return;
            allItems = v ;
            if(isAddingMembers) {
                List<ExUserInfo> list = new LinkedList<>();
                for (ExUserInfo user2 : allItems) {
                    boolean ok = true;
                    for (String user1 : groupMembers) {
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
    }

    public static class ItemViewHo extends RecyclerView.ViewHolder {
        ItemSelectNameCardBinding view;

        public ItemViewHo(@NonNull View itemView) {
            super(ItemSelectNameCardBinding.inflate(LayoutInflater.from(itemView.getContext()), (ViewGroup) itemView, false).getRoot());
            view = ItemSelectNameCardBinding.bind(this.itemView);
        }
    }

    public static class StickyViewHo extends RecyclerView.ViewHolder {
        ItemPsrsonStickyBinding view;

        public StickyViewHo(@NonNull View itemView) {
            super(ItemPsrsonStickyBinding.inflate(LayoutInflater.from(itemView.getContext()), (ViewGroup) itemView, false).getRoot());
            view = ItemPsrsonStickyBinding.bind(this.itemView);
        }
    }
}
