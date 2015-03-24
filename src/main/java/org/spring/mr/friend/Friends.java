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
		//key ��value ���ı��ļ���һ���û�������ȥ�����û��ĺ��ѣ���ȥ�������ѵĺ��ѣ��㷨�ο�word
		protected void map(LongWritable key, Text value,
				Context context)
				throws IOException, InterruptedException {
			String line=value.toString();
			//get friends    
			String f_key="fri_"+ line;//�û��ĺ����б�����fri ��ͷ�ģ��û���key
			Jedis jedis=new Jedis("192.168.1.7");
			List<String> my_friends=jedis.lrange(f_key, 0, -1);
			//��һ��map����friend �б�Ž�ȥ�������������жϺ��ѵĺ����Ƿ����û���������棬�Ͳ���Ҫÿ��ȥ����ֱ��ȡkey�Ϳ���
			//�������ѭ��Ҫ��һ����Ч��������������ѭ���������map����Ҫ��������ѭ��Ҫ��
			HashMap<String, Integer>map_friend=new HashMap<String, Integer>();
			for(String t:my_friends){
				map_friend.put(t, 1);
			}
			
			//�õ����ѵĺ���
			for(String t:my_friends){
				String ff_key="fri_"+t;//���ѵ�key
				List<String>f_friends=jedis.lrange(ff_key, 0, -1);
				for(String t1:f_friends){
					//if t1 not exist in line's freind list then count++
					Integer n=map_friend.get(t1);
					//Ҫ��ֹ�û����ѵ��������Լ������
					if(n==null && !t1.equals(line)){
						//�������û������ѵ����ѵļ���
						String word=line+":"+t1;
						Text out=new Text(word);//ת����hadoop��text����
						context.write(out, one);
					}
				}
			}
			
			//��Ϊ��һ�����û������ģ�����������ÿһ���û���Ҫȥ����һ��jedis ��disconnect Ч�ʲ��ߣ���������Ӧ��д�ɹ����ļ��������õ��������ǵ����Ļ�Ҫע�⻥�������
			jedis.disconnect();
			
		}
	}
	//map��������û������ѵ�����һ���ۼӣ���reduce��������
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
		//��ToolRunner��ִ��run����������tool ���ﴫ��wordcount��Ϊ������tool �����ǲ���
		int res=ToolRunner.run(new Friends(), args);
		System.exit(res);
	}
	@Override
	//run������tool���涨��Ľ��
	public int run(String[] args) throws Exception {
		//�õ�Configuration���� ��confgured��getConf��������ȡ�����԰ѵ�ǰhadoop�����ö��õ�������ȥ���úܶ���
		Configuration conf=getConf();
		Job job=new Job(conf, "Load Redis");
		//hadoop������mapreduce��ʱ���ȥ����wordcount����,����job���е�class
		job.setJarByClass(Friends.class);
		job.setOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(reduce.class);
		//�����������������,����������hdfs�е��ı��ļ�
		job.setInputFormatClass(TextInputFormat.class);
		//����������ͣ��������Լ�д��RedisOutputFormat
		job.setOutputFormatClass(RedisOutputFormat.class);
		
		//job.setOutputFormatClass(TextOutputFormat.class);
		
		//���������·��
		FileInputFormat.addInputPath(job,new Path(args[0]));
		//���������·��
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//�������ؾͷ���0
		return job.waitForCompletion(true)?0:1;
	}

}
