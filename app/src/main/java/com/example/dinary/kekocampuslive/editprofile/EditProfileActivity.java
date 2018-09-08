package com.example.dinary.kekocampuslive.editprofile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MainActivity;
import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.util.ChoosePicHelper;
import com.example.dinary.kekocampuslive.util.UtilImg;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.Map;

/**
 * 编辑个人信息界面
 */
public class EditProfileActivity extends AppCompatActivity {

    private Toolbar mTitlebar;
    private View mAvatarView;
    private ImageView mAvatarImg;
    private EditProfile mNickNameEdt;
    private EditProfile mGenderEdt;
    private EditProfile mSignEdt;
    private EditProfile mRenzhengEdt;
    private EditProfile mLocationEdt;

    private ProfileTextView mIdView;
    private ProfileTextView mLevelView;
    private ProfileTextView mGetNumsView;
//    private ProfileTextView mSendNumsView;

    private Button mCompleteBtn;

    private TIMUserProfile mUserProfile;    //个人信息
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findAllViews();
        setTitleBar();
        setListener();
        setIconKey();   //设置默认图标和字段
        getSelfProfile();   //获取个人信息并更新控件
    }

    /**
     * 从服务器获取个人信息并更新控件
     */
    private void getSelfProfile() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyApplication.getContext(),"获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                mUserProfile = timUserProfile;
                updateViews(timUserProfile);    //更新个人信息列表
            }
        });
    }

    /**
     * 更新控件个人信息
     * @param timUserProfile
     */
    private void updateViews(TIMUserProfile timUserProfile) {
        String faceUrl = timUserProfile.getFaceUrl();   //头像url
        if (TextUtils.isEmpty(faceUrl)) //没有则加载本地默认头像
            UtilImg.loadRound(R.drawable.default_avatar, mAvatarImg);
        else    //加载服务器用户头像
            UtilImg.loadRound(faceUrl, mAvatarImg);

        mNickNameEdt.updateValue(timUserProfile.getNickName());
        long genderValue = timUserProfile.getGender().getValue();
        String genderStr = genderValue == 1 ? "男" : "女";
        mGenderEdt.updateValue(genderStr);
        mSignEdt.updateValue(timUserProfile.getSelfSignature());
        mLocationEdt.updateValue(timUserProfile.getLocation());
        mIdView.updateValue(timUserProfile.getIdentifier());

        //获取定制字段的值（byte）
        Map<String, byte[]> customInfo = timUserProfile.getCustomInfo();
        mRenzhengEdt.updateValue(getCustomerValue(customInfo, CustomProfile.CUSTOM_RENZHENG, "未知"));
        mLevelView.updateValue(getCustomerValue(customInfo, CustomProfile.CUSTOM_LEVEL, "0"));
        mGetNumsView.updateValue(getCustomerValue(customInfo, CustomProfile.CUSTOM_GET, "0"));
//        mSendNumsView.updateValue(getCustomerValue(customInfo, CustomProfile.CUSTOM_SEND, "0"));
    }

    /**
     * 获取定制字段的值
     */
    private String getCustomerValue(Map<String,byte[]> customerInfo, String key, String defaultValue){
        if (customerInfo != null){
            byte[] valueBytes = customerInfo.get(key);
            if (valueBytes != null)
                return new String(valueBytes);
        }
        return defaultValue;
    }

    /**
     * 设置默认图标和字段
     */
    private void setIconKey() {
        mNickNameEdt.set(R.drawable.ic_info_nickname, "昵称", "取个好名字吧");
        mGenderEdt.set(R.drawable.ic_info_gender, "性别", "男");
        mSignEdt.set(R.drawable.ic_info_sign, "签名", "这个人很帅，什么都没有留下...");
        mRenzhengEdt.set(R.drawable.ic_info_renzhen, "认证", "未认证");
        mLocationEdt.set(R.drawable.ic_info_location, "地区", "未知");
        mIdView.set(R.drawable.ic_info_id, "ID", MyApplication.getApplication().getSelfProfile().getIdentifier());
        mLevelView.set(R.drawable.ic_info_level, "等级", MyApplication.getApplication().getSelfProfile().getLevel()+"");
        mGetNumsView.set(R.drawable.ic_info_get, "收到礼物", "0");
//        mSendNumsView.set(R.drawable.ic_info_send, "送出礼物", "0");
    }

    private void setListener() {
        mAvatarView.setOnClickListener(clickListener);
        mNickNameEdt.setOnClickListener(clickListener);
        mGenderEdt.setOnClickListener(clickListener);
        mSignEdt.setOnClickListener(clickListener);
        mRenzhengEdt.setOnClickListener(clickListener);
        mLocationEdt.setOnClickListener(clickListener);
        mCompleteBtn.setOnClickListener(clickListener);
    }

    /*设置标题*/
    private void setTitleBar() {
        mTitlebar.setTitle("编辑个人信息");
        mTitlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mTitlebar);
    }

    private void findAllViews() {
        mTitlebar = findViewById(R.id.editprofile_title_bar);

        mNickNameEdt = findViewById(R.id.nick_name);
        mAvatarView = findViewById(R.id.avatar);
        mAvatarImg = (ImageView) findViewById(R.id.avatar_img);
        mGenderEdt =  findViewById(R.id.gender);
        mSignEdt =  findViewById(R.id.sign);
        mRenzhengEdt =  findViewById(R.id.confirm);

        mLocationEdt =  findViewById(R.id.location);
        mIdView =  findViewById(R.id.id);
        mLevelView = findViewById(R.id.level);
        mGetNumsView = findViewById(R.id.get_gifts);

        mCompleteBtn = (Button) findViewById(R.id.btn_profile_complete_edit);
    }

    /**
     * 设置点击事件
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.avatar) {
                //修改头像
                choosePic();
            } else if (id == R.id.nick_name) {
                //修改昵称
                showEditNickNameDialog();
            } else if (id == R.id.gender) {
                //修改性别
                showEditGenderDialog();
            } else if (id == R.id.sign) {
                //修改签名
                showEditSignDialog();
            } else if (id == R.id.confirm) {
                //修改认证
//                showEditRenzhengDialog();
            } else if (id == R.id.location) {
                //修改位置
                showEditLocationDialog();
            } else if (id == R.id.btn_profile_complete_edit) {
                //完成，保存信息,点击跳转到主界面
                MyApplication.getApplication().setSelfProfile(mUserProfile);
                Intent intent = new Intent();
                intent.setClass(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };

    private ChoosePicHelper mPicChooserHelper;

    private void choosePic() {
        if (mPicChooserHelper == null) {
            mPicChooserHelper = new ChoosePicHelper(this, ChoosePicHelper.PicType.Avatar);
            mPicChooserHelper.setOnChooseResultListener(new ChoosePicHelper.OnChooseResultListener() {
                @Override
                public void onSuccess(String url) {
                    updateAvatar(url);
                }

                @Override
                public void onFail(String msg) {
                    Toast.makeText(MyApplication.getContext(), "选择失败：" + msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
        mPicChooserHelper.showPicChooserDialog();
    }

    /*更新头像*/
    private void updateAvatar(String url) {
        TIMFriendshipManager.getInstance().setFaceUrl(url, new TIMCallBack() {

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyApplication.getContext(), "头像更新失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                //更新头像成功
                getSelfProfile();
            }
        });
    }

    /*从choosepichelper中选择本地图片之后的回调操作*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPicChooserHelper != null)
            mPicChooserHelper.onActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    /*修改认证*/
//    private void showEditRenzhengDialog() {
//        EditStrDialog dialog = new EditStrDialog(this);
//        dialog.setOnokListener(new EditStrDialog.OnokListener() {
//            @Override
//            public void onOk(String title, String content) {
//                TIMFriendshipManager.getInstance().(content, new TIMCallBack() {
//                    @Override
//                    public void onError(int i, String s) {
//                        Toast.makeText(MyApplication.getContext(), "更新认证失败：" + s, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        getSelfProfile();
//                    }
//                });
//            }
//        });
//        dialog.show("认证", R.drawable.ic_info_renzhen, mRenzhengEdt.getValue());
//    }

    /*更新位置*/
    private void showEditLocationDialog() {
        EditStrDialog dialog = new EditStrDialog(this);
        dialog.setOnokListener(new EditStrDialog.OnokListener() {
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setLocation(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MyApplication.getContext(), "更新位置失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        getSelfProfile();
                    }
                });
            }
        });
        dialog.show("地区", R.drawable.ic_info_location, mLocationEdt.getValue());
    }

    /*更新昵称*/
    private void showEditNickNameDialog() {
        EditStrDialog dialog = new EditStrDialog(this);
        dialog.setOnokListener(new EditStrDialog.OnokListener() {
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setNickName(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MyApplication.getContext(), "更新昵称失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        getSelfProfile();
                    }
                });
            }
        });
        dialog.show("昵称", R.drawable.ic_info_nickname, mNickNameEdt.getValue());
    }

    /*修改性别*/
    private void showEditGenderDialog() {
        EditGendleDialog dialog = new EditGendleDialog(this);
        dialog.setOnChangeGenderListener(new EditGendleDialog.OnChangeGenderListener() {
            @Override
            public void onChangeGender(boolean isMale) {
                TIMFriendGenderType gender = isMale ? TIMFriendGenderType.Male : TIMFriendGenderType.Female;
                TIMFriendshipManager.getInstance().setGender(gender, new TIMCallBack() {

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MyApplication.getContext(), "更新性别失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //更新成功
                        getSelfProfile();
                    }
                });
            }
        });
        dialog.show();
    }

    /*修改个性签名*/
    private void showEditSignDialog() {
        EditStrDialog dialog = new EditStrDialog(this);
        //实现dialog的onOkListener接口属性的方法
        dialog.setOnokListener(new EditStrDialog.OnokListener() {
            @Override
            public void onOk(String title, String content) {
                TIMFriendshipManager.getInstance().setSelfSignature(content, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MyApplication.getContext(), "更新签名失败：" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        //重新更新控件信息
                        getSelfProfile();
                    }
                });
            }
        });
        //显示dialog
        dialog.show("个性签名", R.drawable.ic_info_sign, mSignEdt.getValue());
    }


}
