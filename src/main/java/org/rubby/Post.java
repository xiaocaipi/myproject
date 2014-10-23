package org.rubby;

public class Post {

	String username;//发送的用户名]
	String content;//发送的内容
	String ts;//发送的时间
	
	
	public Post(String username, String content, String ts) {
		this.username = username;
		this.content = content;
		this.ts = ts;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	
	
}
