package org.spring.mr.shortestpath2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import redis.clients.jedis.Jedis;

//在map中做一个最短路径的查找，查找某一个顶点到其他所有顶点的最短路径
//map的输入是data。txt
public class Map  extends Mapper<Text, Text, Text, Text>{
	
	Jedis jedis;
	List<String> allNodes;
	
	@Override
	//一个map会调用一个setup方法
	protected void setup(Context context)
			throws IOException, InterruptedException {
		jedis=new Jedis("192.168.1.9");
		allNodes=jedis.lrange("all_nodes", 0, -1);
		
	}
	
	//从集合中根据集合的名字找到这个节点
	public static Node getNodeByName(String n,List<Node> l){
		for(Node node :l){
			System.out.println("nodeaaa:"+node.name);
			if(n.equals(node.name)){
				return node;
			}
		}
		return null;
	}
	//从l中找到一个到起始节点最短的节点，并把最短节点从l中remove
	public static Node getShortestNode(List<Node> l){
		//2个临时变量
		Node n=null;
		int d=Integer.MAX_VALUE;
		//2个步骤  1找到最短的节点，2 把找到最短的节点从list中删除掉
		for(int i=0;i<l.size();i++){
			Node node=l.get(i);
			if(node.getDistance()<=d){
				n=node;
				d=n.getDistance();
			}
		}
		for(int i=0;i<l.size();i++){
			Node node=l.get(i);
			if(node.name.equals(n.name)){
				l.remove(node);
			}
		}
		
		return n;
	}
	//判断一个节点是否在一个集合中存在
	public static boolean exist(String name,List<Node> l){
		for(int i=0;i<l.size();i++){
			Node node=l.get(i);
			if(node.name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	//每一个key进来就计算出所有其他节点到key节点的最短路径
	protected void map(Text key, Text value,
			Context context)
			throws IOException, InterruptedException {
		
		//这里用蝶姐斯特拉伪代码
		//生命t集合 和s集合
		List<Node> T =new ArrayList<Node>();
		List<Node> S =new ArrayList<Node>();
		
		//先要初始化
		for(String n:allNodes){
			T.add(new Node(n, Integer.MAX_VALUE));
		}
		System.out.println("key.tostring:"+key.toString());
		 //把起始节点的长度改为0,这里要对每一个节点都求出到其他节点的最短路径
		
		getNodeByName(key.toString(), T).setDistance(0);
		
		while(T.size()>0){
			//找到最短节点，并从t中去除
			Node n=getShortestNode(T);
			//找到最短节点的所有临节点
			List<String>adjNodes=jedis.lrange("node_"+n.name, 0, -1);
			//对所有临界点的距离加1
			int distance=n.getDistance()+1;
			//找节点的路径记录下来，每次做一次累加
			String bp;
			//如果bp为空是起始节点
			if(n.getBackpointer()==null){
				bp=n.name;
			}else {
				bp=n.getBackpointer()+":"+n.name;
			}
			//遍历临节点
			for(String adjName:adjNodes){
				//根据算法 ，如果不属于s集合则会进行长度的计算
				if(!exist(adjName, S)){
					//从t集合找到临节点
					Node adjNode=getNodeByName(adjName, T);
					//如果临界点的长度大于计算出来的长度
					if(distance<adjNode.getDistance()){
						adjNode.setDistance(distance);
						adjNode.setBackpointer(bp);
					}
				}
			}
			S.add(n);
		}
		//s是所有key到其他节点的最短路劲节点
		//对每一个到起始节点的最短节点进行输出，是起始节点+目的节点+所经过的路径
		//key 是起始节点，n是目的节点
		for(Node n:S){
			Text t=new Text(key.toString()+":"+n.name+"("+n.getBackpointer()+")");
			context.write(t, new Text(""));
		}
	
	}

}
