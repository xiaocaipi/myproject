package org.rubby;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends BaseAction {
	//表单的参数被struts2 会自动通过set和get赋值到对象中，可以直接使用
	String username;
	String password;
	String password2;
	String content;
	HbaseIf hbase;
	
	public UserAction(){
		username=new String();
		password=new String();
		hbase=HbaseIf.getInstance();
		content=new String();
	}
	
	
	
	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public String getPassword2() {
		return password2;
	}



	public void setPassword2(String password2) {
		this.password2 = password2;
	}



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String follow() throws Exception{
		if(hbase.follow(login_name, username)){
			errmsg="关注"+username+"成功";
		}
		else{
			errmsg="关注"+username+"失败";
		}
		
		return SUCCESS;
	}
	
	public String unfollow() throws Exception{
		if(hbase.unfollow(login_name, username)){
			errmsg="取消关注成功";
		}
		else{
			errmsg="取消关注失败";
		}
		
		return SUCCESS;
	}

	public String post()throws Exception{
		if(hbase.post(login_name, content)){
			errmsg="发布成功";
		}
		else{
			errmsg="发布失败";
		}
		
		return SUCCESS;
	}
	
	//所有struts 的action都是返回String的类型
	public String Login() throws Exception{
		long id=	hbase.checkPassword(username, password);
		if(id>0){
			//使用struts2的session功能
			//得到actioncontext
			ActionContext ac=ActionContext.getContext();
			Map session=ac.getSession();
			session.put("login_name", username);
			session.put("login_id", id);
			
			login_name=username;
			login_id=id;
			return SUCCESS;
		}
		errmsg="登入失败";
		return ERROR;
		
	}
	
	public String Logout() throws Exception{
		login_name="";
		login_id=0;
		ActionContext ac=ActionContext.getContext();
		Map session=ac.getSession();
		session.put("login_name", username);
		session.put("login_id", login_id);
		
		return SUCCESS;
	}
	
	public String Register() throws Exception{
		if(username.equals("")){
			return SUCCESS;
		}
		if(username.length()<3|| password.length()<3){
			errmsg="注册失败";
			return SUCCESS;
		}
		if(!password.equals(password2)){
			errmsg="2次输入的密码不一致";
			return SUCCESS;
		}
		
		errmsg="注册成功";
		hbase.createNewUser(username, password);
		return SUCCESS;
	}
	
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}


}
