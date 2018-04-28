package com.like.frame.rapiddevframe;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.like.frame.rapiddevframe.fragment.HomeFragment;
import com.like.frame.rapiddevframe.fragment.JokeListFragment;
import com.like.frame.rapiddevframe.fragment.PersonalFragment;
import com.like.frame.rapiddevframe.fragment.ListFragment;
import com.like.rapidui.base.BaseActivity;
import com.like.rapidui.ui.icon.widget.IconFontWrapLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private List<Fragment> viewList = new ArrayList<>();
    private String[] mTabText = {"首页", "列表", "笑话", "账户"};
    private String[] icons = new String[]{"{fa-paw}", "{fa-bars}", "{fa-smile-o}", "{fa-star}"};
    private String[] selectedIcons = new String[]{"{fa-paw}", "{fa-bars}", "{fa-smile-o spin}", "{fa-star}"};
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewList.add(new HomeFragment());
        viewList.add(new ListFragment());
        viewList.add(new JokeListFragment());
        viewList.add(new PersonalFragment());
        mTabLayout = findViewById(R.id.tab);
        mTabLayout.setOnApplyWindowInsetsListener((view, windowInsets) -> windowInsets.consumeSystemWindowInsets());
        ViewPager viewPager = findViewById(R.id.viewpager);
        CusFragmentPagerAdapter adapter = new CusFragmentPagerAdapter(getSupportFragmentManager(), viewList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(viewPager);
        setupTabs();
    }

    @SuppressLint("All")
    private void setupTabs() {
        if (mTabLayout == null || mTabLayout.getTabCount() <= 0) return;
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
            IconFontWrapLayout layout = view.findViewById(R.id.ivw_tab);
            layout.setTitle(mTabText[i]).setIcon(icons[i]).setIconSelected(selectedIcons[i]).commit();
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab == null) continue;
            tab.setCustomView(view);
        }
    }

    private class CusFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        CusFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabText[position];
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}
