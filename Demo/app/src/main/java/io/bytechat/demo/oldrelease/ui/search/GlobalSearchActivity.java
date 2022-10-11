package io.bytechat.demo.oldrelease.ui.search;

import static io.bytechat.demo.ui.auth.AuthActivity.authVM;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import io.bytechat.demo.adapter.SearchPagerAdapter;
import io.bytechat.demo.databinding.ActivityGlobalSearchBinding;
import io.openim.android.ouiconversation.databinding.ActivityViewChatHistoryBinding;

public class GlobalSearchActivity extends AppCompatActivity {

    private ActivityGlobalSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGlobalSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authVM.search.postValue("");
                finish();
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                authVM.search.postValue(query);
                //Do searching
            }

        });

        SearchPagerAdapter searchPagerAdapter = new SearchPagerAdapter(this, getSupportFragmentManager(), "");
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(searchPagerAdapter);
        TabLayout tabs = binding.tabs;
        int tapSelected = getIntent().getIntExtra("Tap Selected",0);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(tapSelected).select();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("MainActivity", "New position = " + position);
            }
        });

    }
}
