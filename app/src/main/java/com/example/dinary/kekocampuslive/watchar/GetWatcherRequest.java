package com.example.dinary.kekocampuslive.watchar;

import com.example.dinary.kekocampuslive.beans.Watcher;
import com.example.dinary.kekocampuslive.util.HttpUtil;
import com.example.dinary.kekocampuslive.util.LogUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import common.Common;
import common.ResponseBody;

/**
 * Created by Administrator.
 */
public class GetWatcherRequest extends HttpUtil {
    private static final String HOST = Common.URL + "live?action=getWatcher";

    public String getUrl(String roomId) {
        return HOST + "&roomId=" + roomId;
    }

    //加入房间URL
    public String getJoinRoomUrl(String identifier,String roomId) {
        return Common.URL + "live?action=joinroom&userId=" + identifier + "&roomId=" + roomId;
    }

    @Override
    protected void onFail(IOException e) {
        sendFailMsg(-100,e.toString());
    }

    @Override
    protected void onResponseSuccess(String body) {
        WatcherResponseObj watcherResponseObj = gson.fromJson(body, WatcherResponseObj.class);
        if (watcherResponseObj == null) {
            sendFailMsg(-101, "数据格式错误");
            return;
        }

        if (watcherResponseObj.code.equals(ResponseBody.CODE_SUCCESS)) {
            Set<String> userSet = new HashSet<>();
            if (watcherResponseObj.data == null || watcherResponseObj.data.size() == 0)
                //没有观众
                sendSuccMsg(null);
            else {
                for (Watcher watcher:watcherResponseObj.data){
                    userSet.add(watcher.getUserId());
                    LogUtil.d("watcherId",watcher.getUserId());
                }
                sendSuccMsg(userSet);
            }
        } else if (watcherResponseObj.code.equals(ResponseBody.CODE_FAIL)) {
            sendFailMsg(Integer.valueOf(watcherResponseObj.errCode), watcherResponseObj.errMsg);
        }
    }

    @Override
    protected void onResponseFail(int code) {
        sendFailMsg(code,"服务器异常");
    }
}
