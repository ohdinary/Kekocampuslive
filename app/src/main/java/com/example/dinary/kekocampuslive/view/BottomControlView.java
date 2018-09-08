package com.example.dinary.kekocampuslive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.dinary.kekocampuslive.R;

/**
 * 直播底部控制栏
 */
public class BottomControlView extends RelativeLayout {
    private ImageView optionView;
    private ImageView giftView;     //礼物

    public BottomControlView(Context context) {
        super(context);
        init();
    }

    public BottomControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_control,this, true);
        findAllViews();
    }

    private void findAllViews() {
        findViewById(R.id.view_bottom_chat).setOnClickListener(onClickListener);
        findViewById(R.id.view_bottom_close).setOnClickListener(onClickListener);
        giftView = (ImageView) findViewById(R.id.view_bottom_gift);
        giftView.setOnClickListener(onClickListener);
        optionView = (ImageView) findViewById(R.id.view_bottom_option);
        optionView.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.view_bottom_chat:
                    // 显示聊天操作栏
                    if (onControlListener != null)
                        onControlListener.onChatListener();
                    break;
                case R.id.view_bottom_close:
                    //退出直播
                    if (onControlListener != null)
                        onControlListener.onCloseListener();
                    break;
                case R.id.view_bottom_gift:
                    // 显示礼物选择九宫格
                    if (onControlListener != null)
                        onControlListener.onGiftClick();
                    break;
                case R.id.view_bottom_option:
                    if (onControlListener != null)
                        onControlListener.onOptionClick(view);
                    break;
            }
        }
    };

    private OnControlListener onControlListener;
    public void setControlListener(OnControlListener controlListener) {
        this.onControlListener = controlListener;
    }

    /*判断是否是主播，如果是就不显示礼物*/
    public void setIsHost(boolean isHost) {
        if (isHost) {
            giftView.setVisibility(INVISIBLE);
            optionView.setVisibility(VISIBLE);
        } else {
            optionView.setVisibility(INVISIBLE);
            giftView.setVisibility(VISIBLE);
        }
    }

    public interface OnControlListener{
        public void onChatListener();
        public void onCloseListener();
        public void onGiftClick();
        public void onOptionClick(View view);
    }
}
