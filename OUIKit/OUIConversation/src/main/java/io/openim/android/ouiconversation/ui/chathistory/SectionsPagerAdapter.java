package io.openim.android.ouiconversation.ui.chathistory;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.openim.android.ouiconversation.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static String[] TAB_TITLES;
    private final Context mContext;
    String chatID;

    public SectionsPagerAdapter(Context context, FragmentManager fm,String chatID) {
        super(fm);
        this.chatID = chatID ;
        mContext = context;
        TAB_TITLES = new String[]{mContext.getString(io.openim.android.ouicore.R.string.information),mContext.getString(io.openim.android.ouicore.R.string.picture),
            mContext.getString(io.openim.android.ouicore.R.string.video),mContext.getString(io.openim.android.ouicore.R.string.file)};
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0) {
            InformationFragment fragment = new InformationFragment();
            fragment.chatID = chatID ;
            return fragment;
        }
        if(position == 1) {
            PicturesFragment fragment = new PicturesFragment();
            fragment.chatID = chatID ;
            return fragment;
        }
        if(position == 2) {
            VideosFragment fragment = new VideosFragment();
            fragment.chatID = chatID ;
            return fragment;
        }
        if(position == 3) {
            FilesFragment fragment = new FilesFragment();
            fragment.chatID = chatID ;
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
        // Show 2 total pages.
        return TAB_TITLES.length;
    }
}
