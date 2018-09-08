package com.example.dinary.kekocampuslive.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.model.Constants;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVText;

/**
 * 发送消息/弹幕
 */
public class BottomChatView extends LinearLayout {
    private CheckBox mSwitchChatType;
    private EditText mChatContent;
    private TextView mSend;

    public BottomChatView(Context context) {
        super(context);
        init();
    }

    public BottomChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int paddingPx = (int) (getResources().getDisplayMetrics().density * 10 + 0.5f);
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        setBackgroundColor(Color.parseColor("#ccffffff"));
        LayoutInflater.from(getContext()).inflate(R.layout.view_buttom_chat, this, true);

        findAllViews();
    }

    private void findAllViews() {
        mSwitchChatType = (CheckBox) findViewById(R.id.swich_chat_type);
        mChatContent = (EditText) findViewById(R.id.bottom_edittext);
        mSend = (TextView) findViewById(R.id.bottom_button_send);

        mSwitchChatType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mChatContent.setHint("发送弹幕聊天消息");
                } else {
                    mChatContent.setHint("和大家聊点什么吧");
                }
            }
        });
        //默认发送聊天消息(打开发送弹幕)
        mSwitchChatType.setChecked(false);
        mSwitchChatType.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));//隐去默认小框样式
        mChatContent.setHint("和大家聊点什么吧");
        mSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送聊天消息
                sendMsg();
            }
        });
    }

    private void sendMsg() {
        if (onSwitchTypeListener != null) {
            ILVCustomCmd customCmd = new ILVCustomCmd();
            customCmd.setType(ILVText.ILVTextType.eGroupMsg);
            boolean isDanmu = mSwitchChatType.isChecked();
            if (isDanmu) {
                customCmd.setCmd(Constants.CMD_CHAT_MSG_DANMU);
            } else {
                customCmd.setCmd(Constants.CMD_CHAT_MSG_LIST);
            }
            customCmd.setParam(mChatContent.getText().toString());
            onSwitchTypeListener.onChatSend(customCmd);//设置消息内容
        }
    }

    private onSwitchTypeListener onSwitchTypeListener;

    public void setOnSwitchTypeListener(BottomChatView.onSwitchTypeListener onSwitchTypeListener) {
        this.onSwitchTypeListener = onSwitchTypeListener;
    }

    public interface onSwitchTypeListener{
        public void onChatSend(ILVCustomCmd msg);
    }
}
