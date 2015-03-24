package org.spring.mr.shortestpath2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;
//对于一个节点，当有多个路径的时候找到最短的那一条
public class Reduce extends Reducer<Text, Text, Text, Text> {

	
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		
		context.write(key, new Text(""));
	}
}
