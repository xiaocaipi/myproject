package org.spring.mr.shortestpath;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;
//对于一个节点，当有多个路径的时候找到最短的那一条
public class Reduce extends Reducer<Text, Text, Text, Text> {

	private Text outValues=new Text();
	//存放目的节点名字
    private String targetNode;
    
    //定义一个enum 给counter使用
    public static enum PathCounter{
    	TARGET_NODE_FOUND
    }
    
    //重载reduce 的setup, 他会被mapreduce自动调用，每个reduce方法会调用一次
     @Override
    protected void setup(Context context)
    		throws IOException, InterruptedException {
    	
    	 //通过context 可以得到在configuration里面设置的targetnode
    	 targetNode=context.getConfiguration().get(Main.TARGET_NODE);
    }
	
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		int minDistance=Integer.MAX_VALUE;
		Node shortestAdjacentNode=null;
		Node originalNode=null;
		//同一个key node都会在values里面
		for(Text textValue:values){
			Node node=Node.fromMR(textValue.toString());
			if(node.containAjacentNodes()){
				//要生命originalNode 是因为 map有2种输出，1是直接输出，是带令节点的，还有一种在for循环输出，是不带令节点的，最后的reduce方法输出也是要带令节点的，所以要直接输出的记录
				originalNode=node;
			}
			if(node.getDistance()<minDistance){
				minDistance=node.getDistance();
				shortestAdjacentNode=node;
			}
		}
		if(shortestAdjacentNode!=null){
			originalNode.setDistance(minDistance);
			originalNode.setBackpointer(shortestAdjacentNode.getBackpointer());
		}
		outValues.set(originalNode.toString());
		context.write(key, outValues);
		
		//如果计算到了targetnode ，并且这个targernode的长度不是初始化int.max 的值（因为map会对每个节点都进行输出）   就可以结束循环调用,让外面的循环知道已经找到了。
		if(targetNode.equals(key.toString())&& minDistance!=Integer.MAX_VALUE){
			//从reduce中传递参数出去，这里使用mapreduce的counter，使用counter必须要定义enum
			//通过context来获得当前的counter,这里参数类型是个enum，是countername
			Counter counter=context.getCounter(PathCounter.TARGET_NODE_FOUND);
			counter.increment(minDistance);//把最短路径设置进去PathCounter
		}
		
	}
}
