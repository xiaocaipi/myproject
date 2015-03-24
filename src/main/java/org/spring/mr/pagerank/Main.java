package org.spring.mr.pagerank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;


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
		String inputFile=args[0];//������ļ�data.txt
		String outputDir=args[1];//�����·��
		iterate(inputFile,outputDir);
	}

	private static void iterate(String input, String output) throws Exception {
		Configuration conf=new Configuration();
		Path outputPath=new Path(output);
		//��mapreduce���������Ŀ¼��ɾ��
		outputPath.getFileSystem(conf).delete(outputPath, true);
		//��ȥ���������Ŀ¼
		outputPath.getFileSystem(conf).mkdirs(outputPath);
		//�����Ŀ¼����input.txt
		Path inputPath=new Path(outputPath, "input.txt");
		/*
		 * ��һ�����������д�����ļ�Ҳ����ͼ���ڶ������µ��ļ�������output·���µģ����ֽ�input.txt
		 */
		int numNodes=createInputFile(new Path(input),inputPath);
		
		//���žͽ���ѭ������mapreduce�Ĺ���
		  int iter = 1;
		  //��������
		  double desiredConvergence = 0.01;
		 
		  while (true) {
			  //�������Ŀ¼��Ŀ¼�������ֵ�Ŀ¼
		      Path jobOutputPath =
		          new Path(outputPath, String.valueOf(iter));

		      System.out.println("======================================");
		      System.out.println("=  Iteration:    " + iter);
		      System.out.println("=  Input path:   " + inputPath);
		      System.out.println("=  Output path:  " + jobOutputPath);
		      System.out.println("======================================");
		      //ƽ��ÿ��prֵ�ı仯С������ʱ��ͽ�������ѭ��	
		      if (calcPageRank(inputPath, jobOutputPath, numNodes) <
		          desiredConvergence) {
		        System.out.println(
		            "Convergence is below " + desiredConvergence +
		                ", we're done");
		        break;
		      }
		      inputPath = jobOutputPath;
		      iter++;
		    }
		
	}
//��ʼ����������,��ͼ�е�ÿһ���ڵ�һ��Ĭ�ϵ�prֵ
//main����������������ʱ  a	b	c
//��ʼ������mapreduce���Ŀ¼��input.txt �� a	0.5		b		c   ������ʱ��Ϊmapreduce�����룬ÿ�ε�������������
	private static int createInputFile(Path file, Path targetFile) throws Exception {
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);
		//�������ͼ�Ľڵ���
		int numNOdes=getNumNodes(file);
		//��ÿ�ʼ��prֵ��������1/�ڵ��� 
		double initialPageRank=1.0/ (double)numNOdes;
		//��������ļ�
		OutputStream os=fs.create(targetFile);
		LineIterator iter=IOUtils.lineIterator(fs.open(file), "UTF8");
		while (iter.hasNext()){
			String line=iter.nextLine();
			String[] parts=StringUtils.split(line);
			//����node����
			Node node=new Node().setPageRank(initialPageRank)
					.setAdjacentNodeNames(
							Arrays.copyOfRange(parts, 1, parts.length));
			//���¹���input.txt
			IOUtils.write(parts[0]+'\t'+node.toString()+'\n', os);
		}
		os.close();
		return numNOdes;
	}
//�����ļ���ǰ�ж�����,�ļ��ж����о��ж��ٸ��ڵ�
	private static int getNumNodes(Path file)  throws Exception {
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);
		return IOUtils.readLines(fs.open(file)).size();
		
	}
	//����mapreduce�ĺ���
	  public static double calcPageRank(Path inputPath, Path outputPath, int numNodes)
		      throws Exception {
		    Configuration conf = new Configuration();
		    conf.setInt(Reduce.CONF_NUM_NODES_GRAPH, numNodes);

		    Job job = new Job(conf);
		    job.setJarByClass(Main.class);
		    job.setMapperClass(Map.class);
		    job.setReducerClass(Reduce.class);
		    //KeyValueTextInputFormat ��ʽ��һ���еĵ�һ����Ϊkey�������������Ϊvalue
		    job.setInputFormatClass(KeyValueTextInputFormat.class);

		    job.setMapOutputKeyClass(Text.class);
		    job.setMapOutputValueClass(Text.class);

		    FileInputFormat.setInputPaths(job, inputPath);
		    FileOutputFormat.setOutputPath(job, outputPath);

		    if (!job.waitForCompletion(true)) {
		      throw new Exception("Job failed");
		    }
		    //��һ��mapreduce������֮���ȥ�������counter�����нڵ�prֵ�仯���ܺ�
		    long summedConvergence = job.getCounters().findCounter(
		        Reduce.Counter.CONV_DELTAS).getValue();
		    //����1000 �ٳ��Խڵ�ĸ��� ���ƽ��ÿ��prֵ�ı仯
		    double convergence =
		        ((double) summedConvergence /
		            Reduce.CONVERGENCE_SCALING_FACTOR) /
		            (double) numNodes;

		    System.out.println("======================================");
		    System.out.println("=  Num nodes:           " + numNodes);
		    System.out.println("=  Summed convergence:  " + summedConvergence);
		    System.out.println("=  Convergence:         " + convergence);
		    System.out.println("======================================");

		    return convergence;
		  }

}
