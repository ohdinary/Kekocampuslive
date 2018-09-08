package com.example.dinary.kekocampuslive.fregments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dinary.kekocampuslive.R;

/**
 * Created by zhouwei on 17/4/23.
 */

public class ClassifyFragment extends Fragment {


    private String mFrom;
    public static ClassifyFragment newInstance(String from){
        ClassifyFragment fragment = new ClassifyFragment();
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
        View view = inflater.inflate(R.layout.home_fragment_layout,null);

        return view;
    }
}
