package com.like.frame.rapiddevframe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.entity.Banner;
import com.like.frame.rapiddevframe.entity.Book;
import com.like.rapidui.base.DataParser;
import com.like.rapidui.base.PagingParam;
import com.like.rapidui.base.BaseListFragment;
import com.like.rapidui.ui.SimpleDividerDecoration;
import com.like.rapidui.ui.picasso.transformation.CircleTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezy.ui.view.BannerView;

/**
 * Created By Like on 2017/10/10.
 */
@SuppressWarnings("ALL")
public class HomeFragment extends BaseListFragment<Book> {

    @Override
    public int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar mToolbar = mRootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        fixToolbar(mToolbar);
        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemDragAndSwipeCallback((BaseItemDraggableAdapter) mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        ((BaseItemDraggableAdapter) mAdapter).enableDragItem(itemTouchHelper, R.id.iv_image, true);
        load();
    }

    @Override
    public String getUrl() {
        return "https://api.douban.com/v2/book/search";
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "哈利波特");
        return params;
    }

    @Override
    public PagingParam getPagingParam() {
        return new PagingParam("count", "start");
    }

    @Override
    public DataParser getDataParser() {
        return json -> {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray books = jsonObject.getJSONArray("books");
                return books.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    @Override
    public View getHeadView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_index_head, null);
        BannerView bannerView = view.findViewById(R.id.banner);
        bannerView.setTitleAdapter((BannerView.TitleAdapter<Banner>) Banner::getTitle);
        bannerView.setViewFactory((BannerView.ViewFactory<Banner>) (banner, position, container) -> {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.get().load(banner.getUrl()).into(imageView);
            return imageView;
        });
        List<Banner> banner = new ArrayList<>();
        banner.add(new Banner(R.mipmap.a, "哈哈"));
        banner.add(new Banner(R.mipmap.a1, "呵呵"));
        banner.add(new Banner(R.mipmap.e, "嘿嘿"));
        bannerView.setDataList(banner);
        bannerView.start();
        return view;
    }

    @Override
    public int getItemView() {
        return R.layout.item_book;
    }

    @Override
    public void convert(BaseViewHolder helper, final Book item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_summary, item.getSummary());
        List<String> list = new ArrayList<>();
        list.addAll(item.getAuthor());
        list.addAll(item.getTranslator());
        helper.setText(R.id.tv_info, TextUtils.join("/", list));
        Picasso.get().load(item.getImage()).transform(new CircleTransformation()).into((ImageView) helper.getView(R.id.iv_image));
        helper.itemView.setOnClickListener(v -> showShort(item.getSummary()));
    }

    @Override
    public boolean getEnableDrag() {
        return true;
    }

}
