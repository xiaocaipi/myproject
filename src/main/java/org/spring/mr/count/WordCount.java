package org.spring.mr.count;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

//统计单词出现的频率使用 
//继承Configured 和tool用于方便发起mapreduce任务
public class WordCount  extends Configured implements Tool{
	
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>{
		private final static  IntWritable one=new IntWritable(1);
		
		@Override
		protected void map(LongWritable key, Text value,
				Context context)
				throws IOException, InterruptedException {
			//输入行的内容是这样的
			//192.168.1.10 - - [11/Apr/2013:22:10:52 +0800] "GET /hadoop2/suggestion/ajax/sug.do?query=123 HTTP/1.1" 200 29
			String line=value.toString();
			//提取 后面的参数的值 query=123
			//先分割双引号
			String a[]=line.split("\"");
			//在accesslog里面会产生点击查询的请求，还会有ajax 请求，所以要排除ajax请求 ，查询的请求有sug.jsp 字符串
			if(a[1].indexOf("sug.jsp?query")>0){
				//在split 函数中有| 对前后2个都进行分割
				String b[]=a[1].split("query=| ");
				//如果查询的是中文，accesslog记录的是转码的，需要对其decode成中文
				//URLEncoder.encode("卡卡", "utf-8") 对卡卡转成utf-8码
				String tmp=URLDecoder.decode(b[2], "utf-8");
				Text wor=new Text(tmp);
				context.write(wor, one);
			}
			
		}
	}
	public static class reduce extends Reducer<Text, IntWritable,Text, IntWritable>{
		
	
		protected void reduce(Text key, Iterator<IntWritable> value,
				Context context)
				throws IOException, InterruptedException {
			int sum=0;
			while(value.hasNext()){
				sum+=value.next().get();
			}
			context.write(key, new IntWritable(sum));
		}
		
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//用ToolRunner来执行run方法，传入tool 这里传入wordcount因为集成了tool 后面是参数
		int res=ToolRunner.run(new WordCount(), args);
		System.exit(res);
	}
	@Override
	//run方法是tool里面定义的借口
	public int run(String[] args) throws Exception {
		//得到Configuration对象 用confgured中getConf方法来获取，可以把当前hadoop的配置都得到，不用去设置很多了
		Configuration conf=getConf();
		Job job=new Job(conf, "Load Redis");
		//hadoop在启动mapreduce的时候会去创建wordcount对象,设置job运行的class
		job.setJarByClass(WordCount.class);
		job.setOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(reduce.class);
		//设置任务输入的类型,这里输入是hdfs中的文本文件
		job.setInputFormatClass(TextInputFormat.class);
		//设置输出类型，这里是自己写的RedisOutputFormat
		job.setOutputFormatClass(RedisOutputFormat.class);
		//设置输入的路径
		FileInputFormat.addInputPath(job,new Path(args[0]));
		//设置输出的路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//正常返回就返回0
		return job.waitForCompletion(true)?0:1;
	}

}
