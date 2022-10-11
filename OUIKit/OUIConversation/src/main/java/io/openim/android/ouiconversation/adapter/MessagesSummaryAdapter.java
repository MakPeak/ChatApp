package io.openim.android.ouiconversation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.ui.frowardmessage.ChatHistoryMergeMessagesActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.widget.MessageHoldDialog;
import io.openim.android.sdk.models.Message;

public class MessagesSummaryAdapter extends RecyclerView.Adapter<MessagesSummaryAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    Message message ;
    ChatVM vm ;
    List<String> summaryList = new ArrayList<>();
    MessageHoldDialog dialog;
    View itemView;

    public void setSummaryList(List<String> groupInfoList) {
        this.summaryList = groupInfoList;
    }

    // data is passed into the constructor
    public MessagesSummaryAdapter(Context context, ChatVM vm, Message message, MessageHoldDialog dialog, View itemView) {
        this.mInflater = LayoutInflater.from(context);
        this.message = message ;
        this.vm = vm ; 
        this.context = context;
        this.dialog = dialog;
        this.itemView = itemView;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.merge_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message msg = message.getMergeElem().getMultiMessage().get(position) ;
        String nickname = msg.getSenderNickname() ;
        if(nickname == null)
            nickname = "";
        if(nickname.length()>12){
            nickname = msg.getSenderNickname().substring(0,12)+"...";
        }
        String summaryMsg ="" ;
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
            case Constant.MsgType.VOICE:
                summaryMsg = nickname + ": sent a voice";
                break;
            case Constant.MsgType.CARD:
                summaryMsg = nickname + ": sent a name card";
                break;
            case Constant.MsgType.FILE:
                summaryMsg = nickname + ": sent a file";
                break;
            default:
                summaryMsg = "Unsupported type...";

        }
        holder.tvSummary.setText(summaryMsg);
        holder.tvSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatHistoryMergeMessagesActivity.messages = message.getMergeElem().getMultiMessage();
                ChatHistoryMergeMessagesActivity.title = message.getMergeElem().getTitle() ;
                vm.getContext().startActivity(new Intent(vm.getContext() , ChatHistoryMergeMessagesActivity.class));
            }
        });
        holder.tvSummary.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog.showDialog(message, Constant.MsgType.MERGE, itemView);
                return true;
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        try {
            return Math.min(5,summaryList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        return 5;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSummary;

        ViewHolder(View itemView) {
            super(itemView);
            tvSummary = itemView.findViewById(R.id.summary);

        }

    }

}
