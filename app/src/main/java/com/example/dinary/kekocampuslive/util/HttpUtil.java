package com.example.dinary.kekocampuslive.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 通过OkHttp向服务器发送请求，通过注册一个回调处理服务器响应
 */
public abstract class HttpUtil {
    public final int WHAT_FAIL = 0;
    public final int WHAT_SUCC = 1;
    protected static OkHttpClient client = new OkHttpClient();
    protected static final Gson gson = new Gson();

    //使用handler解决OkHttp不能在子线程中更新UI的问题
    private Handler handler = new Handler(Looper.getMainLooper()){
        //在这里进行要处理的操作
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == WHAT_SUCC){
                if (onRequestListener != null){
                    onRequestListener.onSuccess(msg.obj);
//                    LogUtil.d("httputil_handlemessage",msg.obj.toString());
                }
            }else if (what == WHAT_FAIL){
                if (onRequestListener != null)
                    onRequestListener.onFail(msg.arg1, (String) msg.obj);
            }
        }
    };
    /**
     * 使用handler发送成功消息
     * @param obj  消息体
     * @param <T>
     */
    public<T> void sendSuccMsg(T obj){
        Message msg = handler.obtainMessage(WHAT_SUCC);
        msg.obj = obj;
        handler.sendMessage(msg);
    }
    /**
     * 使用handler发送失败消息
     * @param code  错误码
     * @param reason  原因
     */
    public void sendFailMsg(int code, String reason){
        Message msg = handler.obtainMessage(WHAT_FAIL);
        msg.arg1 = code;
        msg.obj = reason;
        handler.sendMessage(msg);
    }

    /**
     * handler在UI线程处理事务的逻辑
     * @param T 从服务器返回的数据
     */
    private OnRequestListener onRequestListener;
    public interface OnRequestListener<T>{
        void onSuccess(T obj);
        void onFail(int code, String msg);
    }
    public void setOnRequestListener(OnRequestListener onRequestListener) {
        this.onRequestListener = onRequestListener;
    }

    /**
     * 发送http请求
     * @param address   地址
     * @param callback  回调接口
     */
    public void sendOkHttpRequest(String address, okhttp3.Callback callback){
        final Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
                onFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //如果响应成功
                if (response.isSuccessful()){
                    onResponseSuccess(response.body().string());
                }else { //响应失败错误码
                    onResponseFail(response.code());
                }
            }
        });
    }

    /**
     * http请求
     * @param url
     */
    public void sendRequest(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //不是UI线程
                if (response!=null)
                    onResponseSuccess(response.body().string());
                else
                    onResponseFail(response.code());
            }
        });
    }

    /**
     * 简单请求
     * @param address
     * @param callback
     */
    public static void sendRequest(String address,Callback callback){
        final Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    protected abstract void onFail(IOException e);

    protected abstract void onResponseFail(int code);

    protected abstract void onResponseSuccess(String body);


    /**
     * 使用Retrofit请求
     */
//    public static void request(Map<String,String> map, retrofit2.Callback<ResponseBody<Object>> callback){
//        //步骤4:创建Retrofit对象
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Common.URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        // 步骤5:创建 网络请求接口 的实例
//        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
//        //对 发送请求 进行封装
//        retrofit2.Call<ResponseBody<Object>> call = request.request(map);
//        //步骤6:发送网络请求(异步)
//        call.enqueue(callback);
//    }
}
