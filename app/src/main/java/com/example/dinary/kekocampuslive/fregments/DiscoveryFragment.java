package com.example.dinary.kekocampuslive.fregments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.adapters.LiveCellAdapter;
import com.example.dinary.kekocampuslive.beans.LiveCell;
import com.example.dinary.kekocampuslive.host.SearchRequest;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouwei on 17/4/23.
 */

public class DiscoveryFragment extends Fragment {

    private String mFrom;

    private View view;
    private RecyclerView recyclerView;
    private List<LiveCell> liveCells = new ArrayList<>();   //数据
    private LiveCellAdapter adapter;

    public static DiscoveryFragment newInstance(String from){
        DiscoveryFragment fragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mFrom = getArguments().getString("from");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discovery,null);
        recyclerView = view.findViewById(R.id.discovery_recycle);

        updateList();
        requestForData(MyApplication.hostId);

        return view;
    }

    //更新组件
    private void updateList() {
        adapter = new LiveCellAdapter(liveCells);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getContext())); //线性显示，类似于listview
//        recyclerView.setLayoutManager(new GridLayoutManager(MyApplication.getContext(),2)); //这里用线性宫格显示 类似于grid view
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        recyclerView.setAdapter(adapter);
    }

    //todo 从服务器查询数据
    private void requestForData(String hostId) {
        SearchRequest request = new SearchRequest();
        //请求成功
        request.setOnRequestListener(new HttpUtil.OnRequestListener<List<LiveCell>>() {
            @Override
            public void onSuccess(List<LiveCell> obj) {
                adapter.removeAllLiveInfo();    //清空列表
//                LogUtil.d("home",obj.toString());
                adapter.pasteLivelistInfo(obj); //注入新值
                if (obj!=null)
                    LogUtil.d("livelist_celllist",obj.toString());
                Toast.makeText(MyApplication.getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(MyApplication.getContext(),"请求列表失败:"+msg,Toast.LENGTH_SHORT).show();
            }
        });

        SearchRequest.LiveListParam param = new SearchRequest.LiveListParam();
        param.hostId = hostId;    //请求参数
        if (!TextUtils.isEmpty(hostId)){
            request.sendRequest(param.getUrlParam());
            LogUtil.d("search_url",param.getUrlParam());
            MyApplication.hostId = "";
        }
    }
}
