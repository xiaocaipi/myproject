package org.spring.mr.shortestpath;

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


//进行数据初始化的工作
public class Main {
	
	//全局变量作为在findShortestPath  和main 函数中job 传递参数的桥梁
	public static final String TARGET_NODE="shortestpath.targetnode";
	
    //参数有输入的文件，输出的文件，应用的起始节点
	//输出的文件是data.txt  输出是input.txt（Lily）
	public static void createInputFile(Path file,Path targetFile,String startNode) throws Exception{
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);//得到filesystem对象
		
		//创建输出文件的流
		DataOutputStream os=fs.create(targetFile);
		
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
			System.out.println(parts[0]);
			int distance=Integer.MAX_VALUE;
			//如果是开始节点
			if(startNode.equals(parts[0])){
				distance=0;
			}
			//用ioutils 将输出内容格式化，在写到input.txt里面,要2个\t  先是nodename 和长度和backpoints 所以要2个/t
			IOUtils.write(parts[0]+"\t"+String.valueOf(distance)+"\t\t", os);
			//再是临界点
			IOUtils.write(StringUtils.join(parts, "\t",1,parts.length), os);
			//最后输出换行
			IOUtils.write("\n", os);
		}
		os.close();
	}
	
	//找最短路径，完成mapreduce的调用
	//利用返回值来表明是否需要继续调用   true 不需要继续了
	public static boolean findShortestPath(Path inputPath, Path outputPath,String startNode,String targetNode) throws IOException, InterruptedException, ClassNotFoundException{
		Configuration conf=new Configuration();
		
		//conf 不设置的话，会读默认的，也可以自己设置参数信息，可以再mapreduce中使用，这里设置targetnode
		//conf 可以设置keyvalue 的值
		//要放在new job 前面，因为job里面的conf 是用到拷贝的方式将configuration进行拷贝，所以首先应该设置node
		conf.set(TARGET_NODE, targetNode);
		
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
		
		//在mapreduce结束之后去判断counter是否被设置了,去找reduce方法中设置的counter
		Counter counter=job.getCounters().findCounter(Reduce.PathCounter.TARGET_NODE_FOUND);
		//如果counter！=null 说明被找到了
		//counter的默认值是0，所以如果》0的话就说明reduce 设置了
		if(counter!=null && counter.getValue()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String startNode =args[0];
		String targetNode=args[1];
		String inputFile=args[2];//输入的文件data.txt
		String outputdir=args[3];//输出的目录，并不是文件
		Configuration conf=new Configuration();
		//在hdfs中path 有2中一种是代表文件，一种是代表路径
		Path outputPath=new Path(new Path(outputdir),"input.txt");//指向一个对象
		createInputFile(new Path(inputFile),outputPath,startNode);
		
		//mapreduce的调用
		//findShortestPath(outputPath, new Path("/output1"), startNode, targetNode);
		
		//循环调用findShortestPath
		//先定义目录
		Path jobInput ,jobOutput;
		jobInput=outputPath;//初始是input.txt
		int iter=1;//定义数据来做输出的子目录,初始的子目录是1
		
		while(true){
			jobOutput=new Path(new Path(outputdir),String.valueOf(iter));
			//调用 返回是true 说明已经找到可以退出
			if(findShortestPath(jobInput, jobOutput, startNode, targetNode)){
				break;
				
			}
			//第二次以后的话把上一次的输出，作为这一次的输入
			//这里初始的输入是个文件，第二次以后的输入是个目录，hadoop会去自动找目录下的文件
			jobInput=jobOutput;
			iter++;
		}
		
		
	}

}
