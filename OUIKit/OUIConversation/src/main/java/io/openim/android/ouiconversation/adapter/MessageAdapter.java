package io.openim.android.ouiconversation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouicore.base.BaseApp;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.L;
import io.openim.android.sdk.models.Message;

public class MessageAdapter extends RecyclerView.Adapter {

// implements StickyHeaders, StickyHeaders.ViewSetup{
    public boolean isSelecting = false ;
    public int position =-1;
    private RecyclerView recyclerView;
    ChatVM vm ;
    List<Message> messages;
    //自己的userId
    public static String OWN_ID;
    boolean hasStorage;
    public Context context;

    public MessageAdapter(Context context) {
        OWN_ID = LoginCertificate.getCache(BaseApp.instance()).userID;
        hasStorage = AndPermission.hasPermissions(BaseApp.instance(), Permission.Group.STORAGE);
        this.context=context;
    }

    public void setVm(ChatVM vm){
        this.vm = vm ;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getContentType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MessageViewHolder.createViewHolder(parent, viewType,vm);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (getItemViewType(position) != Constant.LOADING) {
            MessageViewHolder.MsgViewHolder msgViewHolder = (MessageViewHolder.MsgViewHolder) holder;
            msgViewHolder.setMessageAdapter(this);
            msgViewHolder.bindData(message, position);
            if (null != recyclerView)
                msgViewHolder.bindRecyclerView(recyclerView);
            //announcementStr=msgViewHolder.getAnnouncementStr();
        }

    }

    @Override
    public int getItemCount() {
        return null == messages ? 0 : messages.size();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void bindRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

//    @Override
//    public boolean isStickyHeader(int position) {
//        if(position %11 ==0)
//            return true;
//        return false;
//    }
//
//    @Override
//    public void setupStickyHeaderView(View stickyHeader) {
//        ViewCompat.setElevation(stickyHeader, 10);
//    }
//
//    @Override
//    public void teardownStickyHeaderView(View stickyHeader) {
//        ViewCompat.setElevation(stickyHeader, 0);
//    }
}
