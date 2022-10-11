package io.openim.android.ouiconversation.ui.frowardmessage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.adapter.AdapterMyGroup;
import io.openim.android.ouiconversation.databinding.FragmentMyFriendBinding;
import io.openim.android.ouiconversation.databinding.FragmentMyGroupsBinding;
import io.openim.android.ouiconversation.entity.ExUserInfo;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.vm.ForwardMessageVM;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.sdk.models.GroupInfo;
import io.openim.android.sdk.models.Message;

public class MyGroupsFragment extends BaseFragment<ForwardMessageVM> {

    FragmentMyGroupsBinding binding ;
    private AdapterMyGroup adapterMyGroup;
    Message msg ;
    ChatVM chatVM;
    List<GroupInfo> groupInfoList = new ArrayList<>();
    public MyGroupsFragment(Message msg, ChatVM chatVM) {
        this.msg = msg ;
        this.chatVM = chatVM ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bindVM(ForwardMessageVM.class);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyGroupsBinding.inflate(inflater, container, false);
//        vm.setIView(this);
        initView();

        vm.getJoinedGroupList();
        listener();
        return binding.getRoot();
    }

    private void listener() {
        vm.groupJoinedInfo.observe(this.getActivity() , data->{
            if(data == null)
                return;
            adapterMyGroup.setGroupInfoList(data);
            groupInfoList.addAll(data);
            adapterMyGroup.notifyDataSetChanged();
        });
        vm.sendMessageResult.observe(this,data->{
            if(data == null) return;
            if((Objects.equals(chatVM.groupID, data.getGroupID()) && chatVM.groupID != null)||
                (Objects.equals(chatVM.otherSideID, data.getRecvID())&& chatVM.otherSideID != null ) ){
                chatVM.isForwardedToSameConversation.setValue(data);
            }
        });
        vm.searchForward.observe(this, data->{
            if (null == groupInfoList || groupInfoList.isEmpty()) return;
            List<GroupInfo> items = groupInfoList;
            List<GroupInfo> selectedList = new LinkedList<>();

            for (GroupInfo item : items) {
                if (item.getGroupName().toLowerCase().contains(data.toString().toLowerCase())) {
                    selectedList.add(item);
                }
            }
            adapterMyGroup.setGroupInfoList(selectedList);
            adapterMyGroup.notifyDataSetChanged();
        });
    }

    private void initView() {
        System.out.println("sdaflkhj " + msg);
        adapterMyGroup = new AdapterMyGroup(this.getActivity(),vm,msg);
        binding.rvUserJoinedGroups.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        binding.rvUserJoinedGroups.setAdapter(adapterMyGroup);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
