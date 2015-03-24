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
		//emit(node-name, node) //输出
		context.write(key, value);
		//如果当前的节点不是无穷大，将所有临节点长度加1并且输出
		if(node.isDistanceSet()){
			int neighborDistance=node.getDistance()+1;
			//把当前节点加到bp后面,因为临节点在计算长度的时候会经过node
			String backpointer=node.constructBackPointer(key.toString());
			//遍历所有的临节点
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
