package io.openim.android.ouicontact.ui;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import static io.openim.android.ouicore.utils.Common.authViewModel;
import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.openim.android.ouicontact.databinding.FragmentContactBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterFrequentContacts;
import io.openim.android.ouicontact.ui.adapters.AdapterNewFriendsRequest;
import io.openim.android.ouicontact.ui.adapters.AdapterNewGroupsRequest;
import io.openim.android.ouicontact.ui.mygroups.MyGroupsActivity;
import io.openim.android.ouicontact.ui.newfriends.NewFriendActivity;
import io.openim.android.ouicontact.ui.newgroups.NewGroupActivity;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.Constant;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.ouicore.vm.AuthViewModel;
import io.openim.android.sdk.OpenIMClient;
import io.openim.android.sdk.listener.OnBase;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;
import io.openim.android.sdk.models.GroupApplicationInfo;
import io.openim.android.sdk.models.UserInfo;

@Route(path = Routes.Contact.HOME)
public class ContactFragment extends BaseFragment<ContactVM> implements AdapterFrequentContacts.ItemClickListener {

    private FragmentContactBinding view;
    private AdapterFrequentContacts adapterFrequentContacts;
    AddGroupDialog addGroupDialog ;
    AddFriendDialog addFriendDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ContactVM.class);
        super.onCreate(savedInstanceState);
//        vm.getAllFriend();
        authViewModel.counterContact.setValue(0);
        vm.getRecvGroupApplicationList();
        vm.getRecvFriendApplicationList();
        addGroupDialog = new AddGroupDialog(getActivity());
        addFriendDialog = new AddFriendDialog(getActivity());

    }
    @Override
    public void onStart() {
        super.onStart();
        System.out.println("onstart triggered "+authViewModel.friendDotNum.getValue() + " "+authViewModel.groupDotNum.getValue() + " "  +authViewModel.counterContact.getValue());
        if(authViewModel.friendDotNum.getValue() == 0){
            view.tvBadgeNewFriends.setVisibility(View.GONE);
        } else {
            view.tvBadgeNewFriends.setVisibility(View.VISIBLE);
            view.tvBadgeNewFriends.setText(String.valueOf(authViewModel.friendDotNum.getValue()));
        }

        if(authViewModel.groupDotNum.getValue() == 0){
            view.tvBadgeNewGroups.setVisibility(View.GONE);
        } else {
            view.tvBadgeNewGroups.setVisibility(View.VISIBLE);
            view.tvBadgeNewGroups.setText(String.valueOf(authViewModel.groupDotNum.getValue()));
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = FragmentContactBinding.inflate(getLayoutInflater());
        initView();
        click();
        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

    }

    private void click() {
        vm.groupApply.observe(requireActivity(), data->{
            int cnt= 0 ;
            for (GroupApplicationInfo info : data){
                if (info.getHandleResult() == 0){
                    cnt++;
                }
            }
            System.out.println( " cnt resulte : " +cnt);
            authViewModel.groupDotNum.setValue(cnt);
        });
        vm.friendApply.observe(requireActivity(), data->{
            int cnt= 0 ;
            for (FriendApplicationInfo info : data){
                if (info.getHandleResult() == 0){
                    cnt++;
                }
            }
            authViewModel.friendDotNum.setValue(cnt);
        });
        view.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    intent = new Intent(getContext(), Class.forName("io.bytechat.demo.oldrelease.ui.search.GlobalSearchActivity"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });

        view.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        view.rlNewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewFriendActivity.class);
                startActivity(intent);
            }
        });

        view.rlNewGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewGroupActivity.class);
                startActivity(intent);
            }
        });

        view.rlMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyFriendsActivity.class);
                startActivity(intent);
            }
        });

        view.rlMyGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyGroupsActivity.class);
                startActivity(intent);
            }
        });

        view.rlTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TagsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {

        authViewModel.friendDotNum.observe(requireActivity(), data ->{
            if(data > 0) {
                view.tvBadgeNewFriends.setVisibility(View.VISIBLE);
                view.tvBadgeNewFriends.setText(String.valueOf(data));
                authViewModel.counterContact.setValue(data);
            } else {
                view.tvBadgeNewFriends.setVisibility(View.GONE);
            }
        });

        authViewModel.groupDotNum.observe(requireActivity(), data ->{
           if(data > 0) {
                view.tvBadgeNewGroups.setVisibility(View.VISIBLE);
                view.tvBadgeNewGroups.setText(String.valueOf(data));
                authViewModel.counterContact.setValue(data);
           } else {
                view.tvBadgeNewGroups.setVisibility(View.GONE);
           }
        });

        ArrayList<FriendInfo> friendInfoWithoutBlackListArrayList = new ArrayList<>();
        ArrayList<FriendInfo> friendInfoArrayList = new ArrayList<>();
        Set<String> titles = new HashSet<String>();

        for( FriendInfo friendInfo : Constant.FREQUENT_CONTACTS_LIST ) {
            if( titles.add( friendInfo.getUserID() )) {
                friendInfoArrayList.add( friendInfo );
            }
        }

        List<String> userIDs = new ArrayList<>();
        userIDs.addAll(titles);

        OpenIMClient.getInstance().userInfoManager.getUsersInfo(new OnBase<List<UserInfo>>() {
            @Override
            public void onError(int code, String error) {
                vm.userInfo.setValue(null);
            }

            @Override
            public void onSuccess(List<UserInfo> data) {
                vm.userInfo.setValue(data);
            }
        }, userIDs);

        vm.userInfo.observe(requireActivity(), data -> {
            if(null == data || data.isEmpty()) return;
            friendInfoArrayList.clear();
            friendInfoWithoutBlackListArrayList.clear();

            // Filtering the list without Black list
            for(int i = 0; i < data.size(); i++){
                if(!data.get(i).isBlacklist()){
                    FriendInfo friendInfo = new FriendInfo(data.get(i).getUserID(), data.get(i).getNickname(), data.get(i).getFaceURL(),
                        data.get(i).getGender(), data.get(i).getPhoneNumber(), data.get(i).getBirth(), data.get(i).getEmail(), data.get(i).getRemark(),
                        data.get(i).getEx(), 0, 0, "");
                    friendInfoArrayList.add(friendInfo);
                }
            }

            //Matching and sorting according to frequent contacts from Contact List Fragment
            for(int i = 0; i < Constant.FREQUENT_CONTACTS_LIST.size(); i++){
                for(int j = 0; j < friendInfoArrayList.size(); j++){
                    if(Constant.FREQUENT_CONTACTS_LIST.get(i).getUserID().equalsIgnoreCase(friendInfoArrayList.get(j).getUserID())){
                        friendInfoWithoutBlackListArrayList.add(friendInfoArrayList.get(j));
                    }
                }
            }

            //Removing duplication from the list
            ArrayList<FriendInfo> friendInfoArrayListDistinct = new ArrayList<>();
            Set<String> titles1 = new HashSet<String>();
            for( FriendInfo friendInfo : friendInfoWithoutBlackListArrayList ) {
                if( titles1.add( friendInfo.getUserID() )) {
                    friendInfoArrayListDistinct.add( friendInfo );
                }
            }

            view.rvFreqContacts.setLayoutManager(new LinearLayoutManager(getContext()));
            adapterFrequentContacts = new AdapterFrequentContacts(vm.getContext(), friendInfoArrayListDistinct, this::onContactItemClick);
            view.rvFreqContacts.setNestedScrollingEnabled(false);
            view.rvFreqContacts.setAdapter(adapterFrequentContacts);
        });
    }

    @Override
    public void onContactItemClick(String fromUserID, String fromNickName) {
        vm.searchOneConversation( fromUserID, 1);

        Intent intent = null;
        try {
            intent = new Intent(getContext(), Class.forName("io.openim.android.ouiconversation.ui.ChatActivity"));
            intent.putExtra(K_NAME, fromNickName);
            intent.putExtra(ID, fromUserID);
//            intent.putExtra(CONVERSATION_ID, vm.conversationID.getValue());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(!Constant.SCAN_USER_ID.isEmpty()){
            addFriendDialog.showDialog(Constant.SCAN_USER_ID);
            Constant.SCAN_USER_ID = "";
        }

        if(!Constant.SCAN_GROUP_ID.isEmpty()){
            addGroupDialog.showDialog(Constant.SCAN_GROUP_ID);
            Constant.SCAN_GROUP_ID = "";
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

}
