package org.rubby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.avro.generated.HBase;

import com.opensymphony.xwork2.ActionSupport;

public class IndexAction extends BaseAction {
	
	Set<String> follow;
	Set<String> unfollow;
	List<Post>posts;
	
	
	
	
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public Set<String> getFollow() {
		return follow;
	}
	public void setFollow(Set<String> follow) {
		this.follow = follow;
	}
	public Set<String> getUnfollow() {
		return unfollow;
	}
	public void setUnfollow(Set<String> unfollow) {
		this.unfollow = unfollow;
	}
	public IndexAction(){
		follow=new HashSet<String>();
		unfollow=new HashSet<String>();
		posts=new ArrayList<Post>();
	}
	@Override
	public String execute() throws Exception {
		if(login_name.equals("")){
			return SUCCESS;
		}
		HbaseIf hbase=HbaseIf.getInstance();
		follow=hbase.getFollow(login_name);
		Set<String> all=hbase.getAllUser();
		for(String s:all){
			//set 可以直接去检查一个String
			if(!follow.contains(s)){
				unfollow.add(s);
			}
		}
		posts=hbase.getPost(login_name);
		return SUCCESS;
	}


}
