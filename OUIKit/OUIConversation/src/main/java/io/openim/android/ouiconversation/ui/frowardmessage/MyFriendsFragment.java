package io.openim.android.ouiconversation.ui.frowardmessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import br.com.simplepass.loadingbutton.customViews.OnAnimationEndListener;
import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.FragmentMyFriendBinding;
import io.openim.android.ouiconversation.databinding.ItemPsrsonForwardMessageBinding;
import io.openim.android.ouiconversation.databinding.ItemPsrsonStickyBinding;
import io.openim.android.ouiconversation.entity.ExUserInfo;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.vm.ForwardMessageVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.UserInfo;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MyFriendsFragment extends BaseFragment<ForwardMessageVM> {
    FragmentMyFriendBinding binding ;
    private RecyclerViewAdapter<ExUserInfo, RecyclerView.ViewHolder> adapter;
    List<ExUserInfo> allItems ;
    List<String> groupMembers ;
    String groupId ;
    boolean isAddingMembers = false ;
    Message msg ;
    ChatVM chatVM ;

    public MyFriendsFragment(Message msg, ChatVM chatVM) {
        this.msg = msg;
        this.chatVM = chatVM;
        System.out.println("chatVM : " + chatVM);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(ForwardMessageVM.class);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyFriendBinding.inflate(inflater, container, false);
        initView();

        vm.getAllFriend();
        listener();
        return binding.getRoot();
    }

    private void initView() {

        binding.scrollView.fullScroll(View.FOCUS_DOWN);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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
                    itemViewHo.view.send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemViewHo.view.send.startAnimation();

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    itemViewHo.view.send.revertAnimation(new Function0<Unit>() {
                                        @Override
                                        public Unit invoke() {
                                            itemViewHo.view.send.setText("Sent");
                                            itemViewHo.view.send.setEnabled(false);
                                            itemViewHo.view.send.setBackgroundColor(Color.GRAY);
                                            return null;
                                        }
                                    });

                                }
                            }, 1000);

//                            timer.schedule(timerTask,1000);
                            vm.groupID = null ;
                            vm.otherSideID = data.userInfo.getUserID();
                            System.out.println(" merger message title is : " + msg.getMergeElem());
                            if(msg.getMergeElem().getTitle() != null)
                                vm.sendMsg(msg);
                            else
                                vm.sendMsg(vm.createForwardMessage(msg));
                        }
                    });
                    itemViewHo.view.item.setOnClickListener(v -> {
                        data.isSelect = !data.isSelect;
                        notifyItemChanged(position);
                        int num = getSelectNum();
                    });
                } else {
                    StickyViewHo stickyViewHo = (StickyViewHo) holder;
                    stickyViewHo.view.title.setText(data.sortLetter);
                }
            }
        };
        binding.recyclerView.setAdapter(adapter);

//        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
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
        vm.sendMessageResult.observe(this,data->{
            if(data == null) return;
            if((Objects.equals(chatVM.groupID, data.getGroupID()) && chatVM.groupID != null)||
                (Objects.equals(chatVM.otherSideID, data.getRecvID())&& chatVM.otherSideID != null ) ){
                chatVM.isForwardedToSameConversation.setValue(data);
            }
        });
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

            binding.sortView.setLetters(sorted);

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

        vm.searchForward.observe(this, data->{
            if (null == allItems || allItems.isEmpty()) return;
            List<ExUserInfo> items = allItems;
            List<ExUserInfo> selectedList = new LinkedList<>();

            for (ExUserInfo item : items) {
                if (item.isSticky == true) continue;

                if (item.userInfo.getFriendInfo().getNickname().toLowerCase().contains(data.toString().toLowerCase())) {
                    selectedList.add(item);
                }
            }
            adapter.setItems(selectedList);
            adapter.notifyDataSetChanged();
        });

        binding.sortView.setOnLetterChangedListener((letter, position) -> {
            for (int i = 0; i < adapter.getItems().size(); i++) {
                ExUserInfo exUserInfo = adapter.getItems().get(i);
                if (!exUserInfo.isSticky)
                    continue;
                if (exUserInfo.sortLetter.equalsIgnoreCase(letter)) {
                    View viewByPosition = binding.recyclerView.getLayoutManager().findViewByPosition(i);
                    if (viewByPosition != null) {
                        binding.scrollView.smoothScrollTo(0, viewByPosition.getTop());
                    }
                    return;
                }
            }
        });
    }

    public static class ItemViewHo extends RecyclerView.ViewHolder {
        ItemPsrsonForwardMessageBinding view;

        public ItemViewHo(@NonNull View itemView) {
            super(ItemPsrsonForwardMessageBinding.inflate(LayoutInflater.from(itemView.getContext()), (ViewGroup) itemView, false).getRoot());
            view = ItemPsrsonForwardMessageBinding.bind(this.itemView);
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
