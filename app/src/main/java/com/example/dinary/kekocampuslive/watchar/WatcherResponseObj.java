package com.example.dinary.kekocampuslive.watchar;

import com.example.dinary.kekocampuslive.beans.Watcher;

import java.util.List;

import common.ResponseBody;

/**
 * 请求观众列表的返回体
 */
public class WatcherResponseObj extends ResponseBody{
    //观众id集合
    public List<Watcher> data;
}
