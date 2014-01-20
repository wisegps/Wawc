package com.wise.data;

import java.util.List;
import java.util.Map;

/**
 * 说说数据
 * @author Mr.Wang
 */
public class Article {
	private String create_time;
	private String city;
	private String lat;
	private String lon;
	private String content;
	private String title;
	private String name;
	private int cust_id;
	private int blog_id;
	private String _id;
	private List<String> praisesList;
	private List<String> commentList;
	private List<Map<String,String>> imageList;
	private int _v;
	
	private String JSONDatas;   //一篇文章的所有数据
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
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
	public int getCust_id() {
		return cust_id;
	}
	public void setCust_id(int cust_id) {
		this.cust_id = cust_id;
	}
	public int getBlog_id() {
		return blog_id;
	}
	public void setBlog_id(int blog_id) {
		this.blog_id = blog_id;
	}
	public List<String> getPraisesList() {
		return praisesList;
	}
	public void setPraisesList(List<String> praisesList) {
		this.praisesList = praisesList;
	}
	public List<String> getCommentList() {
		return commentList;
	}
	public void setCommentList(List<String> commentList) {
		this.commentList = commentList;
	}
	public List<Map<String, String>> getImageList() {
		return imageList;
	}
	public void setImageList(List<Map<String, String>> imageList) {
		this.imageList = imageList;
	}
	public int get_v() {
		return _v;
	}
	public void set_v(int _v) {
		this._v = _v;
	}
	public String getJSONDatas() {
		return JSONDatas;
	}
	public void setJSONDatas(String jSONDatas) {
		JSONDatas = jSONDatas;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}
