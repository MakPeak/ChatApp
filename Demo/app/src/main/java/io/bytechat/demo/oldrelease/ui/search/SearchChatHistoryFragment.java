package io.bytechat.demo.oldrelease.ui.search;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.adapter.SearchChatHistoryAdapter;

import io.bytechat.demo.databinding.FragmentSearchChatHistoryBinding;
import io.bytechat.demo.oldrelease.vm.SearchVM;

import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.Message;

public class SearchChatHistoryFragment extends BaseFragment<SearchVM> implements SearchChatHistoryAdapter.ItemClickListener {

    private FragmentSearchChatHistoryBinding binding;
    private SearchChatHistoryAdapter searchChatHistoryFileAdapter;
    private List<Message> messageList = new ArrayList<>();
    public String search = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        vm.searchChat(authVM.search.getValue(), Constant.MsgType.TXT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchChatHistoryBinding.inflate(getLayoutInflater(), container, false);

        initView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        authVM.search.observe(requireActivity(), data->{
            if(!authVM.search.getValue().isEmpty()) {
                binding.rvSearchChatHistory.setVisibility(View.VISIBLE);
//                binding.ivNoResults.setVisibility(View.GONE);
                binding.tvNoResults.setVisibility(View.GONE);
                vm.searchChat(data, Constant.MsgType.TXT);
            } else {
                binding.rvSearchChatHistory.setVisibility(View.GONE);
//                binding.ivNoResults.setVisibility(View.VISIBLE);
                binding.tvNoResults.setVisibility(View.VISIBLE);
            }
        });

        if(!authVM.search.getValue().isEmpty()) {
            vm.searchChat(authVM.search.getValue(), Constant.MsgType.TXT);
        }
        initView();

    }

    @Override
    public void onResume(){
        super.onResume();
        vm.searchChat(authVM.search.getValue(), Constant.MsgType.TXT);
        initView();
    }

    private void initView() {

        binding.rvSearchChatHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.message.observe(requireActivity(), data->{
             if(data.size() > 0) {
                 binding.tvNoResults.setVisibility(View.GONE);
                 searchChatHistoryFileAdapter = new SearchChatHistoryAdapter(getContext(), data, this::onChatItemClick);
                 binding.rvSearchChatHistory.setAdapter(searchChatHistoryFileAdapter);
             } else {
                 binding.tvNoResults.setVisibility(View.VISIBLE);
                 binding.rvSearchChatHistory.setVisibility(View.GONE);
             }
        });

    }

    @Override
    public void onChatItemClick(String fromUserID, String fromNickName, Message startMsg) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(K_NAME, fromNickName);
        intent.putExtra(ID, fromUserID);
        Gson gson = new Gson();
        String startM = gson.toJson(startMsg);
        intent.putExtra("Message", startM);
        startActivity(intent);
    }
}
