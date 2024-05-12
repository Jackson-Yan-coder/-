package com.jfinal.weixin.Controller;

import com.jfinal.core.Controller;

public class DianyingController extends Controller {
    public String index(){
        String dy_id=this.getPara("dyId");
        System.out.println(dy_id);
        return "index";
    }
}
