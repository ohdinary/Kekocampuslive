package com.example.dinary.kekocampuslive.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.ChatMsgInfo;
import com.example.dinary.kekocampuslive.util.LogUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * 弹幕处理
 */
public class DanmuView extends LinearLayout{
    private static final String TAG = DanmuView.class.getSimpleName();
    private DanmuItemView item1, item2, item3, item4;
    private List<ChatMsgInfo> msgInfos = new LinkedList<>(); //itemview不够时用来缓存消息的队列

    public DanmuView(Context context) {
        super(context);
        init();
    }

    public DanmuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanmuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu, this, true);
        findAllViews();
    }

    private void findAllViews() {
        item1 = (DanmuItemView) findViewById(R.id.danmu1);
        item2 = (DanmuItemView) findViewById(R.id.danmu2);
        item3 = (DanmuItemView) findViewById(R.id.danmu3);
        item4 = (DanmuItemView) findViewById(R.id.danmu4);

        //初始化都不可见
        item4.setVisibility(INVISIBLE);
        item1.setVisibility(INVISIBLE);
        item2.setVisibility(INVISIBLE);
        item3.setVisibility(INVISIBLE);

        item1.setOnAvaliableListener(avaliableListener);
        item2.setOnAvaliableListener(avaliableListener);
        item3.setOnAvaliableListener(avaliableListener);
        item4.setOnAvaliableListener(avaliableListener);
    }

    private DanmuItemView.OnAvaliableListener avaliableListener = new DanmuItemView.OnAvaliableListener() {
        @Override
        public void onAvaliable() {
            //有可用的itemview
            //从msgList中获取之前缓存下来的消息，然后显示出来。
            if (msgInfos.size() > 0){
                LogUtil.d(TAG,"显示缓存弹幕");
                ChatMsgInfo msgInfo = msgInfos.remove(0);
                addMsgInfo(msgInfo);
            }
        }
    };

    /*添加弹幕消息*/
    public void addMsgInfo(ChatMsgInfo msgInfo){
        DanmuItemView itemView = getAvaliableItemView();
        if (itemView == null){
            //没有可用itemview
            msgInfos.add(msgInfo);
        }else {
            //有可用itemview
            itemView.showMsgInfo(msgInfo);
        }
    }

    /*获取可用弹幕View*/
    public DanmuItemView getAvaliableItemView(){
        if (item1.getVisibility() == INVISIBLE)
            return item1;
        if (item2.getVisibility() == INVISIBLE)
            return item2;
        if (item3.getVisibility() == INVISIBLE)
            return item3;
        if (item4.getVisibility() == INVISIBLE)
            return item4;
        return null;
    }
}
