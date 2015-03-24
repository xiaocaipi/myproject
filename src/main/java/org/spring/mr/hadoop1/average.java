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


public class average {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		String inputPath="hdfs://hadoop.main:9000/input/";//杈撳叆鐨勬槸涓�釜鐩綍锛屽彲浠ユ湁澶氫釜鏂囦欢
		String outputPath="hdfs://hadoop.main:9000/output5/";
		
		Job job=new Job();
		job.setJarByClass(average.class);//璁剧疆job杩愯鐨刢lass
		job.setJobName("wordCount");//璁剧疆job鐨刵ame
		FileInputFormat.addInputPath(job, new Path(inputPath));//璁剧疆杈撳叆鐨刪dfs涓殑鍐呭
		FileOutputFormat.setOutputPath(job, new Path(outputPath));//璁剧疆杈撳嚭鍦版柟
		job.setMapperClass(MapTest.class);
		job.setReducerClass(ReduceTest.class);
		job.setCombinerClass(ReduceTest.class);//璁剧疆map鍦╮educe涔嬪墠锛屽厛鑷繁reduce锛岃嚜宸卞悎骞剁殑杈撳嚭浣滀负鎬荤殑ruduce鐨勮緭鍏ワ紝鑷繁鐨勫悎骞舵槸閽堝浜庤妭鐐圭殑
		job.setOutputKeyClass(Text.class);//璁剧疆杈撳嚭鐨刱ey绫�鏄痳educe鐨勮緭鍑�
		job.setOutputValueClass(IntWritable.class);//杈撳嚭value鐨刢lass
		System.exit(job.waitForCompletion(true)?0:1);//绛塲ob杩愯瀹屾垚锛岀▼搴忓氨閫�嚭
	}
	//缁ф壙mapper鍩虹被锛屽疄鐜拌嚜宸辩殑map绫�
	//绗竴涓弬鏁版槸杈撳叆閿殑绫诲瀷锛岀浜屼釜杈撳叆鐨剉alue绫诲瀷锛岀涓変釜杈撳嚭锛岀鍥涗釜鏄緭鍏alue鐨勭被鍨�
	//杩欎簺鏁版嵁绫诲瀷蹇呴』鐢╤adoop鐨�
	//杈撳叆鐨勬槸涓�釜split
	public static class MapTest extends Mapper<LongWritable,Text, Text, IntWritable> {
		
		@Override
		//map灏辨妸鑾峰彇鐨勬暟瀛楄浆鎹㈡垚int杈撳嚭
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
				String line=value.toString();
				StringTokenizer stringTokenizer=new StringTokenizer(line," ");
				String name=stringTokenizer.nextToken();
				String score=stringTokenizer.nextToken();
				context.write(new Text(name), new IntWritable(Integer.parseInt(score)));
				
		}
		
		
		
	}
	//reducer 涔熸槸鍥涗釜鍙傛暟锛屽墠2涓緭鍏ュ弬鏁版槸map閲岄潰鐨勮緭鍑哄弬鏁�
	//鍚庨潰2涓弬鏁板彲闅忔剰锛屽湪杩欓噷涓�釜鏄崟璇嶏紝涓�釜鏄嚭鐜扮殑棰戠巼
	//reduce鐨勮緭鍏ュ繀椤诲搴攎ap鐨勮緭鍑猴紝reduce鐨勮緭鍑哄繀椤诲搴攋ob鐨勮緭鍏�
	public static class ReduceTest extends Reducer<Text, IntWritable, Text, IntWritable> {

		
		@Override
		protected void reduce(Text key, Iterable<IntWritable> texts,Context context)
				throws IOException, InterruptedException {
			//reduce 鐨勮緭鍏ョ殑key 璋冪敤鏄粠灏忓埌澶ц璋冪敤鐨勶紝鏄幓鎺夐噸澶嶇殑锛岃鎺掑簭鐨勮瘽锛岄噸澶嶇殑鏄鐨勶紝閲嶅鐨勬槸鍦ㄨ凯浠ｅ櫒閲岄潰锛岃寰幆涓�
			int sum=0;
			int count=0;
			for(IntWritable aa:texts){
				sum+=aa.get();
				count++;
			}
			int avg=sum/count;
			context.write(key, new IntWritable(avg));
		}
		
		
	}

}
