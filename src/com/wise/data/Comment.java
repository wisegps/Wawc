package com.wise.data;
/**
 * 评论数据
 * @author Mr.Wang
 * admire_person  1表示加星  2表示未加星
 * comment_article_id  外键 关联说说表
 */
public class Comment {
	private int comment_id;
	
	private String comment_user;
	
	private String comment_time;
	
	private String comment_content;
	
	private int admire_person;
	
	private int comment_article_id;

	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public String getComment_user() {
		return comment_user;
	}

	public void setComment_user(String comment_user) {
		this.comment_user = comment_user;
	}

	public String getComment_time() {
		return comment_time;
	}

	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public int getAdmire_person() {
		return admire_person;
	}

	public void setAdmire_person(int admire_person) {
		this.admire_person = admire_person;
	}

	public int getComment_article_id() {
		return comment_article_id;
	}

	public void setComment_article_id(int comment_article_id) {
		this.comment_article_id = comment_article_id;
	}
}
