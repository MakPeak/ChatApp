package io.openim.android.ouiconversation.vm;


import static io.openim.android.ouiconversation.adapter.MessageAdapter.OWN_ID;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_ENTER_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_INVITED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_KICKED_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.MEMBER_QUIT_NOTIFICATION;
import static io.openim.android.ouiconversation.utils.Constant.MsgType.REVOKE;
import static io.openim.android.ouicore.utils.Common.UIHandler;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.adapter.MessageAdapter;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.im.IMEvent;
import io.openim.android.ouicore.im.IMUtil;
import io.openim.android.ouicore.utils.L;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnAdvanceMsgListener;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnMsgSendCallback;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.OfflinePushInfo;
import io.openim.android.sdk.models.ReadReceiptInfo;
import io.openim.android.sdk.models.RevokedInfo;

public class ChatVM extends BaseViewModel<ChatVM.ViewAction> implements OnAdvanceMsgListener {
    public MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<Message>> newMessages = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<List<Message>> selectedMessages = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<Message> replyMessage = new MutableLiveData<>();
    public ObservableBoolean typing = new ObservableBoolean(false);
    public MutableLiveData<Boolean> grayedSendButton = new MutableLiveData<Boolean>(false);
    public MutableLiveData<String> inputMsg = new MutableLiveData<>("");
    MutableLiveData<Boolean> isNoData = new MutableLiveData<>(false);
    public MutableLiveData<List<GroupInfo>> groupsInfo = new MutableLiveData<>();
    public MutableLiveData<Message> isForwardedToSameConversation = new MutableLiveData<>();
    private MessageAdapter messageAdapter;
    private Observer<String> inputObserver;
    public Message startMsg = null; // 消息体，取界面上显示的消息体对象
    public String otherSideID = ""; // 消息发送方
    public String groupID = ""; // 接受消息的群ID
    public String searchingForMessage = ""; // 接受消息的群ID
    public String conversationID = "";  // conversation ID
    public boolean isSingleChat = true; //是否单聊 false 群聊
    int count = 20; //条数
    Message loading;
    public MutableLiveData<Integer> banUpdate = new MutableLiveData<>();//for updating input box after grp ban/unban
    public MutableLiveData<Integer> memberBanUpdate = new MutableLiveData<>();//for updating input box after member ban/unban
    public MutableLiveData<Boolean> groupAllBan = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupAllBanCancel = new MutableLiveData<>();
    public MutableLiveData<Boolean> youAreRemoved = new MutableLiveData<>();
    public MutableLiveData<Boolean> youAreAdded = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupMemberMutedIsYou = new MutableLiveData<>();
    public MutableLiveData<Boolean> groupMemberUNMutedIsYou = new MutableLiveData<>();
    public MutableLiveData<Boolean> disableInputVisibility=new MutableLiveData<>();
    public MutableLiveData<Boolean> groupMembersUpdated=new MutableLiveData<>();
    public MutableLiveData<List<GroupMembersInfo>> userInfoList = new MutableLiveData<>();
    public MutableLiveData<List<String>> adminUIdList=new MutableLiveData<>();

    //--@mention
    public String filterStringForMention="";
    public Boolean showMentionList=false;

    @Override
    protected void viewCreate() {
        super.viewCreate();
        loading = new Message();
        loading.setContentType(Constant.LOADING);

        //加载消息记录
        loadHistoryMessage();
        //标记所有消息已读
//        markReaded(null);
        if (isSingleChat)
            listener();
        else {
            markGroupMessagesRead();
            inputMsg.observeForever(inputObserver = s -> {
                int cnt = 0 ;
                for(int i =0  ;i < s.length() ;i++)
                {
                    if(Character.isWhitespace(s.charAt(i)) || s.charAt(i)=='/'){
                        cnt++;
                        continue;
                    }
                }
                if(cnt == s.length()){
                    grayedSendButton.setValue(true);
                    return;
                }

                    if (inputMsg.getValue().isEmpty() || inputMsg.getValue() == null) {
                        grayedSendButton.setValue(true);
                    } else {
                        grayedSendButton.setValue(false);
                    }
                }
            );
        }

        IMEvent.getInstance().addAdvanceMsgListener(this);
    }

    @Override
    protected void viewDestroy() {
        super.viewDestroy();
        IMEvent.getInstance().removeAdvanceMsgListener(this);
        inputMsg.removeObserver(inputObserver);
    }

    /**
     * 标记已读
     *
     * @param msgIDs 为null 清除里列表小红点
     */
    private void markReaded(List<String> msgIDs) {
        if (isSingleChat)
            OpenIMClient.getInstance().messageManager.markC2CMessageAsRead(null, otherSideID, null == msgIDs ? new ArrayList<>() : msgIDs);
        else
            OpenIMClient.getInstance().messageManager.markGroupMessageAsRead(null, groupID, null == msgIDs ? new ArrayList<>() : msgIDs);
    }

    private void markGroupMessagesRead() {
        try {
            OpenIMClient.getInstance().conversationManager.markGroupMessageHasRead(new OnBase<String>() {
                @Override
                public void onError(int code, String error) {
                    Log.d("markGroupMessageHasRead", " onError " + code + " error :" + error);
                }

                @Override
                public void onSuccess(String data) {
                    Log.d("markGroupMessageHasRead", " onSuccess ");
                }
            },conversationID.substring(6,conversationID.length()));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void listener() {
        //提示对方我正在输入
        inputMsg.observeForever(inputObserver = s -> {
            int cnt = 0 ;
            for(int i =0  ;i < s.length() ;i++)
            {
                if(Character.isWhitespace(s.charAt(i)) || s.charAt(i)=='/'){
                    cnt++;
                    continue;
                }
            }
            if(cnt == s.length()){
                grayedSendButton.setValue(true);
                return;
            }

            if (inputMsg.getValue().isEmpty() || inputMsg.getValue() == null) {
                    grayedSendButton.setValue(true);

                } else {
                    grayedSendButton.setValue(false);
                }


                OpenIMClient.getInstance().messageManager.typingStatusUpdate(new OnBase<String>() {
                    @Override
                    public void onError(int code, String error) {

                    }

                    @Override
                    public void onSuccess(String data) {

                    }
                }, otherSideID, "");
            }
        );
    }

    public void setMessageAdapter(MessageAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }

    public void loadHistoryMessage() {
        //otherSideID, groupID, conversationID, startMsg, count
        Log.d("ChatVM", "loadHistoryMessage: otherSideID - " + otherSideID + " groupID-> " + groupID + " conversationID-> " + conversationID);
        Log.d("ChatVM", "loadHistoryMessage:  startMsg " + startMsg + " count " + count);
        OpenIMClient.getInstance().messageManager.getHistoryMessageList(new OnBase<List<Message>>() {
            @Override
            public void onError(int code, String error) {
                Log.e("loadHistoryMessage", "onError: " + error);
            }

            @Override
            public void onSuccess(List<Message> data) {
                List<Message> list = messages.getValue();
                if (data.isEmpty()) {
                    if (!messages.getValue().isEmpty()) {
                        isNoData.setValue(true);
                        removeLoading(list);
                    } else
                        messages.postValue(new LinkedList<>());
                    return;
                } else {
                    startMsg = data.get(0);
                    Collections.reverse(data);
                }
                if (list.isEmpty()) {
                    List<String> msgIDs = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        if (!data.get(i).isRead()) {
                            msgIDs.add(data.get(i).getClientMsgID());
                            data.get(i).setRead(true);
                        }
                    }

                    // FOR SINGLE CHAT - markC2CMessageAsRead
                    OpenIMClient.getInstance().messageManager.markC2CMessageAsRead(new OnBase<String>() {
                        @Override
                        public void onError(int code, String error) {
                            System.out.println("Mark Read Failure LOAD" + code + " " + error);
                            for (int i = 0; i < data.size(); i++) {

                                // hide the read receipt when we are not friend
                                if (code == 201) {
                                    data.get(i).setRead(true);
                                } else {
                                    data.get(i).setRead(false);
                                }
//                                messages.postValue(data);

                            }
                        }

                        @Override
                        public void onSuccess(String success) {
                            System.out.println("Mark Read Success LOAD" + success);
                            for (int i = 0; i < data.size(); i++) {
                                data.get(i).setRead(true);
//                                messages.postValue(data);
                            }
                        }
                    }, otherSideID, msgIDs);

                    // FOR GROUP CHAT - markGroupMessageAsRead
                    if(!isSingleChat) {
                        OpenIMClient.getInstance().messageManager.markGroupMessageAsRead(new OnBase<String>() {
                            @Override
                            public void onError(int code, String error) {
                                Log.d("markGroupMessageAsRead", " onError " + code + " error :" + error);
                                System.out.println("Mark Read Failure LOAD" + code + " " +error) ;
                                for(int i = 0; i < data.size(); i++){

                                    // hide the read receipt when we are not friend
                                    if(code == 201){
                                        data.get(i).setRead(true);
                                    }else{
                                        data.get(i).setRead(false);
                                    }
//                                messages.postValue(data);

                                }
                            }

                            @Override
                            public void onSuccess(String success) {
                                Log.d("markGroupMessageHasRead", " onSuccess ");
                                System.out.println("Mark Read Success LOAD" + success);
                                for(int i = 0; i < data.size(); i++){
                                    data.get(i).setRead(true);
//                                messages.postValue(data);
                                }
                            }
                        }, groupID, msgIDs);
                    }

                    msgIDs.clear();
                    // FOR SINGLE CHAT - markMessageAsReadByConID
                    OpenIMClient.getInstance().messageManager.markMessageAsReadByConID(new OnBase<String>() {
                        @Override
                        public void onError(int code, String error) {
                            System.out.println("Mark Read Failure LOAD" + code + " " + error);
                            for (int i = 0; i < data.size(); i++) {
                                if (code == 201) {
                                    data.get(i).setRead(true);
                                } else {
                                    data.get(i).setRead(false);
                                }
                            }
                        }

                        @Override
                        public void onSuccess(String success) {
                            System.out.println("Mark Read Success LOAD" + success);
                            for (int i = 0; i < data.size(); i++) {
                                data.get(i).setRead(true);
                            }
                        }
                    }, conversationID, msgIDs);

                    // FOR GROUP CHAT - markGroupMessageAsRead
                    if(!isSingleChat) {
                        OpenIMClient.getInstance().messageManager.markGroupMessageAsRead(new OnBase<String>() {
                            @Override
                            public void onError(int code, String error) {
                                Log.d("markGroupMessageAsRead", " onError " + code + " error :" + error);
                                System.out.println("Mark Read Failure LOAD" + code + " " +error) ;
                                for(int i = 0; i < data.size(); i++){
                                    if(code == 201){
                                        data.get(i).setRead(true);
                                    }else{
                                        data.get(i).setRead(false);
                                    }
                                }
                            }

                            @Override
                            public void onSuccess(String success) {
                                Log.d("markGroupMessageAsRead", " onSuccess ");
                                System.out.println("Mark Read Success LOAD" + success);
                                for(int i = 0; i < data.size(); i++){
                                    data.get(i).setRead(true);
                                }
                            }
                        }, groupID, msgIDs);
                    }

                    // FOR GROUP CHAT - markGroupMessageHasRead
                    if(!isSingleChat) {
                        OpenIMClient.getInstance().conversationManager.markGroupMessageHasRead(new OnBase<String>() {
                            @Override
                            public void onError(int code, String error) {
                                Log.d("markGroupMessageHasRead", " onError " + code + " error :" + error);
                            }

                            @Override
                            public void onSuccess(String data) {
                                Log.d("markGroupMessageHasRead", " onSuccess ");
                            }
                        }, groupID);
                    }

                }
                // for adding date to messages list
                if(!messages.getValue().isEmpty()){
                    // remove date from last message after loading new messages
                    checkDateAfterLoading(data);
                }
                for(int i = 0 ; i < data.size() ;i++){
                    // add data message
                    if(data.get(i).getContentType() == Constant.MsgType.Date)
                        continue;
                    checkDate(data,i);
                }
                newMessages.setValue(data);
                removeLoading(list);
                list.addAll(data);
                IMUtil.calChatTimeInterval(list);
                list.add(loading);
//                System.out.println("list size " + data.size() + " " + messages.getValue().size());
                messages.postValue(list);
//                messageAdapter.notifyItemRangeChanged(list.size() - 1 - data.size(), list.size() - 1);
            }

        }, otherSideID, groupID, conversationID, startMsg, count);
    }
    public void checkDateAfterLoading(List<Message> data){
        int i = 0 ;
        int idxMainList = messages.getValue().size()-2 ;
        String lastMSGYear = new SimpleDateFormat("yyyy").format(new Date(messages.getValue().get(idxMainList).getSendTime()));
        String currMSGYear = new SimpleDateFormat("yyyy").format(new Date(data.get(i).getSendTime()));
        String format = "dd/MM/yyyy";
        if(lastMSGYear.equals(currMSGYear)){
            format = "dd/MM";
        }
        String currMSGDate = messages.getValue().get(idxMainList).getContent();
        String prevMSGDate = new SimpleDateFormat(format).format(new Date(data.get(i).getSendTime()));
        if(currMSGDate.equals(prevMSGDate)){
            messages.getValue().remove(idxMainList);
            messageAdapter.notifyItemRemoved(idxMainList);        }

    }

    // check date for two msgs
    public void checkDate(List<Message> data , int i){
        // adding message to first item after sending a message or reciveing new message
        if(i == -1){
            if(data.isEmpty()){
                // handling sending message after clear the data
                String format = "MM/dd";
                String currMSGDate = new SimpleDateFormat(format).format(new Date(System.currentTimeMillis()));
                Message message = new Message();
                message.setContent(currMSGDate);
                message.setSendID("123");
                message.setSendTime(System.currentTimeMillis());
                message.setContentType(Constant.MsgType.Date);
                messages.getValue().add(0,message);
                messageAdapter.notifyItemInserted(0);
                return;
            }
            String nowMSGDate = new SimpleDateFormat("yyyy").format(System.currentTimeMillis());
            String currMSGYear = new SimpleDateFormat("yyyy").format(new Date(data.get(0).getSendTime()));
            String format = "dd/MM/yyyy";
            if(nowMSGDate.equals(currMSGYear)){
                format = "MM/dd";
            }

            String currMSGDate = new SimpleDateFormat(format).format(new Date(System.currentTimeMillis()));
            String prevMSGDate = new SimpleDateFormat(format).format(new Date(data.get(0).getSendTime()));
            if(! currMSGDate.equals(prevMSGDate)){

                Message message = new Message();
                message.setContent(currMSGDate);
                message.setSendID("123");
                message.setSendTime(System.currentTimeMillis());
                message.setContentType(Constant.MsgType.Date);
                messages.getValue().add(0,message);
                messageAdapter.notifyItemInserted(0);
            }
            return;
        }
        if(i != data.size()-1){
            String nowMSGDate = new SimpleDateFormat("yyyy").format(System.currentTimeMillis());
            String currMSGYear = new SimpleDateFormat("yyyy").format(new Date(data.get(i).getSendTime()));
            String format = "dd/MM/yyyy";
            if(nowMSGDate.equals(currMSGYear)){
                format = "MM/dd";
            }

            String currMSGDate = new SimpleDateFormat(format).format(new Date(data.get(i).getSendTime()));
            String prevMSGDate = new SimpleDateFormat(format).format(new Date(data.get(i+1).getSendTime()));
            if(! currMSGDate.equals(prevMSGDate)){

                Message message = new Message();
                message.setContent(currMSGDate);
                message.setSendID("123");
                message.setSendTime(System.currentTimeMillis());
                message.setContentType(Constant.MsgType.Date);
                data.add(i+1,message);
            }

        }else{
            String nowMSGDate = new SimpleDateFormat("yyyy").format(System.currentTimeMillis());
            String currMSGYear = new SimpleDateFormat("yyyy").format(new Date(data.get(i).getSendTime()));
            String format = "dd/MM/yyyy";
            if(nowMSGDate.equals(currMSGYear)){
                format = "MM/dd";
            }
            String currMSGDate = new SimpleDateFormat(format).format(new Date(data.get(i).getSendTime()));
            Message message = new Message();
            message.setContent(currMSGDate);
            message.setSendID("123");
            message.setSendTime(System.currentTimeMillis());
            message.setContentType(Constant.MsgType.Date);
            data.add(i+1,message);
        }
    }
    //移除加载视图
    private void removeLoading(List<Message> list) {
        int index = list.indexOf(loading);
        if (index > -1) {
            list.remove(index);
            messageAdapter.notifyItemRemoved(index);
        }
    }

    //发送消息已读回执
    public void sendMsgReadReceipt(int firstVisiblePosition, int lastVisiblePosition) {
        int size = messages.getValue().size();
        if (size > firstVisiblePosition || size < lastVisiblePosition) return;

        List<Message> megs = messages.getValue().subList(firstVisiblePosition, lastVisiblePosition);
        List<String> msgIds = new ArrayList<>();
        for (Message meg : megs) {
            if (!meg.isRead() && meg.getSendID().equals(otherSideID))
                msgIds.add(meg.getClientMsgID());
        }
        OpenIMClient.getInstance().messageManager.markC2CMessageAsRead(null, otherSideID, msgIds);
    }

    private Runnable typRunnable = () -> {
        typing.set(false);
    };

    @Override
    public void onRecvNewMessage(Message msg) {
        System.out.println("onRecvNewMessage" + msg.getContent() +" "+ msg.getContentType()+ " " + this.otherSideID+ " " +msg.getSendID());
        if (isSingleChat) {
            if (TextUtils.isEmpty(msg.getSendID()) || (!msg.getSendID().equals(this.otherSideID) && !msg.getSendID().equals(LoginCertificate.getCache(this.getContext()).userID)) || msg.getGroupID() != null) return;
        } else {
            System.out.println("onRecvNewMessage" + msg.getContent() +" "+ msg.getContentType()+ " " + this.groupID+ " " +msg.getGroupID());
            if (TextUtils.isEmpty(msg.getGroupID()) || !msg.getGroupID().equals(groupID)) return;
            List<String> ids = new ArrayList<>();
            ids.add(msg.getClientMsgID());
            markReaded(ids);
        }
        if(msg.getContentType() == REVOKE)
            return;

        System.out.println("onRecvNewMessage" + msg.getContent());
        System.out.println("onRecvNewMessage" + msg.getContent());

        boolean isTyp = msg.getContentType() == Constant.MsgType.TYPING;
        if (isSingleChat) {
            if (msg.getSendID().equals(otherSideID)) {
                UIHandler.post(() -> typing.set(isTyp));
                if (isTyp) {
                    UIHandler.removeCallbacks(typRunnable);
                    UIHandler.postDelayed(typRunnable, 5000);
                }
            }
        }
        if (!isTyp) {
            try {

                if (msg.getContentType() == Constant.MsgType.GROUP_MUTED_NOTIFICATION) {
                    JSONObject jsonDetail = new JSONObject((new JSONObject(msg.getContent())).getString("jsonDetail"));
                    String opUserId = new JSONObject(jsonDetail.getString("opUser")).getString("userID");
                    if (opUserId.equalsIgnoreCase(OWN_ID) || adminUIdList.getValue().contains(OWN_ID)) {
                        this.banUpdate.postValue(0);
                    } else {
                        this.banUpdate.postValue(Constant.MsgType.GROUP_MUTED_NOTIFICATION);
                    }

                } else if (msg.getContentType() == Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION) {
                    JSONObject jsonDetail = new JSONObject((new JSONObject(msg.getContent())).getString("jsonDetail"));
                    String opUserId = new JSONObject(jsonDetail.getString("opUser")).getString("userID");
                    if (opUserId.equalsIgnoreCase(OWN_ID)) {
                        this.banUpdate.postValue(0);
                    } else {
                        this.banUpdate.postValue(Constant.MsgType.GROUP_CANCEL_MUTED_NOTIFICATION);
                    }
                } else if (msg.getContentType() == Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION) {
                    JSONObject jsonDetail = new JSONObject((new JSONObject(msg.getContent())).getString("jsonDetail"));
                    String uid = new JSONObject(jsonDetail.getString("mutedUser")).getString("userID");
                    if (uid.equalsIgnoreCase(OWN_ID))
                        this.groupMemberMutedIsYou.postValue(true);
                    //this.memberBanUpdate.postValue(Constant.MsgType.GROUP_MEMBER_MUTED_NOTIFICATION);
                } else if (msg.getContentType() == Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION) {
                    JSONObject jsonDetail = new JSONObject((new JSONObject(msg.getContent())).getString("jsonDetail"));
                    String uid = new JSONObject(jsonDetail.getString("mutedUser")).getString("userID");
                    if (uid.equalsIgnoreCase(OWN_ID))
                        groupMemberUNMutedIsYou.postValue(true);
                    //this.memberBanUpdate.postValue(Constant.MsgType.GROUP_MEMBER_CANCEL_MUTED_NOTIFICATION);
                }

                if(msg.getContentType()==MEMBER_INVITED_NOTIFICATION || msg.getContentType()==MEMBER_KICKED_NOTIFICATION
                ||msg.getContentType()==MEMBER_ENTER_NOTIFICATION ||msg.getContentType()==MEMBER_QUIT_NOTIFICATION){
                    //when in chat, if user is added or removed the atMention list needs to be updated
                    getGroupMemberList(groupID);
                }
//                //HANDLING OF INPUTBOX VISIBILITY

//                else if(msg.getContentType()==MEMBER_KICKED_NOTIFICATION){
//                    try {
//                        JSONObject messageContentJSONObject = new JSONObject(msg.getContent());
//                        JSONObject jsonObject = new JSONObject(messageContentJSONObject.getString("jsonDetail"));
//                        JSONArray jsonArr = new JSONArray(jsonObject.getString("kickedUserList"));
//                        // vm.youAreRemoved.setValue(false);
//                        for (int i = 0; i < jsonArr.length(); i++) {
//                            UserModel userModel=new UserModel();
//                            userModel.userID=jsonArr.getJSONObject(i).getString("userID");
//                            if (userModel.userID.equalsIgnoreCase(OWN_ID)) {
//                                this.groupMemberMutedIsYou.postValue(true);
//                                break;
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }else if(msg.getContentType()==MEMBER_INVITED_NOTIFICATION){
//                    try {
//                        JSONObject messageContentJSONObject = new JSONObject(msg.getContent());
//                        JSONObject jsonObject = new JSONObject(messageContentJSONObject.getString("jsonDetail"));
//                        JSONArray jsonArr = new JSONArray(jsonObject.getString("invitedUserList"));
//                        // vm.youAreRemoved.setValue(false);
//                        for (int i = 0; i < jsonArr.length(); i++) {
//                            UserModel userModel=new UserModel();
//                            userModel.userID=jsonArr.getJSONObject(i).getString("userID");
//                            if (userModel.userID.equalsIgnoreCase(OWN_ID)) {
//                                this.groupMemberMutedIsYou.postValue(true);
//                                break;
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }


                // end
            } catch (JSONException e) {
                e.printStackTrace();
            }

            messages.getValue().add(0, msg);
            UIHandler.post(() -> {
                System.out.println("Step 2 "+messages.getValue().size());
                checkDate(messages.getValue(),-1);
                if(messages.getValue().size() == 1){
                    List<Message> list = new LinkedList<>();
                    list.add(msg);
                    IMUtil.calChatTimeInterval(list);
                    startMsg = msg;
                    messages.postValue(list);
                    messages.getValue().add(msg);
                    System.out.println("list size : "+ list.size());
                    messageAdapter.notifyItemInserted(0);
                }else
                    messageAdapter.notifyItemInserted(0);
                IView.scrollToPosition(0);
//                checkDate(messages.getValue() , 0);


            });

            //清除列表小红点
//            markReaded(null);
            //标记本条消息已读
            if (isSingleChat) {
                List<String> ids = new ArrayList<>();
                ids.add(msg.getClientMsgID());
                markReaded(ids);
            }
        }
    }

    @Override
    public void onRecvC2CReadReceipt(List<ReadReceiptInfo> list) {
        try {
            for(int i = 0; i < list.get(0).getMsgIDList().size(); i++ ){
                messages.getValue().get(i).setRead(true);
            }
            messageAdapter.notifyItemRangeChanged(0, list.get(0).getMsgIDList().size());
        } catch (Exception e){
            e.printStackTrace();
        }

//        for(int i = 0; i < messages.getValue().size(); i++ ){
//            for(int j = 0; j < list.size(); j++) {
//                for(int k = 0; k < list.get(j).getMsgIDList().size(); k++){
//                    try {
//                        if (messages.getValue().get(i).getClientMsgID().equalsIgnoreCase(list.get(j).getMsgIDList().get(k))) {
//                            messages.getValue().get(i).setRead(true);
//                            messageAdapter.notifyItemChanged(i);
////                            messageAdapter.notifyDataSetChanged();
//                        }
//                    } catch (Exception e) {
//                        System.out.println("read message receiver");
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }

//        IView.scrollToPosition(0);
    }

    @Override
    public void onRecvGroupMessageReadReceipt(List<ReadReceiptInfo> list) {
        System.out.println("onRecvC2CReadReceipt" + list.get(0).getMsgIDList().size());
        for(int i = 0; i < messages.getValue().size(); i++ ){
            for(int j = 0; j < list.size(); j++) {
                for(int k = 0; k < list.get(j).getMsgIDList().size(); k++){
                    try {
                        if (messages.getValue().get(i).getClientMsgID().equalsIgnoreCase(list.get(j).getMsgIDList().get(k))) {
                            messages.getValue().get(i).setRead(true);
                            int readCount = messages.getValue().get(i).getAttachedInfoElem().getGroupHasReadInfo().getGroupMemberCount() -
                                messages.getValue().get(i).getAttachedInfoElem().getGroupHasReadInfo().getHasReadCount() - 1 ;
                            messages.getValue().get(i).getAttachedInfoElem().getGroupHasReadInfo().setGroupMemberCount(readCount);
                            messageAdapter.notifyItemRangeChanged(0, list.get(0).getMsgIDList().size());
                        }
                    } catch (Exception e) {
                        System.out.println("read message receiver");
                        e.printStackTrace();
                    }
                }
            }
        }
//        try {
//            for(int i = 0; i < list.get(0).getMsgIDList().size(); i++ ){
//                messages.getValue().get(i).setRead(true);
//                int increaseUnreadCount = messages.getValue().get(i).getAttachedInfoElem().getGroupHasReadInfo().getHasReadCount() + 1;
//                messages.getValue().get(i).getAttachedInfoElem().getGroupHasReadInfo().setGroupMemberCount(increaseUnreadCount);
//            }
//            messageAdapter.notifyItemRangeChanged(0, list.get(0).getMsgIDList().size());
//        } catch (Exception e){
//            System.out.println("natnalkj");
//            e.printStackTrace();
//        }
    }

    @Override
    public void onRecvMessageRevoked(String msgId) {
        try{
            System.out.println("onRecvMessageRevoked");
            for(int i = 0 ; i < messages.getValue().size();i++){
                Message message = messages.getValue().get(i);
                if(message.getClientMsgID()!= null && message.getClientMsgID().equals(msgId)){
                    System.out.println("Message revoked: " + message.getContent());
                    messageAdapter.getMessages().get(i).setContentType(REVOKE);
                    messageAdapter.notifyItemChanged(i);
//                    messageAdapter.getMessages().remove(i);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onRecvMessageRevokedV2(RevokedInfo info) {

    }

    public void revokeMessage(Message msg) {
        int ixd = messages.getValue().indexOf(msg);

        int finalIxd = ixd;
        System.out.println("msg : " + msg.getContent() + " " + msg.getSessionType() + " " + msg.getGroupID() + " " + msg.getRecvID());
        OpenIMClient.getInstance().messageManager.revokeMessage(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                L.e("onError in revoking msg function code " + code + "  error :" + error);
            }

            @Override
            public void onSuccess(String data) {
                msg.setContentType(111);
                int i = messages.getValue().indexOf(msg);
                messageAdapter.getMessages().get(i).setContentType(111);
                messageAdapter.notifyItemChanged(i);
//                messages.getValue().remove(finalIxd);
//                messageAdapter.notifyItemRemoved(finalIxd);

                L.e("onSuccess msg revoked successfully");
            }
        }, msg);

    }

    public void deleteMessage(Message msg) {
        deleteMessage(msg, null);
    }
    public void deleteMessage(Message msg, OnBase<String> onBase) {
        int ixd = messages.getValue().indexOf(msg);

        int finalIxd = ixd;
        OpenIMClient.getInstance().messageManager.deleteMessageFromLocalAndSvr(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                if (onBase != null)
                    onBase.onError(code, error);
                L.e("onError in deleting msg function code " + code + "  error :" + error);
            }

            @Override
            public void onSuccess(String data) {
                try {
                    if (ixd >= 0 && ixd < messages.getValue().size()) {
                        // remove data from
                        if (( ixd == 0 || messages.getValue().get(ixd - 1).getContentType() == Constant.MsgType.Date) &&
                            messages.getValue().get(ixd + 1).getContentType() == Constant.MsgType.Date) {
                            messages.getValue().remove(ixd + 1);
                            messageAdapter.notifyItemRemoved(ixd + 1);
                        }
                        messages.getValue().remove(finalIxd);
                        messageAdapter.notifyItemRemoved(finalIxd);
                        L.e("onSuccess msg deleted successfully");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (onBase != null)
                    onBase.onSuccess(data);
            }
        }, msg);
    }

    public Message createMergerMessage(String title) {
        List<String> summaryList = new LinkedList<>();
        for (Message msg : selectedMessages.getValue()) {
            String summaryMsg;
            String nickname = msg.getSenderNickname() ;
            if(nickname.length()>12){
                nickname = msg.getSenderNickname().substring(0,12)+"...";
            }
            switch (msg.getContentType()) {
                case Constant.MsgType.TXT:
                    summaryMsg = nickname + ": " + msg.getContent().substring(0, Math.min(25, msg.getContent().length())) + (((25 <= msg.getContent().length())) ? "..." : "");
                    break;
                case Constant.MsgType.PICTURE:
                    summaryMsg = nickname + ": sent an image";
                    break;
                case Constant.MsgType.VIDEO:
                    summaryMsg = nickname + ": sent a video";
                    break;
                case Constant.MsgType.FILE:
                    summaryMsg = nickname + ": sent a file";
                    break;
                default:
                    summaryMsg = "Unsupported type...";

            }
            System.out.println(summaryMsg);
            summaryList.add(summaryMsg);
        }
        return OpenIMClient.getInstance().messageManager.createMergerMessage(selectedMessages.getValue(), title, summaryList);
    }

    public void deleteMessages() {
        for (Message msg : selectedMessages.getValue()) {

            OpenIMClient.getInstance().messageManager.deleteMessageFromLocalAndSvr(new OnBase<String>() {
                @Override
                public void onError(int code, String error) {
                    L.e("onError in deleting msg function code " + code + "  error :" + error);
                }

                @Override
                public void onSuccess(String data) {

                    L.e("onSuccess msg deleted successfully");
                }
            }, msg);
            System.out.println("you are deleting this message :  " + msg.getContent());
        }
        for (Message msg : selectedMessages.getValue()) {
            int ixd = messages.getValue().indexOf(msg);
            messages.getValue().remove(ixd);
            messageAdapter.notifyItemRemoved(ixd);
        }
        selectedMessages.setValue(new LinkedList<>());
//        messageAdapter.notifyDataSetChanged();

    }

    public Message createReplyMessage(Message msg, String newMsg) {
        return OpenIMClient.getInstance().messageManager.createQuoteMessage(newMsg, msg);
    }


    public void sendMsg(Message msg) {
        checkDate(messages.getValue(),-1);
        messages.getValue().add(0, msg);
        if(messages.getValue().size() == 1) {
            List<Message> list = new LinkedList<>();
            list.add(msg);
            IMUtil.calChatTimeInterval(list);
            startMsg = msg;
            messages.postValue(list);
            messages.getValue().add(msg);
            messageAdapter.notifyItemInserted(0);
        }else
            messageAdapter.notifyItemInserted(0);

        IView.scrollToPosition(0);

        List<Message> megs = messages.getValue();
        OfflinePushInfo offlinePushInfo = new OfflinePushInfo();  // 离线推送的消息备注；不为null
        //message notification //
       /* String titleMsg;
        String nickname = msg.getSenderNickname() ;
        if(nickname.length()>12){
            nickname = msg.getSenderNickname().substring(0,12)+"...";
        }
        switch (msg.getContentType()) {
            case Constant.MsgType.TXT:
                titleMsg = nickname + ": " + msg.getContent().substring(0, Math.min(25, msg.getContent().length())) + (((25 <= msg.getContent().length())) ? "..." : "");
                break;
            case Constant.MsgType.PICTURE:
                titleMsg = nickname + ": sent an image";
                break;
            case Constant.MsgType.VIDEO:
                titleMsg = nickname + ": sent a video";
                break;
            case Constant.MsgType.FILE:
                titleMsg = nickname + ": sent a file";
                break;
            default:
                titleMsg = msg.getContent();
        }*/
        ////
        offlinePushInfo.setTitle(msg.getContent());
        OpenIMClient.getInstance().messageManager.sendMessage(new OnMsgSendCallback() {
            @Override
            public void onError(int code, String error) {
                L.e("onError in sendMsg function");
                int index = megs.indexOf(msg);
                msg.setStatus(3);
                megs.add(index, msg);
                megs.remove(index + 1);
                System.out.println("msg status in error : " + megs.get(index).getContent() + megs.get(index).getStatus() + " " + index );
                messageAdapter.notifyItemChanged(index);
//                IView.scrollToPosition(0);
            }

            @Override
            public void onProgress(long progress) {
                L.e("onProgress in sendMsg function");
            }

            @Override
            public void onSuccess(Message message) {
                // 返回新的消息体；替换发送传入的，不然扯回消息会有bug
                int index = megs.indexOf(msg);
                L.e("onSuccess in sendMsg function idx "+index);
                message.setStatus(2);
                msg.setStatus(2);
                megs.add(index, message);
                megs.remove(index + 1);
                messageAdapter.notifyItemChanged(index);
                System.out.println("message.setStatus0 : " + megs.get(0).getStatus() + " "+ megs.get(0).getContentType()+ " " + megs.get(0).getContent());
                System.out.println("message.setStatus1 : " + megs.get(1).getStatus() + " "+ megs.get(1).getContentType()+ " " + megs.get(1).getContent());
//                IView.scrollToPosition(0);

            }
        }, msg, otherSideID, groupID, offlinePushInfo);

    }

    public void addForwardMessage(Message msg) {
        messages.getValue().add(0, msg);
        if(messages.getValue().size() == 1) {
            List<Message> list = new LinkedList<>();
            list.add(msg);
            IMUtil.calChatTimeInterval(list);
            startMsg = msg;
            messages.postValue(list);
            messages.getValue().add(msg);
            System.out.println("list size : " + list.size());
            messageAdapter.notifyItemInserted(0);
        }else
            messageAdapter.notifyItemInserted(0);

        IView.scrollToPosition(0);
    }

    public interface ViewAction extends IView {
        void scrollToPosition(int position);
    }


    //Function for getting the owner id needed in GroupChatSettingsActivity
    public void getGroupInfo(String groupID) {
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        groupIds.add(groupID);
        OpenIMClient.getInstance().groupManager.getGroupsInfo(new OnBase<List<GroupInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupInfo> data) {
                groupsInfo.setValue(data);
            }
        }, groupIds);
    }


    public void getGroupMemberList(String groupID) {
        List<String> groupIds = new ArrayList<>(); // 群ID集合
        //groupIds.add(searchContent);
        OpenIMClient.getInstance().groupManager.getGroupMemberList(new OnBase<List<GroupMembersInfo>>() {
            @Override
            public void onError(int code, String error) {
                userInfoList.setValue(null);
                System.out.println("userInfoListcode : " + code + "error : " + error);
            }

            @Override
            public void onSuccess(List<GroupMembersInfo> data) {
                System.out.println("userInfoList It's done successfully ");
                userInfoList.setValue(data);
            }
        }, groupID, 0, 0, 10000);
    }
}
