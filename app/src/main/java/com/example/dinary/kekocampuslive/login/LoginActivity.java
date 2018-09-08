package com.example.dinary.kekocampuslive.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dinary.kekocampuslive.MainActivity;
import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.R;
import com.example.dinary.kekocampuslive.register.RegisterActivity;
import common.Common;
import com.example.dinary.kekocampuslive.util.DBUtil;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import common.ResponseBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mAccountEdt;
    private EditText mPasswordEdt;
    private Button mLogginBtn;
    private Button mRegisterBtn;
    private TextView mForgetPsw;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE); //默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
        editor = sharedPreferences.edit();

        findAllView();
        addListener();

        //登录到主界面
//        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//        finish();
    }

    private void addListener() {
        //登录
        mLogginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        //注册
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        //忘记密码
        mForgetPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPasswordByEmail();
            }
        });
    }

    /*通过邮箱找回密码*/
    private void findPasswordByEmail() {
        String address = Common.URL + "?action=" + Common.EMAIL + "&userName=" + mAccountEdt.getText().toString();
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(LoginActivity.this, "找回密码失败!" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String ss = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ss.equals("1")){
                            Toast.makeText(LoginActivity.this, "新密码已发送至您的邮箱!" , Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "发送失败!" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void findAllView() {
        mAccountEdt = findViewById(R.id.login_account);
        mPasswordEdt = findViewById(R.id.login_password);
        mLogginBtn = findViewById(R.id.login);
        mRegisterBtn = findViewById(R.id.login_btn_register);
        mForgetPsw = findViewById(R.id.forget_password);

        //从sharedpreference中获取账号密码
        if (editor!=null){
            mAccountEdt.setText(sharedPreferences.getString("userName", ""));
            mAccountEdt.setSelection(mAccountEdt.length());
            mPasswordEdt.setText(sharedPreferences.getString("password",""));
        }
    }

    /**
     * 登录具体逻辑
     */
    private void login(){
        final String accountStr = mAccountEdt.getText().toString(); //用户名
        final String passwordStr = mPasswordEdt.getText().toString();   //密码

        if (TextUtils.isEmpty(accountStr) || TextUtils.isEmpty(passwordStr)){
            Toast.makeText(this, R.string.account_or_password_cannot_null, Toast.LENGTH_LONG).show();
            return;
        }

        /*
         调用腾讯IM登录:账号登录(托管模式)
         参数:
         id - 用户id
         pwd - 用户密码
         listener - 回调
         */
        ILiveLoginManager.getInstance().tlsLogin(accountStr, passwordStr, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                //登陆成功。
                loginLive(accountStr, data);
                editor.putString("userName", accountStr);
                editor.putString("password", passwordStr);
                editor.commit();    //一定要提交！
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(LoginActivity.this, "tls登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginLive(final String accountStr, String data) {
        //iLiveSDK 登录(独立模式下直接使用该接口，托管模式需先用tlsLogin登录)
        //参数:
        //id - 用户id
        //sig - 用户密钥
        ILiveLoginManager.getInstance().iLiveLogin(accountStr, data, new ILiveCallBack() {

            @Override
            public void onSuccess(Object data) {
                //最终登录成功
                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                getSelfInfo();
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //登录失败
                Toast.makeText(LoginActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*登录成功获取个人信息*/
    private void getSelfInfo() {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                //登录失败
                Toast.makeText(LoginActivity.this, "获取信息失败：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //获取自己信息成功,将信息保存在MyApplication中
                MyApplication.getApplication().setSelfProfile(timUserProfile);
            }
        });
    }

    /*使用自己的服务器登录*/
    private void loginWithSelfServer(final String accountStr, final String passwordStr){
        String address = Common.URL + "?action=" + Common.LOGIN + "&userName=" + accountStr + "&password=" + passwordStr;
        LogUtil.d("login", address);

        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //登录失败
//                Toast.makeText(LoginActivity.this, "登录失败!" , Toast.LENGTH_SHORT).show();
                LogUtil.e("login","登录失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将账号存到sharedpreferences中
                editor.putString("userName", accountStr);
                editor.putString("password", passwordStr);
                editor.commit();    //一定要提交！

                //登陆成功。
                String result = response.body().string();   //string()方法只能使用一次!
                if (result.equals("1")){
                    //调用runOnUIThread方法切换到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //保存用户信息
                            Connector.getDatabase();    //创建数据库
                            if (DBUtil.saveNameAndPsw(accountStr, mPasswordEdt.getText().toString()))
                                LogUtil.d("saveUser", "创建新用户");
                            else
                                LogUtil.d("saveUser", "用户已存在");

                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            getSelfInfo();
                            finish();
                        }
                    });
                }
            }
        });
    }

    /*使用retrofit登录*/
//    private void loginWithRetrofit(final String accountStr,final String passwordStr){
//        Map<String,String> map = new HashMap<>();
//        map.put("action",Common.LOGIN);
//        map.put("userName",accountStr);
//        map.put("password",passwordStr);
//        HttpUtil.request(map, new retrofit2.Callback<ResponseBody<Object>>() {
//            @Override
//            public void onResponse(retrofit2.Call<ResponseBody<Object>> call, retrofit2.Response<ResponseBody<Object>> response) {
//                //将账号存到sharedpreferences中
//                editor.putString("userName", accountStr);
//                editor.putString("password", passwordStr);
//                editor.commit();    //一定要提交！
//
//                //登陆成功。
//                String result = response.body().code;   //string()方法只能使用一次!
//                if (result.equals("1")){
//                    //调用runOnUIThread方法切换到主线程处理逻辑
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent();
//                            intent.setClass(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//
//                            getSelfInfo();
//                            finish();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<ResponseBody<Object>> call, Throwable t) {
//                //登录失败
//                Toast.makeText(LoginActivity.this, "登录失败!" , Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private void register(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
