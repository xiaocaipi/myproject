package org.spring.seri;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

//java 要序列化 要实现可序列化接口
//java 序列化的好处就是实现了接口之后不需要任何的编码
public class JavaSeri implements Serializable {
	
	public long a,b,c;
	
	//用java的序列化机制来序列化对象
	public JavaSeri(long a,long b,long c){
		this.a=a;
		this.b=b;
		this.c=c;
	}

	public static void main(String[] args) throws Exception {
		
		JavaSeri a=new JavaSeri(1, 2, 3);
		//将对象序列化到一个文件中去
		FileOutputStream fos=new FileOutputStream("temp.out");
		ObjectOutputStream objout=new ObjectOutputStream(fos);
		//把对象写到文件中去
		objout.writeObject(a);
		objout.close();
	}

}
