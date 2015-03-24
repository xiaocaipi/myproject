package org.spring.mr.friend;

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
		//reduce 的输出会有2个一样的情况如 bob:vincent  1 bob:vincent  1
		//write函数也是读的reduce输出的一行行的读，每次是一行
		//每次mapreduce会重新对所有的数据计算一遍，这样需要对原有的推荐数据做删除，因为要用到jedis的zincrby对相同数据做累加，所以在write函数里做删除每次来一条就删除redis的记录，这样就不能累加了，所以在脚本里面每次启动做统一删除
		public void write(K key, V value) throws IOException,
				InterruptedException {
			//判断key是否为空 2中清空 一个是null 一种是对象为null nullwritable 是hadoop自带的
			boolean nullkey= key==null ;
			boolean nullvalue= value==null ;
			if(nullkey || nullvalue){
				return ;
			}
			//传进来的key 是像bob:vincent(给bob推荐vincent好友) 这样的  value 是数字
			String[] s=key.toString().split(":");
			int score=Integer.parseInt(value.toString());
			String k="ref_"+s[0];
			//把推荐的好友存在sorted set 里面 ，sortedset 里面可以放分数
			//给ref_bob里面推荐vincent 
			//存在redis 数据库 
			//如果某个用户的ref已经存在了zincrby会对分数做累加
			
			jedis.zincrby(k, score, s[1]);
			
			
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
