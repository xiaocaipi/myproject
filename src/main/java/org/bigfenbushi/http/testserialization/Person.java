package org.bigfenbushi.http.testserialization;

import java.util.Date;

public class Person  implements java.io.Serializable {

	/**
	 * 
	 */
	//对于java的序列化方式来说，序列化的时候会把序列化id保存到序列化文件里面，
	//在反序列化的时候，拿序列化id和现有的对象做对比，如果序列化id一致的话，才会认为是一个对象
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private int age;
	
	private String address;
	
	private Date birth;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	
	
	
	

	
}
