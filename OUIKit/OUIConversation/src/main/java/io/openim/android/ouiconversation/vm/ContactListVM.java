package io.openim.android.ouiconversation.vm;

import static io.openim.android.ouicore.utils.Common.UIHandler;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.openim.android.ouicore.base.BaseViewModel;
import io.openim.android.ouicore.base.IView;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.entity.MsgConversation;
import io.openim.android.ouicore.im.IMEvent;
import io.openim.android.ouicore.im.IMUtil;
import io.openim.android.ouicore.net.bage.GsonHel;
import io.openim.android.ouicore.utils.L;
import io.openim.android.ouicore.widget.WaitDialog;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnAdvanceMsgListener;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.listener.OnConversationListener;
import io.openim.android.sdk.listener.OnFriendshipListener;
import io.openim.android.sdk.listener.OnGroupListener;
import io.openim.android.sdk.models.BlacklistInfo;
import io.openim.android.sdk.models.ConversationInfo;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupApplicationInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.GroupMembersInfo;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.ReadReceiptInfo;
import io.openim.android.sdk.models.RevokedInfo;
import io.openim.android.sdk.models.UserInfo;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ContactListVM extends BaseViewModel<ContactListVM.ViewAction> implements OnConversationListener, OnAdvanceMsgListener, OnFriendshipListener, OnGroupListener {
    public static MutableLiveData<Boolean> isActivityActive = new MutableLiveData<>(true);
    public MutableLiveData<List<MsgConversation>> conversations = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<String> nickname = new MutableLiveData<>("");
    public MutableLiveData<String> pinConversation = new MutableLiveData<>("");
    public MutableLiveData<String> deleteConversation = new MutableLiveData<>("");
    public MutableLiveData<String> avatar = new MutableLiveData<>("");
    public MutableLiveData<Integer> unreadCounter = new MutableLiveData<>(0);
    public MutableLiveData<Integer> visibility = new MutableLiveData<>(View.INVISIBLE);
    public MutableLiveData<List<GroupApplicationInfo>> groupApply = new MutableLiveData<>();
    public MutableLiveData<List<FriendApplicationInfo>> friendApply = new MutableLiveData<>();
    public MutableLiveData<Integer> counter = new MutableLiveData<>(0);
    private LoginCertificate loginCertificate;
    private final CompositeDisposable compositeDisposable= new CompositeDisposable();

    WaitDialog mainWaitDialog;

    @Override
    protected void viewCreate() {
        IMEvent.getInstance().addConversationListener(this);
        IMEvent.getInstance().addAdvanceMsgListener(this);
        IMEvent.getInstance().addFriendListener(this);
        mainWaitDialog = showWait();
        initLoginAccess();
    }

    private void initLoginAccess() {
        loginCertificate = LoginCertificate.getCache(getContext());
        Disposable disposable = Flowable.create((FlowableOnSubscribe<String>) emitter -> {
            //Do First Api Call Mak
            System.out.println( " Api Call : OpenIMClient Login API");
                OpenIMClient.getInstance().login(new OnBase<String>() {
                    @Override
                    public void onError(int code, String error) {
                        System.out.println("Api Call : onError code : " + code + " error: " + error);
                        mainWaitDialog.dismiss();
                        switch (code) {
                            case 101:
                                break;
                            case 702:
                            case 703:
                                IView.onErr(error, 702);
                                break;
                            default:
                                IView.onErr(error, 400);
                                break;
                        }

                    }

                    @Override
                    public void onSuccess(String data) {
                        System.out.println("onSuccess" + data);
                        L.e("user_token:" + loginCertificate.token);
                        L.e("user id:" + loginCertificate.userID);
                        getLoginStatus();
                        emitter.onNext(loginCertificate.token);

                    }
                }, loginCertificate.userID, loginCertificate.token);
        }, BackpressureStrategy.BUFFER)
            .concatMap(this::_itemsFromNetworkCall)
            .subscribeOn(Schedulers.io())
            .subscribe( result -> {
                System.out.println( " Api Call : Completed Second API response::" +result);
            }, (Consumer<Throwable>) throwable -> {
                System.out.println( " Api Call : Error in Second API ::" +throwable.getLocalizedMessage());
            }, () -> {
                System.out.println( " Api Call : Do Action Second API ::");

            });
        compositeDisposable.add(disposable);

        if (null != loginCertificate.nickname)
            nickname.setValue(loginCertificate.nickname);
        UIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("UIHandler.postDelayed");
                undataConversation();
                getRecvGroupApplicationList();
                getRecvFriendApplicationList();

            }
        }, 2000);
    }

    private Disposable getStatus() {
        return Flowable.interval(5000, TimeUnit.MILLISECONDS)
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .map(o -> {
                System.out.println("Api Call : delay interval started 5 sec:: "+(System.currentTimeMillis()));
                long loginStatus = OpenIMClient.getInstance().getLoginStatus();
                return loginStatus;
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((loginStatus) ->{
                //this method is observed on main thread
                System.out.println("Api Call: Main thread Response loginStatus:: "+loginStatus);
                if (loginStatus!=101){
                    System.out.println("Api Call: Matt: "+loginStatus);
                    IView.onErr("multi_device_login", 702);
                }
            });
    }
    private void getLoginStatus(){
        compositeDisposable.add(getStatus());
    }


    private Flowable<String> _itemsFromNetworkCall(String str) {

        return Flowable.create(emitter -> {
            //Do Second Api Call
            OpenIMClient.getInstance().userInfoManager.getSelfUserInfo(new OnBase<UserInfo>() {
                @Override
                public void onError(int code, String error) {
                    L.e("error :" + code + " " + error);
                    mainWaitDialog.dismiss();
                }
                @Override
                public void onSuccess(UserInfo data) {
                    mainWaitDialog.dismiss();
                    // 返回当前登录用户的资料
                    L.e("onSuccess :" + data.getFaceURL());
                    L.e("onSuccess :" + data.getNickname());
                    loginCertificate.nickname = data.getNickname();
                    loginCertificate.faceURL = data.getFaceURL();
                    loginCertificate.cache(getContext());
                    nickname.setValue(loginCertificate.nickname);
                    avatar.setValue(loginCertificate.faceURL);
                    visibility.setValue(View.VISIBLE);
                    emitter.onNext(data.getFaceURL());
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public void getProfileData() {
//        WaitDialog showWait = showWait();
        OpenIMClient.getInstance().userInfoManager.getSelfUserInfo(new OnBase<UserInfo>() {
            @Override
            public void onError(int code, String error) {
                L.e("error :" + code);
//                showWait.dismiss();
            }

            @Override
            public void onSuccess(UserInfo data) {
                // 返回当前登录用户的资料
//                showWait.dismiss();
                nickname.setValue(data.getNickname());
                avatar.setValue(data.getFaceURL());
            }
        });
    }

    public void getRecvGroupApplicationList() {
        OpenIMClient.getInstance().groupManager.getRecvGroupApplicationList(new OnBase<List<GroupApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onSuccess(List<GroupApplicationInfo> data) {
                if (!data.isEmpty()) {
                    groupApply.postValue(data);
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getHandleResult() == 0) {
                            counter.postValue(counter.getValue() + 1);
                        }
                    }
                }
            }
        });
    }

    public void getRecvFriendApplicationList() {
        OpenIMClient.getInstance().friendshipManager.getRecvFriendApplicationList(new OnBase<List<FriendApplicationInfo>>() {
            @Override
            public void onError(int code, String error) {
                System.out.println("getRecvFriendApplicationList onSuccess :" + code + error);
            }

            @Override
            public void onSuccess(List<FriendApplicationInfo> data) {
                System.out.println("getRecvFriendApplicationList onSuccess :" + data.size());
                if (!data.isEmpty())
                    friendApply.setValue(data);
            }
        });
    }

    @NonNull
    private WaitDialog showWait() {
        WaitDialog waitDialog = new WaitDialog(getContext());
        if (ContactListVM.isActivityActive.getValue()) {
            waitDialog.setNotDismiss();
            waitDialog.show();
        }

        return waitDialog;
    }

    public void undataConversation() {
        System.out.println("Update convs ...");
//        WaitDialog waitDialog = showWait();

        OpenIMClient.getInstance().conversationManager.getAllConversationList(new OnBase<List<ConversationInfo>>() {
            @Override
            public void onError(int code, String error) {
                L.e("------AllConversationList---error--" + code + " " + error);
                conversations.getValue().clear();
//                waitDialog.dismiss();
                IView.onErr(error, code);
            }

            @Override
            public void onSuccess(List<ConversationInfo> data) {
                L.e("------AllConversationList---size--" + data.size());
                conversations.getValue().clear();
                for (ConversationInfo datum : data) {
                    conversations.getValue().add(new MsgConversation(GsonHel.fromJson(datum.getLatestMsg(), Message.class), datum));
                }
                conversations.setValue(conversations.getValue());
//                waitDialog.dismiss();
            }
        });
    }

    public void pinConversation(MsgConversation conversation) {
        OpenIMClient.getInstance().conversationManager.pinConversation(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                Log.e("pinConversation Error", "onError: " + error + " " + code);
            }

            @Override
            public void onSuccess(String data) {
                pinConversation.setValue(data);
                Log.d("pinConversation success", "onSuccess: " + data);

            }
        }, conversation.conversationInfo.getConversationID(), !conversation.conversationInfo.isPinned());
    }

    public void deleteConversation(MsgConversation conversation) {
        System.out.println(" " + conversation.conversationInfo.getConversationID() + " " + conversation.conversationInfo.isPinned());
        OpenIMClient.getInstance().conversationManager.deleteConversationFromLocalAndSvr(new OnBase<String>() {
            @Override
            public void onError(int code, String error) {
                Log.e("deleteConv Error", "onError: " + error + " " + code);
            }

            @Override
            public void onSuccess(String data) {
                Log.d("deleteConv success", "onSuccess: " + conversations.getValue().size());
                conversations.getValue().remove(conversation);
                List<MsgConversation> value = conversations.getValue();
                conversations.setValue(value);
            }
        }, conversation.conversationInfo.getConversationID());
    }

    @Override
    public void onConversationChanged(List<ConversationInfo> list) {
        System.out.println("onConversationChanged");
        undataConversation();
    }

    private void sortConversation(List<ConversationInfo> list) {
        List<MsgConversation> msgConversations = new ArrayList<>();
        for (ConversationInfo info : list) {
            msgConversations.add(new MsgConversation(GsonHel.fromJson(info.getLatestMsg(), Message.class), info));
            Iterator<MsgConversation> iterator = conversations.getValue().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().conversationInfo.getConversationID().equals(info.getConversationID()))
                    iterator.remove();
            }
        }
        conversations.getValue().addAll(msgConversations);
        Collections.sort(conversations.getValue(), IMUtil.simpleComparator());
        conversations.setValue(conversations.getValue());
    }

    @Override
    public void onNewConversation(List<ConversationInfo> list) {
        sortConversation(list);
    }

    @Override
    public void onSyncServerFailed() {

    }

    @Override
    public void onSyncServerFinish() {

    }

    @Override
    public void onSyncServerStart() {

    }

    @Override
    public void onTotalUnreadMessageCountChanged(int i) {
        L.e("onTotalUnreadMessageCountChanged");
    }

    @Override
    public void onRecvNewMessage(Message msg) {

    }

    @Override
    public void onRecvC2CReadReceipt(List<ReadReceiptInfo> list) {

    }

    @Override
    public void onRecvGroupMessageReadReceipt(List<ReadReceiptInfo> list) {

    }

    @Override
    public void onRecvMessageRevoked(String msgId) {

    }

    @Override
    public void onRecvMessageRevokedV2(RevokedInfo info) {

    }


    @Override
    public void onBlacklistAdded(BlacklistInfo u) {

    }

    @Override
    public void onBlacklistDeleted(BlacklistInfo u) {

    }

    @Override
    public void onFriendApplicationAccepted(FriendApplicationInfo u) {
        System.out.println("onFriendApplicationAccepted" + u.getFromNickname());
//        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() - 1);
        getRecvFriendApplicationList();
    }

    @Override
    public void onFriendApplicationAdded(FriendApplicationInfo u) {
        System.out.println("MattFriendApp onFriendApplicationAdded " + u.getFromNickname());
//        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() + 1);
        getRecvFriendApplicationList();
    }

    @Override
    public void onFriendApplicationDeleted(FriendApplicationInfo u) {
        System.out.println("MattFriendApp onFriendApplicationDeleted " + u.getFromNickname());
//        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() - 1);
        getRecvFriendApplicationList();
    }

    @Override
    public void onFriendApplicationRejected(FriendApplicationInfo u) {
        System.out.println("MattFriendApp onFriendApplicationRejected " + u.getFromNickname());
//        authViewModel.friendDotNum.setValue(authViewModel.friendDotNum.getValue() - 1);
        getRecvFriendApplicationList();
    }

    @Override
    public void onFriendInfoChanged(FriendInfo u) {

    }

    @Override
    public void onFriendAdded(FriendInfo u) {
        System.out.println("MattFriendApp onFriendAdded " + u.getNickname());
        getRecvFriendApplicationList();
    }

    @Override
    public void onFriendDeleted(FriendInfo u) {
        System.out.println("MattFriendApp onFriendDeleted " + u.getNickname());
        getRecvFriendApplicationList();
    }

    @Override
    public void onGroupApplicationAccepted(GroupApplicationInfo info) {
        getRecvGroupApplicationList();
    }

    @Override
    public void onGroupApplicationAdded(GroupApplicationInfo info) {
        getRecvGroupApplicationList();
    }

    @Override
    public void onGroupApplicationDeleted(GroupApplicationInfo info) {
        getRecvGroupApplicationList();
    }

    @Override
    public void onGroupApplicationRejected(GroupApplicationInfo info) {
        getRecvGroupApplicationList();
    }

    @Override
    public void onGroupInfoChanged(GroupInfo info) {

    }

    @Override
    public void onGroupMemberAdded(GroupMembersInfo info) {

    }

    @Override
    public void onGroupMemberDeleted(GroupMembersInfo info) {

    }

    @Override
    public void onGroupMemberInfoChanged(GroupMembersInfo info) {

    }

    @Override
    public void onJoinedGroupAdded(GroupInfo info) {
        getRecvGroupApplicationList();
    }

    @Override
    public void onJoinedGroupDeleted(GroupInfo info) {
        getRecvGroupApplicationList();
    }


    public interface ViewAction extends IView {
        void onErr(String msg, int code);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
