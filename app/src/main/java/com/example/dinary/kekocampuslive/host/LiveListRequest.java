package com.example.dinary.kekocampuslive.host;

import com.example.dinary.kekocampuslive.beans.LiveCell;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import common.Common;

/**
 * 请求直播列表
 */
public class LiveListRequest extends HttpUtil {

    private static final String address = Common.URL + "live?action=" + Common.LIVE_LIST ;

    /*参数类*/
    public static class LiveListParam{
        public int pageIndex;
        //生成请求URL
        public String getUrlParam(){
            return address + "&pageIndex=" + pageIndex;
        }
    }

    @Override
    protected void onFail(IOException e) {
        sendFailMsg(-100,e.toString());
    }

    @Override
    protected void onResponseFail(int code) {
        sendFailMsg(code,"服务器异常");
    }

    @Override
    protected void onResponseSuccess(String body) {
        LiveListResponseObj responseObj = gson.fromJson(body,LiveListResponseObj.class);
        if (responseObj == null){
            sendFailMsg(-100, "数据格式错误!");
            return;
        }
        if (responseObj.code.equals(LiveListResponseObj.CODE_SUCCESS)){
            sendSuccMsg(responseObj.data.content);
            LogUtil.d("livelistrequest_body",responseObj.data.content.toString());
        }else {
            sendFailMsg(Integer.parseInt(responseObj.errCode),responseObj.errMsg);
        }
    }
}
