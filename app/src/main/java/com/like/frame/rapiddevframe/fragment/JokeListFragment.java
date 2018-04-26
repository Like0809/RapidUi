package com.like.frame.rapiddevframe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.like.frame.rapiddevframe.R;
import com.like.rapidui.fragment.BaseFragment;

import java.util.ArrayList;

public class JokeListFragment extends BaseFragment {

    @Override
    public int getContentView() {
        return R.layout.fragment_joke;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ViewPager mPager = view.findViewById(R.id.viewPager);
        TabLayout mTab = view.findViewById(R.id.tab);
        fixToolbar(toolbar);
        final ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(JokeFragment.getInstance(2));
        fragments.add(JokeFragment.getInstance(3));
        mPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "文字";
                    case 1:
                        return "图片";
                }
                return null;
            }
        });
        mTab.setupWithViewPager(mPager, true);
    }
}
