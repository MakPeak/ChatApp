package io.bytechat.demo.oldrelease.ui.search;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;
import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.app.Activity;
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

import io.bytechat.demo.databinding.FragmentSearchContactsBinding;
import io.bytechat.demo.oldrelease.vm.SearchVM;

import io.openim.android.ouiconversation.ui.ChatActivity;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.FriendInfo;

public class SearchContactsFragment extends BaseFragment<SearchVM> implements SearchContactsAdapter.ItemClickListener {

    private FragmentSearchContactsBinding binding;
    private SearchContactsAdapter searchContactsAdapter;
    private List<FriendInfo> friendInfoList = new ArrayList<>();
    public String search = "";
    Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        vm.searchContacts(authVM.search.getValue());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchContactsBinding.inflate(getLayoutInflater(), container, false);

//        // This callback will only be called when MyFragment is at least Started.
//        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
//            @Override
//            public void handleOnBackPressed() {
//                getActivity().finish();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        authVM.search.observe(requireActivity(), data->{
            if(!authVM.search.getValue().isEmpty()) {
                binding.rvSearchContacts.setVisibility(View.VISIBLE);
//                binding.ivNoResults.setVisibility(View.GONE);
                binding.tvNoResults.setVisibility(View.GONE);
                vm.searchContacts(data);
            } else {
                binding.rvSearchContacts.setVisibility(View.GONE);
//                binding.ivNoResults.setVisibility(View.VISIBLE);
                binding.tvNoResults.setVisibility(View.VISIBLE);
            }
        });

        if(!authVM.search.getValue().isEmpty()) {
            vm.searchContacts(authVM.search.getValue());
        }
        initView();

    }

    @Override
    public void onResume(){
        super.onResume();
        vm.searchContacts(authVM.search.getValue());
        initView();
    }

    private void initView() {

        binding.rvSearchContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.friendInfo.observe(requireActivity(), data->{
            if(data.size() > 0) {
                binding.tvNoResults.setVisibility(View.GONE);
                searchContactsAdapter = new SearchContactsAdapter(getContext(), data, this::onContactItemClick);
                binding.rvSearchContacts.setAdapter(searchContactsAdapter);
            } else {
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.rvSearchContacts.setVisibility(View.GONE);
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

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            vm.searchContacts(authVM.search.getValue());
//            initView();
//        } else {
//            System.out.println("GONE");
//        }
//    }

}
