package io.openim.android.ouiconversation.ui.chathistory;

import static io.openim.android.sdk.enums.MessageType.PICTURE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.databinding.FragmentPicturesBinding;
import io.openim.android.ouiconversation.databinding.PictureItemLayoutBinding;
import io.openim.android.ouiconversation.vm.ChatHistoryVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;

public class PicturesFragment extends BaseFragment<ChatHistoryVM> implements ChatHistoryVM.ViewAction {
    FragmentPicturesBinding binding ;
    String chatID ;
    RecyclerViewAdapter<Message, PicturesFragment.RecyclerViewItem> adapter ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(ChatHistoryVM.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new RecyclerViewAdapter<Message, PicturesFragment.RecyclerViewItem>(PicturesFragment.RecyclerViewItem.class) {
            @Override
            public void onBindView(@NonNull PicturesFragment.RecyclerViewItem holder, Message data, int position) {
                holder.v.imageView.load(data.getPictureElem().getSourcePicture().getUrl());
                holder.v.imageView.setBackground(null);
            }
        };
        List<Message> list = new LinkedList<>();
        adapter.setItems(list);
        binding.recyclerView.setAdapter(adapter);
        vm.searchMessage(PICTURE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPicturesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onSuccess(SearchResult body) {
        List<Message> list = new LinkedList<>();
        for (Message o : body.getSearchResultItems().get(0).getMessageList()){
            list.add(o);
        }
        adapter.setItems(list);
    }

    @Override
    public void onEmpty() {

    }

    public static class RecyclerViewItem extends RecyclerView.ViewHolder {
        public PictureItemLayoutBinding v;

        public RecyclerViewItem(@NonNull View parent) {
            super((PictureItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = PictureItemLayoutBinding.bind(this.itemView);

        }
    }
}
