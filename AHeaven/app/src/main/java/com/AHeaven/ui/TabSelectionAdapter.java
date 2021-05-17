package com.AHeaven.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.AHeaven.R;
import com.AHeaven.ui.tabs.FirstPageContainer;
import com.AHeaven.ui.tabs.ProfileFragment;
import com.AHeaven.ui.tabs.QueueFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabSelectionAdapter extends FragmentPagerAdapter {
    static Fragment[] page = new Fragment[3];  //тут хранятся страницы во viewPager

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2,R.string.tab_text_3};
    private final Context mContext; //main activity

    public TabSelectionAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        page[0] = FirstPageContainer.newInstance();
        page[1] = QueueFragment.newInstance();
        page[2] = ProfileFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return page[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        //number of pages.
        return page.length;
    }
}