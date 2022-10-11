package io.bytechat.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.R;
import io.openim.android.sdk.models.Message;

public class SearchChatHistoryAdapter extends RecyclerView.Adapter<SearchChatHistoryAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<Message> messageList = new ArrayList<>();
    ItemClickListener mClickListener;

    // data is passed into the constructor
    public SearchChatHistoryAdapter(Context context, List<Message> messageList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.messageList = messageList;
        this.mClickListener = mClickListener;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_chat_history, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvUsername.setText(messageList.get(position).getSenderNickname());
        holder.tvMsg.setText(messageList.get(position).getContent());
        long milliseconds = messageList.get(position).getSendTime();
        SimpleDateFormat DateFor = new SimpleDateFormat("MM-dd");
        String stringDate = DateFor.format(milliseconds);
        holder.tvTime.setText(stringDate);

        Glide.with(context)
            .load(messageList.get(position).getSenderFaceUrl())
            .into(holder.ivUser);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onChatItemClick(messageList.get(position).getSendID(), messageList.get(position).getSenderNickname(), messageList.get(position));
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(messageList != null) {
            return messageList.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername, tvMsg, tvTime;
        ImageView ivUser;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user_name);
            tvMsg = itemView.findViewById(R.id.tv_user_msg);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivUser = itemView.findViewById(R.id.iv_user);

        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onChatItemClick(String fromUserID, String fromNickName, Message startMsg);
    }
}

