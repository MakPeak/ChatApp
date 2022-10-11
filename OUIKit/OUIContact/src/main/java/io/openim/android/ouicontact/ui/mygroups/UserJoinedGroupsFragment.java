package io.openim.android.ouicontact.ui.mygroups;

import static io.openim.android.ouicore.utils.Constant.CONVERSATION_ID;
import static io.openim.android.ouicore.utils.Constant.GROUP_ID;
import static io.openim.android.ouicore.utils.Constant.ID;
import static io.openim.android.ouicore.utils.Constant.K_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.Navigation;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.databinding.FragmentUserJoinedGroupsBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterUserCreatedJoinedGroups;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.entity.LoginCertificate;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.sdk.models.GroupInfo;

@Route(path = Routes.Contact.HOME)
public class UserJoinedGroupsFragment extends BaseFragment<ContactVM> implements AdapterUserCreatedJoinedGroups.ItemClickListener {

    private FragmentUserJoinedGroupsBinding binding;
    private AdapterUserCreatedJoinedGroups adapterUserCreatedJoinedGroups;
    private List<GroupInfo> groupInfoList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ContactVM.class);
        super.onCreate(savedInstanceState);
        vm.getJoinedGroupList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserJoinedGroupsBinding.inflate(getLayoutInflater());

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        initView("");

        binding.rlUserCreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_userJoinedGroupsFragment_to_userCreatedGroupsFragment);
            }
        });

        vm.searchMyGroups.observe(requireActivity(), data -> {
            if(!data.equalsIgnoreCase("")) {
                initView(data);
            } else {
                initView(data);
            }
        });
    }

    private void initView(String search) {

        vm.groupJoinedInfo.observe(requireActivity(), data->{
            List<GroupInfo> groupApplicationInfoList = new ArrayList<>();
            for(int i = 0; i < data.size(); i++){
                if(!(data.get(i).getCreatorUserID().equalsIgnoreCase(Objects.requireNonNull(LoginCertificate.getCache(getContext())).userID))){
                    groupApplicationInfoList.add(data.get(i));
                }
            }
            groupInfoList.addAll(groupApplicationInfoList);
            if (!search.isEmpty()) {
                List<GroupInfo> filteredlist = new ArrayList<>();
                for (GroupInfo item : groupApplicationInfoList) {
                    if (item.getGroupID().toLowerCase().contains(search.toLowerCase()) || item.getGroupName().toLowerCase().contains(search.toLowerCase())) {
                        filteredlist.add(item);
                    }

                }
                if (filteredlist.isEmpty()) {
                    binding.rvUserJoinedGroups.setVisibility(View.GONE);
                    binding.tvNoMoreResults.setVisibility(View.VISIBLE);
                } else {
                    adapterUserCreatedJoinedGroups.filterList(filteredlist);
                    binding.rvUserJoinedGroups.setVisibility(View.VISIBLE);
                    binding.tvNoMoreResults.setVisibility(View.GONE);
                }
                adapterUserCreatedJoinedGroups = new AdapterUserCreatedJoinedGroups(getContext(), filteredlist, this::onGroupItemClick);
                binding.rvUserJoinedGroups.setAdapter(adapterUserCreatedJoinedGroups);
            } else {
                adapterUserCreatedJoinedGroups = new AdapterUserCreatedJoinedGroups(getContext(), groupApplicationInfoList, this::onGroupItemClick);
                binding.rvUserJoinedGroups.setAdapter(adapterUserCreatedJoinedGroups);
            }

        });
    }

    @Override
    public void onGroupItemClick(String groupID, String groupName) {
        vm.searchOneConversation( groupID, 2);

        Intent intent = null;
        try {
            intent = new Intent(getContext(), Class.forName("io.openim.android.ouiconversation.ui.ChatActivity"));
            intent.putExtra(K_NAME, groupName);
            intent.putExtra(GROUP_ID, groupID);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
