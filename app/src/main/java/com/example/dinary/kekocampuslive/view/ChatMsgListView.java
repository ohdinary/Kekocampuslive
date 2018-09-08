package com.example.dinary.kekocampuslive.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.ChatMsgInfo;
import com.example.dinary.kekocampuslive.util.UtilImg;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgListView extends RelativeLayout {
    private ListView mChatMsgList;
    private ListViewAdapter adapter;

    public ChatMsgListView(Context context) {
        super(context);
        init();
    }
    public ChatMsgListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ChatMsgListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_msg_list,this,true);
        findAllViews();
    }

    private void findAllViews() {
        mChatMsgList = findViewById(R.id.chat_msg_list);
        adapter = new ListViewAdapter();
        mChatMsgList.setAdapter(adapter);
        //点击消息列表获取用户信息
        mChatMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatMsgInfo msgInfo = adapter.getItem(i);
                showUserInfoDialog(msgInfo.getSenderId());
            }
        });
    }

    /*获取用户信息*/
    private void showUserInfoDialog(String sendId) {
        List<String> ids = new ArrayList<>();
        ids.add(sendId);
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(),"获取用户信息失败!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                Context context = ChatMsgListView.this.getContext();
                if (context instanceof Activity){
                    Toast.makeText(context,timUserProfile.getNickName(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*添加消息*/
    public void addMsgInfo(ChatMsgInfo info){
        if (info != null){
            adapter.addMsgInfo(info);
            mChatMsgList.smoothScrollToPosition(adapter.getCount());
        }
    }
    public void addMsgInfos(List<ChatMsgInfo> info){
        if (info != null){
            adapter.addMsgInfos(info);
            mChatMsgList.smoothScrollToPosition(adapter.getCount());
        }
    }

    class ListViewAdapter extends BaseAdapter{

        List<ChatMsgInfo> msgInfos = new ArrayList<>();

        public void addMsgInfo(ChatMsgInfo info) {
            if (info != null) {
                msgInfos.add(info);
                notifyDataSetChanged();
            }
        }
        public void addMsgInfos(List<ChatMsgInfo> infos) {
            if (infos != null) {
                msgInfos.addAll(infos);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return msgInfos.size();
        }

        @Override
        public ChatMsgInfo getItem(int i) {
            return msgInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MsgHolder holder;
            if (view == null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_chat_msg_list_item,null);
                holder = new MsgHolder(view);
                view.setTag(holder);
            }else {
                holder = (MsgHolder) view.getTag();
            }
            holder.bindData(msgInfos.get(i));
            return view;
        }
    }

    private class MsgHolder{
        private ImageView userAvatar;
        private TextView msgContent;

        private ChatMsgInfo info;

        public MsgHolder(View view) {
            userAvatar = view.findViewById(R.id.sender_avatar);
            msgContent = view.findViewById(R.id.chat_content);
        }

        /*绑定消息列表项数据*/
        public void bindData(ChatMsgInfo info){
            this.info = info;
            String avatarUrl = info.getAvatar();
            if (avatarUrl != null){
                UtilImg.loadRound(avatarUrl,userAvatar);    //加载用户头像
            }else {
                UtilImg.loadRound(R.drawable.default_avatar,userAvatar);    //加载默认头像
            }
            msgContent.setText(info.getContent());  //聊天消息
        }
    }
}
