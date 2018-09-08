package com.example.dinary.kekocampuslive.host;

import com.example.dinary.kekocampuslive.beans.LiveCell;

import java.util.List;

import common.ResponseBody;

/**
 * 直播列表返回体
 */
public class LiveListResponseObj extends ResponseBody {
    //格式必须要和返回的数据一致!!!
    //jpa分页查询得到的数据不能直接返回,要先解析
    public data data;
    public class data{
        public List<LiveCell> content;
        public boolean last;
        public int totalPages;
        public int totalElements;
        public int number;
        public int size;
        public String sort;
        public boolean first;
        public int numberOfElements;
    }
}
