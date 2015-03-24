package org.spring.mr.test;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.*;

public class debug{
	
   public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
     private final static IntWritable one = new IntWritable(1);
     private Text word = new Text();

     
     public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
       String line = value.toString();
       String[] arr = line.split(",");
       
       word.set(arr[0]);
       context.write(word, one);
     }
   }

   public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
     public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
    	int i=0;
    	for (IntWritable value : values){
    		i++;
    	}
 		context.write(key, new IntWritable(i));
     }
   }

   public static void main(String[] args) throws Exception {
	   	File file=new File("D:\\hadoop_outfile\\");
	   	if(	file.exists()){
	   		file.delete();
	   	}
	   
	   	Job job = new Job();
		job.setJarByClass(debug.class);
		job.setJobName("CdrStat");
		job.getConfiguration().set("mapred.job.tracker", "local");
		job.getConfiguration().set("fs.default.name", "local");
		FileInputFormat.addInputPath(job, new Path("D:\\hadoop_inputfile\\"));
		FileOutputFormat.setOutputPath(job, new Path("D:\\hadoop_outfile\\"));
		job.setMapperClass(Map.class);
		//job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(2);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
   }
}