package com.jfinal.weixin.controller;

import com.jfinal.core.Controller;

import javax.sound.sampled.Control;

public class DianYingContorller extends Controller {
    public String index(){
        String dy_id=this.getPara("dyId");
        System.out.println(dy_id);

        return "index";
    }
}
