package org.spring.mr.hadoop1;


import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

//察看每个地势所产生的话单
public class CdrStat {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		String inputPath="hdfs://hadoop.main:9000/input/";//输入的是一个目录，可以有多个文件
		String outputPath="hdfs://hadoop.main:9000/output/";
		
		Job job=new Job();
		job.setJarByClass(CdrStat.class);//设置job运行的class
		job.setJobName("CdrStat");//设置job的name
		FileInputFormat.addInputPath(job, new Path(inputPath));//设置输入的hdfs中的内容
		FileOutputFormat.setOutputPath(job, new Path(outputPath));//设置输出地方
		job.setMapperClass(MapTest.class);
		job.setReducerClass(ReduceTest.class);
		
		//job.setCombinerClass(ReduceTest.class);//设置map在reduce之前，先自己reduce，自己合并的输出作为总的ruduce的输入，自己的合并是针对于节点的
		job.setOutputKeyClass(Text.class);//设置输出的key类
		job.setOutputValueClass(IntWritable.class);//输出value的class
		job.setNumReduceTasks(2);//设置mapreduce 需要几个reduce来执行
		System.exit(job.waitForCompletion(true)?0:1);//等job运行完成，程序就退出
	}
	
	public static class MapTest extends Mapper<LongWritable,Text, Text, IntWritable> {
		
		private final static IntWritable one=new IntWritable(1);
		private final static Text word=new Text();
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String  line=value.toString();
			//对行按照，分割
			String []arr=line.split(",");
			//去的地势的字段
			word.set(arr[1]);
			//输出必须是hadoop的类型
			context.write(word,one);
			
		}
		
		
		
	}
	//reducer 也是四个参数，前2个输入参数是map里面的输出参数
	//后面2个参数可随意，在这里一个是单词，一个是出现的频率
	public static class ReduceTest extends Reducer<Text, IntWritable, Text, IntWritable> {

		//map输出的内容想“hadoop” 1 “hadoop” 1这样的形式，text是hadoop  后面的迭代器是对value的值，其中text只有一个 
		//输出也用context输出
		@Override
		protected void reduce(Text text, Iterable<IntWritable> iterables,Context context)
				throws IOException, InterruptedException {
			int i=0;
			//只要计算出有几个i就可以
			for(IntWritable intWritable :iterables){
				i++;
			}
			context.write(text,new IntWritable(i));
			
			
		}
		
		
	}

}
