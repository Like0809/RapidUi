package com.like.frame.rapiddevframe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.like.frame.rapiddevframe.ApiUrl;
import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.entity.Joke;
import com.like.rapidui.base.DataParam;
import com.like.rapidui.base.PagingParam;
import com.like.rapidui.base.BaseListFragment;
import com.like.rapidui.ui.icon.widget.IconTextView;
import com.like.rapidui.ui.picasso.transformation.CircleTransformation;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JokeFragment extends BaseListFragment<Joke> {

    private int mType;

    public static JokeFragment getInstance(int type) {
        JokeFragment instance = new JokeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type", 2);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load();
    }

    @Override
    public String getUrl() {
        return ApiUrl.API_JOKE;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "" + mType);
        return params;
    }

    @Override
    public int getItemView() {
        return R.layout.item_joke_text;
    }

    @Override
    public PagingParam getPagingParam() {
        return new PagingParam("", "page");
    }

    @Override
    public DataParam getDataParam() {
        return new DataParam(200, "code", "msg", "data");
    }

    @Override
    public void convert(BaseViewHolder helper, Joke item) {
        helper.setText(R.id.name, item.getName());
        helper.setText(R.id.text, item.getText());
        if (!TextUtils.isEmpty(item.getProfile_image()))
            Picasso.get().load(item.getProfile_image()).placeholder(R.mipmap.no_pic).error(R.mipmap.no_pic).transform(new CircleTransformation()).into((ImageView) helper.getView(R.id.avatar));
        else
            Picasso.get().load(R.mipmap.no_pic).transform(new CircleTransformation()).into((ImageView) helper.getView(R.id.avatar));
        ((IconTextView) helper.getView(R.id.comment)).setNormalText(String.format("{fa-comments-o} %s", item.getComment()));
        ((IconTextView) helper.getView(R.id.hate)).setNormalText(String.format("{fa-thumbs-o-down} %s", item.getHate()));
        ((IconTextView) helper.getView(R.id.love)).setNormalText(String.format("{fa-thumbs-o-up} %s", item.getLove()));
        helper.setText(R.id.time, item.getCreated_at().substring(0, 10));
        if (mType == 3) {
            helper.setGone(R.id.image, true);
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            int num = (int) (Math.random() * 5 + 1);
            for (int i = 1; i < num; i++) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(item.getImage0());
                info.setBigImageUrl(item.getImage0());
                imageInfo.add(info);
            }
            ((NineGridView) helper.getView(R.id.image)).setAdapter(new NineGridViewClickAdapter(getContext(), imageInfo));
        } else {
            helper.setGone(R.id.image, false);
        }
    }
}


