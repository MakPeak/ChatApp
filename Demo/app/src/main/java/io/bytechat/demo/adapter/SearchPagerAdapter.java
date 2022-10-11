package io.bytechat.demo.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.bytechat.demo.R;
import io.bytechat.demo.oldrelease.ui.search.SearchAllFragment;
import io.bytechat.demo.oldrelease.ui.search.SearchChatHistoryFragment;
import io.bytechat.demo.oldrelease.ui.search.SearchContactsFragment;
import io.bytechat.demo.oldrelease.ui.search.SearchFileFragment;
import io.bytechat.demo.oldrelease.ui.search.SearchGroupFragment;
import io.openim.android.ouiconversation.ui.chathistory.PlaceholderFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SearchPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    public String search;
    private static String[] TAB_TITLES = new String[]{};

    public SearchPagerAdapter(Context context, FragmentManager fm, String search) {
        // BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT must for fragment onResume
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.search = search ;
        this.mContext = context;
        TAB_TITLES = mContext.getResources().getStringArray(io.openim.android.ouicore.R.array.array_search);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            SearchAllFragment searchAllFragment = new SearchAllFragment();
            searchAllFragment.search = search ;
            return searchAllFragment;
        }
        if(position == 1) {
            SearchContactsFragment searchContactsFragment = new SearchContactsFragment();
            searchContactsFragment.search = search ;
            return searchContactsFragment;
        }
        if(position == 2) {
            SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
            searchGroupFragment.search = search ;
            return searchGroupFragment;
        }
        if(position == 3) {
            SearchChatHistoryFragment searchChatHistoryFragment = new SearchChatHistoryFragment();
            searchChatHistoryFragment.search = search ;
            return searchChatHistoryFragment;
        }
        if(position == 4) {
            SearchFileFragment fragment = new SearchFileFragment();
            fragment.search = search ;
            return fragment;
        }
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}
