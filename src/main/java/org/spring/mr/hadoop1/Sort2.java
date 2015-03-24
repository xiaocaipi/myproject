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
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

//对主叫号码进行排序
//reduce会对key进行排序的
public class Sort2 {

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
		job.setJarByClass(Sort2.class);//设置job运行的class
		job.setJobName("CdrStat");//设置job的name
		FileInputFormat.addInputPath(job, new Path(inputPath));//设置输入的hdfs中的内容
		FileOutputFormat.setOutputPath(job, new Path(outputPath));//设置输出地方
		job.setMapperClass(MapTest.class);
		job.setReducerClass(ReduceTest.class);
		
		//job.setCombinerClass(ReduceTest.class);//设置map在reduce之前，先自己reduce，自己合并的输出作为总的ruduce的输入，自己的合并是针对于节点的
		job.setOutputKeyClass(Text.class);//设置输出的key类
		job.setOutputValueClass(IntWritable.class);//输出value的class
		job.setNumReduceTasks(2);//设置mapreduce 需要几个reduce来执行
		job.setPartitionerClass(TestPar.class);//设置自定义的partition
		System.exit(job.waitForCompletion(true)?0:1);//等job运行完成，程序就退出
	}
	//自定义一个partition
	//必须继承Partitioner，需要制定一个key和value 这个kv是map的输出
	//这个partition是及偶数的partition
	public static class TestPar extends Partitioner<Text, IntWritable>{

		@Override
		//getPartition 的输入是map的一个keyvalue的输出
//		/getPartition的输出是一个int，表示要放到哪一个reduce任务中间去
		//reduce任务有一个编号，从0开始 setNumReduceTasks(2) 这里设置2个，所以返回0和1
		public int getPartition(Text arg0, IntWritable arg1, int arg2) {
			// TODO Auto-generated method stub
			
			//因为是个手机号码前几位是一样的所以从7位开始，防止溢出
			//因为返回0或1 所以%2
			return Integer.parseInt(arg0.toString().substring(7))%2;
		}
		
	}
	//自定义全局排序的partition
	public static class TestPar1 extends Partitioner<Text, IntWritable>{

		@Override
		//里面的逻辑可以自己写，这里只是用500条来做
		public int getPartition(Text arg0, IntWritable arg1, int arg2) {
			if( Integer.parseInt(arg0.toString().substring(7))<500)
			{
				return 0;
			}	
			else{
				return 1;
			}
			}
		
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
			//取一个主叫号码
			word.set(arr[0]);
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
