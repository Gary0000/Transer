package com.scott.example.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.scott.example.R;

import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-21 14:00</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskGroupViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private Context mContext;

    private TaskGroupViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TaskGroupViewPagerAdapter(FragmentManager fm,List<Fragment> frgs,Context context) {
        this(fm);
        mFragments = frgs;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return mContext.getString(R.string.upload);
        } else if(position == 1) {
            return mContext.getString(R.string.download);
        }
        return super.getPageTitle(position);
    }
}
