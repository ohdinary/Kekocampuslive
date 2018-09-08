package com.example.dinary.kekocampuslive.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.ChatMsgInfo;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.example.dinary.kekocampuslive.util.UtilImg;

/**
 * 弹幕条
 */
public class DanmuItemView extends RelativeLayout{

    private static final String TAG = DanmuItemView.class.getSimpleName();
    private ImageView mUserAvatar;
    private TextView mContent;
    private TextView mUserName;
    private TranslateAnimation animation;

    public DanmuItemView(Context context) {
        super(context);
        init();
    }

    public DanmuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DanmuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_danmu_item,this,true);

        findAllViews();
        //创建动画,水平位移
        animation = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(),R.anim.danmu_item_enter);
        animation.setAnimationListener(animationListener);
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            //弹幕弹入
            LogUtil.d(TAG, DanmuItemView.this+"onAnimationStart VISIBLE");
            setVisibility(VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //弹幕弹出
            LogUtil.d(TAG, DanmuItemView.this+"onAnimationStart INVISIBLE");
            setVisibility(INVISIBLE);
            if (onAvaliableListener != null)
                onAvaliableListener.onAvaliable();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /*将消息绑定到view中显示出来*/
    public void showMsgInfo(ChatMsgInfo danmuInfo){
        String avatar = danmuInfo.getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            UtilImg.loadRound(R.drawable.default_avatar, mUserAvatar);
        } else {
            UtilImg.loadRound(avatar, mUserAvatar);
        }
        mUserName.setText(danmuInfo.getSenderName());
        mContent.setText(danmuInfo.getContent());

        //在动画监听里面做处理，调用post保证在动画结束之后再start
        //解决start之后直接end的情况。
        post(new Runnable() {
            @Override
            public void run() {
                DanmuItemView.this.startAnimation(animation);
            }
        });
    }

    private void findAllViews() {
        mUserAvatar = this.findViewById(R.id.user_avatar);
        mUserName = this.findViewById(R.id.user_name);
        mContent = this.findViewById(R.id.chat_content);
    }

    private OnAvaliableListener onAvaliableListener;

    public void setOnAvaliableListener(OnAvaliableListener onAvaliableListener) {
        this.onAvaliableListener = onAvaliableListener;
    }

    public interface OnAvaliableListener{
        public void onAvaliable();
    }
}
