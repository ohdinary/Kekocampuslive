package com.example.dinary.kekocampuslive.fregments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.adapters.LiveCellAdapter;
import com.example.dinary.kekocampuslive.beans.LiveCell;
import com.example.dinary.kekocampuslive.editprofile.EditProfileActivity;
import com.example.dinary.kekocampuslive.host.LiveListRequest;
import com.example.dinary.kekocampuslive.search.SearchActivity;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.example.dinary.kekocampuslive.util.UtilImg;
import com.tencent.TIMUserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 */
public class HomeFragment extends Fragment {

    private String mFrom;
    private View view;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    View headerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    LiveCell[] liveCells = new LiveCell[]{
            new LiveCell(001,"10001","小熙","R.drawable.image2","","测试", 3),
            new LiveCell(002,"10002","dinary","R.drawable.image2","","测试", 0),
            new LiveCell(003,"10003","Sharden","R.drawable.image2","","测试", 3),
    };
    private List<LiveCell> liveCellList = new ArrayList<>();
    private LiveCellAdapter adapter;

    public static HomeFragment newInstance(String from){
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null)
            mFrom = getArguments().getString("from");

        //设置有菜单加载
        setHasOptionsMenu(true);
        //刷新列表
        onRefreshEvent();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment_layout, container, false);
        //设置标题栏
        Toolbar toolbar = view.findViewById(R.id.home_tab_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //滑动菜单
        setSlideMenu();
        //直播列表
        setRecycleView();
        //搜索
//        search();

        return view;
    }

    private void search() {
        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
    }

    /**
     * 直播列表
     */
    private void setRecycleView() {
        for (int i=0; i<liveCells.length; i++){
            liveCellList.add(liveCells[i]);
        }
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        adapter = new LiveCellAdapter(liveCellList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext())); //线性显示，类似于listview
//        recyclerView.setLayoutManager(new GridLayoutManager(MyApplication.getContext(),2)); //这里用线性宫格显示 类似于grid view
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.home_refresh);
        /*刷新,从自己服务器获获取直播列表*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshEvent();
            }
        });
    }

    /*todo 刷新直播列表*/
    private void onRefreshEvent() {
        LiveListRequest request = new LiveListRequest();
        //请求成功
        request.setOnRequestListener(new HttpUtil.OnRequestListener<List<LiveCell>>() {
            @Override
            public void onSuccess(List<LiveCell> obj) {
                adapter.removeAllLiveInfo();    //清空列表
//                LogUtil.d("home",obj.toString());
                adapter.pasteLivelistInfo(obj); //注入新值
                LogUtil.d("livelist_celllist",obj.toString());
                swipeRefreshLayout.setRefreshing(false); //隐藏刷新按钮
                Toast.makeText(MyApplication.getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(MyApplication.getContext(),"请求列表失败:"+msg,Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        LiveListRequest.LiveListParam param = new LiveListRequest.LiveListParam();
        param.pageIndex = 0;    //请求第一页的20条数据
        request.sendRequest(param.getUrlParam());
    }

    /**
     * 滑动菜单初始化及事件
     */
    private void setSlideMenu() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();    //取得ActionBar的实例
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);          //让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); //设置一个导航按钮图标
        }
        drawerLayout = view.findViewById(R.id.drawable_layout);
        navView = view.findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_subscribe);  //第一个菜单项默认选中

        //点击侧滑栏打开个人信息编辑界面,要通过navigation获取headlayout实例,再注册监听事件
        headerView = navView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();    //关闭滑动菜单
                return true;
            }
        });
    }

    /**
     * 加载搜索菜单
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();   //Activity的onCreateOptionsMenu()会在之前调用到,先clear()一下, 这样按钮就只有Fragment中设置的自己的了,不会有Activity中的按钮.
        inflater.inflate(R.menu.search_menu, menu);
    }

    /**
     * 菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d("menuid",item.getItemId()+"");
        switch (item.getItemId()){
            //点击图标，打开侧滑栏
            case android.R.id.home:
                //更新侧滑栏控件
                TIMUserProfile profile = MyApplication.getApplication().getSelfProfile();
                if (TextUtils.isEmpty(profile.getFaceUrl()))
                    UtilImg.loadRound(R.drawable.default_avatar, (ImageView) headerView.findViewById(R.id.main_icon_image));
                else
                    UtilImg.loadRound(profile.getFaceUrl(), (ImageView) headerView.findViewById(R.id.main_icon_image));
                ((TextView)headerView.findViewById(R.id.username)).setText(profile.getNickName());
                ((TextView)headerView.findViewById(R.id.Signature)).setText(profile.getSelfSignature());

                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:
                LogUtil.d("search",111+"");
                getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
            default:
        }
        return true;
    }
}
