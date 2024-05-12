package com.jfinal.weixin.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MusicUtil {

    public String searchMusic(String search,int page) throws UnsupportedEncodingException {
        String search_url="https://www.kuwo.cn/search/searchMusicBykeyWord?vipver=1&client=kt&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&mobi=1&issubtitle=1&show_copyright_off=1&pn="+page+"&rn=20&all="+ URLEncoder.encode(search,"UTF-8");
        String s=HttpKit.get(search_url);
        String value="";
        JSONArray array= JSONObject.parseObject(s).getJSONArray("abslist");

        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject=array.getJSONObject(i);
            value+=jsonObject.getString("NAME")+"："+jsonObject.getString("DC_TARGETID")+"\n";
        }
        return  value;
    }

    public String getMp3Url(String mId)
    {
        String url="http://www.xintuo1.cn/kw/"+mId;

        return HttpKit.get(url);
    }
}
