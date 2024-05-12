package com.jfinal.weixin.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DianYingUtil {
    public String searchMusic(String search,int page) throws UnsupportedEncodingException {
        String search_url="https://api.tiankongapi.com/api.php/provide/vod/?ac=list&pg="+page+"&wd="+
             URLEncoder.encode(search,"UTF-8");
        String s= HttpKit.get(search_url);
        String value="";
        JSONArray array= JSONObject.parseObject(s).getJSONArray("list");
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject=array.getJSONObject(i);
            value+=jsonObject.getString("vod_name")+":"+jsonObject.getString("vod_id")+"\n";
        }
        return value;
    }
}
