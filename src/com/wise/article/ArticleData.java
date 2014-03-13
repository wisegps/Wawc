package com.wise.article;

import java.util.List;

public class ArticleData {
    String create_time;
    String logo;
    String content;
    String title;
    String name;
    String cust_id;
    String blog_id;
    List<PicData> picDatas;
    public String getCreate_time() {
        return create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCust_id() {
        return cust_id;
    }
    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }
    public String getBlog_id() {
        return blog_id;
    }
    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }
    public List<PicData> getPicDatas() {
        return picDatas;
    }
    public void setPicDatas(List<PicData> picDatas) {
        this.picDatas = picDatas;
    }    
}
