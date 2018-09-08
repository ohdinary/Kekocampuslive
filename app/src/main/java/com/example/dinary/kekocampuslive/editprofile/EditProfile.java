package com.example.dinary.kekocampuslive.editprofile;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dinary.kekocampuslive.R;

/**
 * 个人信息可修改项（箭头可见）
 */
public class EditProfile extends LinearLayout {

    private ImageView mImage;
    private TextView mKey;
    private TextView mValue;
    private ImageView mArrow;

    public EditProfile(Context context) {
        super(context);
        init();
    }

    public EditProfile(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditProfile(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*加载布局项*/
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_profile_edit, this, true);
        findAllViews();
    }

    private void findAllViews() {
        mImage = findViewById(R.id.profile_icon);
        mKey = findViewById(R.id.profile_key);
        mValue = findViewById(R.id.profile_value);
        mArrow = findViewById(R.id.right_arrow);
    }

    /**
     * 动态设置图标、名称、值
     * @param resId
     * @param key
     * @param value
     */
    public void set(int resId, String key, String value){
        mImage.setImageResource(resId);
        mKey.setText(key);
        mValue.setText(value);
    }

    /*将箭头设置为不可见*/
    protected void disableEdit(){
        mArrow.setVisibility(GONE);
    }

    //设置值
    public void updateValue(String value) {
        mValue.setText(value);
        mValue.setSelectAllOnFocus(true);
    }
    public String getValue() {
        return mValue.getText().toString();
    }
}
