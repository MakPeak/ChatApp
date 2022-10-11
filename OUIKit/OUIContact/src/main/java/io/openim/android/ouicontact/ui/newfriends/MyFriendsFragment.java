package io.openim.android.ouicontact.ui.newfriends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.Navigation;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.List;

import io.openim.android.ouicontact.R;
import io.openim.android.ouicontact.databinding.FragmentMyFriendsBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterMyFriends;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.sdk.models.FriendApplicationInfo;

@Route(path = Routes.Contact.HOME)
public class MyFriendsFragment extends BaseFragment<ContactVM> {

    private FragmentMyFriendsBinding binding;
    private AdapterMyFriends adapterMyFriends;
    private List<FriendApplicationInfo> friendApplicationInfoList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ContactVM.class);
        super.onCreate(savedInstanceState);
        vm.getSendFriendApplicationList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyFriendsBinding.inflate(getLayoutInflater());

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

        initView();

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals(getContext().getResources().getString(io.openim.android.ouicore.R.string.new_friends_heading))) {
//                    Toast.makeText(getContext(), "Shift", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.action_myFriendsFragment_to_newFriendsRequestFragment);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        List<FriendApplicationInfo> filteredlist = new ArrayList<>();
        for (FriendApplicationInfo item : friendApplicationInfoList) {
            if (item.getToUserID().toLowerCase().contains(text.toLowerCase()) || item.getToNickname().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            adapterMyFriends.filterList(filteredlist);
            binding.rvMyFriends.setVisibility(View.GONE);
            binding.tvNoMoreResults.setVisibility(View.VISIBLE);
        } else {
            adapterMyFriends.filterList(filteredlist);
            binding.rvMyFriends.setVisibility(View.VISIBLE);
            binding.tvNoMoreResults.setVisibility(View.GONE);
        }

        if(text.isEmpty()){
            adapterMyFriends.filterList(friendApplicationInfoList);
        }
    }

    private void initView() {

        // Spinner Drop down elements
        List<String> elements = new ArrayList<String>();
        elements.add(getContext().getResources().getString(io.openim.android.ouicore.R.string.my_requests));
        elements.add(getContext().getResources().getString(io.openim.android.ouicore.R.string.new_friends_heading));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_spinner, elements);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        binding.spinner.setAdapter(dataAdapter);

        vm.friendSent.observe(requireActivity(), data->{
            friendApplicationInfoList.addAll(data);
            adapterMyFriends = new AdapterMyFriends(getContext(), data);
            binding.rvMyFriends.setAdapter(adapterMyFriends);
        });

    }
}
