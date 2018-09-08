package com.example.dinary.kekocampuslive.editprofile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.widget.TransParentDialog;

/**
 * 编辑个人信息输入框
 */
public class EditStrDialog extends TransParentDialog {

    private TextView mTitle;
    private EditText mContent;
    private TextView mBtnOk;

    private OnokListener onokListener;

    private String mTitleStr;

    public EditStrDialog(Activity activity) {
        super(activity);
        //加载布局
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_str_profile, null, false);
        mTitle = view.findViewById(R.id.dialog_editprofile_title);
        mContent = view.findViewById(R.id.dialog_editprofile_content);
        mBtnOk = view.findViewById(R.id.dialog_editprofile_ok);
        setContentView(view);

        //设置对话框宽高
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100,
                WindowManager.LayoutParams.WRAP_CONTENT);

        //注册按钮点击事件
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = mContent.getText().toString();
                //调用接口的方法向服务器发送请求
                if (onokListener != null)
                    onokListener.onOk(mTitleStr, content);

                hide(); //隐藏dialog
            }
        });
    }

    /**
     * 设置编辑框的主题、显示图片、默认值,并显示
     * @param title
     * @param resId
     * @param defaultContent
     */
    public void show(String title, int resId, String defaultContent) {
        mTitleStr = title;
        mTitle.setText("请输入" + title);

        mContent.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        mContent.setText(defaultContent);

        show(); //显示dialog
    }

    public void setOnokListener(OnokListener onokListener) {
        this.onokListener = onokListener;
    }

    /*点击按钮执行的事件*/
    public interface OnokListener{
        void onOk(String title, String content);
    }
}
