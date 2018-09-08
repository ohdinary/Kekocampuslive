package com.example.dinary.kekocampuslive.createlive;

import com.example.dinary.kekocampuslive.beans.LiveCell;
import common.ResponseBody;


/**
 * 请求房间返回信息
 */
public class RoomInfoResponseObj extends ResponseBody {
    public LiveCell data;   //直播房间

    @Override
    public String toString() {
        return "data:"+data+"\ncode:"+code+"\nerrcode:"+errCode+"\nerrMsg:"+errMsg;
    }
}
