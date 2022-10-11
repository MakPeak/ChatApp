package io.openim.android.ouiconversation.ui.chathistory;

import static io.openim.android.sdk.enums.MessageType.VIDEO;

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

import io.openim.android.ouiconversation.databinding.FragmentVideosBinding;
import io.openim.android.ouiconversation.databinding.VideosItemLayoutBinding;
import io.openim.android.ouiconversation.vm.ChatHistoryVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;


public class VideosFragment extends BaseFragment<ChatHistoryVM> implements ChatHistoryVM.ViewAction {
    FragmentVideosBinding binding ;
    String chatID ;
    RecyclerViewAdapter<Message, VideosFragment.RecyclerViewItem> adapter ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(ChatHistoryVM.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        adapter = new RecyclerViewAdapter<Message, VideosFragment.RecyclerViewItem>(VideosFragment.RecyclerViewItem.class) {
            @Override
            public void onBindView(@NonNull VideosFragment.RecyclerViewItem holder, Message data, int position) {
                holder.v.imageView.load(data.getVideoElem().getSnapshotUrl());
                holder.v.imageView.setBackground(null);
            }
        };
        List<Message> list = new LinkedList<>();
        adapter.setItems(list);
        binding.recyclerView.setAdapter(adapter);
        vm.searchMessage(VIDEO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideosBinding.inflate(inflater, container, false);
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
        public VideosItemLayoutBinding v;

        public RecyclerViewItem(@NonNull View parent) {
            super((VideosItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = VideosItemLayoutBinding.bind(this.itemView);

        }
    }
}
