package com.example.dinary.kekocampuslive.editprofile;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 个人信息不可修改项
 */
public class ProfileTextView extends EditProfile {
    public ProfileTextView(Context context) {
        super(context);
        disableEdit();
    }

    public ProfileTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        disableEdit();
    }

    public ProfileTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        disableEdit();
    }
}
