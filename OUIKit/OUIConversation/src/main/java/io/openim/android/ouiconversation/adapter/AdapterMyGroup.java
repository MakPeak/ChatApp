package io.openim.android.ouiconversation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.vm.ForwardMessageVM;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.Message;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class AdapterMyGroup extends RecyclerView.Adapter<AdapterMyGroup.ViewHolder> {

    private LayoutInflater mInflater;
    Context context;
    ForwardMessageVM vm ;
    Message msg;
    List<GroupInfo> groupInfoList = new ArrayList<>();

    public void setGroupInfoList(List<GroupInfo> groupInfoList) {
        this.groupInfoList = groupInfoList;
    }

    // data is passed into the constructor
    public AdapterMyGroup(Context context,ForwardMessageVM vm,Message msg) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.vm = vm ;
        this.msg = msg ;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_mygroup_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvUsername.setText(groupInfoList.get(position).getGroupName());
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.send.startAnimation();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.send.revertAnimation(new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                holder.send.setText("Sent");
                                holder.send.setEnabled(false);
                                holder.send.setBackgroundColor(Color.GRAY);
                                return null;
                            }
                        });

                    }
                }, 1000);


                vm.groupID = groupInfoList.get(position).getGroupID() ;
                vm.otherSideID = null;
                if(msg.getMergeElem().getTitle() != null)
                    vm.sendMsg(msg);
                else
                    vm.sendMsg(vm.createForwardMessage(msg));
            }
        });

        if (groupInfoList.get(position).getFaceURL() != null && !groupInfoList.get(position).getFaceURL().isEmpty())
            Glide.with(context)
                .load(groupInfoList.get(position).getFaceURL())
                .into(holder.ivUserImage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (groupInfoList != null) {
            return groupInfoList.size();
        }
        return 0;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        ImageView ivUserImage;
        CircularProgressButton send;

        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user);
            ivUserImage = itemView.findViewById(R.id.iv_user);
            send = itemView.findViewById(R.id.send);

        }

    }

}
