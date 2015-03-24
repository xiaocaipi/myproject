package org.spring.mr.shortestpath;

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


//�������ݳ�ʼ���Ĺ���
public class Main {
	
	//ȫ�ֱ�����Ϊ��findShortestPath  ��main ������job ���ݲ���������
	public static final String TARGET_NODE="shortestpath.targetnode";
	
    //������������ļ���������ļ���Ӧ�õ���ʼ�ڵ�
	//������ļ���data.txt  �����input.txt��Lily��
	public static void createInputFile(Path file,Path targetFile,String startNode) throws Exception{
		Configuration conf=new Configuration();
		FileSystem fs=file.getFileSystem(conf);//�õ�filesystem����
		
		//��������ļ�����
		DataOutputStream os=fs.create(targetFile);
		
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
			System.out.println(parts[0]);
			int distance=Integer.MAX_VALUE;
			//����ǿ�ʼ�ڵ�
			if(startNode.equals(parts[0])){
				distance=0;
			}
			//��ioutils ��������ݸ�ʽ������д��input.txt����,Ҫ2��\t  ����nodename �ͳ��Ⱥ�backpoints ����Ҫ2��/t
			IOUtils.write(parts[0]+"\t"+String.valueOf(distance)+"\t\t", os);
			//�����ٽ��
			IOUtils.write(StringUtils.join(parts, "\t",1,parts.length), os);
			//����������
			IOUtils.write("\n", os);
		}
		os.close();
	}
	
	//�����·�������mapreduce�ĵ���
	//���÷���ֵ�������Ƿ���Ҫ��������   true ����Ҫ������
	public static boolean findShortestPath(Path inputPath, Path outputPath,String startNode,String targetNode) throws IOException, InterruptedException, ClassNotFoundException{
		Configuration conf=new Configuration();
		
		//conf �����õĻ������Ĭ�ϵģ�Ҳ�����Լ����ò�����Ϣ��������mapreduce��ʹ�ã���������targetnode
		//conf ��������keyvalue ��ֵ
		//Ҫ����new job ǰ�棬��Ϊjob�����conf ���õ������ķ�ʽ��configuration���п�������������Ӧ������node
		conf.set(TARGET_NODE, targetNode);
		
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
		
		//��mapreduce����֮��ȥ�ж�counter�Ƿ�������,ȥ��reduce���������õ�counter
		Counter counter=job.getCounters().findCounter(Reduce.PathCounter.TARGET_NODE_FOUND);
		//���counter��=null ˵�����ҵ���
		//counter��Ĭ��ֵ��0�����������0�Ļ���˵��reduce ������
		if(counter!=null && counter.getValue()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String startNode =args[0];
		String targetNode=args[1];
		String inputFile=args[2];//������ļ�data.txt
		String outputdir=args[3];//�����Ŀ¼���������ļ�
		Configuration conf=new Configuration();
		//��hdfs��path ��2��һ���Ǵ����ļ���һ���Ǵ���·��
		Path outputPath=new Path(new Path(outputdir),"input.txt");//ָ��һ������
		createInputFile(new Path(inputFile),outputPath,startNode);
		
		//mapreduce�ĵ���
		//findShortestPath(outputPath, new Path("/output1"), startNode, targetNode);
		
		//ѭ������findShortestPath
		//�ȶ���Ŀ¼
		Path jobInput ,jobOutput;
		jobInput=outputPath;//��ʼ��input.txt
		int iter=1;//�������������������Ŀ¼,��ʼ����Ŀ¼��1
		
		while(true){
			jobOutput=new Path(new Path(outputdir),String.valueOf(iter));
			//���� ������true ˵���Ѿ��ҵ������˳�
			if(findShortestPath(jobInput, jobOutput, startNode, targetNode)){
				break;
				
			}
			//�ڶ����Ժ�Ļ�����һ�ε��������Ϊ��һ�ε�����
			//�����ʼ�������Ǹ��ļ����ڶ����Ժ�������Ǹ�Ŀ¼��hadoop��ȥ�Զ���Ŀ¼�µ��ļ�
			jobInput=jobOutput;
			iter++;
		}
		
		
	}

}
