package com.example.dinary.kekocampuslive.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.dinary.kekocampuslive.R;

/**
 * 选择照片选择框
 */
public class ChoosePicDialog extends TransParentDialog {

    private ChoosePicListener onDialogClickListener;

    public ChoosePicDialog(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_choose_pic_layout, null, false);
        setContentView(view);
        setWidthAndHeight(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        View camera = view.findViewById(R.id.pic_camera);
        View picLib = view.findViewById(R.id.pic_album);
        View cancel = view.findViewById(R.id.pic_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
                if (onDialogClickListener != null) {
                    onDialogClickListener.onCamera();
                }
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hide();
                if (onDialogClickListener != null) {
                    onDialogClickListener.onAlbum();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    /*选择照片事件*/
    public interface ChoosePicListener {
        void onCamera();    //拍照事件
        void onAlbum();     //选择本地照片
    }

    public void setOnDialogClickListener(ChoosePicListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    /**
     * 初始化dialog并显示
     */
    @Override
    public void show() {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);

        super.show();
    }
}
