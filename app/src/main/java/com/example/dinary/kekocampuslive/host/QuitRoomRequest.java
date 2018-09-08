package com.example.dinary.kekocampuslive.host;

import com.example.dinary.kekocampuslive.util.HttpUtil;

import java.io.IOException;

import common.Common;

/**
 * 退出房间请求
 */
public class QuitRoomRequest extends HttpUtil {

    private static final String RequestParamKey_RoomId = "roomId";
    private static final String RequestParamKey_UserId = "hostId";

    public String getUrl(String roomId,String hostId){
        return Common.URL + "live?action=" + Common.QUIT_ROOM
                + "&" + RequestParamKey_RoomId + "=" + roomId
                + "&" + RequestParamKey_UserId + "=" + hostId
                ;
    }

    @Override
    protected void onFail(IOException e) {
        sendFailMsg(-100, e.getMessage());
    }


    @Override
    protected void onResponseFail(int code) {
        sendFailMsg(code, "服务出现异常");
    }

    @Override
    protected void onResponseSuccess(String body) {

    }
}
