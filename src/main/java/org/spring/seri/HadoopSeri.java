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

import org.apache.hadoop.io.Writable;

//hadoop 要实现wtitable 的接口
public class HadoopSeri implements Writable {
	
	public long a,b,c;
	

	public HadoopSeri(long a,long b,long c){
		this.a=a;
		this.b=b;
		this.c=c;
	}

	public HadoopSeri() {
		a=b=c=0l;
	}

	public static void main(String[] args) throws Exception {
		
		HadoopSeri a=new HadoopSeri(1, 2, 3);
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
		HadoopSeri b=new HadoopSeri();
		b.readFields(objin);
		objin.close();
		System.out.println(b.a+","+b.b+","+b.c);
	}

	@Override
	//write 就是将一个object 序列化到一个数组中
	//hadoop 的序列化要自己去操作
	public void write(DataOutput out) throws IOException {
		out.writeLong(a);
		out.writeLong(b);
		out.writeLong(c);
		
		
	}

	@Override
	//就是从一个数据中去反序列化
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		//要和写的顺序对应起来
		this.a=in.readLong();
		this.b=in.readLong();
		this.c=in.readLong();
		
		
	}

}
