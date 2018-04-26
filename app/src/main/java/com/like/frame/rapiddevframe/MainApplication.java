package com.like.frame.rapiddevframe;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.like.rapidui.PagingParam;
import com.like.rapidui.RapidUi;
import com.like.rapidui.ui.icon.Iconify;
import com.like.rapidui.ui.icon.fonts.entypo.EntypoModule;
import com.like.rapidui.ui.icon.fonts.fontawesome.FontAwesomeModule;
import com.like.rapidui.ui.icon.fonts.material.MaterialModule;
import com.lzy.ninegrid.NineGridView;
import com.squareup.picasso.Picasso;

/**
 * Created By Like on 2016/8/11.
 */
@SuppressWarnings("unused")
public class MainApplication extends Application {
    private static MainApplication mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Iconify.with(new MaterialModule())
                .with(new EntypoModule())
                .with(new FontAwesomeModule());
        RapidUi rapidUi = RapidUi.getInstance(this);
        //rapidUi.setPagingParam(new PagingParam("count", "start"));
        NineGridView.setImageLoader(new PicassoImageLoader());
    }

    private class PicassoImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Picasso.get().load(url)//
                    .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    public static Context getAppContext() {
        return mApp;
    }

    public static MainApplication getApp() {
        return mApp;
    }

}
