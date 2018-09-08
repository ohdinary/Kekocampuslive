package com.example.dinary.kekocampuslive.createlive;

import android.util.Log;

import common.Common;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;

import common.ResponseBody;

import java.io.IOException;

/**
 * 创建房间请求类
 */
public class CreateLIveRequest extends HttpUtil{

    private static final String RequestParamKey_UserId = "hostId";
    private static final String RequestParamKey_UserAvatar = "hostAvatar";
    private static final String RequestParamKey_UserName = "hostName";
    private static final String RequestParamKey_LiveTitle = "liveTitle";
    private static final String RequestParamKey_LiveCover = "liveCover";

    public static class CreateRoomParam {
        public String userId;
        public String userAvatar;
        public String userName;
        public String liveTitle;
        public String liveCover;
    }

    /**
     * 请求URL
     * @param param 参数类
     * @return
     */
    public String getUrl(CreateRoomParam param){
        return Common.URL + Common.LIVE
                + "?" + "action=" + Common.CREATE_ROOM
                + "&" + RequestParamKey_UserId + "=" + param.userId
                + "&" + RequestParamKey_UserAvatar + "=" + param.userAvatar
                + "&" + RequestParamKey_UserName + "=" + param.userName
                + "&" + RequestParamKey_LiveTitle + "=" + param.liveTitle
                + "&" + RequestParamKey_LiveCover + "=" + param.liveCover
                ;
    }

    @Override
    protected void onFail(IOException e) {
        //请求失败
        sendFailMsg(-100, e.getMessage());
    }

    @Override
    protected void onResponseFail(int code) {
        sendFailMsg(code, "服务出现异常");
    }

    @Override
    protected void onResponseSuccess(String body) {
        //解析返回的数据
        RoomInfoResponseObj responseObject = gson.fromJson(body, RoomInfoResponseObj.class);
        LogUtil.d("createroom",body);
        if (responseObject == null) {
            sendFailMsg(-101, "数据格式错误");
            return;
        }
        //返回直播房间信息成功
        if (responseObject.code.equals(ResponseBody.CODE_SUCCESS)) {
            sendSuccMsg(responseObject.data);
            LogUtil.d("createliverequest_data",responseObject.data.toString());
        }//返回直播房间信息失败
        else if (responseObject.code.equals(ResponseBody.CODE_FAIL)) {
            sendFailMsg(Integer.valueOf(responseObject.errCode), responseObject.errMsg);
        }
    }
}
