package org.spring.mr.shortestpath;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map  extends Mapper<Text, Text, Text, Text>{
	private Text outkey=new Text();
	private Text outvalue= new Text();
	
	@Override
	protected void map(Text key, Text value,
			Context context)
			throws IOException, InterruptedException {
		Node node=Node.fromMR(value.toString());
		//emit(node-name, node) //���
		context.write(key, value);
		//�����ǰ�Ľڵ㲻������󣬽������ٽڵ㳤�ȼ�1�������
		if(node.isDistanceSet()){
			int neighborDistance=node.getDistance()+1;
			//�ѵ�ǰ�ڵ�ӵ�bp����,��Ϊ�ٽڵ��ڼ��㳤�ȵ�ʱ��ᾭ��node
			String backpointer=node.constructBackPointer(key.toString());
			//�������е��ٽڵ�
			for(int i=0;i<node.getAdjacentNodeNames().length;i++){
				String neighbor=node.getAdjacentNodeNames()[i];
				outkey.set(neighbor);
				Node adjacentNode=new Node()
				.setBackpointer(backpointer)
				.setDistance(neighborDistance);
				
				outvalue.set(adjacentNode.toString());
				System.out.println(adjacentNode.toString());
				context.write(outkey, outvalue);
				
				
			}
		}
	}

}
