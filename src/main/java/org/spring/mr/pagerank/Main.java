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


//进行数据初始化的工作
public class Main {
	
	//全局变量作为在findShortestPath  和main 函数中job 传递参数的桥梁
	public static final String TARGET_NODE="shortestpath.targetnode";
	
    //参数有输入的文件
	//输出的文件是data.txt,保存到redis数据库中
	public static void createInputFile(Path file) throws Exception{
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);//得到filesystem对象
		Jedis jedis=new Jedis("192.168.1.9");
		
		
		//LineIterator循环读取一个文件一行一行的迭代器,用的是common。io 下的在hadoop安装目录有
		//要传入输出文件的流，这里用fs.open，第二个读入文件的编码方式
		LineIterator iter=IOUtils.lineIterator(fs.open(file), "UTF8");
		while(iter.hasNext()){
			//只要对data.txt中的行加个长度就可以
			//Integer.MAX_VALUE  来做无穷大
			String line=iter.nextLine();
			//每行使用tab分割,某人按照tab来进行分割,用hadoop common lang 中的
			System.out.println(line);
			String [] parts=StringUtils.split(line);
			
			jedis.lpush("all_nodes", parts[0]);//all_nodes 保存所有节点的名字
			
			String nodeName="node_"+parts[0];
			//把临界点放到nodeName的列表中去
			if(parts.length>1){
				for(int i=1;i<parts.length;i++){
					jedis.lpush(nodeName, parts[i]);
				}
			}
		}
		
	}
	
	//找最短路径，完成mapreduce的调用
	//利用返回值来表明是否需要继续调用   true 不需要继续了
	public static boolean findShortestPath(Path inputPath, Path outputPath) throws IOException, InterruptedException, ClassNotFoundException{
		Configuration conf=new Configuration();
		
		
		Job job=new Job(conf);
		
		job.setJarByClass(Main.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		//设置输入的格式用KeyValueTextInputFormat 这是将文本作为行，讲行中第一个字段作为key来解析，后面的部分作为value   
		//之前用的TextInputFormat 是将行号作为key，后面部分作为value
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
		String inputFile=args[0];//输入的文件data.txt
		String outputDir=args[1];//输出的路径
		iterate(inputFile,outputDir);
	}

	private static void iterate(String input, String output) throws Exception {
		Configuration conf=new Configuration();
		Path outputPath=new Path(output);
		//把mapreduce产生的输出目录给删除
		outputPath.getFileSystem(conf).delete(outputPath, true);
		//再去创建输出的目录
		outputPath.getFileSystem(conf).mkdirs(outputPath);
		//在输出目录创建input.txt
		Path inputPath=new Path(outputPath, "input.txt");
		/*
		 * 第一个是主函数中传入的文件也就是图，第二个数新的文件，放在output路径下的，名字叫input.txt
		 */
		int numNodes=createInputFile(new Path(input),inputPath);
		
		//接着就进入循环调用mapreduce的过程
		  int iter = 1;
		  //允许的误差
		  double desiredConvergence = 0.01;
		 
		  while (true) {
			  //设置输出目录，目录是以数字的目录
		      Path jobOutputPath =
		          new Path(outputPath, String.valueOf(iter));

		      System.out.println("======================================");
		      System.out.println("=  Iteration:    " + iter);
		      System.out.println("=  Input path:   " + inputPath);
		      System.out.println("=  Output path:  " + jobOutputPath);
		      System.out.println("======================================");
		      //平均每个pr值的变化小于误差的时候就进行跳出循环	
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
//初始化整个数据,给图中的每一个节点一个默认的pr值
//main函数传进来的数据时  a	b	c
//初始完后放入mapreduce输出目录的input.txt 是 a	0.5		b		c   这数据时作为mapreduce的输入，每次迭代都是这样的
	private static int createInputFile(Path file, Path targetFile) throws Exception {
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);
		//获得整个图的节点数
		int numNOdes=getNumNodes(file);
		//获得开始的pr值，这里用1/节点数 
		double initialPageRank=1.0/ (double)numNOdes;
		//打开输出的文件
		OutputStream os=fs.create(targetFile);
		LineIterator iter=IOUtils.lineIterator(fs.open(file), "UTF8");
		while (iter.hasNext()){
			String line=iter.nextLine();
			String[] parts=StringUtils.split(line);
			//构建node对象
			Node node=new Node().setPageRank(initialPageRank)
					.setAdjacentNodeNames(
							Arrays.copyOfRange(parts, 1, parts.length));
			//重新构建input.txt
			IOUtils.write(parts[0]+'\t'+node.toString()+'\n', os);
		}
		os.close();
		return numNOdes;
	}
//计算文件当前有多少行,文件有多少行就有多少个节点
	private static int getNumNodes(Path file)  throws Exception {
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);
		return IOUtils.readLines(fs.open(file)).size();
		
	}
	//调用mapreduce的函数
	  public static double calcPageRank(Path inputPath, Path outputPath, int numNodes)
		      throws Exception {
		    Configuration conf = new Configuration();
		    conf.setInt(Reduce.CONF_NUM_NODES_GRAPH, numNodes);

		    Job job = new Job(conf);
		    job.setJarByClass(Main.class);
		    job.setMapperClass(Map.class);
		    job.setReducerClass(Reduce.class);
		    //KeyValueTextInputFormat 格式将一行中的第一个作为key，后面的所有作为value
		    job.setInputFormatClass(KeyValueTextInputFormat.class);

		    job.setMapOutputKeyClass(Text.class);
		    job.setMapOutputValueClass(Text.class);

		    FileInputFormat.setInputPaths(job, inputPath);
		    FileOutputFormat.setOutputPath(job, outputPath);

		    if (!job.waitForCompletion(true)) {
		      throw new Exception("Job failed");
		    }
		    //在一次mapreduce调用完之后会去调用这个counter，所有节点pr值变化的总和
		    long summedConvergence = job.getCounters().findCounter(
		        Reduce.Counter.CONV_DELTAS).getValue();
		    //除以1000 再除以节点的个数 算出平均每个pr值的变化
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
