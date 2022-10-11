package io.openim.android.ouiconversation.ui.groupsettings;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import io.openim.android.ouiconversation.databinding.ActivityViewChatHistoryBinding;
import io.openim.android.ouiconversation.ui.chathistory.SectionsPagerAdapter;

public class ViewChatHistoryActivity extends AppCompatActivity {

    private ActivityViewChatHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewChatHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String chatID = getIntent().getExtras().getString("ChatID");
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),chatID);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        int tapSelected = getIntent().getIntExtra("Tap Selected",0);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(tapSelected).select();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
