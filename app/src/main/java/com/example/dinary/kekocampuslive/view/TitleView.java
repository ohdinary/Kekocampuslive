package com.example.dinary.kekocampuslive.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.util.UtilImg;
import com.example.dinary.kekocampuslive.widget.UserInfoDialog;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主播观众列表栏
 */
public class TitleView extends LinearLayout {

    private ImageView hostAvatarImgView;//主播头像
    private TextView watchersNumView;//观看人数。
    private int watcherNum = 0;

    private RecyclerView watcherListView;   //观众列表
    private WatcherAdapter watcherAdapter;

    private String hostId; //主播id

    public TitleView(Context context) {
        super(context);
        init();
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.view_title, this, true);

        findAllViews();
    }

    private void findAllViews() {
        hostAvatarImgView = (ImageView) findViewById(R.id.host_avatar);
        watchersNumView = (TextView) findViewById(R.id.watchers_num);
        hostAvatarImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点击头像，显示主播详情信息对话框
                showUserInfoDialog(hostId);
            }
        });

        watcherListView = (RecyclerView) findViewById(R.id.watch_list);
        watcherListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        watcherListView.setLayoutManager(layoutManager);
        //设置adapter
        watcherAdapter = new WatcherAdapter(getContext());
        watcherListView.setAdapter(watcherAdapter);
    }

    /*显示用户详情信息*/
    private void showUserInfoDialog(String senderId) {
        List<String> ids = new ArrayList<String>();
        ids.add(senderId);
        TIMFriendshipManager.getInstance().getUsersProfile(ids, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(TitleView.this.getContext(), "请求用户信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                Context context = TitleView.this.getContext();
                if(context instanceof Activity) {
                    UserInfoDialog userInfoDialog = new UserInfoDialog((Activity) context, timUserProfiles.get(0));
                    userInfoDialog.show();
                }
            }
        });
    }

    /*设置主播信息：id、头像*/
    public void setHost(TIMUserProfile userProfile) {
        if(userProfile == null){
            //加载默认头像
            UtilImg.loadRound(R.drawable.default_avatar, hostAvatarImgView);
        }else {
            hostId = userProfile.getIdentifier();   //主播id
            String avatarUrl = userProfile.getFaceUrl();   //主播头像
            if (TextUtils.isEmpty(avatarUrl)) {
                UtilImg.loadRound(R.drawable.default_avatar, hostAvatarImgView);
            } else {
                UtilImg.loadRound(avatarUrl, hostAvatarImgView);
            }
        }
    }

    //添加观众信息
    public void addWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            watcherAdapter.addWatcher(userProfile);
            watcherNum++;
            watchersNumView.setText("观众:" + watcherNum);
        }
    }

    public void addWatchers(List<TIMUserProfile> userProfileList){
        if(userProfileList != null){
            watcherAdapter.addWatchers(userProfileList);
            watcherNum+= userProfileList.size();
            watchersNumView.setText("观众:" + watcherNum);
        }
    }

    public void removeWatcher(TIMUserProfile userProfile) {
        if (userProfile != null) {
            watcherAdapter.removeWatcher(userProfile);
            watcherNum--;
            watchersNumView.setText("观众:" + watcherNum);
        }
    }


    public class WatcherAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private List<TIMUserProfile> watcherList = new ArrayList<TIMUserProfile>();     //观众信息
        private Map<String , TIMUserProfile> watcherMap = new HashMap<String , TIMUserProfile>();//观众id—观众信息

        WatcherAdapter(Context context) {
            mContext = context;
        }

        /*添加观众信息*/
        public void addWatchers(List<TIMUserProfile> userProfileList){
            if(userProfileList == null){
                return;
            }
            for(TIMUserProfile userProfile : userProfileList){
                if (userProfile != null) {
                    boolean inWatcher = watcherMap.containsKey(userProfile.getIdentifier());
                    if(!inWatcher) {
                        watcherList.add(userProfile);
                        watcherMap.put(userProfile.getIdentifier(), userProfile);
                    }
                }
            }
            notifyDataSetChanged();
        }
        public void addWatcher(TIMUserProfile userProfile) {
            if (userProfile != null) {
                boolean inWatcher = watcherMap.containsKey(userProfile.getIdentifier());
                if(!inWatcher) {
                    watcherList.add(userProfile);
                    watcherMap.put(userProfile.getIdentifier(), userProfile);
                    notifyDataSetChanged();
                }
            }
        }

        //从观众列表中删除观众信息
        public void removeWatcher(TIMUserProfile userProfile) {
            if (userProfile == null) {
                return;
            }
            TIMUserProfile targetUser = watcherMap.get(userProfile.getIdentifier());
            if(targetUser != null) {
                watcherList.remove(targetUser);
                watcherMap.remove(targetUser.getIdentifier());
                notifyDataSetChanged();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_watcher, parent, false);
            WatcherHolder holder = new WatcherHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof WatcherHolder) {
                ((WatcherHolder) holder).bindData(watcherList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return watcherList.size();
        }

        private class WatcherHolder extends RecyclerView.ViewHolder {

            private ImageView avatarImg;

            public WatcherHolder(View itemView) {
                super(itemView);
                avatarImg = (ImageView) itemView.findViewById(R.id.user_avatar);
            }

            public void bindData(final TIMUserProfile userProfile) {
                String avatarUrl = userProfile.getFaceUrl();
                if (TextUtils.isEmpty(avatarUrl)) {
                    UtilImg.loadRound(R.drawable.default_avatar, avatarImg);
                } else {
                    UtilImg.loadRound(avatarUrl, avatarImg);
                }
                avatarImg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUserInfoDialog(userProfile.getIdentifier());
                    }
                });
            }
        }
    }

}
