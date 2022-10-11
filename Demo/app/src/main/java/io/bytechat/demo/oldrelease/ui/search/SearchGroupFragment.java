package io.bytechat.demo.oldrelease.ui.search;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.GROUP_ID;
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

import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.adapter.SearchContactsAdapter;

import io.bytechat.demo.adapter.SearchGroupAdapter;
import io.bytechat.demo.databinding.FragmentSearchContactsBinding;
import io.bytechat.demo.databinding.FragmentSearchGroupBinding;
import io.bytechat.demo.oldrelease.vm.SearchVM;

import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.FriendInfo;

public class SearchGroupFragment extends BaseFragment<SearchVM> implements SearchGroupAdapter.ItemClickListener  {

    private FragmentSearchGroupBinding binding;
    private SearchGroupAdapter searchGroupAdapter;
    private List<FriendInfo> friendInfoList = new ArrayList<>();
    public String search = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        vm.searchGroup(authVM.search.getValue());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchGroupBinding.inflate(getLayoutInflater(), container, false);

        initView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        authVM.search.observe(requireActivity(), data->{
            if(!authVM.search.getValue().isEmpty()) {
                binding.rvSearchGroup.setVisibility(View.VISIBLE);
//                binding.ivNoResults.setVisibility(View.GONE);
                binding.tvNoResults.setVisibility(View.GONE);
                vm.searchGroup(data);
            } else {
                binding.rvSearchGroup.setVisibility(View.GONE);
//                binding.ivNoResults.setVisibility(View.VISIBLE);
                binding.tvNoResults.setVisibility(View.VISIBLE);
            }
        });

        if(!authVM.search.getValue().isEmpty()) {
            vm.searchGroup(authVM.search.getValue());
        }
        initView();

    }

    @Override
    public void onResume(){
        super.onResume();
        vm.searchGroup(authVM.search.getValue());
        initView();
    }

    private void initView() {

        binding.rvSearchGroup.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.groupInfo.observe(requireActivity(), data->{
            if(data.size() > 0) {
                searchGroupAdapter = new SearchGroupAdapter(getContext(), data, this::onGroupItemClick);
                binding.rvSearchGroup.setAdapter(searchGroupAdapter);
                binding.tvNoResults.setVisibility(View.GONE);
            } else {
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.rvSearchGroup.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onGroupItemClick(String groupID, String groupName) {
        vm.searchOneConversation( groupID, 2);

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(K_NAME, groupName);
        intent.putExtra(GROUP_ID, groupID);
//        intent.putExtra(CONVERSATION_ID, vm.conversationID.getValue());
        startActivity(intent);

    }
}
