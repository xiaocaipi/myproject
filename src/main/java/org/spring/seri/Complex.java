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
public class Complex implements Writable {
	
	public List<String>l;
	

	public Complex(List<String>l){
		this.l=l;
	}

	public Complex() {
		l=null;
	}

	public static void main(String[] args) throws Exception {
		List<String>l=new ArrayList<String>();
		l.add("123");
		l.add("456");
		Complex a=new Complex(l);
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
		Complex b=new Complex();
		b.readFields(objin);
		objin.close();
		for(String s:b.l){
			System.out.println(s);
		}
	}

	@Override
	//write 就是将一个object 序列化到一个数组中
	//hadoop 的序列化要自己去操作
	public void write(DataOutput out) throws IOException {
		//list 序列化 ,要将list 的长度写出去
				out.writeInt(l.size());
		//然后再遍历依次写
				for(String s:l){
					out.writeBytes(s);
					//写完之后可以加回车换行符,之后以要加是因为在读的时候要一行一行来读
					out.writeBytes("\n");
				}
		
		
	}

	@Override
	//就是从一个数据中去反序列化
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		//反序列化时候得到list的长度
		int n=in.readInt();
		if(l==null){
			l=new ArrayList<String>();
		}
		//根据读出来的长度，依次的去读
		for(int i=0;i<n;i++){
			//这里要用readline 所以在上面序列化的时候才加个换行
			l.add(in.readLine());
		}
		
		
		
	}

}
