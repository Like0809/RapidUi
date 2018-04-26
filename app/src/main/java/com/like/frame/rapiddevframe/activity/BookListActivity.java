package com.like.frame.rapiddevframe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.like.frame.rapiddevframe.R;
import com.like.frame.rapiddevframe.entity.Book;
import com.like.rapidui.DataParser;
import com.like.rapidui.PagingParam;
import com.like.rapidui.activity.BaseListActivity;
import com.like.rapidui.ui.picasso.transformation.RoundedTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookListActivity extends BaseListActivity<Book> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar.setTitle("图书列表");
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


    //对于各色各样的分页参数，可以在RapidUI类中进行全局设置，也可以在实际的Activity中重载此方法
    @Override
    public PagingParam getPagingParam() {
        return new PagingParam("count", "start");
    }


    //对于奇形怪状的数据，可以自己重载解析器
    //找到对应的列表数据作为返回值
    // TODO: 2018/4/26 有空可以设计为只需要返回数据路径（如：/data/books），提取数据的代码由框架负责
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
        helper.itemView.setOnClickListener(v -> showShort(item.getSummary()));
    }
}