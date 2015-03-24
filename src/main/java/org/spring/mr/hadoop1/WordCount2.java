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


public class WordCount2 {

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
		job.setJarByClass(WordCount2.class);//设置job运行的class
		job.setJobName("wordCount");//设置job的name
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
	//继承mapper基类，实现自己的map类
	//第一个参数是输入键的类型，第二个输入的value类型，第三个输出键的类型，第四个是输入value的类型
	//这些数据类型必须用hadoop的
	//输入的是一个split
	public static class MapTest extends Mapper<LongWritable,Text, Text, IntWritable> {
		//split 根据输入的数据类型，如果是text，在默认的情况下，hadoop会将每一行作为数据输入到map函数
		//key 是每一行的num  没一行的值是value  在mapper中已经定义输出，所以在这只需顶堤context，用其进行输出
		//定义变量
		private final static IntWritable one=new IntWritable(1);
		private final static Text word=new Text();
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String  line=value.toString();
			StringTokenizer stringTokenizer=new StringTokenizer(line);
			while(stringTokenizer.hasMoreElements()){
				word.set(stringTokenizer.nextToken());//对text赋值
				context.write(word,one);//用context对象进行输出，表示这行的这个单词出现一次
			}
			
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
