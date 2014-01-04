package com.wise.data;
/**
 * 说说数据
 * @author Mr.Wang
 * article_comment_id  外键   关联评论表
 */
public class Article {
	
	private int article_id;
	
	private String publish_user;
	
	private String publish_time;
	
	private String publish_content;
	
	private int article_comment_id;

	public int getArticle_id() {
		return article_id;
	}

	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}

	public String getPublish_user() {
		return publish_user;
	}

	public void setPublish_user(String publish_user) {
		this.publish_user = publish_user;
	}

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public String getPublish_content() {
		return publish_content;
	}

	public void setPublish_content(String publish_content) {
		this.publish_content = publish_content;
	}

	public int getArticle_comment_id() {
		return article_comment_id;
	}

	public void setArticle_comment_id(int article_comment_id) {
		this.article_comment_id = article_comment_id;
	}
}
