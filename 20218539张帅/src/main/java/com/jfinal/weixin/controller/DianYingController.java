package com.jfinal.weixin.controller;

import com.jfinal.core.Controller;


public class DianYingController extends Controller {


    public String index(){

        String dy_id = this.getPara("dyId");

        System.out.println(dy_id);

        return "index";
    }
}
