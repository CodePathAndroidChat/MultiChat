package com.example.jason.multichatapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jason.multichatapp.Constants;
import com.example.jason.multichatapp.fragments.LoginFragment;
import com.example.jason.multichatapp.fragments.SignUpFragment;

/**
 * LoginFragmentPagerAdapter-
 */

public class LoginFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 2;
    private static final String[] titles = new String[]{Constants.LOG_IN, Constants.SIGN_UP};
    private Context context;

    public LoginFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                return LoginFragment.newInstance();
            case 1 :
                return SignUpFragment.newInstance();
        }
        return SignUpFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
