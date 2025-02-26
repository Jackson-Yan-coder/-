/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.demo;

import com.jfinal.weixin.entity.Users;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.*;
import com.jfinal.weixin.sdk.msg.in.event.*;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutMusicMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.jfinal.weixin.uti.DianYingUtil;
import com.jfinal.weixin.uti.MusicUtil;
import com.sun.deploy.security.SelectableSecurityManager;
import jdk.nashorn.internal.ir.CallNode;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * 将此 WeixinMsgController 在 YourJFinalConfig 中注册路由，
 * 并设置好 weixin 开发者中心的 URL 与 token ，使 URL 指向该
 * WeixinMsgController 继承自父类 MsgController 的 index
 * 方法即可直接运行看效果，在此基础之上修改相关的方法即可进行实际项目开发
 *
 * 注意：高版本 jfinal 需要在 configRoute(Routes routes) 中配置
 *       routes.setMappingSuperClass(true);
 *      才能将超类 MsgController 中的 index() 映射为 action
 */
public class WeixinMsgController extends MsgControllerAdapter {


    public static HashMap<String, Users> userMap=new HashMap<>();

    private MusicUtil musicUtil=new MusicUtil();

    private DianYingUtil dyUtil=new DianYingUtil();
    @Override
    protected void processInTextMsg(InTextMsg inTextMsg) {
        OutTextMsg outMsg = new OutTextMsg(inTextMsg);

        String userMsg= inTextMsg.getContent();
        if(userMsg.startsWith("#")) {
            String cmd="";
            if(userMsg.indexOf("@")>0){
                cmd = userMsg.substring(1, userMsg.indexOf("@"));
            }else{
                cmd = userMsg.substring(1);
            }
            if (cmd.equals("音乐")) {
                String search = userMsg.substring(userMsg.indexOf("@") + 1);
                //如果为空证明第一次查询
                Users users=new Users();
                users.setUserId(inTextMsg.getFromUserName());
                users.setMusic_search(search);
                users.setPage(0);
                userMap.put(users.getUserId(),users);
//                else{
//                    users=userMap.get(inTextMsg.getFromUserName());
//                    if(!users.getMusic_search().equals(search)){
//                        users.setPage(1);
//
//                    }
//                }
                String value = null;
                try {
                    value=musicUtil.searchMusic(search,users.getPage());
                    value+="下一页输入:%%2 ";
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                outMsg.setContent(value);
                render(outMsg);
            }else if(cmd.equals("帮助")){
                StringBuffer sb=new StringBuffer("1,搜索音乐格式为：@音乐#内容\n");
                sb.append("2,听指定音乐输入音乐格式：@ID\n");
                outMsg.setContent(sb.toString());
                render(outMsg);
            }else if(cmd.equals("电影")){
                String search = userMsg.substring(userMsg.indexOf("@") + 1);
                //如果为空证明第一次查询
                Users users=new Users();
                users.setUserId(inTextMsg.getFromUserName());
                users.setDy_search(search);
                users.setDy_page(1);
                userMap.put(users.getUserId(),users);
                String value = null;
                try {
                    value=dyUtil.searchDianYing(search,users.getDy_page());
                    if(value==null||value.equals("")){
                        outMsg.setContent("已经是最后一页了。");
                        render(outMsg);
                    }else{

                    }
                        value+="下一页输入:%%2";

                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                outMsg.setContent(value);
                render(outMsg);
            }
        }
        else if(userMsg.startsWith("@")){
                String mId=inTextMsg.getContent().substring(1);
                String mp3url=musicUtil.getMp3Url(mId);
                OutMusicMsg outMusicMsg = new OutMusicMsg(inTextMsg);
                outMusicMsg.setTitle("点击听音乐");
                outMusicMsg.setHqMusicUrl(mp3url);
                render(outMusicMsg);

//翻页
        }else if(userMsg.startsWith("%%2")){
            Users user=null;
            System.out.println(userMap.get(inTextMsg.getFromUserName()));
            if (userMap.get(inTextMsg.getFromUserName())!=null){
                user=userMap.get(inTextMsg.getFromUserName());
                int page=user.getPage();
                String  value = null;
                try {
                    page++;
                    if(page!=0){
                        value="上一页输入：%%1\n";
                    }

                    value += musicUtil.searchMusic(user.getMusic_search(), page);
                    System.out.println(value);
                    value+="下一页输入：%%3";
                    user.setPage(page);
                    userMap.put(user.getUserId(),user);
                     outMsg.setContent(value);
                    render(outMsg);

                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //翻上一页
        else if(userMsg.startsWith("%%1"))
        {
            //先判断是否查询了音乐
            Users user=null;
            if(userMap.get(inTextMsg.getFromUserName())!=null){
                user=userMap.get(inTextMsg.getFromUserName());
                int page=user.getPage();
                String value ="";
                try {

                    if(page!=0)
                    {
                        value="上一页请输入：%%1\n";
                    }
                    page--;
                    value = musicUtil.searchMusic(user.getMusic_search(),page);
                    value+="下一页请输入：%%2";
                    user.setPage(page);
                    userMap.put(user.getUserId(),user);
                    outMsg.setContent(value);
                    render(outMsg);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //电影翻页
        else if(userMsg.startsWith("%%2")){
            Users user=null;
            System.out.println(userMap.get(inTextMsg.getFromUserName()));
            if (userMap.get(inTextMsg.getFromUserName())!=null){
                user=userMap.get(inTextMsg.getFromUserName());
                int page=user.getDy_page();
                String  value ="";
                try {
                    page++;
                    if(page!=0){
                        value="上一页输入：%%1\n";
                    }
                    value += dyUtil.searchDianYing(user.getDy_search(), page);
                    if(value==null||value.equals("")){
                        outMsg.setContent("已经是最后一页了。");
                        render(outMsg);
                    }else{
                        System.out.println(value);
                        value+="下一页输入：%%3";
                        user.setDy_page(page);
                        userMap.put(user.getUserId(),user);
                        outMsg.setContent(value);
                        render(outMsg);
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }else if(userMsg.startsWith("*")){
                String search = userMsg.substring(1);
            }
        }

        else {
                outMsg.setContent("请输入#帮助进行查询");
                render(outMsg);
        }
        //outMsg.setContent("我是你爸爸~");

    }

    @Override
    protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
        OutTextMsg outMsg = new OutTextMsg(inVoiceMsg);
        outMsg.setContent("语音消息~");
    }

    @Override
    protected void processInVideoMsg(InVideoMsg inVideoMsg) {
        OutTextMsg outMsg = new OutTextMsg(inVideoMsg);
        outMsg.setContent("接收视频消息~");
    }

    @Override
    protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
        OutTextMsg outMsg = new OutTextMsg(inShortVideoMsg);
        outMsg.setContent("小视频消息~");
    }

    @Override
    protected void processInLocationMsg(InLocationMsg inLocationMsg) {
        //转发给多客服PC客户端
        OutCustomMsg outCustomMsg = new OutCustomMsg(inLocationMsg);
        render(outCustomMsg);
    }

    @Override
    protected void processInLinkMsg(InLinkMsg inLinkMsg)
    {
        //转发给多客服PC客户端
        OutCustomMsg outCustomMsg = new OutCustomMsg(inLinkMsg);
        render(outCustomMsg);
    }

    @Override
    protected void processInCustomEvent(InCustomEvent inCustomEvent)
    {
        log.debug("测试方法：processInCustomEvent()");
        renderNull();
    }

    @Override
    protected void processInImageMsg(InImageMsg inImageMsg)
    {
        //转发给多客服PC客户端
        OutCustomMsg outCustomMsg = new OutCustomMsg(inImageMsg);
        render(outCustomMsg);
    }

    /**
     * 实现父类抽方法，处理关注/取消关注消息
     */
    @Override
    protected void processInFollowEvent(InFollowEvent inFollowEvent)
    {
        if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent()))
        {
            log.debug("关注：" + inFollowEvent.getFromUserName());
            OutTextMsg outMsg = new OutTextMsg(inFollowEvent);
            outMsg.setContent("这是Jfinal-weixin测试服务</br>\r\n感谢您的关注");
            render(outMsg);
        }
        // 如果为取消关注事件，将无法接收到传回的信息
        if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent()))
        {
            log.debug("取消关注：" + inFollowEvent.getFromUserName());
        }
    }

    @Override
    protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent)
    {
        if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent()))
        {
            log.debug("扫码未关注：" + inQrCodeEvent.getFromUserName());
            OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
            outMsg.setContent("感谢您的关注，二维码内容：" + inQrCodeEvent.getEventKey());
            render(outMsg);
        }
        if (InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent()))
        {
            log.debug("扫码已关注：" + inQrCodeEvent.getFromUserName());
        }
    }

    @Override
    protected void processInLocationEvent(InLocationEvent inLocationEvent)
    {
        log.debug("发送地理位置事件：" + inLocationEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
        outMsg.setContent("地理位置是：" + inLocationEvent.getLatitude());
        render(outMsg);
    }

    @Override
    protected void processInMassEvent(InMassEvent inMassEvent)
    {
        log.debug("测试方法：processInMassEvent()");
        renderNull();
    }

    /**
     * 实现父类抽方法，处理自定义菜单事件
     */
    @Override
    protected void processInMenuEvent(InMenuEvent inMenuEvent)
    {
        log.debug("菜单事件：" + inMenuEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
        outMsg.setContent("菜单事件内容是：" + inMenuEvent.getEventKey());
        render(outMsg);
    }

    @Override
    protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults)
    {
        log.debug("语音识别事件：" + inSpeechRecognitionResults.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inSpeechRecognitionResults);
        outMsg.setContent("语音识别内容是：" + inSpeechRecognitionResults.getRecognition());
        render(outMsg);
    }

    @Override
    protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent)
    {
        log.debug("测试方法：processInTemplateMsgEvent()");
        renderNull();
    }

//    /**
//     * 实现父类抽方法，处理文本消息
//     * 本例子中根据消息中的不同文本内容分别做出了不同的响应，同时也是为了测试 jfinal weixin sdk的基本功能：
//     *     本方法仅测试了 OutTextMsg、OutNewsMsg、OutMusicMsg 三种类型的OutMsg，
//     *     其它类型的消息会在随后的方法中进行测试
//     */
//    protected void processInTextMsg(InTextMsg inTextMsg) {
//        String msgContent = inTextMsg.getContent().trim();
//        // 帮助提示
//        if ("help".equalsIgnoreCase(msgContent) || "帮助".equals(msgContent)) {
//            OutTextMsg outMsg = new OutTextMsg(inTextMsg);
//            outMsg.setContent(helpStr);
//            render(outMsg);
//        }
//        // 图文消息测试
//        else if ("news".equalsIgnoreCase(msgContent) || "新闻".equalsIgnoreCase(msgContent)) {
//            OutNewsMsg outMsg = new OutNewsMsg(inTextMsg);
//            outMsg.addNews("JFinal 2.0 发布,JAVA 极速 WEB+ORM 框架", "本星球第一个极速开发框架", "https://mmbiz.qlogo.cn/mmbiz/KJoUl0sqZFS0fRW68poHoU3v9ulTWV8MgKIduxmzHiamkb3yHia8pCicWVMCaFRuGGMnVOPrrj2qM13u9oTahfQ9A/0?wx_fmt=png", "http://mp.weixin.qq.com/s?__biz=MzA4NjM4Mjk2Mw==&mid=211063163&idx=1&sn=87d54e2992237a3f791f08b5cdab7990#rd");
//            outMsg.addNews("JFinal 1.8 发布,JAVA 极速 WEB+ORM 框架", "现在就加入 JFinal 极速开发世界，节省更多时间去跟女友游山玩水 ^_^", "http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq1ibBkhSA1BibMuMxLuHIvUfiaGsK7CC4kIzeh178IYSHbYQ5eg9tVxgEcbegAu22Qhwgl5IhZFWWXUw/0", "http://mp.weixin.qq.com/s?__biz=MjM5ODAwOTU3Mg==&mid=200313981&idx=1&sn=3bc5547ba4beae12a3e8762ababc8175#rd");
//            outMsg.addNews("JFinal 1.6 发布,JAVA 极速 WEB+ORM 框架", "JFinal 1.6 主要升级了 ActiveRecord 插件，本次升级全面支持多数源、多方言、多缓", "http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq0fcR8VmNCgugHXv7gVlxI6w95RBlKLdKUTjhOZIHGSWsGvjvHqnBnjIWHsicfcXmXlwOWE6sb39kA/0", "http://mp.weixin.qq.com/s?__biz=MjM5ODAwOTU3Mg==&mid=200121522&idx=1&sn=ee24f352e299b2859673b26ffa4a81f6#rd");
//            render(outMsg);
//        }
//        // 音乐消息测试
//        else if ("music".equalsIgnoreCase(msgContent) || "音乐".equals(msgContent)) {
//            OutMusicMsg outMsg = new OutMusicMsg(inTextMsg);
//            outMsg.setTitle("When The Stars Go Blue-Venke Knutson");
//            outMsg.setDescription("建议在 WIFI 环境下流畅欣赏此音乐");
//            outMsg.setMusicUrl("http://www.jfinal.com/When_The_Stars_Go_Blue-Venke_Knutson.mp3");
//            outMsg.setHqMusicUrl("http://www.jfinal.com/When_The_Stars_Go_Blue-Venke_Knutson.mp3");
//            outMsg.setFuncFlag(true);
//            render(outMsg);
//        }
//        else if ("美女".equalsIgnoreCase(msgContent)) {
//            OutNewsMsg outMsg = new OutNewsMsg(inTextMsg);
//            outMsg.addNews(
//                    "JFinal 宝贝更新喽",
//                    "jfinal 宝贝更新喽，我们只看美女 ^_^",
//                    "https://mmbiz.qlogo.cn/mmbiz/KJoUl0sqZFRHa3VrmibqAXRfYPNdiamFnpPTOvXoxsFlRoOHbVibGhmHOEUQiboD3qXWszKuzWpibFxsVW1RmNB9hPw/0?wx_fmt=jpeg",
//                    "http://mp.weixin.qq.com/s?__biz=MzA4NjM4Mjk2Mw==&mid=211356950&idx=1&sn=6315a1a2848aa8cb0694bf1f4accfb07#rd");
//            // outMsg.addNews("秀色可餐", "JFinal Weixin 极速开发就是这么爽，有木有 ^_^", "http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq2GJLC60ECD7rE7n1cvKWRNFvOyib4KGdic3N5APUWf4ia3LLPxJrtyIYRx93aPNkDtib3ADvdaBXmZJg/0", "http://mp.weixin.qq.com/s?__biz=MjM5ODAwOTU3Mg==&mid=200987822&idx=1&sn=7eb2918275fb0fa7b520768854fb7b80#rd");
//
//            render(outMsg);
//        }
//        else if ("视频教程".equalsIgnoreCase(msgContent) || "视频".equalsIgnoreCase(msgContent)) {
//            renderOutTextMsg("\thttp://pan.baidu.com/s/1nt2zAT7  \t密码:824r");
//        }
//        // 其它文本消息直接返回原值 + 帮助提示
//        else {
//            renderOutTextMsg("\t文本消息已成功接收，内容为： " + inTextMsg.getContent() + "\n\n" + helpStr);
//        }
//    }
//
//    /**
//     * 实现父类抽方法，处理图片消息
//     */
//    protected void processInImageMsg(InImageMsg inImageMsg) {
//        OutImageMsg outMsg = new OutImageMsg(inImageMsg);
//        // 将刚发过来的图片再发回去
//        outMsg.setMediaId(inImageMsg.getMediaId());
//        render(outMsg);
//    }
//
//    /**
//     * 实现父类抽方法，处理语音消息
//     */
//    protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
//        OutVoiceMsg outMsg = new OutVoiceMsg(inVoiceMsg);
//        // 将刚发过来的语音再发回去
//        outMsg.setMediaId(inVoiceMsg.getMediaId());
//        render(outMsg);
//    }
//
//    /**
//     * 实现父类抽方法，处理视频消息
//     */
//    protected void processInVideoMsg(InVideoMsg inVideoMsg) {
//        /* 腾讯 api 有 bug，无法回复视频消息，暂时回复文本消息代码测试
//        OutVideoMsg outMsg = new OutVideoMsg(inVideoMsg);
//        outMsg.setTitle("OutVideoMsg 发送");
//        outMsg.setDescription("刚刚发来的视频再发回去");
//        // 将刚发过来的视频再发回去，经测试证明是腾讯官方的 api 有 bug，待 api bug 却除后再试
//        outMsg.setMediaId(inVideoMsg.getMediaId());
//        render(outMsg);
//        */
//        OutTextMsg outMsg = new OutTextMsg(inVideoMsg);
//        outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inVideoMsg.getMediaId());
//        render(outMsg);
//    }
//
//    @Override
//    protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg)
//    {
//        OutTextMsg outMsg = new OutTextMsg(inShortVideoMsg);
//        outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inShortVideoMsg.getMediaId());
//        render(outMsg);
//    }
//
//    /**
//     * 实现父类抽方法，处理地址位置消息
//     */
//    protected void processInLocationMsg(InLocationMsg inLocationMsg) {
//        OutTextMsg outMsg = new OutTextMsg(inLocationMsg);
//        outMsg.setContent("已收到地理位置消息:" +
//                            "\nlocation_X = " + inLocationMsg.getLocation_X() +
//                            "\nlocation_Y = " + inLocationMsg.getLocation_Y() +
//                            "\nscale = " + inLocationMsg.getScale() +
//                            "\nlabel = " + inLocationMsg.getLabel());
//        render(outMsg);
//    }
//
//    /**
//     * 实现父类抽方法，处理链接消息
//     * 特别注意：测试时需要发送我的收藏中的曾经收藏过的图文消息，直接发送链接地址会当做文本消息来发送
//     */
//    protected void processInLinkMsg(InLinkMsg inLinkMsg) {
//        OutNewsMsg outMsg = new OutNewsMsg(inLinkMsg);
//        outMsg.addNews("链接消息已成功接收", "链接使用图文消息的方式发回给你，还可以使用文本方式发回。点击图文消息可跳转到链接地址页面，是不是很好玩 :)" , "http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq1ibBkhSA1BibMuMxLuHIvUfiaGsK7CC4kIzeh178IYSHbYQ5eg9tVxgEcbegAu22Qhwgl5IhZFWWXUw/0", inLinkMsg.getUrl());
//        render(outMsg);
//    }
//
//    @Override
//    protected void processInCustomEvent(InCustomEvent inCustomEvent)
//    {
//        System.out.println("processInCustomEvent() 方法测试成功");
//    }
//
//    /**
//     * 实现父类抽方法，处理关注/取消关注消息
//     */
//    protected void processInFollowEvent(InFollowEvent inFollowEvent) {
//        OutTextMsg outMsg = new OutTextMsg(inFollowEvent);
//        outMsg.setContent("感谢关注 JFinal Weixin 极速开发服务号，为您节约更多时间，去陪恋人、家人和朋友 :) \n\n\n " + helpStr);
//        // 如果为取消关注事件，将无法接收到传回的信息
//        render(outMsg);
//    }
//
//    /**
//     * 实现父类抽方法，处理扫描带参数二维码事件
//     */
//    protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
//        OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
//        outMsg.setContent("processInQrCodeEvent() 方法测试成功");
//        render(outMsg);
//    }
//
//    /**
//     * 实现父类抽方法，处理上报地理位置事件
//     */
//    protected void processInLocationEvent(InLocationEvent inLocationEvent) {
//        OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
//        outMsg.setContent("processInLocationEvent() 方法测试成功");
//        render(outMsg);
//    }
//
//    @Override
//    protected void processInMassEvent(InMassEvent inMassEvent)
//    {
//        System.out.println("processInMassEvent() 方法测试成功");
//    }
//
//    /**
//     * 实现父类抽方法，处理自定义菜单事件
//     */
//    protected void processInMenuEvent(InMenuEvent inMenuEvent) {
//        renderOutTextMsg("processInMenuEvent() 方法测试成功");
//    }
//
//    /**
//     * 实现父类抽方法，处理接收语音识别结果
//     */
//    protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
//        renderOutTextMsg("语音识别结果： " + inSpeechRecognitionResults.getRecognition());
//    }
//
//    // 处理接收到的模板消息是否送达成功通知事件
//    protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
//        String status = inTemplateMsgEvent.getStatus();
//        renderOutTextMsg("模板消息是否接收成功：" + status);
//    }
//    @Override
//    protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
//        logger.debug("摇一摇周边设备信息通知事件：" + inShakearoundUserShakeEvent.getFromUserName());
//        OutTextMsg outMsg = new OutTextMsg(inShakearoundUserShakeEvent);
//        outMsg.setContent("摇一摇周边设备信息通知事件UUID：" + inShakearoundUserShakeEvent.getUuid());
//        render(outMsg);
//    }
//
//    @Override
//    protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {
//        logger.debug("资质认证成功通知事件：" + inVerifySuccessEvent.getFromUserName());
//        OutTextMsg outMsg = new OutTextMsg(inVerifySuccessEvent);
//        outMsg.setContent("资质认证成功通知事件：" + inVerifySuccessEvent.getExpiredTime());
//        render(outMsg);
//    }
//
//    @Override
//    protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent){
//        logger.debug("资质认证失败通知事件：" + inVerifyFailEvent.getFromUserName());
//        OutTextMsg outMsg = new OutTextMsg(inVerifyFailEvent);
//        outMsg.setContent("资质认证失败通知事件：" + inVerifyFailEvent.getFailReason());
//        render(outMsg);
//    }
//
//    /**
//     * 处理微信硬件绑定和解绑事件
//     * @param InEqubindEvent 处理微信硬件绑定和解绑事件
//     */
//    @Override
//    protected void processInEqubindEvent(InEqubindEvent msg) {
//        String deviceType = msg.getDeviceType();
//        String deviceID = msg.getDeviceID();
//        String openID = msg.getOpenID();
//        String event = msg.getEvent();
//        String sessionID= msg.getSessionID();
//
//        System.out.printf("收到消息,Openid,DeviceId,event:%s;%s;%s",msg.getOpenID(),msg.getDeviceID(),msg.getEvent());
//        System.out.println(":DeviceType:" + deviceType);
//        System.out.println(":sessionID:" + sessionID);
//
//        // 存储用户和设备的绑定关系
//        // 设备绑定/解绑事件可以回复空包体
//        if(InEqubindEvent.BIND.equals(event)){
//            // DO 业务
//        }else{
//            // DO 业务
//        }
//        //respons event
//        OutEquDataMsg oeqmsg = new OutEquDataMsg(msg);
//        oeqmsg.setContent("");
//        oeqmsg.setDeviceID(deviceID);
//        oeqmsg.setDeviceType(deviceType);
//        oeqmsg.setSessionID(sessionID);
//        render(oeqmsg);
//    }
//
//    /**
//     * 处理微信硬件发来数据
//     * @param InEquDataMsg 处理微信硬件发来数据
//     */
//    @Override
//    protected void processInEquDataMsg(InEquDataMsg msg) {
//        String reqContent = msg.getContent();
//        // Base64解码
//        byte[] reqRaw = Base64Utils.decodeBase64(reqContent);
//        // 反序列化
//        BlueLight lightReq = BlueLight.parse(reqRaw);
//
//        // 逻辑处理
//        // demo中 推送消息给用户微信
//        String reqText = lightReq.body;
//        System.out.println("recv text:" + reqText);
//        String transText = "收到设备发送的数据：";
//
//        byte[] reqTextRaw = reqText.getBytes(Charsets.UTF_8);
//
//        if (reqTextRaw.length > 0 && reqTextRaw[reqTextRaw.length - 1] == 0) {
//            // 推送给微信用户的内容去掉末尾的反斜杠零'\0'
//            transText = transText + new String(reqTextRaw, 0, reqTextRaw.length - 1, Charsets.UTF_8);
//        } else{
//            transText = transText + reqText;
//        }
//
//        // 推送文本消息给微信
//        //MpApi.customSendText(openID, transText);
//
//        // demo中 回复 收到的内容给设备
//        BlueLight lightResp = BlueLight.build(BlueLight.CmdId.SEND_TEXT_RESP, reqText, lightReq.head.seq);
//        // 序列化
//        byte[] respRaw = lightResp.toBytes();
//        // Base64编码
//        String respCon = Base64Utils.encode(respRaw);
//        System.out.println(respCon);
//        // 设备消息接口必须回复符合协议的xml
//        //TODO 解析并获取测量值
//        renderOutTextMsg(transText);
//    }
}






