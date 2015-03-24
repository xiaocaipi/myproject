package org.spring.mr.hadoop1;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;



public class UploadFile {

	
	public static void main(String[] args) {
		///home/spring/j2ee/tomcat/webapps/Upload/upload/aaa.doc    hdfs://hadoop.main:9000/aaa.doc
		String localStr="/home/spring/RUNNING.txt";//文件的原始路径
		String dstr="hdfs://hadoop.main:9000/aa212121a.doc";//要保存到hadoop的文件路径名
		try {
			InputStream inputStream=new BufferedInputStream(new FileInputStream(localStr) );//制造一个输入流
			Configuration conf=new Configuration();
			FileSystem fs=FileSystem.get(URI.create(dstr),conf);
			OutputStream outputStream=fs.create(new Path(dstr));//从一个路径得到一个outputstream
			IOUtils.copyBytes(inputStream, outputStream, 4096,true);//4096代表的是4k，ture表示是否要关闭
			System.out.println("success");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
