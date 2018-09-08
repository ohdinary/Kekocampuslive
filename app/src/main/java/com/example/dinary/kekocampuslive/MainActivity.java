package com.example.dinary.kekocampuslive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dinary.kekocampuslive.createlive.CreateLiveActivity;
import com.example.dinary.kekocampuslive.util.LogUtil;

public class MainActivity extends BaseActivity{

    private TabLayout mTabLayout;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        /*获取所有碎片*/
        mFragments = DataGenerator.getFragments("TabLayout Tab");
        initView();

        /*主界面*/
//        SnapHelperFragment fragment = new SnapHelperFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.activity_main,fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //todo 搜索结果列表
        if (!TextUtils.isEmpty(MyApplication.hostId)){
            LogUtil.d("search_hostId",MyApplication.hostId);
            View view = mTabLayout.getTabAt(2).getCustomView();
            ImageView icon = view.findViewById(R.id.tab_content_image);
            TextView text = view.findViewById(R.id.tab_content_text);
            icon.setImageResource(DataGenerator.mTabResPressed[2]);
            text.setTextColor(getResources().getColor(android.R.color.black));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragments[2]).commit();
        }
    }

    /**
     * 初始化底部导航条和主界面
     */
    private void initView() {
        mTabLayout = findViewById(R.id.bottom_tab_layout);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //替换tab对应的页面
                onTabItemSelected(tab.getPosition());

                //Tab选中后，改变各个tab的状态
                for (int i=0; i<mTabLayout.getTabCount(); i++){
                    View view = mTabLayout.getTabAt(i).getCustomView();
                    ImageView icon = view.findViewById(R.id.tab_content_image);
                    TextView text = view.findViewById(R.id.tab_content_text);
                    if (i == tab.getPosition()) { //选中状态
//                        mTabLayout.getTabAt(i).setIcon(getResources().getDrawable(DataGenerator.mTabResPressed[i]));
                        icon.setImageResource(DataGenerator.mTabResPressed[i]);
                        text.setTextColor(getResources().getColor(android.R.color.black));
                    } else {
//                        mTabLayout.getTabAt(i).setIcon(getResources().getDrawable(DataGenerator.mTabRes[i]));
                        icon.setImageResource(DataGenerator.mTabRes[i]);
                        text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
//        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.tab_home_selector)).setText(DataGenerator.mTabTitle[0]));
//        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.tab_attention_selector)).setText(DataGenerator.mTabTitle[1]));
//        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.tab_discovery_selector)).setText(DataGenerator.mTabTitle[2]));
//        mTabLayout.addTab(mTabLayout.newTab().setIcon(getResources().getDrawable(R.drawable.tab_profile_selector)).setText(DataGenerator.mTabTitle[3]));

        // 提供自定义的布局添加Tab View
        for(int i=0; i<=4; i++){
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(DataGenerator.getTabView(this, i)));
        }
    }

    /**
     * 替换选中tab对应主界面显示的内容
     * @param position
     */
    private void onTabItemSelected(int position){
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = mFragments[0];
                break;
            case 1:
                fragment = mFragments[1];
                break;
            case 2:
                //发布直播
                startActivity(new Intent(MainActivity.this, CreateLiveActivity.class));
                break;
            case 3:
                fragment = mFragments[2];
                break;
            case 4:
                fragment = mFragments[3];
                break;
            default:
                break;
        }
        //替换当前tab对应主界面显示的fragment
        if (fragment!=null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


}
