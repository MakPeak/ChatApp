package io.bytechat.demo.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.bytechat.demo.R;
import io.bytechat.demo.ui.view.ViewSelectAvatar;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {

    private List<Integer> avatars;
    ViewSelectAvatar sView ;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            avatar =  view.findViewById(R.id.avatar_icon);
        }
    }

    public AvatarAdapter(List<Integer> dataSet, ViewSelectAvatar view ) {
        avatars = dataSet;
        this.sView = view ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.avatar_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.avatar.setImageResource(avatars.get(position));
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("onBindViewHolder: ","1111 " + avatars.get(position));
                sView.avatarSelected(avatars.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatars.size();
    }
}
