package io.openim.android.ouiconversation.ui.chathistory;

import static io.openim.android.ouiconversation.utils.Constant.MsgType.TXT;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.FragmentInformationBinding;
import io.openim.android.ouiconversation.databinding.InformationTabItemBinding;
import io.openim.android.ouiconversation.vm.ChatHistoryVM;
import io.openim.android.ouicore.adapter.RecyclerViewAdapter;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.Message;
import io.openim.android.sdk.models.SearchResult;

public class InformationFragment extends BaseFragment<ChatHistoryVM> implements ChatHistoryVM.ViewAction{
    FragmentInformationBinding binding ;
    String chatID ;
    RecyclerViewAdapter<Message, RecyclerViewItem> adapter ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(ChatHistoryVM.class);
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter<Message, RecyclerViewItem>(RecyclerViewItem.class) {
            @Override
            public void onBindView(@NonNull RecyclerViewItem holder, Message data, int position) {
                holder.v.message.setText(data.getContent());
                holder.v.iv2.load(data.getSenderFaceUrl());
                holder.v.iv2.setBackgroundResource(R.drawable.background_gradient_user_chat_btn);
                holder.v.sender.setText(data.getSenderNickname());
            }
        };


        List<Message> list = new LinkedList<>();
        adapter.setItems(list);
        binding.recyclerView.setAdapter(adapter);

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable editable) {
                vm.ConvId.setValue(chatID);
                vm.searchKeyword.setValue(editable.toString());
                if(!editable.toString().isEmpty())
                    vm.searchMessage(TXT);
                else
                    adapter.setItems(new LinkedList<>());
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onEmpty() {
        adapter.setItems(new LinkedList<>());
    }
    @Override
    public void onSuccess(SearchResult body) {
        super.onSuccess(body);
        List<Message> list = new LinkedList<>();
        for (Message o : body.getSearchResultItems().get(0).getMessageList()){
            list.add(o);
        }
        adapter.setItems(list);
    }

    @Override
    public void onError(String error) {
        super.onError(error);
    }

    public static class RecyclerViewItem extends RecyclerView.ViewHolder {
        public InformationTabItemBinding v;

        public RecyclerViewItem(@NonNull View parent) {
            super((InformationTabItemBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) parent, false).getRoot()));
            v = InformationTabItemBinding.bind(this.itemView);

        }
    }
}
