package org.rubby;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport {
	
	String errmsg;
	String login_name;
	long login_id;
	
	

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}


	public long getLogin_id() {
		return login_id;
	}

	public void setLogin_id(long login_id) {
		this.login_id = login_id;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
	
	public BaseAction(){
		try {
			errmsg=new String ();
			ActionContext actionContext=ActionContext.getContext();
			Map session=actionContext.getSession();
			login_name=(String)session.get("login_name");
			login_id=(Long)session.get("login_id");
		} catch (Exception e) {
			login_name="";
			login_id=0;
			e.printStackTrace();
		}
		
	}

}
