package io.bytechat.demo.oldrelease.ui.main;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static io.openim.android.ouiconversation.adapter.MessageAdapter.OWN_ID;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.BURN_AFTER_READING_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.FRIEND_ADDED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_CREATED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_INFO_SET_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_MUTED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.GROUP_OWNER_TRANSFERRED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_ENTER_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_INVITED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_KICKED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_QUIT_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MENTION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.OA_NOTIFICATION;
import static io.openim.android.ouicore.entity.LoginCertificate.signOut;
import static io.openim.android.ouicore.utils.Common.authViewModel;
import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.FREQUENT_CONTACTS_LIST;
import static io.openim.android.ouicore.utils.Constant.GROUP_ID;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import cn.jpush.android.api.JPushInterface;
import io.bytechat.demo.databinding.FragmentContactListBinding;
import io.bytechat.demo.databinding.LayoutAddActionBinding;
import io.bytechat.demo.oldrelease.ui.search.GlobalSearchActivity;
import io.bytechat.demo.ui.SplashActivity;
import io.bytechat.demo.ui.auth.AuthActivity;
import io.bytechat.demo.ui.profile.MyInformationActivity;
import io.bytechat.demo.ui.widget.AddFriendDialog;
import io.bytechat.demo.ui.widget.AddGroupDialog;
import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.LayoutContactItemBinding;
import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.vm.ContactListVM;
import io.openim.android.ouicore.base.BaseApp;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.entity.MsgConversation;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.Constant;
import io.openim.android.ouicore.utils.MaterialRefreshView;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.TimeUtil;
import io.openim.android.sdk.models.AtElem;
import io.openim.android.sdk.models.FriendInfo;

@Route(path = Routes.Conversation.CONTACT_LIST)
public class ContactListFragment extends BaseFragment<ContactListVM> implements ContactListVM.ViewAction {

    private FragmentContactListBinding view;
    private CustomAdapter adapter;
    Activity activity;
    int counter = 0;
    AddGroupDialog addGroupDialog;
    AddFriendDialog addFriendDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ContactListVM.class);
        super.onCreate(savedInstanceState);
        addGroupDialog = new AddGroupDialog(getActivity());
        addFriendDialog = new AddFriendDialog(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        authViewModel.counterContact.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer > 0) {
                    BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_address_book);
                    badge.setNumber(integer);
                    badge.setVisible(true);
                } else {
                    BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_address_book);
                    badge.setVisible(false);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = FragmentContactListBinding.inflate(getLayoutInflater());
        view.setLifecycleOwner(this);
        view.setVm(vm);
        init();
        return view.getRoot();
    }

    @SuppressLint("NewApi")
    private void init() {
        view.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SwipeMenuCreator mSwipeMenuCreator = (leftMenu, rightMenu, position) -> {
            SwipeMenuItem top;
            SwipeMenuItem delete;
            delete = new SwipeMenuItem(getContext());
            delete.setText(io.openim.android.ouicore.R.string.delete);
            delete.setHeight(MATCH_PARENT);
            delete.setWidth(Common.dp2px(73));
            delete.setTextSize(16);
            delete.setTextColor(getContext().getColor(android.R.color.white));
            delete.setBackground(getActivity().getDrawable(R.drawable.remove_side_item_recyclerview_background));

            top = new SwipeMenuItem(getContext());
            top.setText(io.openim.android.ouicore.R.string.pin);
            top.setHeight(MATCH_PARENT);
            top.setWidth(Common.dp2px(73));
            top.setTextSize(16);
            top.setTextColor(getContext().getColor(android.R.color.white));
            top.setBackground(getActivity().getDrawable(R.drawable.pin_side_item_recyclerview_background));
            if (vm.conversations.getValue().get(position).conversationInfo.isPinned())
                top.setText(io.openim.android.ouicore.R.string.un_pin);

            //右侧添加菜单
            rightMenu.addMenuItem(top);
            rightMenu.addMenuItem(delete);
        };
        view.recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        view.recyclerView.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
            int menuPosition = menuBridge.getPosition();
            Log.d("menuPosition", "init: " + adapterPosition + " " + menuBridge.getPosition());
            // 0 => mean it's a pin button , 1 => mean it's remove button
            if (menuPosition == 0) {
                vm.pinConversation(vm.conversations.getValue().get(adapterPosition));
            } else {
                if (vm.conversations.getValue().size() > 0) {
                    vm.deleteConversation(vm.conversations.getValue().get(adapterPosition));
                }
            }
            menuBridge.closeMenu();

        });
        view.recyclerView.setOnItemClickListener((view, position) -> {
            MsgConversation msgConversation = vm.conversations.getValue().get(position);
            Boolean isSystemNotification = false;
//            if(msgConversation.lastMsg != null && msgConversation.lastMsg.getContentType() == io.openim.android.ouiconversation.utils.Constant.MsgType.OA_NOTIFICATION) {
//                isSystemNotification = true;
//            }
            Log.d("ContactList", "init:msgConversation.conversationInfo.isNotInGroup() -> " + msgConversation.conversationInfo.isNotInGroup());
            Log.d("Checking sys msg", "init: " + msgConversation.conversationInfo.getConversationType() + " " +
                msgConversation.conversationInfo.getShowName());
            isSystemNotification = msgConversation.conversationInfo.getConversationType() == 4;

            Intent intent = new Intent(getContext(), ChatActivity.class)
                .putExtra(K_NAME, msgConversation.conversationInfo.getShowName()).putExtra("isPined", msgConversation.conversationInfo.isPinned())
                .putExtra("isSystemNotification", isSystemNotification).putExtra("isBurn", msgConversation.conversationInfo.isPrivateChat())
                .putExtra("isNotInGroup", msgConversation.conversationInfo.isNotInGroup());//是否还在群里 - Are you still in group

            if (msgConversation.conversationInfo.getConversationType() == 1 || msgConversation.conversationInfo.getConversationType() == 4)
                intent.putExtra(ID, msgConversation.conversationInfo.getUserID());
            if (msgConversation.conversationInfo.getConversationType() == 2)
                intent.putExtra(GROUP_ID, msgConversation.conversationInfo.getGroupID());

            intent.putExtra(CONVERSATION_ID, msgConversation.conversationInfo.getConversationID());

            startActivity(intent);
            ContactListVM.isActivityActive.setValue(false);
        });
        adapter = new CustomAdapter(getContext());
        view.recyclerView.setAdapter(adapter);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(100, 100);
        view.recyclerViewLayout.setRefreshView(new MaterialRefreshView(this.getContext()), layoutParams);


//        view.recyclerView.addItemDecoration(new DefaultItemDecoration(getActivity().getColor(android.R.color.transparent), 1, 36));
        FREQUENT_CONTACTS_LIST.clear();
        vm.conversations.observe(requireActivity(), v -> {
//            if (null == v || v.size() == 0) return;
            int cnt = 0;
            int counter = 0;
            for (int i = 0; i < v.size(); i++) {
                counter = counter + v.get(i).conversationInfo.getUnreadCount();
            }
            for (int i = 0; i < v.size(); i++) {
                if (v.get(i).conversationInfo.getConversationType() == 1) {
                    FriendInfo friendInfo = new FriendInfo(v.get(i).conversationInfo.getUserID(), v.get(i).conversationInfo.getShowName(), v.get(i).conversationInfo.getFaceURL(),
                        0, "", "", "", "", "", 0, 0, "");
                    FREQUENT_CONTACTS_LIST.add(friendInfo);
//                    cnt++;
//                    if(cnt == 2)
//                        break;
                }
            }

            System.out.println("Unread Msgs Count: " + counter);
            vm.unreadCounter.postValue(counter);

            if (counter > 99) {
                BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_home);
                badge.setMaxCharacterCount(3);
                badge.setNumber(counter);
                badge.setVisible(true);
            } else {
                if (counter == 0) {
                    BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_home);
                    badge.setVisible(false);
                } else {
                    BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_home);
                    badge.setNumber(counter);
                    badge.setVisible(true);
                }
            }

            adapter.setConversationInfos(v);
            if (!view.recyclerView.isComputingLayout()) {
                // add your code here
//                synchronized (adapter){
                adapter.notifyDataSetChanged();
//                }
            }
            view.recyclerViewLayout.setRefreshing(false);
        });

        // Badge counter for New Friends
        vm.friendApply.observe(requireActivity(), data -> {
            int counter = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getHandleResult() == 0) {
                    counter++;
                }
            }
            if (counter > 0) {
                authViewModel.counterContact.setValue(counter);
            }
        });

//        // Badge counter for New Groups
//        vm.groupApply.observe(requireActivity(), data-> {
//            int counter = 0;
//            for (int i = 0; i < data.size(); i++) {
//                if(data.get(i).getHandleResult() == 0){
//                    counter++;
//                }
//            }
//            if(counter != 0){
//                BadgeDrawable badge = MainActivity.navView.getOrCreateBadge(io.bytechat.demo.R.id.navigation_address_book);
//                badge.setNumber(counter);
//            }
//
//        });

        vm.nickname.observe(this.getActivity(), data -> {
            if (data.length() > 12)
                data = data.substring(0, 12) + "...";
            view.nickname.setText(data);
        });

        vm.visibility.observe(this.getActivity(), v -> view.isOnline.setVisibility(v));
        vm.avatar.observe(this.getActivity(), data -> {
            Glide.with(this)
                .load(data)
                .centerCrop()
                .into(view.avatar);
        });
        view.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.navView.setSelectedItemId(io.bytechat.demo.R.id.navigation_me);
                Intent intent = new Intent(getContext(), MyInformationActivity.class);
                intent.putExtra("profilePhotoFragment", "profilePhotoFragment");
                startActivity(intent);
            }
        });
        vm.pinConversation.observe(this.getActivity(), data -> {
            if (data != null) {
                System.out.println("daataa " + data);
                adapter.notifyDataSetChanged();
            }
        });
        vm.deleteConversation.observe(this.getActivity(), data -> {
            if (data != null) {
                System.out.println("deleteConversation " + data);
                adapter.notifyDataSetChanged();
            }
        });

        view.recyclerViewLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vm.undataConversation();
            }
        });

        initContactPageListeners();
        showMenu();

    }

    private void initContactPageListeners() {
        authViewModel.friendDotNum.observe(requireActivity(), data -> {
            if (data > 0) {

                authViewModel.counterContact.setValue(data);
            }
        });

        authViewModel.groupDotNum.observe(requireActivity(), data -> {
            if (data > 0) {

                authViewModel.counterContact.setValue(data);
            }
        });
        vm.friendApply.observe(requireActivity(), data -> {
            System.out.println("vm.groupApply.observe");
            int counter = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getHandleResult() == 0) {
                    counter++;
                }
            }
            authViewModel.counterContact.setValue(counter + authViewModel.counterContact.getValue());
            authViewModel.friendDotNum.setValue(counter);
        });

        // Badge counter for New Groups
        vm.groupApply.observe(requireActivity(), data -> {
            int counter = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getHandleResult() == 0) {
                    counter++;
                }
            }
            authViewModel.counterContact.setValue(counter + authViewModel.counterContact.getValue());
            authViewModel.groupDotNum.setValue(counter);

        });
    }

    @Override
    public void onResume() {
        vm.getProfileData();
        super.onResume();
        if (!Constant.SCAN_USER_ID.isEmpty()) {
            addFriendDialog.showDialog(Constant.SCAN_USER_ID);
            Constant.SCAN_USER_ID = "";
        }

        if (!Constant.SCAN_GROUP_ID.isEmpty()) {
            addGroupDialog.showDialog(Constant.SCAN_GROUP_ID);
            Constant.SCAN_GROUP_ID = "";
        }
    }

    public void showMenu() {
        view.cvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GlobalSearchActivity.class);
                startActivity(intent);
            }
        });
        view.addFriend.setOnClickListener(v -> {
            //初始化一个PopupWindow，width和height都是WRAP_CONTENT
            PopupWindow popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutAddActionBinding view = LayoutAddActionBinding.inflate(getLayoutInflater());
            view.scan.setOnClickListener(c -> {
                popupWindow.dismiss();
                Dexter.withContext(ContactListFragment.this.getActivity())
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            startActivity(new Intent(ContactListFragment.this.getActivity(), ScannerActivity.class));

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            Toast.makeText(ContactListFragment.this.getActivity(), "Can't open Scaner Camera", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        }
                    })
                    .check();
            });
            view.addFriend.setOnClickListener(c -> {
                popupWindow.dismiss();
                startActivity(new Intent(this.getActivity(), AddFriendOptionsActivity.class));
            });
            view.addGroup.setOnClickListener(c -> {
                popupWindow.dismiss();
                startActivity(new Intent(this.getActivity(), JoinGroupOptionActivity.class));
            });
            view.createGroup.setOnClickListener(c -> {
                popupWindow.dismiss();
                ARouter.getInstance().build(Routes.Group.CREATE_GROUP).navigation();
            });
            //设置PopupWindow的视图内容
            popupWindow.setContentView(view.getRoot());
            //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.setOutsideTouchable(true);

            //设置PopupWindow消失监听
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });
            //PopupWindow在targetView下方弹出
            popupWindow.showAsDropDown(v);
        });
    }

    @Override
    public void onErr(String msg, int code) {
        if (getActivity() == null) return;
        try {
            if (code == 702) {
                signOut(ContactListFragment.this.getContext());
                JPushInterface.deleteAlias(getActivity().getApplicationContext(), 123456);
                ContactListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), io.openim.android.ouicore.R.string.session_expired, Toast.LENGTH_SHORT).show();
                        ContactListFragment.this.getActivity().finish();
                        startActivity(new Intent(ContactListFragment.this.getContext(), AuthActivity.class));
                    }
                });


            } else {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception:::: " + msg);
        }
    }

    static class CustomAdapter extends SwipeRecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<MsgConversation> conversationInfos;
        private Context context;

        public CustomAdapter(Context context) {
            this.context = context;
        }

        public void setConversationInfos(List<MsgConversation> conversationInfos) {
            this.conversationInfos = conversationInfos;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final LayoutContactItemBinding viewBinding;

            public ViewHolder(LayoutContactItemBinding viewBinding) {
                super(viewBinding.getRoot());
                this.viewBinding = viewBinding;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolder(LayoutContactItemBinding.inflate(LayoutInflater.from(viewGroup.getContext())));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            MsgConversation msgConversation = conversationInfos.get(position);
            viewHolder.viewBinding.avatar.load(msgConversation.conversationInfo.getFaceURL(), msgConversation.conversationInfo.getConversationType() != Constant.SessionType.SINGLE_CHAT);
            viewHolder.viewBinding.nickName.setText(msgConversation.conversationInfo.getShowName());
            if (msgConversation.lastMsg == null)
                viewHolder.viewBinding.lastMsg.setText("");
            else {
                String lastMsg = "";
                JSONObject jsonDetailObj;
                String nickName = "";

                switch (msgConversation.lastMsg.getContentType()) {
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.REPLY:
                        viewHolder.viewBinding.lastMsg.setText(msgConversation.lastMsg.getQuoteElem().getText());
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.PICTURE:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you_sent_an_image));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.someone_sent_an_image));
                        }
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.CARD:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you) + " " +
                                context.getString(io.openim.android.ouicore.R.string.sent_a_card_message));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.sent_a_card_message));
                        }
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.VIDEO:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you_sent_a_video));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.someone_sent_a_video));
                        }
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.FILE:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you_sent_a_file));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.someone_sent_a_file));
                        }
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.MERGE:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you_forwarded_a_message));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.someone_forward_message));
                        }
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.REVOKE:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you_recall_a_message));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.someone_recalled_a_message));
                        }
                        break;
                    case FRIEND_ADDED_NOTIFICATION:
                        Log.d("ContactListFragment", "onBindViewHolder: " + msgConversation.lastMsg);
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.added_as_friend));
                        } else {
                            //viewHolder.viewBinding.lastMsg.setText(msgConversation.lastMsg.getSenderNickname() + " added as friend");
                            nickName = trimString(msgConversation.conversationInfo.getShowName());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.added_as_friend));
                        }
                        break;
                    case OA_NOTIFICATION:
                        viewHolder.viewBinding.avatar.setImageDrawable(context.getDrawable(R.mipmap.bell_white));
                        viewHolder.viewBinding.nickName.setText(context.getString(io.openim.android.ouicore.R.string.system_notification));
                        viewHolder.viewBinding.lastMsg.setText(trimString(msgConversation.lastMsg.getContent()));
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.VOICE:
                        if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(msgConversation.lastMsg.getSendID())) {
                            viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.you_sent_a_voice_message));
                        } else {
                            nickName = trimString(msgConversation.lastMsg.getSenderNickname());
                            viewHolder.viewBinding.lastMsg.setText(nickName + context.getString(io.openim.android.ouicore.R.string.someone_sent_a_voice_message));
                        }
                        break;
                    case io.openim.android.ouiconversation.utils.Constant.MsgType.TXT:
                        viewHolder.viewBinding.lastMsg.setText(msgConversation.lastMsg.getContent());
                        break;

                    // ---------------- GROUP NOTIFICATIONS START ---------------------

                    case MEMBER_KICKED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            JSONArray jsonArray = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail")).getJSONArray("kickedUserList");

                            if (jsonArray.length() > 1) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.some_members_were_moved);
                            } else {
                                if (jsonArray.getJSONObject(0).getString("userID").equalsIgnoreCase(OWN_ID))
                                    lastMsg = context.getString(io.openim.android.ouicore.R.string.you_were_moved);
                                else {
                                    lastMsg = trimString(jsonArray.getJSONObject(0).getString("nickname"));
                                    lastMsg += this.context.getString(io.openim.android.ouicore.R.string.was_moved);
                                    ;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_OWNER_TRANSFERRED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("newGroupOwner").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you_are_new_owner);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("newGroupOwner").getString("nickname"));
                                lastMsg += context.getString(io.openim.android.ouicore.R.string.is_new_owner);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_INFO_SET_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("opUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you_have_changed_group_name_to);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("opUser").getString("nickname"));
                                lastMsg += context.getString(io.openim.android.ouicore.R.string.have_changed_group_name_to);
                                // lastMsg = trimString(jsonDetailObj.getJSONObject("opUser").getString("nickname"));
//                                lastMsg = "Owner have changed group name to ";
                            }
                            String grpName = trimString(jsonDetailObj.getJSONObject("group").getString("groupName"));
                            lastMsg += grpName;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_NOTIFICATION:
                        lastMsg = "";
                        try {//====================
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            if (jsonDetailObj.getJSONObject("group").has("notification")) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.group_announcement_updated);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case MEMBER_INVITED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            JSONArray jsonArray = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail")).getJSONArray("invitedUserList");

                            if (jsonArray.length() > 1) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.some_members_added_to_group);
                            } else {
                                if (jsonArray.getJSONObject(0).getString("userID").equalsIgnoreCase(OWN_ID))
                                    lastMsg = context.getString(io.openim.android.ouicore.R.string.you_added_to_group);
                                else {
                                    lastMsg = trimString(jsonArray.getJSONObject(0).getString("nickname"));
                                    lastMsg += context.getString(io.openim.android.ouicore.R.string.added_to_group);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case MEMBER_ENTER_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("entrantUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("entrantUser").getString("nickname"));
                            }
                            lastMsg += context.getString(io.openim.android.ouicore.R.string.entered_the_group);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_CREATED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("opUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("opUser").getString("nickname"));
                            }
                            lastMsg += context.getString(io.openim.android.ouicore.R.string.created_this_group);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case MEMBER_QUIT_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("quitUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you_quit_the_group);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("quitUser").getString("nickname"));
                                lastMsg += context.getString(io.openim.android.ouicore.R.string.quit_the_group);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case BURN_AFTER_READING_NOTIFICATION:
                        lastMsg = "";
                        try {
                            JSONObject jsonObject1 = new JSONObject(msgConversation.lastMsg.getNotificationElem().getDetail());
                            if (jsonObject1.has("isPrivate") && jsonObject1.getString("isPrivate").equalsIgnoreCase("true")) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.burn_after_reading_on);
                            } else {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.burn_after_reading_off);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_MUTED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("opUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you);
                            } else {
                                //lastMsg = trimString(jsonDetailObj.getJSONObject("opUser").getString("nickname"));
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.owner);
                            }
                            lastMsg += context.getString(io.openim.android.ouicore.R.string.turn_on_all_bans);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_CANCEL_MUTED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("opUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you);
                            } else {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.owner);
                            }
                            lastMsg += context.getString(io.openim.android.ouicore.R.string.turned_off_all_bans);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_MEMBER_MUTED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("mutedUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you_were_banned);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("mutedUser").getString("nickname"));
                                lastMsg += context.getString(io.openim.android.ouicore.R.string.was_banned);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;
                    case GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION:
                        lastMsg = "";
                        try {
                            jsonDetailObj = new JSONObject(new JSONObject(msgConversation.lastMsg.getContent()).getString("jsonDetail"));
                            String uid = jsonDetailObj.getJSONObject("mutedUser").getString("userID");
                            if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(uid)) {
                                lastMsg = context.getString(io.openim.android.ouicore.R.string.you_were_unbanned);
                            } else {
                                lastMsg = trimString(jsonDetailObj.getJSONObject("mutedUser").getString("nickname"));
                                lastMsg += context.getString(io.openim.android.ouicore.R.string.was_unbanned);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;

                    // ---------------- GROUP NOTIFICATIONS END ---------------------
                    case MENTION:
                        lastMsg = "";
                        if (msgConversation.lastMsg != null && msgConversation.lastMsg.getAtElem() != null && msgConversation.lastMsg.getAtElem().getAtUsersInfo() != null
                            && msgConversation.lastMsg.getAtElem().getAtUsersInfo().size() > 0) {
                            lastMsg = msgConversation.lastMsg.getAtElem().getText();
                            AtElem atElem = msgConversation.lastMsg.getAtElem();

                            int startIndex=0, endIndex=0;

                            for (int i = 0; i < atElem.getAtUsersInfo().size(); i++) {
                                if (Objects.requireNonNull(LoginCertificate.getCache(BaseApp.instance())).userID.equals(atElem.getAtUsersInfo().get(i).getAtUserID())) {
                                    lastMsg = lastMsg.replaceFirst("@" + atElem.getAtUsersInfo().get(i).getAtUserID(),
                                        "[" + context.getString(io.openim.android.ouicore.R.string.you_are_mentioned) + "]" + msgConversation.lastMsg.getSenderNickname() + ": ");
                                    startIndex = lastMsg.indexOf("[");
                                    endIndex = lastMsg.indexOf("]");
                                }else {
                                    lastMsg = lastMsg.replaceFirst(atElem.getAtUsersInfo().get(i).getAtUserID(), atElem.getAtUsersInfo().get(i).getGroupNickname());
                                    startIndex=lastMsg.indexOf("@");
                                    endIndex=atElem.getAtUsersInfo().get(i).getGroupNickname().length();
                                }
                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                SpannableString redSpannable= new SpannableString(lastMsg);
                                redSpannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(io.openim.android.ouicore.R.color.purple_500)),
                                    startIndex, endIndex+1 , 0);
                                builder.append(redSpannable);
                                viewHolder.viewBinding.lastMsg.setText(builder, TextView.BufferType.SPANNABLE);
                            }
                        }
                       // viewHolder.viewBinding.lastMsg.setText(lastMsg);
                        break;

                    default:
                        viewHolder.viewBinding.lastMsg.setText(context.getString(io.openim.android.ouicore.R.string.message_dose_not_supported_yet));
                }
            }
            if (msgConversation.conversationInfo.isPinned()) {
                viewHolder.viewBinding.pinIcon.setImageResource(R.mipmap.icon_pin);
                viewHolder.viewBinding.pinIcon.setVisibility(View.VISIBLE);
                viewHolder.viewBinding.mainLayout.setBackgroundColor(Color.parseColor("#CAEFEC"));
            } else {
                viewHolder.viewBinding.pinIcon.setVisibility(View.GONE);
                viewHolder.viewBinding.mainLayout.setBackground(null);
            }
            viewHolder.viewBinding.dndIcon.setVisibility(View.GONE);
            if (msgConversation.conversationInfo.getRecvMsgOpt() == 0) {
                viewHolder.viewBinding.badge.badge.setVisibility(msgConversation.conversationInfo.getUnreadCount() != 0 ? View.VISIBLE : View.GONE);
                viewHolder.viewBinding.badge.badge.setText(msgConversation.conversationInfo.getUnreadCount() + "");
            } else {
                if (msgConversation.conversationInfo.getRecvMsgOpt() == 2) {
                    viewHolder.viewBinding.dndIcon.setImageResource(io.openim.android.ouicore.R.mipmap.dnd_conversation_icon);
                    viewHolder.viewBinding.dndIcon.setVisibility(View.VISIBLE);
                    viewHolder.viewBinding.badge.badge.setVisibility(View.GONE);
                }
            }
            viewHolder.viewBinding.time.setText(TimeUtil.getTimeString(msgConversation.conversationInfo.getLatestMsgSendTime()));
        }

        @Override
        public int getItemCount() {
            return null == conversationInfos ? 0 : conversationInfos.size();
        }

        private String trimString(String str) {
            if (str != null && str.length() > 12) {
                return str.substring(0, 12) + "...";
            } else
                return str;
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

}
