package com.like.frame.rapiddevframe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.entity.Banner;
import com.like.frame.rapiddevframe.entity.Book;
import com.like.frame.rapiddevframe.entity.BookWrapper;
import com.like.rapidui.Request;
import com.like.rapidui.RequestListener;
import com.like.rapidui.RequestStatus;
import com.like.rapidui.callback.DataLoader;
import com.like.rapidui.fragment.BaseListFragment;
import com.like.rapidui.network.ApiMessage;
import com.like.rapidui.network.OkHttpUtils;
import com.like.rapidui.network.callback.StringCallback;
import com.like.rapidui.ui.SimpleDividerDecoration;
import com.like.rapidui.ui.picasso.transformation.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezy.ui.view.BannerView;
import okhttp3.Call;

import static com.like.rapidui.RequestListener.FAILED;
import static com.like.rapidui.RequestListener.SUCCESS;

/**
 * Created By Like on 2017/10/10.
 */

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
        refresh();
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
    public DataLoader getDataLoader() {
        return new DataLoader() {
            @Override
            public void load(final Request request, final RequestListener requestListener) {
                OkHttpUtils.post()
                        .url(request.getUrl())
                        .params(request.getParams())
                        .headers(request.getHeaders())
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                requestListener.onError(RequestStatus.NET_ERR, request, 0, e.getMessage());
                                requestListener.onComplete(request, FAILED);
                            }

                            @Override
                            public void onResponse(String response, int id) {

                                if (ApiMessage.isApiMessage(response)) {
                                    ApiMessage message = new Gson().fromJson(response, ApiMessage.class);
                                    requestListener.onError(RequestStatus.API_ERR, request, message.getCode(), message.getMessage());
                                    requestListener.onComplete(request, FAILED);
                                } else {
                                    BookWrapper bookWrapper = new Gson().fromJson(response, BookWrapper.class);
                                    requestListener.onResponse(request, new Gson().toJson(bookWrapper.getBooks()));
                                    requestListener.onComplete(request, SUCCESS);
                                }
                            }

                            @Override
                            public void onAfter(int id, int status) {
                            }
                        });
            }
        };
    }

    @Override
    public View getHeadView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_index_head, null);
        BannerView bannerView = view.findViewById(R.id.banner);
        bannerView.setTitleAdapter(new BannerView.TitleAdapter<Banner>() {
            @Override
            public CharSequence getTitle(Banner banner) {
                return banner.getTitle();
            }
        });
        bannerView.setViewFactory(new BannerView.ViewFactory<Banner>() {
            @Override
            public View create(Banner banner, int position, ViewGroup container) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.get().load(banner.getUrl()).into(imageView);
                return imageView;
            }
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
        Picasso.get().load(item.getImage()).transform(new RoundedTransformation(20, 0)).into((ImageView) helper.getView(R.id.iv_image));
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShort(item.getSummary());
            }
        });
    }

}
