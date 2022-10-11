package io.openim.android.ouiconversation.ui;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import io.openim.android.ouiconversation.R;
import io.openim.android.ouiconversation.databinding.ActivityForwardMessageBinding;
import io.openim.android.ouiconversation.ui.frowardmessage.SectionsPagerAdapter;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.ouiconversation.vm.ForwardMessageVM;
import io.openim.android.ouicore.base.BaseActivity;
import io.openim.android.sdk.models.Message;

public class ForwardMessageActivity extends BaseActivity<ForwardMessageVM,ActivityForwardMessageBinding> {

    private ActivityForwardMessageBinding binding;
    public static Message forwardMsg ;
    public static ChatVM chatVM ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindVM(ForwardMessageVM.class);
        binding = ActivityForwardMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), forwardMsg,chatVM);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        binding.searchViewForwardMessage.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                vm.searchForward.postValue(query);
                //Do searching
            }

        });


        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
