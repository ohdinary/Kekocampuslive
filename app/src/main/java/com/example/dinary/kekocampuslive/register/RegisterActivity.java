package com.example.dinary.kekocampuslive.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MainActivity;
import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import common.Common;

import com.example.dinary.kekocampuslive.editprofile.EditProfileActivity;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mAccount;
    private EditText mPassword;
    private EditText mRepeatPassword;
    private Button btnRegiset;
    private EditText mMsgCode;
    private TextView mGetMsgCode;

    String userAccountStr;
    String userPassword ;
    String userRepeatPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findAllView();
        addListener();
        setTitleBar();
    }

    private void setTitleBar() {
        mToolbar.setTitle("注册");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
    }

    private void addListener() {
        btnRegiset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        mGetMsgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageCode();
            }
        });
    }

    /*发送短信验证码*/
    private void messageCode() {
        userAccountStr = mAccount.getText().toString();
        mGetMsgCode.setText("验证码已发送");
        if (TextUtils.isEmpty(userAccountStr)){
            Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        String address = Common.URL + "?action=" + Common.MESSAGE + "&userName=" + userAccountStr;
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(RegisterActivity.this, "获取短信验证码失败", Toast.LENGTH_SHORT).show();
                LogUtil.d("register","发送失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 注册
     */
    private void register() {
        userAccountStr = mAccount.getText().toString();
        userPassword = mPassword.getText().toString();
        userRepeatPassword = mRepeatPassword.getText().toString();
        String messageCode = mMsgCode.getText().toString();

        if (TextUtils.isEmpty(userAccountStr) || TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(userRepeatPassword)){
            Toast.makeText(this, R.string.account_or_password_cannot_null, Toast.LENGTH_LONG).show();
            return;
        }
        if (userAccountStr.length()<3 || userPassword.length()<3 || userRepeatPassword.length()<3){
            Toast.makeText(this, "账号密码的长度不能小于3个字符！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!userPassword.equals(userRepeatPassword)){
            Toast.makeText(this, "两次输入的密码不相同！", Toast.LENGTH_LONG).show();
            return;
        }
//        if (TextUtils.isEmpty(messageCode)){
//            Toast.makeText(this, "验证码不能为空!", Toast.LENGTH_LONG).show();
//            return;
//        }

        registerActual(userAccountStr, userPassword);
    }

    /**
     * 使用腾讯IM注册
     * @param accountStr
     * @param passwordStr
     */
    private void registerActual(String accountStr, String passwordStr) {
        ILiveLoginManager.getInstance().tlsRegister(accountStr, passwordStr, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //注册成功
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                //登录一下
                login();
//                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //注册失败
                Toast.makeText(RegisterActivity.this, "注册失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 使用自己服务器注册
     * @param accountStr    账号
     * @param passwordStr   密码
     * @param messageCode   短信验证码
     */
    private void registerActualWithSelfServer(String accountStr, String passwordStr, String messageCode) {
        String address = Common.URL + "?action=" + Common.REGISTER + "&userName=" + accountStr + "&password=" + passwordStr + "&messageCode=" + messageCode;
        LogUtil.d("URL: ",address);
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //注册失败
//                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                LogUtil.d("register","注册失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch (response.body().string()){
                    case "1":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                        break;
                    case "0":
//                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        break;
                    case "-1":
//                        Toast.makeText(RegisterActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*调用腾讯IM登录*/
    private void login() {
        final String accountStr = mAccount.getText().toString();
        String passwordStr = mPassword.getText().toString();
        ILiveLoginManager.getInstance().tlsLogin(accountStr, passwordStr, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                //登陆成功。
                loginLive(accountStr, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(RegisterActivity.this, "tls登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loginLive(String accountStr, String data) {
        ILiveLoginManager.getInstance().iLiveLogin(accountStr, data, new ILiveCallBack() {

            @Override
            public void onSuccess(Object data) {
                //最终登录成功
                //跳转到修改用户信息界面。
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,EditProfileActivity.class);
                startActivity(intent);

                getSelfInfo();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(RegisterActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(RegisterActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功
                MyApplication.getApplication().setSelfProfile(timUserProfile);
            }
        });
    }

    private void findAllView() {
        mToolbar = findViewById(R.id.toolbar_register);
        mAccount = findViewById(R.id.register_account);
        mPassword = findViewById(R.id.register_password);
        mRepeatPassword = findViewById(R.id.register_repeat_password);
        btnRegiset = findViewById(R.id.register_btn_register);
        mMsgCode = findViewById(R.id.message_code);
        mGetMsgCode = findViewById(R.id.get_message_code);
    }
}
