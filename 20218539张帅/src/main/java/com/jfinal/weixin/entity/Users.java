package com.jfinal.weixin.entity;

public class Users {
    private String userId;
    private int page;



    private String dy_search;//电影搜索


    public String getDy_search() {
        return dy_search;
    }

    public void setDy_search(String dy_search) {
        this.dy_search = dy_search;
    }

    public int getDy_page() {
        return dy_page;
    }

    public void setDy_page(int dy_page) {
        this.dy_page = dy_page;
    }

    private int dy_page;

    private String music_search;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getMusic_search() {
        return music_search;
    }

    public void setMusic_search(String music_search) {
        this.music_search = music_search;
    }
}
