package com.sinch.messagingtutorial.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

class SectionsPagerAdapter extends FragmentPagerAdapter
{
    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if(position == 0)
        {
            fragment = new InboxFragment();
        }
        if(position == 1)
        {
            fragment = new FriendsFragment();
        }
        if(position == 2)
        {
            fragment = new ListUsersActivity();
        }
        if(position == 3)
        {
            fragment = new Commander();
        }
        return fragment;
    }

    @Override
    public int getCount()
    {
        // Show 4 total pages.
        return 4;
    }
    @Override
    public CharSequence getPageTitle(int position)
    {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return mContext.getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return mContext.getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }
}