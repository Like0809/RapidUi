package com.like.frame.rapiddevframe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.activity.BookListActivity;
import com.like.frame.rapiddevframe.activity.JokeListActivity;
import com.like.frame.rapiddevframe.activity.UserInfoActivity;
import com.like.rapidui.base.BaseFragment;

/**
 * Created By Like on 2017/9/29.
 */

public class PersonalFragment extends BaseFragment {
    private Toolbar mToolbar;

    @Override
    public int getContentView() {
        return R.layout.fragment_personal;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setTitle("RapidUi - 个人中心");
        view.findViewById(R.id.ifw_menu_1).setOnClickListener((v) -> jumpTo(JokeListActivity.class));
        view.findViewById(R.id.ifw_menu_2).setOnClickListener((v) -> jumpTo(BookListActivity.class));
        view.findViewById(R.id.ifw_menu_3).setOnClickListener((v) -> jumpTo(UserInfoActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        fixToolbar(mToolbar);
    }

}
