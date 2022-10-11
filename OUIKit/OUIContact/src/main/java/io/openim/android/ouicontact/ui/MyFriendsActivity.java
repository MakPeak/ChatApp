package io.openim.android.ouicontact.ui;

import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouicontact.databinding.ActivityMyFriendsBinding;
import io.openim.android.ouicontact.databinding.ActivityMyGroupsBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterFrequentContacts;
import io.openim.android.ouicontact.ui.adapters.AdapterMyFriends;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.SinkHelper;
import io.openim.android.sdk.models.FriendApplicationInfo;
import io.openim.android.sdk.models.FriendInfo;

public class MyFriendsActivity extends BaseActivity<ContactVM, ActivityMyFriendsBinding> implements AdapterFrequentContacts.ItemClickListener {

    AdapterFrequentContacts adapterFrequentContacts;
    private List<FriendInfo> friendInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVM(ContactVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMyFriendsBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

        vm.getAllFriend();

        view.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vm.friendsList.observe(this, data->{
            friendInfoList.addAll(data);
            adapterFrequentContacts = new AdapterFrequentContacts(this, data, this::onContactItemClick);
            view.rvMyFriends.setAdapter(adapterFrequentContacts);
        });

        // below line is to call set on query text listener method.
        view.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        List<FriendInfo> filteredlist = new ArrayList<>();
        // running a for loop to compare elements.
        for (FriendInfo item : friendInfoList) {
            if (item.getUserID().toLowerCase().contains(text.toLowerCase()) || item.getNickname().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            adapterFrequentContacts.filterList(filteredlist);
            view.rvMyFriends.setVisibility(View.GONE);
            view.tvNoMoreResults.setVisibility(View.VISIBLE);
        } else {
            adapterFrequentContacts.filterList(filteredlist);
            view.rvMyFriends.setVisibility(View.VISIBLE);
            view.tvNoMoreResults.setVisibility(View.GONE);
        }

        if(text.isEmpty()){
            adapterFrequentContacts.filterList(friendInfoList);
        }
    }

    @Override
    public void onContactItemClick(String fromUserID, String fromNickName) {
        vm.searchOneConversation( fromUserID, 1);

        Intent intent = null;
        try {
            intent = new Intent(MyFriendsActivity.this, Class.forName("io.openim.android.ouiconversation.ui.ChatActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        intent.putExtra(K_NAME, fromNickName);
        intent.putExtra(ID, fromUserID);
//        intent.putExtra(CONVERSATION_ID, vm.conversationID.getValue());
        startActivity(intent);

//        vm.conversationID.observe(MyFriendsActivity.this, data->{
//
//        });
    }
}
