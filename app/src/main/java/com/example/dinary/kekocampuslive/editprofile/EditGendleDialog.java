package com.example.dinary.kekocampuslive.editprofile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.widget.TransParentDialog;

/**
 * 性别选择框
 */
public class EditGendleDialog extends TransParentDialog {
    private RadioButton maleView;
    private RadioButton femaleView;

    public EditGendleDialog(Activity activity) {
        super(activity);
        View mainView = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_gender_layout, null, false);

        maleView = (RadioButton) mainView.findViewById(R.id.male);
        femaleView = (RadioButton) mainView.findViewById(R.id.female);
        mainView.findViewById(R.id.edit_dialog_ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMaleChecked = maleView.isChecked();
                if (onChangeGenderListener != null) {
                    onChangeGenderListener.onChangeGender(isMaleChecked);
                }
                hide();
            }
        });
        setContentView(mainView);
        setWidthAndHeight(activity.getWindow().getDecorView().getWidth() * 80 / 100, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    /*默认选中男性*/
    public void show(boolean isMale) {
        maleView.setChecked(isMale);
        femaleView.setChecked(!isMale);
        show();
    }

    private OnChangeGenderListener onChangeGenderListener;

    public void setOnChangeGenderListener(OnChangeGenderListener l) {
        onChangeGenderListener = l;
    }

    public interface OnChangeGenderListener {
        void onChangeGender(boolean isMale);
    }
}
