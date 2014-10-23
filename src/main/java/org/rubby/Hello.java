package org.rubby;

import com.opensymphony.xwork2.ActionSupport;

public class Hello extends ActionSupport {
	
	
	private String str;

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	//默认的会调用execute方法
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		str=new String();
		str="aaaa";
		//返回的是struts里面 对应 struts。xml里面result 的name属性 ，默认的可以省略
		return SUCCESS;
	}


}
