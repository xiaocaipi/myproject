package org.spring.mr.friend;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import redis.clients.jedis.Jedis;

//ʵ����redis��ȥд����
//Ϊ�˶���reduce�������������Ҫ��outputformat �̳У����Ǵ�fileoutpurformat���̳�
//����д��һ�������������͵���
public class RedisOutputFormat<K, V> extends FileOutputFormat<K, V>  {
	
	//˽�е������̳�hadoop ��RecordWriter
	protected static class RedisRecordWriter<K,V>extends RecordWriter<K, V>{

		//дһ��RedisRecordWriter�Ĺ��캯�� ��jedis ���󴫽���
		//����ΪRedisRecordWriter ��һ����¼����ģ������洴���Ļ���ÿ��д��¼����ȥ���������Դ����洫����
		RedisRecordWriter(Jedis jedis){
			this.jedis=jedis;
		}
		//���ȶ���jedis����
		private Jedis jedis;
		@Override
		//ÿ��ִ���궼��ִ��close ����͹ر�jedis����
		public void close(TaskAttemptContext arg0) throws IOException,
				InterruptedException {
		       jedis.disconnect();
			
		}

		@Override
		//reduce ���������2��һ��������� bob:vincent  1 bob:vincent  1
		//write����Ҳ�Ƕ���reduce�����һ���еĶ���ÿ����һ��
		//ÿ��mapreduce�����¶����е����ݼ���һ�飬������Ҫ��ԭ�е��Ƽ�������ɾ������ΪҪ�õ�jedis��zincrby����ͬ�������ۼӣ�������write��������ɾ��ÿ����һ����ɾ��redis�ļ�¼�������Ͳ����ۼ��ˣ������ڽű�����ÿ��������ͳһɾ��
		public void write(K key, V value) throws IOException,
				InterruptedException {
			//�ж�key�Ƿ�Ϊ�� 2����� һ����null һ���Ƕ���Ϊnull nullwritable ��hadoop�Դ���
			boolean nullkey= key==null ;
			boolean nullvalue= value==null ;
			if(nullkey || nullvalue){
				return ;
			}
			//��������key ����bob:vincent(��bob�Ƽ�vincent����) ������  value ������
			String[] s=key.toString().split(":");
			int score=Integer.parseInt(value.toString());
			String k="ref_"+s[0];
			//���Ƽ��ĺ��Ѵ���sorted set ���� ��sortedset ������Էŷ���
			//��ref_bob�����Ƽ�vincent 
			//����redis ���ݿ� 
			//���ĳ���û���ref�Ѿ�������zincrby��Է������ۼ�
			
			jedis.zincrby(k, score, s[1]);
			
			
		}
		
	}

	@Override
	//     getRecordWriter ��FileOutputFormat����Ҫʵ�ֵ�һ������
	//��ȡrecordwriter ����  ��ȥдÿһ����¼���������ｫÿһ��record д��redis��ȥ
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {
		// ��Ϊ����linux ����ȥ�ܵ������ñ���
		return new RedisRecordWriter(new Jedis("127.0.0.1"));
	}


}
