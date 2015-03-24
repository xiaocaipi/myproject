package org.spring.mr.count;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import redis.clients.jedis.Jedis;

//实现往redis中去写数据
//为了定制reduce的输出，我们需要从outputformat 继承，我们从fileoutpurformat来继承
//将类写成一个不带数据类型的类
public class RedisOutputFormat<K, V> extends FileOutputFormat<K, V>  {
	
	//私有的类来继承hadoop 的RecordWriter
	protected static class RedisRecordWriter<K,V>extends RecordWriter<K, V>{

		//写一个RedisRecordWriter的构造函数 把jedis 对象传进来
		//，因为RedisRecordWriter 是一个记录级别的，在里面创建的话，每次写记录都会去创建，所以从外面传进来
		RedisRecordWriter(Jedis jedis){
			this.jedis=jedis;
		}
		//首先定义jedis链接
		private Jedis jedis;
		@Override
		//每次执行完都会执行close 里面就关闭jedis链接
		public void close(TaskAttemptContext arg0) throws IOException,
				InterruptedException {
		       jedis.disconnect();
			
		}

		@Override
		//去写reduce任务的,k和v就是reduce任务的输出
		public void write(K key, V value) throws IOException,
				InterruptedException {
			//判断key是否为空 2中清空 一个是null 一种是对象为null nullwritable 是hadoop自带的
			boolean nullkey= key==null ;
			boolean nullvalue= value==null ;
			if(nullkey || nullvalue){
				return ;
			}
			String s=key.toString();
			//存在redis 数据库 
			for(int i=0;i<s.length();i++){
				String k=s.substring(0,i+1);
				int score=Integer.parseInt(value.toString());
				//对k集合中的s对象增加score分数，如s在k中不存在，redis会去创建一个并给与分数
				jedis.zincrby(k, score, s);
				
			}
			
			
		}
		
	}

	@Override
	//     getRecordWriter 是FileOutputFormat必须要实现的一个方法
	//获取recordwriter 对象  ，去写每一条记录，我们这里将每一个record 写到redis里去
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {
		// 因为放在linux 里面去跑的所以用本地
		return new RedisRecordWriter(new Jedis("127.0.0.1"));
	}


}
