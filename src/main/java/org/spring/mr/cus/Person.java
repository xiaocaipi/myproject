package org.spring.mr.cus;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
//自定义的hadoop数据类型，实现了WritableComparable 接口
public class Person implements WritableComparable<Person>{
	String name;
	int age;
	
	public Person(String name, int age){
		this.name = name;
		this.age = age;
	}
	//不带参数的是必须要写的，
	public Person(){
		name = "";
		age = 0;
	}
	
	@Override
	//writable 定义的方法，做反序列化
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		name = in.readLine();
		age = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeBytes(name);
		out.writeBytes("\n");
		out.writeInt(age);
	}

	@Override
	//这个是compare 的compareto 方法，hadoop的比较还有一个是comparewriter 是独立于对象的
	public int compareTo(Person o) {
		// TODO Auto-generated method stub
		//首先比较名字，名字相同再比较age
		int cmp = this.name.compareTo(o.name);
		if(cmp != 0)
			return cmp;
		
		return (this.age<o.age ? -1 : (this.age == o.age ? 0 : 1)); 
	}

	@Override
	//tostring 在mr的框架里会用到，用textoutputformat 会调用tostring 来做reduce的输出
	public String toString() {
		return name + "," + Integer.toString(age);
	}
	//equal是 就是标准的判断2个对象是否相等，首先判断是否是当前类的实例，在判断类的成员是否相等
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Person))
			return false;
		
		Person p = (Person)o;
		return this.name.equals(p.name) && this.age==p.age;
	}
	
	@Override
	//在map 和reduce 之间 reduce在做输入的时候会根据对象的哈希值做一个partition，默认的hashcode的partition，会调用对象的hashcode，将同样的hashcode放到一个reduce 做处理
	public int hashCode(){
		return this.name.hashCode() + age;
	}
}
