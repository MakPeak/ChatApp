package io.openim.android.ouiconversation.ui;

import static android.view.View.GONE;
import static io.openim.android.ouiconversation.ui.GroupChatSettingsActivity.clearChatHistory;
import static io.openim.android.ouiconversation.vm.PrivateChatSettingsVM.isConvCleared;
import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.GROUP_ID;
import static io.openim.android.ouicore.utils.Constant.ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.gson.Gson;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsResultListener;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.MentionsEditText;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.ByteChatTokenizer;
import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.adapter.MessageAdapter;
import io.openim.android.ouiconversation.adapter.MessageViewHolder;
import io.openim.android.ouiconversation.databinding.ActivityChatBinding;
import io.openim.android.ouiconversation.databinding.MentionMemberLayoutBinding;
import io.openim.android.ouiconversation.entity.ByteChatAtUserInfo;
import io.openim.android.ouiconversation.ui.groupsettings.GroupAnnouncementActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.widget.BottomInputCote;
import io.openim.android.ouiconversation.widget.ChoiceBottomBar;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.im.IMEvent;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.Message;

@Route(path = Routes.Conversation.CHAT)
public class ChatActivity extends BaseActivity<ChatVM, ActivityChatBinding> implements ChatVM.ViewAction, QueryTokenReceiver, SuggestionsResultListener, SuggestionsVisibilityManager {

    private MessageAdapter messageAdapter;
    private BottomInputCote bottomInputCote;
    private ChoiceBottomBar choiceBottomBar;
    private boolean isPined,isDND;
    public static boolean isBurn;
    private String ownerId;
    private Boolean isSystemNotification=false;
    private Boolean isNotInGroup=false;
    private
    String loggedInUserId = "";
    ActivityResultLauncher<Intent> groupSettingsActivityResultLauncher;
    //--@mention
    private static final String BUCKET = "people-network";
    List<ByteChatAtUserInfo> byteChatAtUserInfoList=new LinkedList<>();
    public RecyclerViewAdapter<ByteChatAtUserInfo, MentionRecyclerViewItem> adapter;
    public PersonMentionAdapter personMentionAdapter;
    private MentionsEditText editor;
    private Boolean hideInput=false;
    private long clickedNicknameTime = -1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //userId 与 GROUP_ID 互斥
        String userId = getIntent().getStringExtra(ID);
        String groupId = getIntent().getStringExtra(GROUP_ID);
        Gson gson = new Gson();
        Message startMsg = gson.fromJson(getIntent().getStringExtra("Message"), Message.class);
        String conversationID = getIntent().getStringExtra(CONVERSATION_ID);
        String name = getIntent().getStringExtra(io.openim.android.ouicore.utils.Constant.K_NAME);
        isPined = getIntent().getExtras().getBoolean("isPined");
        isDND = getIntent().getExtras().getBoolean("isDND");
        isBurn = getIntent().getExtras().getBoolean("isBurn");
        isSystemNotification=getIntent().getExtras().getBoolean("isSystemNotification");
        isNotInGroup=getIntent().getExtras().getBoolean("isNotInGroup");
        setupFileDowmloader();

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setReadTimeout(30_000)
            .setConnectTimeout(30_000)
            .build();
        PRDownloader.initialize(getApplicationContext(), config);

        bindVM(ChatVM.class);

        groupSettingsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Constant.CLEAR_CHAT_HISTORY) {
                    onDataReturnedFromGroupSettings();
                }
            });

        loggedInUserId = LoginCertificate.getCache(this).userID;
        if (null != userId)
            vm.otherSideID = userId;
        if (null != groupId) {
            vm.isSingleChat = false;
            vm.groupID = groupId;
        }
        if (null != conversationID) {
            vm.conversationID = conversationID;
        }
        if (null != startMsg) {
            vm.startMsg = startMsg;
        }
        super.onCreate(savedInstanceState);

        bindViewDataBinding(ActivityChatBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setChatVM(vm);

        initView(name);
        listener();

        setTouchClearFocus(false);
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        //------------------ @mention -----------------------------------------
       // vm.getGroupMemberList(groupId);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setReverseLayout(true);
        view.layoutInputCote.recyclerMentionList.setLayoutManager(linearLayoutManager);
        personMentionAdapter= new PersonMentionAdapter(new ArrayList<ByteChatAtUserInfo>());
        view.layoutInputCote.recyclerMentionList.setAdapter(personMentionAdapter);

        editor = view.layoutInputCote.chatInput;
        //editor.displa(false);
        editor.setQueryTokenReceiver(this);

        WordTokenizerConfig tokenizerConfig = new WordTokenizerConfig.Builder()
            .setExplicitChars("@")
            .setMaxNumKeywords(20)
            .setThreshold(0)
            .build();

        ByteChatTokenizer tokenizer = new ByteChatTokenizer(tokenizerConfig);
        editor.setTokenizer(tokenizer);
        editor.setQueryTokenReceiver(this::onQueryReceived);
        editor.setSuggestionsVisibilityManager(this);

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                vm.inputMsg.setValue(s.toString());
            }
        });

        vm.userInfoList.observe(this, data->{
            List<String> adminUidList=new ArrayList<>();
            byteChatAtUserInfoList.clear();
            for(GroupMembersInfo groupMembersInfo:data){
                ByteChatAtUserInfo byteChatAtUserInfo=new ByteChatAtUserInfo();
                byteChatAtUserInfo.setAtUserID(groupMembersInfo.getUserID());
                byteChatAtUserInfo.setGroupNickname(groupMembersInfo.getNickname());
                byteChatAtUserInfo.setFaceURL(groupMembersInfo.getFaceURL());
                if(!groupMembersInfo.getUserID().equalsIgnoreCase(loggedInUserId)) {
                    Log.d("atmention", "onCreate: "+groupMembersInfo.getUserID()+groupMembersInfo.getNickname());

                    byteChatAtUserInfoList.add(byteChatAtUserInfo);
                }

                if(groupMembersInfo.getRoleLevel()==Constant.ROLE_LEVEL_ADMIN)
                    adminUidList.add(groupMembersInfo.getUserID());
            }
            vm.groupMembersUpdated.setValue(false);
            vm.adminUIdList.setValue(adminUidList);
            if(hideInput) {
                if (!loggedInUserId.equalsIgnoreCase(ownerId) && !adminUidList.contains(loggedInUserId)) {
                    view.layoutInputCote.linearLayout.setVisibility(View.INVISIBLE);
                    view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @NonNull
    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {

        List<String> buckets = Collections.singletonList(BUCKET);
        List<ByteChatAtUserInfo> suggestions =  getFilteredList(queryToken.getKeywords());//byteChatAtUserInfoList;
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
//        view.layoutInputCote.chatInput.displaySuggestions(false);
       // view.layoutInputCote.chatInput.onReceiveSuggestionsResult(result, BUCKET);
        Log.d("mention", "onQueryReceived: "+buckets);
        onReceiveSuggestionsResult(result, BUCKET);
        return buckets;
    }

    @Override
    public void onReceiveSuggestionsResult(@NonNull SuggestionsResult result, @NonNull String bucket) {
        List<? extends Suggestible> suggestions = result.getSuggestions();
        personMentionAdapter = new PersonMentionAdapter(result.getSuggestions());
        view.layoutInputCote.recyclerMentionList.swapAdapter(personMentionAdapter, true);

        boolean display = suggestions != null && suggestions.size() > 0;
        displaySuggestions(display);
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView picture;

        public ViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.group_member_avatar);
            name = itemView.findViewById(R.id.group_member_nickname);
        }
    }

    private class PersonMentionAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<? extends Suggestible> suggestions;

        public PersonMentionAdapter(List<? extends Suggestible> people) {
            suggestions = people;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mention_member_layout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Suggestible suggestion = suggestions.get(i);
            if (!(suggestion instanceof ByteChatAtUserInfo)) {
                return;
            }

            final ByteChatAtUserInfo person = (ByteChatAtUserInfo) suggestion;
            viewHolder.name.setText(person.getGroupNickname());
            Log.d("faceurl", "onBindViewHolder: "+person.getFaceURL());
            if(person.getFaceURL()!=null && person.getFaceURL()!="") {
                Glide.with(viewHolder.picture.getContext())
                    .load(person.getFaceURL())
                    .into(viewHolder.picture);
            }

            viewHolder.itemView.setOnClickListener(v -> {
//                String tempName=person.getGroupNickname();
//                person.setGroupNickname(tempName);
                ByteChatAtUserInfo temp = new ByteChatAtUserInfo();
                temp.setGroupNickname("@"+person.getGroupNickname());
                temp.setAtUserID(person.getAtUserID());
                temp.setFaceURL(person.getFaceURL());
                editor.insertMention(temp);
                view.layoutInputCote.recyclerMentionList.swapAdapter(new PersonMentionAdapter(new ArrayList<ByteChatAtUserInfo>()), true);
                //person.setGroupNickname(tempName);
                //editor.displaySuggestions(false);
                editor.requestFocus();
            });
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }
    }

    // --------------------------------------------------
    // SuggestionsManager Implementation
    // --------------------------------------------------

    @Override
    public void displaySuggestions(boolean display) {
        if (display) {
            view.layoutInputCote.recyclerMentionList.setVisibility(RecyclerView.VISIBLE);
        } else {
            view.layoutInputCote.recyclerMentionList.setVisibility(RecyclerView.GONE);
        }
    }

    @Override
    public boolean isDisplayingSuggestions() {
        return view.layoutInputCote.recyclerMentionList.getVisibility() == RecyclerView.VISIBLE;
    }

    private List<ByteChatAtUserInfo> getFilteredList(String keywords){
        List<ByteChatAtUserInfo> byteChatAtUserInfoListFiltered=new LinkedList<>();
        for(ByteChatAtUserInfo byteChatAtUserInfo: byteChatAtUserInfoList){
            if(byteChatAtUserInfo.getGroupNickname().toLowerCase().contains(keywords.toLowerCase())){
                byteChatAtUserInfoListFiltered.add(byteChatAtUserInfo);
            }
        }
        return byteChatAtUserInfoListFiltered;
    }

    //----------@mention ends

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageViewHolder.isChatHistory = false ;
        System.out.println("onDestroy : ");
        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        IMEvent.getInstance().removeAdvanceMsgListener(vm);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(String name) {

        if(isSystemNotification){
            MessageViewHolder.isChatHistory = true ;
            view.more.setVisibility(View.GONE);
            view.layoutInputCote.linearLayout.setVisibility(View.GONE);
            //view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.VISIBLE);
        }
        bottomInputCote = new BottomInputCote(this, view.layoutInputCote,view.rootLayout);
        bottomInputCote.setChatVM(vm,this);
        choiceBottomBar = new ChoiceBottomBar(this , view.layoutChoiceBottomBar,vm,name);
        if(name.length()>12){
            name = name.substring(0,12)+"...";
        }
        view.nickName.setText(name);
        String finalName = name;
        view.nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTimeRes = System.currentTimeMillis()- clickedNicknameTime ;
                clickTimeRes = clickTimeRes/1000;
                if( clickTimeRes <= 1 && clickedNicknameTime != -1){
                    return;
                }
                clickedNicknameTime = System.currentTimeMillis();
                ClipboardManager clipboard = (ClipboardManager) ChatActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(finalName, finalName);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatActivity.this, ChatActivity.this.getString(io.openim.android.ouicore.R.string.copy_successfully), Toast.LENGTH_SHORT).show();

            }
        });
        if(isSystemNotification)
            view.nickName.setText(getString(io.openim.android.ouicore.R.string.system_notification));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //倒叙
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(true);

        WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(this);
        wrapContentLinearLayoutManager.setStackFromEnd(false);
        wrapContentLinearLayoutManager.setReverseLayout(true);

        view.recyclerView.setLayoutManager(wrapContentLinearLayoutManager);

        view.recyclerView.addItemDecoration(new DefaultItemDecoration(this.getResources().getColor(android.R.color.transparent), 1, Common.dp2px(16)));
        messageAdapter = new MessageAdapter(this);
        messageAdapter.bindRecyclerView(view.recyclerView);
        messageAdapter.setVm(vm);
        vm.setMessageAdapter(messageAdapter);
        view.recyclerView.setAdapter(messageAdapter);

        vm.messages.observe(this, v -> {
            Log.d("Chat", "loadHistoryMessage initView: messages.observe"+v.size() + " " +messageAdapter.getMessages());
            if (null == v || v.isEmpty()||messageAdapter.getMessages()!= null || ( messageAdapter.getMessages()!= null && !messageAdapter.getMessages().isEmpty())) return;
            Log.d("Chat", "loadHistoryMessage initView: messages.observe 12321 ");

            messageAdapter.setMessages(v);
            messageAdapter.notifyItemRangeChanged(v.size() - 1 - vm.newMessages.getValue().size(), v.size() - 1);
//            scrollToPosition(v.size() - 1 - vm.newMessages.getValue().size());
        });
        view.recyclerView.setOnTouchListener((v, event) -> {
            bottomInputCote.clearFocus();
            Common.hideKeyboard(this, v);
            bottomInputCote.setExpandHide();
            return false;
        });

        view.closeTopBar.setOnClickListener(v->{
            view.announcementTopBar.setVisibility(GONE);
        });

        if(!vm.conversationID.isEmpty())
            vm.getGroupInfo(vm.conversationID.substring(6));

        vm.groupsInfo.observe(this, v->{
            vm.getGroupMemberList(vm.groupID);
            if(!v.isEmpty()) {
                ownerId = v.get(0).getOwnerUserID();
                if(v.get(0).getNotification()!=null && !v.get(0).getNotification().isEmpty()) {
                    view.announcementTextTop.setText(v.get(0).getNotification());
                    view.announcementTopBar.setVisibility(View.VISIBLE);
                }else{
                    view.announcementTopBar.setVisibility(View.GONE);
                }

                //Group status - ok = 0 blocked = 1 Dismissed = 2 Muted  = 3 OR when not a member of group
                //if(!loggedInUserId.equalsIgnoreCase(ownerId) && v.get(0).getStatus()==3){
                if(v.get(0).getStatus()==3 || isNotInGroup){
                    hideInput=true;
                }
            }
        });

        //Handling of Notification on Top
        final Boolean[] announcementViewExpanded = {false};
        view.announcementTopBar.setOnClickListener(v-> {
                if (!announcementViewExpanded[0]) {
                    view.announcementTextTop.setMaxLines(Integer.MAX_VALUE);
                    view.announcementTextTop.setEllipsize(null);
                    announcementViewExpanded[0] = true;
                    view.imageArrow.setBackground(getDrawable(R.mipmap.icon_arrow_up));
                } else {
                    announcementViewExpanded[0] = false;
                    view.announcementTextTop.setMaxLines(1);
                    view.announcementTextTop.setEllipsize(TextUtils.TruncateAt.END);
                    view.imageArrow.setBackground(getDrawable(R.mipmap.icon_arrow_down));
                }
            }
        );
        vm.selectedMessages.observe(this , data->{
            if(data == null || data.isEmpty()) {
                messageAdapter.isSelecting=false;
                messageAdapter.notifyDataSetChanged();
                view.layoutChoiceBottomBar.getRoot().setVisibility(View.INVISIBLE);
                view.layoutInputCote.getRoot().setVisibility(View.VISIBLE);
                return;
            }
            view.layoutChoiceBottomBar.getRoot().setVisibility(View.VISIBLE);
            view.layoutInputCote.getRoot().setVisibility(View.INVISIBLE);

        });
        view.announcementTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    vm.getContext().startActivity(new Intent(vm.getContext() ,
                        GroupAnnouncementActivity.class).putExtra(Constant.GROUP_ID, vm.groupID).putExtra(Constant.OWNER_ID, vm.groupsInfo.getValue().get(0).getOwnerUserID())
                        .putExtra(Constant.GROUP_NAME,vm.groupsInfo.getValue().get(0).getGroupName())
                        .putExtra(Constant.GROUP_FACE_URL,vm.groupsInfo.getValue().get(0).getFaceURL())
                        .putExtra(Constant.GROUP_ANNOUNCEMENT_TIME,vm.groupsInfo.getValue().get(0).getNotificationUpdateTime())
                        .putExtra(Constant.GROUP_ANNOUNCEMENT, vm.groupsInfo.getValue().get(0).getNotification()));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        view.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("single_" + vm.otherSideID+" " + vm.conversationID);
                if(vm.isSingleChat != false)
                    startActivity(new Intent(ChatActivity.this,PrivateChatSettingsActivity.class).putExtra("chatID","single_"+vm.otherSideID)
                        .putExtra("isPined",isPined).putExtra("isDND",isDND).putExtra("isBurn",isBurn)
                    );
                else {
                    Intent i = new Intent(ChatActivity.this, GroupChatSettingsActivity.class)
                        .putExtra("isPined",isPined).putExtra("chatID","group_"+vm.groupID).putExtra("ownerID", ownerId)
                        .putExtra("isNotInGroup", isNotInGroup);
                    groupSettingsActivityResultLauncher.launch(i);
                }

//                    startActivity(new Intent(ChatActivity.this, GroupChatSettingsActivity.class)
//                        .putExtra("isPined",isPined).putExtra("chatID",vm.conversationID).putExtra("ownerID", ownerId));

            }
        });

        //Track Grp/member ban/unban - so as to update the input box accordingly
        vm.banUpdate.observe(this, v->{
            Log.d("banUpdate", "initView: "+v);
            if(v==Constant.MsgType.GROUP_MUTED_NOTIFICATION){
                Log.d("banUpdate", "initView: "+v);
                view.layoutInputCote.linearLayout.setVisibility(View.INVISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.VISIBLE);
            }else if(v==Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION){
                Log.d("banUpdate", "initView: "+v);
                view.layoutInputCote.linearLayout.setVisibility(View.VISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.GONE);
            }
            else if(v==0){
                view.layoutInputCote.linearLayout.setVisibility(View.VISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.GONE);
            }
        });
//
//        vm.memberBanUpdate.observe(this, v->{
//            if(v==Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION){
//                view.layoutInputCote.linearLayout.setVisibility(View.INVISIBLE);
//                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.VISIBLE);
//            }else if(v==Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION){
//                view.layoutInputCote.linearLayout.setVisibility(View.VISIBLE);
//                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.GONE);
//            }
////            else{
////                view.layoutInputCote.linearLayout.setVisibility(View.VISIBLE);
////                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.GONE);
////            }
//        });

        vm.youAreAdded.observe(this, data->{
            if(data){
                view.layoutInputCote.linearLayout.setVisibility(View.VISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.GONE);
            }
        });
        vm.youAreRemoved.observe(this, data->{
            if(data){
                view.layoutInputCote.linearLayout.setVisibility(View.INVISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.VISIBLE);
            }
        });
        vm.groupMemberUNMutedIsYou.observe(this, data->{
            if(data){
                view.layoutInputCote.linearLayout.setVisibility(View.VISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.GONE);
            }
        });
        vm.groupMemberMutedIsYou.observe(this, data->{
            if(data){
                view.layoutInputCote.linearLayout.setVisibility(View.INVISIBLE);
                view.layoutInputCote.linearLayoutDisableInput.setVisibility(View.VISIBLE);
            }
        });

    }

    private void onDataReturnedFromGroupSettings(){
        vm.messages.setValue(new LinkedList<>());
        messageAdapter.setMessages(new LinkedList<>());
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!vm.conversationID.isEmpty())
            vm.getGroupInfo(vm.conversationID.substring(6));
        System.out.println("isConvCleared : " + isConvCleared);
        if(isConvCleared || clearChatHistory)
        {
            isConvCleared = false ;
            clearChatHistory=false;
//            int size = vm.messages.getValue().size();
//            List<Message> messages = new LinkedList<>();
//            vm.messages.getValue().clear();
//            Message message = new Message();
//            message.setContentType(101);
//            message.setContent("tesst");
//            messages.add(message);
            vm.messages.postValue(new LinkedList<>());
//
            messageAdapter.setMessages(null);
            vm.startMsg=null;
//            vm.loadHistoryMessage();
            messageAdapter.notifyDataSetChanged();
        }
    }

    //记录原始窗口高度
    private int mWindowHeight = 0;

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = () -> {
        Rect r = new Rect();
        //获取当前窗口实际的可见区域
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int height = r.height();
        if (mWindowHeight == 0) {
            //一般情况下，这是原始的窗口高度
            mWindowHeight = height;
        } else {
            RelativeLayout.LayoutParams inputLayoutParams = (RelativeLayout.LayoutParams) view.layoutInputCote.getRoot().getLayoutParams();
            if (mWindowHeight == height) {
                inputLayoutParams.bottomMargin = 0;
            } else {
                //两次窗口高度相减，就是软键盘高度
                int softKeyboardHeight = mWindowHeight - height;
                inputLayoutParams.bottomMargin = softKeyboardHeight;
            }
            view.layoutInputCote.getRoot().setLayoutParams(inputLayoutParams);
        }
    };

    private void listener() {
        view.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < vm.messages.getValue().size(); i++ ){
                    if(vm.messages.getValue().get(i).getAttachedInfoElem() != null) {
                        if (vm.messages.getValue().get(i).getAttachedInfoElem().isPrivateChat()) {
                            vm.deleteMessage(vm.messages.getValue().get(i));
                        }
                    }
                }
                finish();
            }
        });
        vm.grayedSendButton.observe(this,v->{
            if(v) {
                bottomInputCote.view.chatSend.setVisibility(View.GONE);
//                bottomInputCote.view.chatSend.setBackgroundColor(Color.GRAY);
                bottomInputCote.view.chatSend.setEnabled(false);
            } else {
                bottomInputCote.view.chatSend.setVisibility(View.VISIBLE);
//                bottomInputCote.view.chatSend.startAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.textview_animation));
//                bottomInputCote.view.chatSend.setBackgroundColor(getResources().getColor(io.openim.android.ouicore.R.color.purple_200));
                bottomInputCote.view.chatSend.setEnabled(true);
            }

        });
        vm.isForwardedToSameConversation.observe(this , data->{
            if(data == null)return;
            vm.addForwardMessage(data);
        });
        view.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) view.recyclerView.getLayoutManager();
                int firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(vm.messages!=null && vm.messages.getValue()!=null) {
                    if (lastVisiblePosition == vm.messages.getValue().size() - 1) {
                        vm.loadHistoryMessage();
                    }
                }
                if (vm.isSingleChat)
                    vm.sendMsgReadReceipt(firstVisiblePosition, lastVisiblePosition);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        bottomInputCote.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        for(int i = 0; i < vm.messages.getValue().size(); i++ ){
            if(vm.messages.getValue().get(i).getAttachedInfoElem() != null) {
                if (vm.messages.getValue().get(i).getAttachedInfoElem().isPrivateChat()) {
                    vm.deleteMessage(vm.messages.getValue().get(i));
                }
            }
        }
        finish();
    }

    @Override
    public void scrollToPosition(int position) {
        view.recyclerView.scrollToPosition(position);
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView" );
                e.printStackTrace();
            }
        }
    }

    private void setupFileDowmloader() {
        FileDownloader.setupOnApplicationOnCreate(this.getApplication())
            .connectionCreator(new FileDownloadUrlConnection
                .Creator(new FileDownloadUrlConnection.Configuration()
                .connectTimeout(15_000) // set connection timeout.
                .readTimeout(15_000) // set read timeout.
            ))
            .commit();
    }

    public static class MentionRecyclerViewItem extends RecyclerView.ViewHolder {
        public MentionMemberLayoutBinding v;

        public MentionRecyclerViewItem(@NonNull View parent) {
            super((MentionMemberLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = MentionMemberLayoutBinding.bind(this.itemView);
        }
    }

}
