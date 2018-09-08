package com.example.dinary.kekocampuslive;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dinary.kekocampuslive.fregments.ClassifyFragment;
import com.example.dinary.kekocampuslive.fregments.DiscoveryFragment;
import com.example.dinary.kekocampuslive.fregments.HomeFragment;
import com.example.dinary.kekocampuslive.fregments.SettingFragment;

/**
 * 主界面tab初始化类
 */
public class DataGenerator {

    public static final int[] mTabRes = new int[]{R.drawable.tab_home_selector,R.drawable.tab_discovery_selector,R.drawable.tab_publish_live,R.drawable.tab_attention_selector,R.drawable.tab_profile_selector};
    public static final int[] mTabResPressed = new int[]{R.drawable.ic_tab_strip_icon_feed_selected,R.drawable.ic_tab_strip_icon_category_selected,R.drawable.tab_publish_live,R.drawable.ic_tab_strip_icon_pgc_selected,R.drawable.ic_tab_strip_icon_profile_selected};
    public static final String []mTabTitle = new String[]{"首页","分类","","发现","设置"};

    /*获取所有fragment*/
    public static Fragment[] getFragments(String from){
        Fragment fragments[] = new Fragment[4];
        fragments[0] = HomeFragment.newInstance(from);
        fragments[1] = ClassifyFragment.newInstance(from);
        fragments[2] = DiscoveryFragment.newInstance(from);
        fragments[3] = SettingFragment.newInstance(from);
        return fragments;
    }

    /**
     * 获取Tab显示的View
     * @param context
     * @param position
     * @return
     */
    public static View getTabView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_content, null);
        ImageView tabIcon = view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(DataGenerator.mTabRes[position]);  //取得对应位置的图片
        TextView tabText = view.findViewById(R.id.tab_content_text);
        tabText.setText(DataGenerator.mTabTitle[position]);
        if (position == 2)
            tabIcon.setPaddingRelative(0,0,0,6);
        return view;
    }
}
