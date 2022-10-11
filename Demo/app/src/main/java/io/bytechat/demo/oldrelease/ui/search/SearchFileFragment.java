package io.bytechat.demo.oldrelease.ui.search;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.bytechat.demo.adapter.SearchFileAdapter;
import io.bytechat.demo.databinding.FragmentSearchFileBinding;
import io.bytechat.demo.oldrelease.vm.SearchVM;

import io.bytechat.demo.utils.UtilsFunctions;
import io.openim.android.ouiconversation.utils.Constant;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.Message;

public class SearchFileFragment extends BaseFragment<SearchVM> implements SearchFileAdapter.ItemClickListener  {

    private FragmentSearchFileBinding binding;
    private SearchFileAdapter searchFileAdapter;
    private List<Message> messageList = new ArrayList<>();
    public String search = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(SearchVM.class);
        super.onCreate(savedInstanceState);
        vm.searchChat(authVM.search.getValue(), Constant.MsgType.FILE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchFileBinding.inflate(getLayoutInflater(), container, false);

        initView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        authVM.search.observe(requireActivity(), data->{
            if(!authVM.search.getValue().isEmpty()) {
                binding.rvSearchFile.setVisibility(View.VISIBLE);
//                binding.ivNoResults.setVisibility(View.GONE);
                binding.tvNoResults.setVisibility(View.GONE);
                vm.searchFile(data, Constant.MsgType.FILE);
            } else {
                binding.rvSearchFile.setVisibility(View.GONE);
//                binding.ivNoResults.setVisibility(View.VISIBLE);
                binding.tvNoResults.setVisibility(View.VISIBLE);
            }
        });

        if(!authVM.search.getValue().isEmpty()) {
            vm.searchFile(authVM.search.getValue(), Constant.MsgType.FILE);
        }
        initView();

    }

    @Override
    public void onResume(){
        super.onResume();
        vm.searchChat(authVM.search.getValue(), Constant.MsgType.FILE);
        initView();
    }

    private void initView() {

        binding.rvSearchFile.setLayoutManager(new LinearLayoutManager(getContext()));
        vm.messageFile.observe(requireActivity(), data->{
            if(data.size() > 0) {
                binding.tvNoResults.setVisibility(View.GONE);
                searchFileAdapter = new SearchFileAdapter(getContext(), data, this::onFileItemClick);
                binding.rvSearchFile.setAdapter(searchFileAdapter);
            } else {
                binding.tvNoResults.setVisibility(View.VISIBLE);
                binding.rvSearchFile.setVisibility(View.GONE);
            }
        });

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
