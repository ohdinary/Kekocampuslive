package com.example.dinary.kekocampuslive.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dinary.kekocampuslive.MyApplication;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 图片加载工具类
 */
public class UtilImg {

    /**
     * 根据url加载图片
     * @param url
     * @param imageView
     */
    public static void load(String url, ImageView imageView){
        Glide.with(MyApplication.context)
                .load(url)
                .into(imageView);
    }

    /**
     * 从本地加载图片
     * @param resId
     * @param imageView
     */
    public static void load(int resId, ImageView imageView){
        Glide.with(MyApplication.context)
                .load(resId)
                .into(imageView);
    }

    /**
     * 加载变形图片
     * @param url
     * @param targetView
     */
    public static void loadRound(String url, ImageView targetView) {
        Glide.with(MyApplication.getContext())
                .load(url)
                .bitmapTransform(new CropCircleTransformation(MyApplication.getContext()))
                .into(targetView);
    }

    public static void loadRound(int resId, ImageView targetView) {
        Glide.with(MyApplication.getContext())
                .load(resId)
                .bitmapTransform(new CropCircleTransformation(MyApplication.getContext()))
                .into(targetView);
    }
}
