package io.openim.android.ouicontact.ui.mygroups;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import io.openim.android.ouicontact.databinding.ActivityMyGroupsBinding;
import io.openim.android.ouicontact.databinding.ActivityNewGroupBinding;
import io.openim.android.ouicontact.vm.ContactVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.ouicore.utils.SinkHelper;

public class MyGroupsActivity extends BaseActivity<ContactVM, ActivityMyGroupsBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bindVMByCache(ContactVM.class);
        super.onCreate(savedInstanceState);
        bindViewDataBinding(ActivityMyGroupsBinding.inflate(getLayoutInflater()));
        setLightStatus();
        SinkHelper.get(this).setTranslucentStatus(view.getRoot());
        view.setContactVM(vm);

        view.ivBackMyGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        view.searchViewMyGroup.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
                callSearch(newText);
//              }
                return true;
            }

            public void callSearch(String query) {
                vm.searchMyGroups.postValue(query);
            }

        });

    }

}
