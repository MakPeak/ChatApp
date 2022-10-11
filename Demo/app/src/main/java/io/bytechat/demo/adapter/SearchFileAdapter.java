package io.bytechat.demo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.R;
import io.openim.android.sdk.models.Message;

public class SearchFileAdapter extends RecyclerView.Adapter<SearchFileAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    List<Message> messageList = new ArrayList<>();
    ItemClickListener mClickListener;

    // data is passed into the constructor
    public SearchFileAdapter(Context context, List<Message> messageList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.messageList = messageList;
        this.mClickListener = mClickListener;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_file, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

//        String fileName = messageList.get(position).getFileElem().getFileName().replaceAll("%20", " ");
        String fileName = "";
        try {
            fileName = java.net.URLDecoder.decode(messageList.get(position).getFileElem().getFileName(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // not going to happen - value came from JDK's own StandardCharsets
            e.printStackTrace();
        }
        holder.tvUsername.setText(fileName);
        int fileSize = (int) messageList.get(position).getFileElem().getFileSize() / 1000;
        holder.tvMsg.setText(String.valueOf(fileSize) + " KB . " + messageList.get(position).getSenderNickname());
        long milliseconds = messageList.get(position).getSendTime();
        SimpleDateFormat DateFor = new SimpleDateFormat("MM-dd");
        String stringDate = DateFor.format(milliseconds);
        holder.tvTime.setText(stringDate);

        int index = fileName.lastIndexOf('.');
        if(index > 0) {
            String extension = fileName.substring(index + 1);
            System.out.println(fileName + "\t" + extension);

            switch(extension) {
                case "pdf":
                    Glide.with(context)
                        .load(getImage("ic_file_ftp"))
                        .into(holder.ivUser);
                    break;
                case "txt":
                    Glide.with(context)
                        .load(getImage("ic_file_txt"))
                        .into(holder.ivUser);
                    break;
                case "ppt":
                    Glide.with(context)
                        .load(getImage("ic_file_ppt"))
                        .into(holder.ivUser);
                    break;
                case "jpg":
                    Glide.with(context)
                        .load(getImage("ic_file_image"))
                        .into(holder.ivUser);
                    break;
                case "jpeg":
                    Glide.with(context)
                        .load(getImage("ic_file_image"))
                        .into(holder.ivUser);
                    break;
                case "xls":
                    Glide.with(context)
                        .load(getImage("ic_file_excel"))
                        .into(holder.ivUser);
                    break;
                case "xlsx":
                    Glide.with(context)
                        .load(getImage("ic_file_excel"))
                        .into(holder.ivUser);
                    break;
                case "mp4":
                    Glide.with(context)
                        .load(getImage("ic_file_video"))
                        .into(holder.ivUser);
                    break;
                case "mp3":
                    Glide.with(context)
                        .load(getImage("ic_file_music"))
                        .into(holder.ivUser);
                    break;
                case "zip":
                    Glide.with(context)
                        .load(getImage("ic_file_zip"))
                        .into(holder.ivUser);
                    break;
                case "rar":
                    Glide.with(context)
                        .load(getImage("ic_file_zip"))
                        .into(holder.ivUser);
                    break;
                case "docx":
                    Glide.with(context)
                        .load(getImage("ic_file_word"))
                        .centerCrop()
                        .into(holder.ivUser);
                    break;
                default:
                    Glide.with(context)
                        .load(getImage("ic_file_other"))
                        .centerCrop()
                        .into(holder.ivUser);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onFileItemClick(messageList.get(position).getFileElem().getSourceUrl(),
                    messageList.get(position).getFileElem().getFilePath(), messageList.get(position).getFileElem().getFileName());
            }
        });

//        Glide.with(context)
//            .load(messageList.get(position).getSenderFaceUrl())
//            .into(holder.ivUser);
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
        void onFileItemClick(String url, String content, String fileName);
    }

    public int getImage(String imageName) {
        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return drawableResourceId;
    }
}

