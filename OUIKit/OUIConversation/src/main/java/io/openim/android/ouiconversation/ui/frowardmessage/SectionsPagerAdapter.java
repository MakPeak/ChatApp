package io.openim.android.ouiconversation.ui.frowardmessage;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.openim.android.ouiconversation.ui.chathistory.PlaceholderFragment;
import io.openim.android.ouiconversation.vm.ChatVM;
import io.openim.android.sdk.models.Message;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    private static final String[] TAB_TITLES = new String[]{"My friends","My groups"};
    private final Context mContext;
    String chatID;
    private Message msg ;
    ChatVM chatVM ;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Message msg, ChatVM chatVM) {
        super(fm);
        this.msg = msg ;
        mContext = context;
        this.chatVM = chatVM ;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("chatVM : " + chatVM);
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0) {
            MyFriendsFragment fragment = new MyFriendsFragment(msg,chatVM);
            return fragment;
        }
        if(position == 1) {
            MyGroupsFragment fragment = new MyGroupsFragment(msg,chatVM);
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
