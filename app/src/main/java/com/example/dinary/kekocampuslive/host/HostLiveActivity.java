package com.example.dinary.kekocampuslive.host;

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
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.example.dinary.kekocampuslive.view.BottomChatView;
import com.example.dinary.kekocampuslive.view.BottomControlView;
import com.example.dinary.kekocampuslive.view.ChatMsgListView;
import com.example.dinary.kekocampuslive.view.DanmuView;
import com.example.dinary.kekocampuslive.view.GiftFullView;
import com.example.dinary.kekocampuslive.view.GiftRepeatView;
import com.example.dinary.kekocampuslive.view.TitleView;
import com.example.dinary.kekocampuslive.view.VipEnterView;
import com.example.dinary.kekocampuslive.widget.HostControlDialog;
import com.example.dinary.kekocampuslive.widget.SizeChangeRelativeLayout;
import com.google.gson.Gson;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tyrantgit.widget.HeartLayout;

/**
 * 主播活动
 */
public class HostLiveActivity extends AppCompatActivity {

    private AVRootView avRootView;  //直播画面显示控件
    private SizeChangeRelativeLayout sizeChangeRelativeLayout;
    private TitleView mTitleView;
    private BottomControlView bottomControlView;    //打开聊天框+退出房间
    private BottomChatView chatView;                //聊天框
    private ChatMsgListView chatMsgListView;        //聊天消息列表
    private DanmuView danmuView;                    //弹幕列表
    private VipEnterView mVipEnterView;             //vip用户进入

    private Timer heartBeatTimer = new Timer();
    private Timer heartTimer = new Timer();
    private Random heartRandom = new Random();
    private HeartLayout heartLayout;        //心跳
    private GiftRepeatView giftRepeatView;  //连续礼物
    private GiftFullView giftFullView;      //全屏礼物

    private HostControlState hostControlState;
    private FlashlightHelper flashlightHelper;

    private int roomId; //房间号
    private HeartBeatRequest mHeartBeatRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);

        findAllViews();
        createRoom();   //创建直播
    }

    private void createRoom() {
        hostControlState = new HostControlState();
        flashlightHelper = new FlashlightHelper();
        //获取房间号
        roomId = getIntent().getIntExtra("roomId",-1);
        if (roomId < 0){
            Toast.makeText(HostLiveActivity.this,"获取房间号失败！",Toast.LENGTH_SHORT).show();
            return;
        }

        /*接收聊天消息和弹幕消息*/
        ILVLiveConfig config = MyApplication.getApplication().getmLiveConfig();
        config.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
                //接收到文本消息
            }

            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                //接收到自定义消息
                if (cmd.getCmd() == Constants.CMD_CHAT_MSG_LIST){
                    //聊天消息
                    String content = cmd.getParam();
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content,id,userProfile.getFaceUrl());
                    chatMsgListView.addMsgInfo(info);
                }else if (cmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU){
                    //弹幕消息
                    String content = cmd.getParam();
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, userProfile.getFaceUrl());
                    chatMsgListView.addMsgInfo(info);

                    String name = userProfile.getNickName();
                    if (TextUtils.isEmpty(name))
                        name = userProfile.getIdentifier();
                    ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content, id, userProfile.getFaceUrl(), name);
                    danmuView.addMsgInfo(danmuInfo);
                }else if (cmd.getCmd() == Constants.CMD_CHAT_GIFT) {
                    //界面显示礼物动画。
                    GiftCmdInfo giftCmdInfo = new Gson().fromJson(cmd.getParam(), GiftCmdInfo.class);
                    int giftId = giftCmdInfo.giftId;
                    String repeatId = giftCmdInfo.repeatId;
                    GiftInfo giftInfo = GiftInfo.getGiftById(giftId);
                    if (giftInfo == null) {
                        return;
                    }
                    if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                        //连续礼物
                        giftRepeatView.showGift(giftInfo, repeatId, userProfile);
                    } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                        //全屏礼物
                        giftFullView.showGift(giftInfo, userProfile);
                    }
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_ENTER) {
                    //用户进入直播
                    mTitleView.addWatcher(userProfile);
                    mVipEnterView.showVipEnter(userProfile);
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_LEAVE) {
                    //用户离开消息
                    mTitleView.removeWatcher(userProfile);
                }
            }

            @Override
            public void onNewOtherMsg(TIMMessage message) {
                //接收到其他消息
            }
        });

        //创建房间配置项
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(ILiveLoginManager.getInstance().getMyUserId()).
                controlRole("LiveMaster")//角色设置
                .autoFocus(true)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收

        //创建房间
        ILVLiveManager.getInstance().createRoom(roomId, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //开始心形动画
                startHeartAnim();
                //开始发送心跳
                startHeartBeat();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //失败的情况下，退出界面。
                Toast.makeText(HostLiveActivity.this, "创建直播失败！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void startHeartBeat() {
        heartBeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //发送心跳包
                if (mHeartBeatRequest == null) {
                    mHeartBeatRequest = new HeartBeatRequest();
                }
                String userId = MyApplication.getApplication().getSelfProfile().getIdentifier();
                String url = mHeartBeatRequest.getUrl(roomId+"", userId);
                mHeartBeatRequest.sendRequest(url);
            }
        }, 0, 4000); //4秒钟 。服务器是10秒钟去检测一次。
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

    //随机获取一种颜色
    private int getRandomColor() {
        return Color.rgb(heartRandom.nextInt(255), heartRandom.nextInt(255), heartRandom.nextInt(255));
    }

    private void findAllViews() {
        sizeChangeRelativeLayout = this.findViewById(R.id.size_change);
        sizeChangeRelativeLayout.setOnSizeChangeListener(new SizeChangeRelativeLayout.OnSizeChangeListener() {
            @Override
            public void onLarge() {
                //键盘隐藏
                chatView.setVisibility(View.INVISIBLE);
                bottomControlView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSmall() {
                //键盘显示
            }
        });

        mTitleView = (TitleView) this.findViewById(R.id.title_view);
        mTitleView.setHost(MyApplication.getApplication().getSelfProfile());

        avRootView = this.findViewById(R.id.live_view);
        ILVLiveManager.getInstance().setAvVideoView(avRootView);

        /*底部发送消息框*/
        chatView = this.findViewById(R.id.bottom_chat_view);
        chatView.setVisibility(View.INVISIBLE);
        chatView.setOnSwitchTypeListener(new BottomChatView.onSwitchTypeListener() {
            @Override
            public void onChatSend(final ILVCustomCmd customCmd) {
                //发送消息
                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());

                ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
                    @Override
                    public void onSuccess(TIMMessage data) {
                        if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_LIST){
                            //如果是列表类型消息,发送给列表显示
                            String chatContent = customCmd.getParam();
                            String userId = MyApplication.getApplication().getSelfProfile().getIdentifier();
                            String avatar = MyApplication.getApplication().getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(chatContent, userId, avatar);
                            chatMsgListView.addMsgInfo(info);
                        }else if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU){
                            String chatContent = customCmd.getParam();
                            String userId = MyApplication.getApplication().getSelfProfile().getIdentifier();
                            String avatar = MyApplication.getApplication().getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(chatContent, userId, avatar);
                            chatMsgListView.addMsgInfo(info);

                            String name = MyApplication.getApplication().getSelfProfile().getNickName();
                            if (TextUtils.isEmpty(name))
                                name = userId;
                            ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(chatContent,userId,avatar,name);
                            danmuView.addMsgInfo(danmuInfo);    //将弹幕添加到消息队列中
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                    }
                });
            }
        });

        /*底部状态控制栏*/
        bottomControlView = this.findViewById(R.id.bottom_control_view);
        bottomControlView.setVisibility(View.VISIBLE);
        bottomControlView.setIsHost(true);
        bottomControlView.setControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChatListener() {
                //点击了聊天按钮，显示聊天操作栏
                chatView.setVisibility(View.VISIBLE);
                bottomControlView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCloseListener() {
                // 点击了关闭按钮，关闭直播
                quitRoom();
            }

            @Override
            public void onGiftClick() {
                //主播界面，不能发送礼物
            }

            @Override
            public void onOptionClick(View view) {
                //显示主播操作对话框
                boolean beautyOn = hostControlState.isBeautyOn();
                boolean flashOn = flashlightHelper.isFlashLightOn();
                boolean voiceOn = hostControlState.isVoiceOn();

                HostControlDialog hostControlDialog = new HostControlDialog(HostLiveActivity.this);

                hostControlDialog.setOnControlClickListener(controlClickListener);
                hostControlDialog.updateView(beautyOn, flashOn, voiceOn);
                hostControlDialog.show(view);
            }
        });

        danmuView = this.findViewById(R.id.danmu_view);
        chatMsgListView = this.findViewById(R.id.list_view);
        giftRepeatView = (GiftRepeatView) findViewById(R.id.gift_repeat_view);
        giftFullView = (GiftFullView) findViewById(R.id.gift_full_view);
        mVipEnterView = (VipEnterView) findViewById(R.id.vip_enter);
        heartLayout = (HeartLayout) findViewById(R.id.heart_layout);
    }

    private HostControlDialog.OnControlClickListener controlClickListener = new HostControlDialog.OnControlClickListener() {
        @Override
        public void onBeautyClick() {
            //点击美颜
            boolean isBeautyOn = hostControlState.isBeautyOn();
            if (isBeautyOn) {
                //关闭美颜
                ILiveRoomManager.getInstance().enableBeauty(0);
                hostControlState.setBeautyOn(false);
            } else {
                //打开美颜
                ILiveRoomManager.getInstance().enableBeauty(50);
                hostControlState.setBeautyOn(true);
            }
        }

        @Override
        public void onFlashClick() {
            // 闪光灯
            boolean isFlashOn = flashlightHelper.isFlashLightOn();
            if (isFlashOn) {
                flashlightHelper.enableFlashLight(false);
            } else {
                flashlightHelper.enableFlashLight(true);
            }
        }

        @Override
        public void onVoiceClick() {
            //声音
            boolean isVoiceOn = hostControlState.isVoiceOn();
            if (isVoiceOn) {
                //静音
                ILiveRoomManager.getInstance().enableMic(false);
                hostControlState.setVoiceOn(false);
            } else {
                ILiveRoomManager.getInstance().enableMic(true);
                hostControlState.setVoiceOn(true);
            }
        }

        @Override
        public void onCameraClick() {
            int cameraId = hostControlState.getCameraid();
            if (cameraId == ILiveConstants.FRONT_CAMERA) {
                ILiveRoomManager.getInstance().switchCamera(ILiveConstants.BACK_CAMERA);
                hostControlState.setCameraid(ILiveConstants.BACK_CAMERA);
            } else if (cameraId == ILiveConstants.BACK_CAMERA) {
                ILiveRoomManager.getInstance().switchCamera(ILiveConstants.FRONT_CAMERA);
                hostControlState.setCameraid(ILiveConstants.FRONT_CAMERA);
            }
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
        heartTimer.cancel();
        heartBeatTimer.cancel();
//        quitRoom();
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
                        logout();
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
        QuitRoomRequest request = new QuitRoomRequest();
        String hostId = MyApplication.getApplication().getSelfProfile().getIdentifier();
        String url = request.getUrl(roomId+"", hostId);
        LogUtil.d("quitroomrequest",url);
        request.sendRequest(url);
    }

    //关闭直播间
    private void logout() {
//        ILiveLoginManager.getInstance().iLiveLogout(null);
        finish();
    }
}
