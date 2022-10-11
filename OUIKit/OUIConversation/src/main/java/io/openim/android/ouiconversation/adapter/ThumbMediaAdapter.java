package io.openim.android.ouiconversation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ItemMediaThumbBinding;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.sdk.models.Message;

public class ThumbMediaAdapter extends RecyclerView.Adapter<ThumbMediaAdapter.ViewHolder> {

    private final ArrayList<Message> list = new ArrayList<>();
    private OnItemClickedListener onItemClickedListener;

    private int selectedPosition = 0;

    @NonNull
    @Override
    public ThumbMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMediaThumbBinding binding = ItemMediaThumbBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbMediaAdapter.ViewHolder holder, int position) {

        holder.itemView.setOnClickListener(v -> onItemClickedListener.onItemClicked(position));

        holder.selectedStroke.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        Message message = list.get(position);
        if (message.getContentType() == Constant.MsgType.PICTURE) {
            holder.playVideoIcon.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                .load(message.getPictureElem().getSourcePicture().getUrl())
                .into(holder.thumbImageView);
        } else {
            holder.playVideoIcon.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                .load(message.getVideoElem().getSnapshotUrl())
                .into(holder.thumbImageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setItems(List<Message> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPosition) {
        int oldSelectedPosition = this.selectedPosition;
        this.selectedPosition = selectedPosition;
        notifyItemChanged(oldSelectedPosition);
        notifyItemChanged(selectedPosition);
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void removeItem(Message message) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(message.getClientMsgID(), list.get(i).getClientMsgID())) {
                list.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbImageView, playVideoIcon, selectedStroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.image);
            playVideoIcon = itemView.findViewById(R.id.iv_play);
            selectedStroke = itemView.findViewById(R.id.selected_stroke);
        }
    }

    public static interface OnItemClickedListener {
        void onItemClicked(int position);
    }
}
