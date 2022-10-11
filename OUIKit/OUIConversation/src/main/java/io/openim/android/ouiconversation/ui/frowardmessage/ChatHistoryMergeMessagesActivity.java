package io.openim.android.ouiconversation.ui.frowardmessage;

import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.GROUP_ID;
import static io.openim.android.ouicore.utils.Constant.ID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.List;

import io.openim.android.ouiconversation.adapter.MessageAdapter;
import io.openim.android.ouiconversation.adapter.MessageViewHolder;
import io.openim.android.ouiconversation.databinding.ActivityChatBinding;
import io.openim.android.ouiconversation.databinding.ActivityChatHistoryMergeMessagesBinding;
import io.openim.android.ouiconversation.databinding.ActivityChatHistoryMergeMessagesBindingImpl;
import io.openim.android.ouiconversation.ui.GroupChatSettingsActivity;
import io.openim.android.ouiconversation.ui.PrivateChatSettingsActivity;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.widget.BottomInputCote;
import io.openim.android.ouiconversation.widget.ChoiceBottomBar;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.Message;

public class ChatHistoryMergeMessagesActivity extends BaseActivity<ChatVM, ActivityChatHistoryMergeMessagesBinding> implements ChatVM.ViewAction {

    private MessageAdapter messageAdapter;
    public static String title ;
    public static List<Message> messages ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String name = title;
        bindVM(ChatVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityChatHistoryMergeMessagesBinding.inflate(getLayoutInflater()));

        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());


        initView(name);
        listener();

        setTouchClearFocus(false);
//        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageViewHolder.isChatHistory = false ;
//        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(String name) {
        MessageViewHolder.isChatHistory = true ;
        view.nickName.setText(name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //倒叙
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(true);

        view.recyclerView.setLayoutManager(linearLayoutManager);
        view.recyclerView.addItemDecoration(new DefaultItemDecoration(this.getResources().getColor(android.R.color.transparent), 1, Common.dp2px(16)));
        messageAdapter = new MessageAdapter(this);
        messageAdapter.bindRecyclerView(view.recyclerView);
        messageAdapter.setVm(vm);
        messageAdapter.setMessages(messages);

        vm.setMessageAdapter(messageAdapter);
        view.recyclerView.setAdapter(messageAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //记录原始窗口高度
    private int mWindowHeight = 0;

//    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = () -> {
//        Rect r = new Rect();
//        //获取当前窗口实际的可见区域
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
//        int height = r.height();
//        if (mWindowHeight == 0) {
//            //一般情况下，这是原始的窗口高度
//            mWindowHeight = height;
//        } else {
//            RelativeLayout.LayoutParams inputLayoutParams = (RelativeLayout.LayoutParams) view.layoutInputCote.getRoot().getLayoutParams();
//            if (mWindowHeight == height) {
//                inputLayoutParams.bottomMargin = 0;
//            } else {
//                //两次窗口高度相减，就是软键盘高度
//                int softKeyboardHeight = mWindowHeight - height;
//                inputLayoutParams.bottomMargin = softKeyboardHeight;
//            }
//            view.layoutInputCote.getRoot().setLayoutParams(inputLayoutParams);
//        }
//    };

    private void listener() {
        view.back.setOnClickListener(v -> finish());


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void scrollToPosition(int position) {
        view.recyclerView.scrollToPosition(position);
    }
}
