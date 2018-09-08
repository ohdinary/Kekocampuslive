package com.example.dinary.kekocampuslive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.GiftInfo;
import com.tencent.TIMUserProfile;

import java.util.LinkedList;
import java.util.List;

/**
 * 礼物重复发送
 */
public class GiftRepeatView extends LinearLayout {

    private GiftRepeatItemView item0, item1;

    private class GiftSenderAndInfo {
        public GiftInfo giftInfo;
        public String repeatId;
        public TIMUserProfile senderProfile;
    }

    //缓存待发送礼物队列
    private List<GiftSenderAndInfo> giftSenderAndInfoList = new LinkedList<GiftSenderAndInfo>();

    public GiftRepeatView(Context context) {
        super(context);
        init();
    }

    public GiftRepeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftRepeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat, this, true);
        findAllViews();
        item0.setVisibility(INVISIBLE);
        item1.setVisibility(INVISIBLE);
    }

    private void findAllViews() {
        item0 = (GiftRepeatItemView) findViewById(R.id.item0);
        item1 = (GiftRepeatItemView) findViewById(R.id.item1);

        item0.setOnGiftItemAvaliableListener(avaliableListener);
        item1.setOnGiftItemAvaliableListener(avaliableListener);
    }

    /*从缓存中取出待发送的礼物信息发送*/
    private GiftRepeatItemView.OnGiftItemAvaliableListener avaliableListener = new GiftRepeatItemView.OnGiftItemAvaliableListener() {
        @Override
        public void onAvaliable() {
            if (giftSenderAndInfoList.size() > 0) {
                GiftSenderAndInfo info = giftSenderAndInfoList.remove(0);
                showGift(info.giftInfo, info.repeatId, info.senderProfile);
            }
        }
    };

    //发送礼物
    public void showGift(GiftInfo giftInfo, String repeatId, TIMUserProfile profile) {
        GiftRepeatItemView avaliableView = getAvaliableView(giftInfo, repeatId, profile);
        if (avaliableView == null) {
            GiftSenderAndInfo info = new GiftSenderAndInfo();
            info.giftInfo = giftInfo;
            info.senderProfile = profile;
            info.repeatId = repeatId;
            giftSenderAndInfoList.add(info);
        } else {
            avaliableView.showGift(giftInfo, repeatId, profile);
        }
    }

    /*获取可用item*/
    private GiftRepeatItemView getAvaliableView(GiftInfo giftInfo, String repeatId, TIMUserProfile profile) {
        if (item0.isAvaliable(giftInfo, repeatId, profile)) {
            return item0;
        }
        if (item1.isAvaliable(giftInfo, repeatId, profile)) {
            return item1;
        }
        if (item0.getVisibility() == INVISIBLE) {
            return item0;
        }
        if (item1.getVisibility() == INVISIBLE) {
            return item1;
        }
        return null;
    }
}
