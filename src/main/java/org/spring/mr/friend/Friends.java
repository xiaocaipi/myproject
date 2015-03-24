package org.spring.mr.friend;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import redis.clients.jedis.Jedis;


public class Friends  extends Configured implements Tool{
	
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>{
		private final static  IntWritable one=new IntWritable(1);
		
		@Override
		//key 和value 是文本文件的一个用户，先是去遍历用户的好友，再去遍历好友的好友，算法参考word
		protected void map(LongWritable key, Text value,
				Context context)
				throws IOException, InterruptedException {
			String line=value.toString();
			//get friends    
			String f_key="fri_"+ line;//用户的好友列表都是以fri 开头的，用户的key
			Jedis jedis=new Jedis("192.168.1.7");
			List<String> my_friends=jedis.lrange(f_key, 0, -1);
			//用一个map来把friend 列表放进去，这样在下面判断好友的好友是否在用户的类表里面，就不需要每次去遍历直接取key就可以
			//最里面的循环要用一个高效的容器，在外面循环把其放入map里面要比在里面循环要好
			HashMap<String, Integer>map_friend=new HashMap<String, Integer>();
			for(String t:my_friends){
				map_friend.put(t, 1);
			}
			
			//得到好友的好友
			for(String t:my_friends){
				String ff_key="fri_"+t;//好友的key
				List<String>f_friends=jedis.lrange(ff_key, 0, -1);
				for(String t1:f_friends){
					//if t1 not exist in line's freind list then count++
					Integer n=map_friend.get(t1);
					//要防止用户朋友的朋友是自己的情况
					if(n==null && !t1.equals(line)){
						//计数是用户和朋友的朋友的计数
						String word=line+":"+t1;
						Text out=new Text(word);//转换成hadoop的text对象
						context.write(out, one);
					}
				}
			}
			
			//因为是一个个用户进来的，所以在这里每一个用户都要去链接一次jedis 在disconnect 效率不高，在正常下应该写成公共文件，或者用单例，但是单例的话要注意互斥的问题
			jedis.disconnect();
			
		}
	}
	//map的输出是用户和朋友的朋友一次累加，用reduce来做汇总
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
		int res=ToolRunner.run(new Friends(), args);
		System.exit(res);
	}
	@Override
	//run方法是tool里面定义的借口
	public int run(String[] args) throws Exception {
		//得到Configuration对象 用confgured中getConf方法来获取，可以把当前hadoop的配置都得到，不用去设置很多了
		Configuration conf=getConf();
		Job job=new Job(conf, "Load Redis");
		//hadoop在启动mapreduce的时候会去创建wordcount对象,设置job运行的class
		job.setJarByClass(Friends.class);
		job.setOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(reduce.class);
		//设置任务输入的类型,这里输入是hdfs中的文本文件
		job.setInputFormatClass(TextInputFormat.class);
		//设置输出类型，这里是自己写的RedisOutputFormat
		job.setOutputFormatClass(RedisOutputFormat.class);
		
		//job.setOutputFormatClass(TextOutputFormat.class);
		
		//设置输入的路径
		FileInputFormat.addInputPath(job,new Path(args[0]));
		//设置输出的路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//正常返回就返回0
		return job.waitForCompletion(true)?0:1;
	}

}
