package io.openim.android.ouiconversation.widget;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static io.openim.android.ouiconversation.utils.Constant.MsgType.TXT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vanniktech.emoji.EmojiPopup;
import com.linkedin.android.spyglass.mentions.MentionSpan;
import com.linkedin.android.spyglass.mentions.MentionsEditable;
import com.linkedin.android.spyglass.suggestions.interfaces.OnSuggestionsVisibilityChangeListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.GroupMemberItemLayoutBinding;
import io.openim.android.ouiconversation.databinding.LayoutContactItemBinding;
import io.openim.android.ouiconversation.databinding.LayoutInputCoteBinding;
import io.openim.android.ouiconversation.databinding.MentionMemberLayoutBinding;
import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.ui.groupsettings.ViewGroupMembersActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.base.BaseApp;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.entity.MsgConversation;
import io.openim.android.ouicore.utils.Common;
import io.openim.android.ouicore.utils.L;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.models.AtUserInfo;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.Message;

/**
 * 聊天页面底部输入栏
 */
public class BottomInputCote {

    public static final String TAG="atmention";
    private ChatVM vm;
    private Context context;
    InputExpandFragment inputExpandFragment;
    public LayoutInputCoteBinding view;
    TouchVoiceDialog touchVoiceDialog;
    boolean hasMicrophone;
    private boolean isEmojisOn = false;

    //@mention
    public List<String> atUserIDList=new LinkedList<>();
    public List<AtUserInfo> atUserInfoList=new LinkedList<>();

    @SuppressLint("WrongConstant")
    public BottomInputCote(ChatActivity context, LayoutInputCoteBinding view , View rootView) {
        this.context=context;
        this.view=view;
        hasMicrophone = AndPermission.hasPermissions(context, Permission.Group.MICROPHONE);


        view.chatSend.setOnClickListener(x -> {
            if(vm.grayedSendButton.getValue()) {
                return;
            }
            final Message msg;
            if(vm.replyMessage.getValue() != null){
                msg = vm.createReplyMessage(vm.replyMessage.getValue(),vm.inputMsg.getValue());
                vm.replyMessage.setValue(null);
                view.replyLayout.setVisibility(GONE);
            }else{
                MentionsEditable mentionsEditable = new MentionsEditable(view.chatInput.getText());
                if(mentionsEditable.getMentionSpans().size()!=0) {
                    atUserInfoList.clear();
                    atUserIDList.clear();

                    List<MentionSpan> mentionSpans = mentionsEditable.getMentionSpans();
                    for (MentionSpan span : mentionSpans) {
                        String tempName = ((AtUserInfo) span.getMention()).getGroupNickname();
                        if(((AtUserInfo) span.getMention()).getGroupNickname().startsWith("@"))
                            ((AtUserInfo) span.getMention()).setGroupNickname(((AtUserInfo) span.getMention()).getGroupNickname().replaceFirst("@",""));

                        atUserInfoList.add((AtUserInfo) span.getMention());
                        atUserIDList.add(((AtUserInfo) span.getMention()).getAtUserID());
                        int start = mentionsEditable.getSpanStart(span);
                        int end = mentionsEditable.getSpanEnd(span);
                        mentionsEditable.replace(start+1, end, ((AtUserInfo) span.getMention()).getAtUserID());
                    }

                    msg = OpenIMClient.getInstance().messageManager.createTextAtMessage(mentionsEditable.toString(), atUserIDList, atUserInfoList, null);
                    Log.d("atmention", "BottomInputCote: " + msg);
                }else{
                    msg= OpenIMClient.getInstance().messageManager.createTextMessage(vm.inputMsg.getValue());
                }
            }
            vm.sendMsg(msg);
            vm.inputMsg.setValue("");
            view.chatInput.setText("");
        });

        view.voice.setOnCheckedChangeListener((v, isChecked) -> {
            view.chatInput.setVisibility(isChecked ? GONE : VISIBLE);
            view.chatSend.setVisibility(isChecked ? GONE : VISIBLE);
            view.touchSay.setVisibility(isChecked ? VISIBLE : GONE);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.chatSend.getWindowToken(), 0);
            if(vm.inputMsg.getValue().isEmpty())
                vm.inputMsg.setValue("");
        });
        try {
            EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(view.chatInput);
            view.emoji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isEmojisOn){
                        view.emoji.setImageResource(R.mipmap.ic_chat_emoji);
                    }else{
                        view.emoji.setImageResource(R.mipmap.ic_chat_keyboard);
                    }
                    emojiPopup.toggle();
                    isEmojisOn = !isEmojisOn ;

                }
            });
            view.chatInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEmojisOn){
                        view.emoji.setImageResource(R.mipmap.ic_chat_emoji);
                        isEmojisOn = false ;
                        emojiPopup.toggle();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


        view.closeReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.replyLayout.setVisibility(GONE);
                vm.replyMessage.setValue(null);
            }
        });
        view.touchSay.setOnLongClickListener(v -> {
            if (null == touchVoiceDialog) {
                touchVoiceDialog = new TouchVoiceDialog(context);
                touchVoiceDialog.setOnSelectResultListener((code, audioPath, duration) -> {
                    if (code == 0) {
                        //录音结束
                        Message message = OpenIMClient.getInstance().messageManager.createSoundMessageFromFullPath(audioPath.getPath(), duration);
                        vm.sendMsg(message);
                    }
                });
            }

            if (hasMicrophone)
                touchVoiceDialog.show();
            else
                AndPermission.with(context)
                    .runtime()
                    .permission(Permission.Group.MICROPHONE)
                    .onGranted(permissions -> {
                        // Storage permission are allowed.
                        hasMicrophone = true;
                    })
                    .onDenied(permissions -> {
                        // Storage permission are not allowed.
                    })
                    .start();
            return false;
        });

        view.chatInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                setExpandHide();
        });

        view.chatMore.setOnClickListener(v -> {
            clearFocus();
            Common.hideKeyboard(BaseApp.instance(), v);
            view.fragmentContainer.setVisibility(VISIBLE);
            if (null == inputExpandFragment) {
                inputExpandFragment = new InputExpandFragment();
                inputExpandFragment.setPage(1);
                inputExpandFragment.setChatVM(vm);
            }
            switchFragment(inputExpandFragment);
        });
    }


    public void dispatchTouchEvent(MotionEvent event) {
        if (null != touchVoiceDialog)
            touchVoiceDialog.dispatchTouchEvent(event);
    }

    public void clearFocus() {
        view.chatInput.clearFocus();
    }

    public void setChatVM(ChatVM vm,ChatActivity context) {
        this.vm = vm;
        view.setChatVM(vm);
        this.context=context;
        vm.replyMessage.observe(context, data ->{
            // handle ui here
            if(data == null) return;
            String replyContent = "", cardName="";
            if(data.getQuoteElem().getQuoteMessage()==null){
                System.out.println("replyMessage.observe" + data.getContent());
                switch (data.getContentType()){
                    case Constant.MsgType.TXT : replyContent = data.getContent() ; break;
                    case Constant.MsgType.PICTURE: replyContent = context.getString(io.openim.android.ouicore.R.string.picture);break;
                    case Constant.MsgType.VIDEO: replyContent = context.getString(io.openim.android.ouicore.R.string.video); break;
                    case Constant.MsgType.FILE: replyContent = context.getString(io.openim.android.ouicore.R.string.file);break;
                    case Constant.MsgType.VOICE: replyContent = context.getString(io.openim.android.ouicore.R.string.voice);break;
                    case Constant.MsgType.MERGE: replyContent = "reply for a combined and forward message";break;
                    //case Constant.MsgType.CARD: replyContent = "reply for name card";break;
                    case Constant.MsgType.CARD:
                        try{
                            cardName = new JSONObject(data.getContent()).getString("nickname");
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                        replyContent = "["+ context.getString(io.openim.android.ouicore.R.string.name_card)+"]"+cardName;
                        break;
                }

//                if(data.getContentType() == Constant.MsgType.PICTURE){
//                    JSONObject jsonObjectContent = null;
//                    try {
//                        jsonObjectContent = new JSONObject(data.getContent());
//                        replyContent = jsonObjectContent.getJSONObject("sourcePicture").get("uuid").toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else if(data.getContentType() == Constant.MsgType.VIDEO){
//                    JSONObject jsonObjectContent = null;
//                    try {
//                        jsonObjectContent = new JSONObject(data.getContent());
//                        replyContent = jsonObjectContent.get("videoUUID").toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else if(data.getContentType() == Constant.MsgType.FILE){
//                    JSONObject jsonObjectContent = null;
//                    try {
//                        jsonObjectContent = new JSONObject(data.getContent());
//                        replyContent = jsonObjectContent.get("fileName").toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                    view.replyText.setText(data.getSenderNickname() + " : " + replyContent);
            } else {
                System.out.println("replyMessage.observe" + data.getQuoteElem().getText());
                switch (data.getQuoteElem().getQuoteMessage().getContentType()){
                    case Constant.MsgType.TXT : replyContent = data.getQuoteElem().getText() ; break;
                    case Constant.MsgType.PICTURE: replyContent = context.getString(io.openim.android.ouicore.R.string.picture);break;
                    case Constant.MsgType.VIDEO: replyContent = context.getString(io.openim.android.ouicore.R.string.video); break;
                    case Constant.MsgType.FILE: replyContent = context.getString(io.openim.android.ouicore.R.string.file);break;
                    case Constant.MsgType.VOICE: replyContent = context.getString(io.openim.android.ouicore.R.string.voice);break;
                    case Constant.MsgType.MERGE: replyContent = "reply for a combined and forward message";break;
                    case Constant.MsgType.CARD: replyContent = "reply for name card";break;
                }
//                if(data.getQuoteElem().getQuoteMessage().getContentType() == Constant.MsgType.PICTURE){
//                    replyContent = data.getQuoteElem().getQuoteMessage().getContent().toString();
//                }
//                if(data.getContentType() == Constant.MsgType.PICTURE){
//                    JSONObject jsonObjectContent = null;
//                    try {
//                        jsonObjectContent = new JSONObject(data.getContent());
//                        replyContent = jsonObjectContent.getJSONObject("sourcePicture").get("uuid").toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else if(data.getContentType() == Constant.MsgType.VIDEO){
//                    JSONObject jsonObjectContent = null;
//                    try {
//                        jsonObjectContent = new JSONObject(data.getContent());
//                        replyContent = jsonObjectContent.get("videoUUID").toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else if(data.getContentType() == Constant.MsgType.FILE){
//                    JSONObject jsonObjectContent = null;
//                    try {
//                        jsonObjectContent = new JSONObject(data.getContent());
//                        replyContent = jsonObjectContent.get("fileName").toString();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                view.replyText.setText(data.getSenderNickname() + " : " + replyContent);
            }

            view.replyLayout.setVisibility(VISIBLE);
        });
    }

    //设置扩展菜单隐藏
    public void setExpandHide() {
        view.fragmentContainer.setVisibility(GONE);
    }

    private int mCurrentTabIndex;
    private BaseFragment lastFragment;


    private void switchFragment(BaseFragment fragment) {
        try {
            if (fragment != null && !fragment.isVisible() && mCurrentTabIndex != fragment.getPage()) {
                FragmentTransaction transaction = ((BaseActivity) context).getSupportFragmentManager().beginTransaction();
                if (!fragment.isAdded()) {
                    transaction.add(view.fragmentContainer.getId(), fragment);
                }
                if (lastFragment != null) {
                    transaction.hide(lastFragment);
                }
                transaction.show(fragment).commit();
                lastFragment = fragment;
                mCurrentTabIndex = lastFragment.getPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
