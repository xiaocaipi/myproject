package org.spring.mr.shortestpath2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import redis.clients.jedis.Jedis;


//进行数据初始化的工作
public class Main {
	
	//全局变量作为在findShortestPath  和main 函数中job 传递参数的桥梁
	public static final String TARGET_NODE="shortestpath.targetnode";
	
    //参数有输入的文件
	//输出的文件是data.txt,保存到redis数据库中
	public static void createInputFile(Path file) throws Exception{
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);//得到filesystem对象
		Jedis jedis=new Jedis("192.168.1.9");
		
		
		//LineIterator循环读取一个文件一行一行的迭代器,用的是common。io 下的在hadoop安装目录有
		//要传入输出文件的流，这里用fs.open，第二个读入文件的编码方式
		LineIterator iter=IOUtils.lineIterator(fs.open(file), "UTF8");
		while(iter.hasNext()){
			//只要对data.txt中的行加个长度就可以
			//Integer.MAX_VALUE  来做无穷大
			String line=iter.nextLine();
			//每行使用tab分割,某人按照tab来进行分割,用hadoop common lang 中的
			System.out.println(line);
			String [] parts=StringUtils.split(line);
			
			jedis.lpush("all_nodes", parts[0]);//all_nodes 保存所有节点的名字
			
			String nodeName="node_"+parts[0];
			//把临界点放到nodeName的列表中去
			if(parts.length>1){
				for(int i=1;i<parts.length;i++){
					jedis.lpush(nodeName, parts[i]);
				}
			}
		}
		
	}
	
	//找最短路径，完成mapreduce的调用
	//利用返回值来表明是否需要继续调用   true 不需要继续了
	public static boolean findShortestPath(Path inputPath, Path outputPath) throws IOException, InterruptedException, ClassNotFoundException{
		Configuration conf=new Configuration();
		
		
		Job job=new Job(conf);
		
		job.setJarByClass(Main.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		//设置输入的格式用KeyValueTextInputFormat 这是将文本作为行，讲行中第一个字段作为key来解析，后面的部分作为value   
		//之前用的TextInputFormat 是将行号作为key，后面部分作为value
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		job.waitForCompletion(true);
	
		
		return false;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		

		System.out.println("arg0=" + args[0]);
		String inputFile=args[0];//输入的文件data.txt
		
		System.out.println("inputFile:"+inputFile);
		Configuration conf=new Configuration();
		
		createInputFile(new Path(inputFile));
		findShortestPath(new Path(inputFile), new Path(args[1]));
		
		
	}

}
