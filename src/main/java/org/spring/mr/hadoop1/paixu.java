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


public class paixu {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		String inputPath="hdfs://hadoop.main:9000/input/";//输入的是一个目录，可以有多个文件
		String outputPath="hdfs://hadoop.main:9000/output5/";
		
		Job job=new Job();
		job.setJarByClass(paixu.class);//设置job运行的class
		job.setJobName("wordCount");//设置job的name
		FileInputFormat.addInputPath(job, new Path(inputPath));//设置输入的hdfs中的内容
		FileOutputFormat.setOutputPath(job, new Path(outputPath));//设置输出地方
		job.setMapperClass(MapTest.class);
		job.setReducerClass(ReduceTest.class);
		job.setCombinerClass(ReduceTest.class);//设置map在reduce之前，先自己reduce，自己合并的输出作为总的ruduce的输入，自己的合并是针对于节点的
		job.setOutputKeyClass(IntWritable.class);//设置输出的key类,是reduce的输出
		job.setOutputValueClass(Text.class);//输出value的class
		System.exit(job.waitForCompletion(true)?0:1);//等job运行完成，程序就退出
	}
	//继承mapper基类，实现自己的map类
	//第一个参数是输入键的类型，第二个输入的value类型，第三个输出，第四个是输入value的类型
	//这些数据类型必须用hadoop的
	//输入的是一个split
	public static class MapTest extends Mapper<LongWritable,Text, IntWritable, Text> {
		
		@Override
		//map就把获取的数字转换成int输出
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
				String line=value.toString();
				System.out.println("aa"+line);
				IntWritable data=new IntWritable();
				if(!line.equals(""))
				data.set(Integer.parseInt(line));
				//context是上面定义的输出
				context.write(data, new Text(""));
		}
		
		
		
	}
	//reducer 也是四个参数，前2个输入参数是map里面的输出参数
	//后面2个参数可随意，在这里一个是单词，一个是出现的频率
	//reduce的输入必须对应map的输出，reduce的输出必须对应job的输入
	public static class ReduceTest extends Reducer<IntWritable, Text, IntWritable, Text> {

		
		@Override
		protected void reduce(IntWritable key, Iterable<Text> texts,Context context)
				throws IOException, InterruptedException {
			//reduce 的输入的key 调用是从小到大被调用的，是去掉重复的，要排序的话，重复的是要的，重复的是在迭代器里面，要循环下
			for(Text text:texts){
				context.write(key, text);
			}
		}
		
		
	}

}
