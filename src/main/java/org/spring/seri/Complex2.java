package org.spring.seri;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Writable;

//hadoop 要实现wtitable 的接口
public class Complex2 implements Writable {
	
	public long a;
	public Complex b;
	

	public Complex2(long a,Complex b){
		this.a=a;
		this.b=b;
	}

	public Complex2() {
		a=0l;
		b=null;
				
	}

	public static void main(String[] args) throws Exception {
		List<String>l=new ArrayList<String>();
		l.add("123");
		l.add("456");
		Complex a1=new Complex(l);
		Complex2 a=new Complex2(1l,a1);
		//将对象序列化到一个文件中去
		FileOutputStream fos=new FileOutputStream("temp.out");
		//这里就不能用ObjectOutputStream 
		DataOutputStream objout=new DataOutputStream(fos);
		//这个是序列化的过程
		a.write(objout);
		objout.close();
		
		//反序列化
		FileInputStream fin=new FileInputStream("temp.out");
		DataInputStream objin=new DataInputStream(fin);
		Complex2 b=new Complex2();
		b.readFields(objin);
		objin.close();
		System.out.println(b.a);
		for(String s:b.b.l){
			System.out.println(s);
		}
	}

	@Override
	//write 就是将一个object 序列化到一个数组中
	//hadoop 的序列化要自己去操作
	public void write(DataOutput out) throws IOException {
		//包含其他对象的序列化，首先把a序列化出去
		out.writeLong(a);
		//然后实现b b本来就实现了hadoop wirtable 接口 可以直接write
		b.write(out);
		
		
		
	}

	@Override
	//就是从一个数据中去反序列化
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		a=in.readLong();
		if(b==null){
			b=new Complex();
		}
		//反序列化话a之后  b是可以直接反序列化
		b.readFields(in);
		
		
		
	}

}
