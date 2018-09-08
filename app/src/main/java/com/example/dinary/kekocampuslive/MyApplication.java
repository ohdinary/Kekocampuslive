package com.example.dinary.kekocampuslive;

import android.app.Application;
import android.content.Context;

import com.example.dinary.kekocampuslive.beans.User;
import com.example.dinary.kekocampuslive.util.QnUploadHelper;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import org.litepal.LitePal;

/**
 * 全局获取context
 */
public class MyApplication extends Application {

    public static Context context;
    private static MyApplication app;

    private ILVLiveConfig mLiveConfig;
    public ILVLiveConfig getmLiveConfig() {
        return mLiveConfig;
    }
    public void setmLiveConfig(ILVLiveConfig mLiveConfig) {
        this.mLiveConfig = mLiveConfig;
    }

    private TIMUserProfile mSelfProfile;    //个人信息
    public static User user;    //个人信息
    public static String hostId;    //搜索内容

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        app = this;
        LitePal.initialize(context);    //解决项目只能配置一个Application的问题，在这里调用litepal的初始化方法

        initLiveSdk();
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 初始化直播SDK
     */
    private void initLiveSdk() {
        //iLiveSDK 初始化
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400115133, 31744);

        //创建用户自定义信息字段
//        List<String> customInfos = new ArrayList<String>();
//        customInfos.add(CustomProfile.CUSTOM_GET);
//        customInfos.add(CustomProfile.CUSTOM_SEND);
//        customInfos.add(CustomProfile.CUSTOM_LEVEL);
//        customInfos.add(CustomProfile.CUSTOM_RENZHENG);
//        TIMManager.getInstance().initFriendshipSettings(CustomProfile.allBaseInfo, customInfos);

        //初始化直播场景
        mLiveConfig = new ILVLiveConfig();
        ILVLiveManager.getInstance().init(mLiveConfig);

        //初始化七牛云sdk
        QnUploadHelper.init("fywLTKHt3JUahQrTPSFrKRt27FjWTBV6Yn8CQFWe",
                "00nzSVpO5yURyMxpPkOP_9shEtnGYDbGJxMavzdL",
                "http://oe0i3jf0i.bkt.clouddn.com/",
                "imooc");

//        LeakCanary.install(this);
    }

    public static MyApplication getApplication() {
        return app;
    }

    public void setSelfProfile(TIMUserProfile timUserProfile) {
        mSelfProfile = timUserProfile;
    }

    public TIMUserProfile getSelfProfile() {
        return mSelfProfile;
    }
}
