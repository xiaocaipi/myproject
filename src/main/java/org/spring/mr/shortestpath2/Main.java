package org.spring.mr.shortestpath2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import redis.clients.jedis.Jedis;


//�������ݳ�ʼ���Ĺ���
public class Main {
	
	//ȫ�ֱ�����Ϊ��findShortestPath  ��main ������job ���ݲ���������
	public static final String TARGET_NODE="shortestpath.targetnode";
	
    //������������ļ�
	//������ļ���data.txt,���浽redis���ݿ���
	public static void createInputFile(Path file) throws Exception{
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);//�õ�filesystem����
		Jedis jedis=new Jedis("192.168.1.9");
		
		
		//LineIteratorѭ����ȡһ���ļ�һ��һ�еĵ�����,�õ���common��io �µ���hadoop��װĿ¼��
		//Ҫ��������ļ�������������fs.open���ڶ��������ļ��ı��뷽ʽ
		LineIterator iter=IOUtils.lineIterator(fs.open(file), "UTF8");
		while(iter.hasNext()){
			//ֻҪ��data.txt�е��мӸ����ȾͿ���
			//Integer.MAX_VALUE  ���������
			String line=iter.nextLine();
			//ÿ��ʹ��tab�ָ�,ĳ�˰���tab�����зָ�,��hadoop common lang �е�
			System.out.println(line);
			String [] parts=StringUtils.split(line);
			
			jedis.lpush("all_nodes", parts[0]);//all_nodes �������нڵ������
			
			String nodeName="node_"+parts[0];
			//���ٽ��ŵ�nodeName���б���ȥ
			if(parts.length>1){
				for(int i=1;i<parts.length;i++){
					jedis.lpush(nodeName, parts[i]);
				}
			}
		}
		
	}
	
	//�����·�������mapreduce�ĵ���
	//���÷���ֵ�������Ƿ���Ҫ��������   true ����Ҫ������
	public static boolean findShortestPath(Path inputPath, Path outputPath) throws IOException, InterruptedException, ClassNotFoundException{
		Configuration conf=new Configuration();
		
		
		Job job=new Job(conf);
		
		job.setJarByClass(Main.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		//��������ĸ�ʽ��KeyValueTextInputFormat ���ǽ��ı���Ϊ�У������е�һ���ֶ���Ϊkey������������Ĳ�����Ϊvalue   
		//֮ǰ�õ�TextInputFormat �ǽ��к���Ϊkey�����沿����Ϊvalue
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		job.waitForCompletion(true);
	
		
		return false;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		

		System.out.println("arg0=" + args[0]);
		String inputFile=args[0];//������ļ�data.txt
		
		System.out.println("inputFile:"+inputFile);
		Configuration conf=new Configuration();
		
		createInputFile(new Path(inputFile));
		findShortestPath(new Path(inputFile), new Path(args[1]));
		
		
	}

}
