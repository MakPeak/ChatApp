package io.openim.android.ouiconversation.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.openim.android.ouiconversation.ui.MediaItemFragment;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.sdk.models.Message;

public class MediaPreviewAdapter extends FragmentStateAdapter {

    private final ArrayList<Message> list = new ArrayList<>();

    public MediaPreviewAdapter(
        @NonNull FragmentManager fragmentManager,
        @NonNull Lifecycle lifecycle
    ) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (list.get(position).getContentType() == Constant.MsgType.PICTURE) {
            return MediaItemFragment.newInstance(
                list.get(position).getPictureElem().getSourcePicture().getUrl(),
                ""
            );
        } else {
            return MediaItemFragment.newInstance(
                list.get(position).getVideoElem().getVideoUrl(),
                list.get(position).getVideoElem().getSnapshotUrl()
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    public Message getItem(int position) {
        return list.get(position);
    }

    public void setItems(List<Message> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
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
}
