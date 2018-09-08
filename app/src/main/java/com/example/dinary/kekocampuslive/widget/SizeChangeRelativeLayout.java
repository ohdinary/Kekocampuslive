package com.example.dinary.kekocampuslive.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 屏幕大小改变
 */
public class SizeChangeRelativeLayout extends RelativeLayout {
    public SizeChangeRelativeLayout(Context context) {
        super(context);
    }

    public SizeChangeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeChangeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 弹出键盘时改变屏幕大小
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangeListener == null) {
            return;
        }
        if (h > oldh) {
            //画面变长，键盘隐藏
            onSizeChangeListener.onLarge();
        } else {
            //画面变短，键盘显示
            onSizeChangeListener.onSmall();
        }
    }

    private OnSizeChangeListener onSizeChangeListener;

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }

    public interface OnSizeChangeListener{
        public void onLarge();
        public void onSmall();
    }
}
