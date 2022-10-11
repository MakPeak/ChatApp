package io.bytechat.demo.oldrelease.ui.search;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import io.bytechat.demo.adapter.SearchContactsAdapter;

import io.bytechat.demo.adapter.SearchFileAdapter;
import io.bytechat.demo.adapter.SearchGroupAdapter;
import io.bytechat.demo.databinding.FragmentSearchAllBinding;
import io.bytechat.demo.oldrelease.vm.SearchVM;

import io.bytechat.demo.utils.UtilsFunctions;
import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.Message;

public class SearchAllFragment extends BaseFragment<SearchVM> implements SearchContactsAdapter.ItemClickListener, SearchGroupAdapter.ItemClickListener,
    SearchChatHistoryAdapter.ItemClickListener, SearchFileAdapter.ItemClickListener{

    private FragmentSearchAllBinding binding;
    private SearchContactsAdapter searchContactsAdapter;
    private SearchGroupAdapter searchGroupAdapter;
    private SearchChatHistoryAdapter searchChatHistoryAdapter;
    private SearchFileAdapter searchFileAdapter;
    private List<FriendInfo> friendInfoList = new ArrayList<>();
    public String search = "";
    boolean contact, group, chat, file;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchAllBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        authVM.search.observe(requireActivity(), data->{
            if(!authVM.search.getValue().isEmpty()) {
                binding.svBody.setVisibility(View.VISIBLE);
//                binding.ivNoResults.setVisibility(View.GONE);
                binding.tvNoResults.setVisibility(View.GONE);
                vm.searchContacts(data);
                vm.searchGroup(data);
                vm.searchChat(data, Constant.MsgType.TXT);
                vm.searchFile(data, Constant.MsgType.FILE);

            } else {
                binding.svBody.setVisibility(View.GONE);
//                binding.ivNoResults.setVisibility(View.VISIBLE);
                binding.tvNoResults.setVisibility(View.VISIBLE);
            }
        });

        if(!authVM.search.getValue().isEmpty()) {
            vm.searchContacts(authVM.search.getValue());
            vm.searchGroup(authVM.search.getValue());
            vm.searchChat(authVM.search.getValue(), Constant.MsgType.TXT);
            vm.searchFile(authVM.search.getValue(), Constant.MsgType.FILE);
        }
        initView();

    }

    @Override
    public void onResume(){
        super.onResume();
        initView();
    }

    private void initView() {

        binding.rvSearchContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.friendInfo.observe(requireActivity(), data->{
            if(data.size() > 0) {
                contact = false;
                binding.tvContactHeading.setVisibility(View.VISIBLE);
                searchContactsAdapter = new SearchContactsAdapter(getContext(), data, this);
                binding.rvSearchContacts.setAdapter(searchContactsAdapter);
            } else {
                contact = true;
                binding.tvContactHeading.setVisibility(View.GONE);
                searchContactsAdapter = new SearchContactsAdapter(getContext(), data, this);
                binding.rvSearchContacts.setAdapter(searchContactsAdapter);
            }
        });
        binding.rvSearchGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.groupInfo.observe(requireActivity(), data->{
            if(data.size() > 0) {
                group = false;
                binding.tvGroupHeading.setVisibility(View.VISIBLE);
                searchGroupAdapter = new SearchGroupAdapter(getContext(), data, this);
                binding.rvSearchGroup.setAdapter(searchGroupAdapter);
            } else {
                group = true;
                binding.tvGroupHeading.setVisibility(View.GONE);
                searchGroupAdapter = new SearchGroupAdapter(getContext(), data, this);
                binding.rvSearchGroup.setAdapter(searchGroupAdapter);
            }
        });
        binding.rvSearchChatHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.message.observe(requireActivity(), data->{
            if(data.size() > 0) {
                chat = false;
                binding.tvChatHeading.setVisibility(View.VISIBLE);
                searchChatHistoryAdapter = new SearchChatHistoryAdapter(getContext(), data, this);
                binding.rvSearchChatHistory.setAdapter(searchChatHistoryAdapter);
            } else {
                chat = true;
                binding.tvChatHeading.setVisibility(View.GONE);
                searchChatHistoryAdapter = new SearchChatHistoryAdapter(getContext(), data, this);
                binding.rvSearchChatHistory.setAdapter(searchChatHistoryAdapter);
            }
        });
        binding.rvSearchFile.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.messageFile.observe(requireActivity(), data->{
            if(data.size() > 0) {
                file = false;
                binding.tvFileHeading.setVisibility(View.VISIBLE);
                searchFileAdapter = new SearchFileAdapter(getContext(), data, this);
                binding.rvSearchFile.setAdapter(searchFileAdapter);
            } else {
                file = true;
                binding.tvFileHeading.setVisibility(View.GONE);
                searchFileAdapter = new SearchFileAdapter(getContext(), data, this);
                binding.rvSearchFile.setAdapter(searchFileAdapter);
            }

            if(contact && group && chat && file){
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.tvContactHeading.setVisibility(View.GONE);
                binding.tvGroupHeading.setVisibility(View.GONE);
                binding.tvChatHeading.setVisibility(View.GONE);
                binding.tvFileHeading.setVisibility(View.GONE);
            }
        });



    }

    @Override
    public void onContactItemClick(String fromUserID, String fromNickName) {

        vm.searchOneConversation( fromUserID, 1);

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(K_NAME, fromNickName);
        intent.putExtra(ID, fromUserID);
//        intent.putExtra(CONVERSATION_ID, vm.conversationID.getValue());
        startActivity(intent);

    }

    @Override
    public void onGroupItemClick(String groupID, String groupName) {

        vm.searchOneConversation( groupID, 2);

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(K_NAME, groupName);
        intent.putExtra(ID, groupID);
//        intent.putExtra(CONVERSATION_ID, vm.conversationID.getValue());
        startActivity(intent);

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

    @Override
    public void onFileItemClick(String url, String content, String fileName) {

        UtilsFunctions utilsFunctions = new UtilsFunctions();
        if (utilsFunctions.checkPermission(getActivity(), 1001, getView())) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                e.getStackTrace();
            }

        }
    }
}
