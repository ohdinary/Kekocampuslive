package com.example.dinary.kekocampuslive.host;

import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;

import java.io.IOException;

import common.Common;

public class SearchRequest extends HttpUtil {

    private static final String address = Common.URL + "live?action=" + Common.SEARCH ;

    /*参数类*/
    public static class LiveListParam{
        public String hostId;
        //生成请求URL
        public String getUrlParam(){
            return address + "&hostId=" + hostId;
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
        LiveRoomSearchResponseObj responseObj = gson.fromJson(body,LiveRoomSearchResponseObj.class);
        if (responseObj == null){
            sendFailMsg(-100, "数据格式错误!");
            return;
        }
        if (responseObj.code.equals(LiveListResponseObj.CODE_SUCCESS)){
            sendSuccMsg(responseObj.data);
            if (responseObj.data!=null)
                LogUtil.d("search_request",responseObj.data.toString());
        }else {
            sendFailMsg(Integer.parseInt(responseObj.errCode),responseObj.errMsg);
        }
    }
}
