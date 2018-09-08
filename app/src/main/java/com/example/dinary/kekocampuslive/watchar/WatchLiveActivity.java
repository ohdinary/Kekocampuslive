package com.example.dinary.kekocampuslive.watchar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.ChatMsgInfo;
import com.example.dinary.kekocampuslive.beans.GiftInfo;
import com.example.dinary.kekocampuslive.model.Constants;
import com.example.dinary.kekocampuslive.model.GiftCmdInfo;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.example.dinary.kekocampuslive.view.BottomChatView;
import com.example.dinary.kekocampuslive.view.BottomControlView;
import com.example.dinary.kekocampuslive.view.ChatMsgListView;
import com.example.dinary.kekocampuslive.view.DanmuView;
import com.example.dinary.kekocampuslive.view.GiftFullView;
import com.example.dinary.kekocampuslive.view.GiftRepeatView;
import com.example.dinary.kekocampuslive.view.TitleView;
import com.example.dinary.kekocampuslive.view.VipEnterView;
import com.example.dinary.kekocampuslive.widget.GiftSelectDialog;
import com.example.dinary.kekocampuslive.widget.SizeChangeRelativeLayout;
import com.google.gson.Gson;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import common.Common;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tyrantgit.widget.HeartLayout;

/**
 * 观看直播
 */
public class WatchLiveActivity extends AppCompatActivity {

    private static final String TAG = "gift";
    private SizeChangeRelativeLayout mSizeChangeLayout;
    private TitleView titleView;
    private AVRootView mLiveView;
    private BottomControlView mControlView;
    private BottomChatView mChatView;
    private ChatMsgListView mChatListView;
    private VipEnterView mVipEnterView;
    private DanmuView mDanmuView;
    private GiftSelectDialog giftSelectDialog;

    private Timer heartTimer = new Timer();
    private Random heartRandom = new Random();
    private GiftFullView giftFullView;
    private GiftRepeatView giftRepeatView;
    private HeartLayout heartLayout;

    private int roomId;     //房间id
    private String hostId;  //主播id

//    private HeartBeatRequest mHeartBeatRequest = null;
//    private Timer heartBeatTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_live);

        findAllViews();
        joinRoom();
    }

    /*进入直播房间*/
    private void joinRoom() {
        roomId = getIntent().getIntExtra("roomId", -1);
        hostId = getIntent().getStringExtra("hostId");
        LogUtil.d("hostId+roomId:",hostId+"  "+roomId);
        if (roomId<0 || TextUtils.isEmpty(hostId)){
            Toast.makeText(WatchLiveActivity.this,"房间号不正确!",Toast.LENGTH_LONG).show();
//            finish();
            return;
        }

        /*接收到消息：聊天信息、弹幕、礼物*/
        ILVLiveConfig config = MyApplication.getApplication().getmLiveConfig();
        config.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
                //文本消息
            }

            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                //接收到自定义消息
                if (cmd.getCmd() == Constants.CMD_CHAT_MSG_LIST) {
                    String content = cmd.getParam();
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, userProfile.getFaceUrl());
                    mChatListView.addMsgInfo(info);
                } else if (cmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU) {
                    //弹幕消息
                    String content = cmd.getParam();
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, userProfile.getFaceUrl());
                    mChatListView.addMsgInfo(info);

                    String name = userProfile.getNickName();
                    if (TextUtils.isEmpty(name)) {
                        name = userProfile.getIdentifier();
                    }
                    ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content, id, userProfile.getFaceUrl(), name);
                    mDanmuView.addMsgInfo(danmuInfo);
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_LEAVE) {
                    //用户离开消息
                    if (hostId.equals(userProfile.getIdentifier())) {
                        //主播退出直播，
                        quitRoom();
                    } else {
                        //观众退出直播
                        titleView.removeWatcher(userProfile);
                    }
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_ENTER) {
                    titleView.addWatcher(userProfile);
                    mVipEnterView.showVipEnter(userProfile);
                }
            }

            @Override
            public void onNewOtherMsg(TIMMessage message) {
                //其他消息
            }
        });

        //加入房间配置项
        ILVLiveRoomOption watchOption = new ILVLiveRoomOption(hostId)
                .autoCamera(false) //是否自动打开摄像头
                .controlRole("Guest") //角色设置
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO) //权限设置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO) //是否开始半自动接收
                .autoMic(false);//是否自动打开mic
        LogUtil.d("joinroom","使用腾讯SDK进入房间");

        //todo 加入房间
        ILVLiveManager.getInstance().joinRoom(roomId, watchOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
//                加入房间成功
                //开始心形动画
                startHeartAnim();
                //同时发送进入直播的消息。
                sendEnterRoomMsg();
                //显示主播的头像
                updateTitleView();
                //开始心跳包
//                startHeartBeat();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(WatchLiveActivity.this,"直播已结束",Toast.LENGTH_SHORT).show();
                quitRoom();
            }
        });

        LogUtil.d("joinroom","成功");
    }

//    private void startHeartBeat() {
//        heartBeatTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                //发送心跳包
//                if (mHeartBeatRequest == null) {
//                    mHeartBeatRequest = new HeartBeatRequest();
//                }
//                String userId = MyApplication.getApplication().getSelfProfile().getIdentifier();
//                String url = mHeartBeatRequest.getUrl(""+roomId, userId);
//                mHeartBeatRequest.sendRequest(url);
//            }
//        }, 0, 4000); //4秒钟 。服务器是10秒钟去检测一次。
//    }

    //TODO 更新观众栏信息
    private void updateTitleView() {
        List<String> list = new ArrayList<String>();
        list.add(hostId);
        //获取主播信息
        TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                //失败：加载默认头像
                titleView.setHost(null);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                //只有一个主播的信息
                titleView.setHost(timUserProfiles.get(0));
            }
        });
        // 添加自己的头像到titleView上。
        titleView.addWatcher(MyApplication.getApplication().getSelfProfile());

        /*todo 由从服务器获取的观众id从云通信SDK获取观众信息,并加入到观众列表中*/
        GetWatcherRequest watcherRequest = new GetWatcherRequest();
        watcherRequest.setOnRequestListener(new HttpUtil.OnRequestListener<Set<String>>() {
            @Override
            public void onSuccess(Set<String> watchers) {
                if (watchers == null) {
                    return;
                }
                List<String> watcherList = new ArrayList<String>();
                watcherList.addAll(watchers);
                /**
                 * 获取用户基本资料（不包括：备注，好友分组）
                 * @param users 要获取资料的用户 identifier 列表
                 * @param cb 回调，OnSuccess 函数的参数中返回包含相应用户的{@see TIMUserProfile}列表
                 */
                TIMFriendshipManager.getInstance().getUsersProfile(watcherList, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                        //失败：
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        //添加已经在房间的观众信息
                        titleView.addWatchers(timUserProfiles);
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                //失败：
            }
        });
        //todo 从服务器获取观众id
        String watcherRequestUrl = watcherRequest.getUrl(roomId+ "");
        watcherRequest.sendRequest(watcherRequestUrl);
        //将观众id加入到服务器
        String joinRoomAddress = watcherRequest.getJoinRoomUrl(MyApplication.getApplication().getSelfProfile().getIdentifier(),roomId+"");
        watcherRequest.sendOkHttpRequest(joinRoomAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d("join","加入房间失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("join","加入房间成功！");
            }
        });
    }

    private void sendEnterRoomMsg() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_ENTER);
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void startHeartAnim() {
        heartTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                heartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        heartLayout.addHeart(getRandomColor());
                    }
                });
            }
        }, 0, 1000); //1秒钟
    }

    private int getRandomColor() {
        return Color.rgb(heartRandom.nextInt(255), heartRandom.nextInt(255), heartRandom.nextInt(255));
    }

    private void findAllViews() {
        mSizeChangeLayout = (SizeChangeRelativeLayout) this.findViewById(R.id.size_change);
        mSizeChangeLayout.setOnSizeChangeListener(new SizeChangeRelativeLayout.OnSizeChangeListener() {
            @Override
            public void onLarge() {
                //键盘隐藏
                mChatView.setVisibility(View.INVISIBLE);
                mControlView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSmall() {
                //键盘显示
            }
        });

        titleView = (TitleView) findViewById(R.id.title_view);

        mLiveView = (AVRootView) this.findViewById(R.id.watch_live_view);
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);

        //底部控制栏
        mControlView = (BottomControlView) this.findViewById(R.id.bottom_control_view);
        mControlView.setIsHost(false);  //不是直播
        mControlView.setControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChatListener() {
                //点击了聊天按钮，显示聊天操作栏
                mChatView.setVisibility(View.VISIBLE);
                mControlView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCloseListener() {
                // 点击了关闭按钮，关闭直播
                quitRoom();
            }

            @Override
            public void onGiftClick() {
                //显示礼物九宫格
                if (giftSelectDialog == null) {
                    giftSelectDialog = new GiftSelectDialog(WatchLiveActivity.this);
                    giftSelectDialog.setGiftSendListener(giftSendListener);
                }
                giftSelectDialog.show();
            }

            @Override
            public void onOptionClick(View view) {
                //操作点击，观众不需要处理
            }
        });

        /*聊天*/
        mChatView = (BottomChatView) findViewById(R.id.bottom_chat_view);
        mChatView.setOnSwitchTypeListener(new BottomChatView.onSwitchTypeListener(){
            @Override
            public void onChatSend(final ILVCustomCmd customCmd) {
                //发送消息
                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
                    @Override
                    public void onSuccess(TIMMessage data) {
                        if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_LIST) {
                            //如果是列表类型的消息，发送给列表显示
                            String chatContent = customCmd.getParam();
                            String userId = MyApplication.getApplication().getSelfProfile().getIdentifier();
                            String avatar = MyApplication.getApplication().getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(chatContent, userId, avatar);
                            mChatListView.addMsgInfo(info); //将消息添加到聊天消息列表汇总
                        } else if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU) {
                            String chatContent = customCmd.getParam();
                            String userId = MyApplication.getApplication().getSelfProfile().getIdentifier();
                            String avatar = MyApplication.getApplication().getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(chatContent, userId, avatar);
                            mChatListView.addMsgInfo(info);

                            String name = MyApplication.getApplication().getSelfProfile().getNickName();
                            if (TextUtils.isEmpty(name)) {
                                name = userId;
                            }
                            ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(chatContent, userId, avatar, name);
                            mDanmuView.addMsgInfo(danmuInfo);  //将消息添加到弹幕队列中
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                    }
                });
            }
        });

        mControlView.setVisibility(View.VISIBLE);
        mChatView.setVisibility(View.INVISIBLE);

        mChatListView = (ChatMsgListView) this.findViewById(R.id.chat_list);
        mVipEnterView = (VipEnterView) findViewById(R.id.vip_enter);
        mDanmuView = (DanmuView) this.findViewById(R.id.danmu_view);

        giftRepeatView = this.findViewById(R.id.gift_repeat_view);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        giftFullView = (GiftFullView) findViewById(R.id.gift_full_view);
    }

    /**
     * 发送礼物事件
     */
    private GiftSelectDialog.OnGiftSendListener giftSendListener = new GiftSelectDialog.OnGiftSendListener() {
        @Override
        public void onGiftSendClick(final ILVCustomCmd customCmd) {
            customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());

            ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
                @Override
                public void onSuccess(TIMMessage data) {
                    if (customCmd.getCmd() == Constants.CMD_CHAT_GIFT) {
                        //界面显示礼物动画。
                        GiftCmdInfo giftCmdInfo = new Gson().fromJson(customCmd.getParam(), GiftCmdInfo.class);
                        int giftId = giftCmdInfo.giftId;
                        final String repeatId = giftCmdInfo.repeatId;
                        GiftInfo giftInfo = GiftInfo.getGiftById(giftId);//获取对应id的礼物信息
                        if (giftInfo == null) {
                            return;
                        }
                        if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                            //连发礼物
                            giftRepeatView.showGift(giftInfo, repeatId, MyApplication.getApplication().getSelfProfile());
                        } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                            //全屏礼物
                            giftFullView.showGift(giftInfo, MyApplication.getApplication().getSelfProfile());
                        }

                        //todo 更新服务器用户发送礼物经验，返回等级
                        String address = Common.URL + "?action=gift&userId=" + MyApplication.getApplication().getSelfProfile().getIdentifier()
                                + "&exp=" + giftInfo.expValue;
                        HttpUtil.sendRequest(address, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                //更新用户信息
                                long level = Long.parseLong(response.body().string());
                                MyApplication.getApplication().getSelfProfile().setLevel(level);
                                TIMFriendshipManager.getInstance().setLevel(level, new TIMCallBack() {
                                    @Override
                                    public void onError(int i, String s) {}

                                    @Override
                                    public void onSuccess() {}
                                });
                            }
                        });
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                }
            });
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quitRoom();
    }

    @Override
    public void onBackPressed() {
        quitRoom();
    }

    /*退出房间*/
    private void quitRoom() {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        customCmd.setCmd(ILVLiveConstants.ILVLIVE_CMD_LEAVE);
        customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
        ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(getApplicationContext(), "退出成功！",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        logout();
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });

        //发送退出消息给服务器
//        QuitRoomRequest request = new QuitRoomRequest();
//        String url = request.getUrl(roomId+"",MyApplication.getApplication().getSelfProfile().getIdentifier());
//        request.sendRequest(url);

        logout();
    }

    private void logout() {
        finish();
    }
}
