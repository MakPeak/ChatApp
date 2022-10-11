package io.openim.android.ouicontact.ui.newgroups;

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
import io.openim.android.ouicontact.databinding.FragmentMyGroupsJoinedBinding;
import io.openim.android.ouicontact.ui.adapters.AdapterMyFriends;
import io.openim.android.ouicontact.ui.adapters.AdapterMyGroups;
import io.openim.android.ouicontact.ui.adapters.AdapterNewGroupsRequest;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseFragment;
import io.openim.android.ouicore.utils.Routes;
import io.openim.android.sdk.models.GroupApplicationInfo;

@Route(path = Routes.Contact.HOME)
public class MyGroupsFragment extends BaseFragment<ContactVM> {

    private FragmentMyGroupsJoinedBinding binding;
    private AdapterMyGroups adapterMyGroups;
    private List<GroupApplicationInfo> groupApplicationInfoList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindVM(ContactVM.class);
        super.onCreate(savedInstanceState);
        vm.getSendGroupApplicationList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyGroupsJoinedBinding.inflate(getLayoutInflater());

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
                if(selectedItem.equals(getContext().getResources().getString(io.openim.android.ouicore.R.string.i_create))) {
//                    Toast.makeText(getContext(), "Shift", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.action_myGroupsFragment_to_newGroupsRequestFragment);
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

        // below line is to call set on query text listener method.
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });

    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        List<GroupApplicationInfo> filteredlist = new ArrayList<>();
        // running a for loop to compare elements.
        for (GroupApplicationInfo item : groupApplicationInfoList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getGroupID().toLowerCase().contains(text.toLowerCase()) || item.getGroupName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
            adapterMyGroups.filterList(filteredlist);
            binding.rvMyGroups.setVisibility(View.GONE);
            binding.tvNoMoreResults.setVisibility(View.VISIBLE);
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapterMyGroups.filterList(filteredlist);
            binding.rvMyGroups.setVisibility(View.VISIBLE);
            binding.tvNoMoreResults.setVisibility(View.GONE);
        }

        if(text.isEmpty()){
            adapterMyGroups.filterList(groupApplicationInfoList);
        }
    }

    private void initView() {

        // Spinner Drop down elements
        List<String> elements = new ArrayList<String>();
        elements.add(getContext().getResources().getString(io.openim.android.ouicore.R.string.i_join ) + "     ");
        elements.add(getContext().getResources().getString(io.openim.android.ouicore.R.string.i_create));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_spinner, elements);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        binding.spinner.setAdapter(dataAdapter);

        vm.groupSent.observe(requireActivity(), data->{
            groupApplicationInfoList.addAll(data);
            adapterMyGroups = new AdapterMyGroups(getContext(), data);
            binding.rvMyGroups.setAdapter(adapterMyGroups);
        });

    }
}
