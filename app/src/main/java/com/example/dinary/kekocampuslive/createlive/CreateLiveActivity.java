package com.example.dinary.kekocampuslive.createlive;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.beans.LiveCell;
import com.example.dinary.kekocampuslive.beans.User;
import com.example.dinary.kekocampuslive.host.HostLiveActivity;
import com.example.dinary.kekocampuslive.util.ChoosePicHelper;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.example.dinary.kekocampuslive.util.UtilImg;
import com.tencent.TIMUserProfile;
import com.tencent.av.TIMAvManager;

public class CreateLiveActivity extends AppCompatActivity {
    private View mSetCoverView;
    private ImageView mCoverImg;    //封面
    private TextView mCoverTipTxt;
    private EditText mTitleEt;      //直播标题
    private TextView mCreateRoomBtn;//发起直播按钮
    private TextView mRoomNoText;   //直播房间号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live);

        findAllViews();
        setupTitlebar();
        setListensers();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_pic_tip:
                    //选择封面
                    showPicChooseDialog();
                    break;
                case R.id.create:
                    //发起直播
                    createRoomRequest();
                    break;
            }
        }
    };

    /*发起直播*/
    private void createRoomRequest() {
        CreateLIveRequest.CreateRoomParam param = new CreateLIveRequest.CreateRoomParam();
        TIMUserProfile selfProfile = MyApplication.getApplication().getSelfProfile();
        User user = MyApplication.user;
        param.userId = selfProfile.getIdentifier();
        param.userAvatar = selfProfile.getFaceUrl();
        String nickName = selfProfile.getNickName();
        param.userName = TextUtils.isEmpty(nickName) ? selfProfile.getIdentifier() : nickName;
        param.liveTitle = mTitleEt.getText().toString();
        param.liveCover = coverUrl;
        //创建房间
        CreateLIveRequest request = new CreateLIveRequest();
        request.setOnRequestListener(new HttpUtil.OnRequestListener<LiveCell>() {
            @Override
            public void onFail(int code, String msg) {
                Toast.makeText(CreateLiveActivity.this, "请求失败：" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(LiveCell roomInfo) {
                //取得房间号
                Toast.makeText(CreateLiveActivity.this, "请求成功：" + roomInfo.getLiveRoomId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(CreateLiveActivity.this, HostLiveActivity.class);
                intent.putExtra("roomId", roomInfo.getLiveRoomId());
                startActivity(intent);

                finish();
            }
        });

        String requestUrl = request.getUrl(param);
        LogUtil.d("createLiveActivity", requestUrl);
        request.sendRequest(requestUrl);
    }

    private ChoosePicHelper helper ;

    //选择封面图片
    private void showPicChooseDialog() {
        if (helper == null){
            helper = new ChoosePicHelper(this, ChoosePicHelper.PicType.Cover);
            helper.setOnChooseResultListener(new ChoosePicHelper.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) {
                    //加载图片成功,更新封面
                    updatePic(url);
                }

                @Override
                public void onFail(String msg) {
                    //获取图片失败
                    Toast.makeText(CreateLiveActivity.this, "选择失败：" + msg, Toast.LENGTH_SHORT).show();
                }
            });
            helper.showPicChooserDialog();
        }
    }

    private String coverUrl = null;

    /*更新封面*/
    private void updatePic(String url) {
        coverUrl = url;
        LogUtil.d("createliveactivity_coverurl",url);
        UtilImg.load(url, mCoverImg);   //加载图片
        mCoverTipTxt.setVisibility(View.GONE);  //隐去提示
    }

    private void setListensers() {
        mCoverTipTxt.setOnClickListener(listener);
        mCreateRoomBtn.setOnClickListener(listener);
    }

    private void findAllViews() {
        mSetCoverView = findViewById(R.id.set_cover);
        mCoverImg = (ImageView) findViewById(R.id.cover);
        mCoverTipTxt = (TextView) findViewById(R.id.tv_pic_tip);
        mTitleEt = (EditText) findViewById(R.id.title);
        mCreateRoomBtn = (TextView) findViewById(R.id.create);
        mRoomNoText = (TextView) findViewById(R.id.room_no);
    }

    private void setupTitlebar() {
        Toolbar titlebar = (Toolbar) findViewById(R.id.titlebar);
        titlebar.setTitle("开始我的直播");
        titlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(titlebar);
    }
}
