package org.spring.mr.shortestpath2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import redis.clients.jedis.Jedis;

//��map����һ�����·���Ĳ��ң�����ĳһ�����㵽�������ж�������·��
//map��������data��txt
public class Map  extends Mapper<Text, Text, Text, Text>{
	
	Jedis jedis;
	List<String> allNodes;
	
	@Override
	//һ��map�����һ��setup����
	protected void setup(Context context)
			throws IOException, InterruptedException {
		jedis=new Jedis("192.168.1.9");
		allNodes=jedis.lrange("all_nodes", 0, -1);
		
	}
	
	//�Ӽ����и��ݼ��ϵ������ҵ�����ڵ�
	public static Node getNodeByName(String n,List<Node> l){
		for(Node node :l){
			System.out.println("nodeaaa:"+node.name);
			if(n.equals(node.name)){
				return node;
			}
		}
		return null;
	}
	//��l���ҵ�һ������ʼ�ڵ���̵Ľڵ㣬������̽ڵ��l��remove
	public static Node getShortestNode(List<Node> l){
		//2����ʱ����
		Node n=null;
		int d=Integer.MAX_VALUE;
		//2������  1�ҵ���̵Ľڵ㣬2 ���ҵ���̵Ľڵ��list��ɾ����
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
	//�ж�һ���ڵ��Ƿ���һ�������д���
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
	//ÿһ��key�����ͼ�������������ڵ㵽key�ڵ�����·��
	protected void map(Text key, Text value,
			Context context)
			throws IOException, InterruptedException {
		
		//�����õ���˹����α����
		//����t���� ��s����
		List<Node> T =new ArrayList<Node>();
		List<Node> S =new ArrayList<Node>();
		
		//��Ҫ��ʼ��
		for(String n:allNodes){
			T.add(new Node(n, Integer.MAX_VALUE));
		}
		System.out.println("key.tostring:"+key.toString());
		 //����ʼ�ڵ�ĳ��ȸ�Ϊ0,����Ҫ��ÿһ���ڵ㶼����������ڵ�����·��
		
		getNodeByName(key.toString(), T).setDistance(0);
		
		while(T.size()>0){
			//�ҵ���̽ڵ㣬����t��ȥ��
			Node n=getShortestNode(T);
			//�ҵ���̽ڵ�������ٽڵ�
			List<String>adjNodes=jedis.lrange("node_"+n.name, 0, -1);
			//�������ٽ��ľ����1
			int distance=n.getDistance()+1;
			//�ҽڵ��·����¼������ÿ����һ���ۼ�
			String bp;
			//���bpΪ������ʼ�ڵ�
			if(n.getBackpointer()==null){
				bp=n.name;
			}else {
				bp=n.getBackpointer()+":"+n.name;
			}
			//�����ٽڵ�
			for(String adjName:adjNodes){
				//�����㷨 �����������s���������г��ȵļ���
				if(!exist(adjName, S)){
					//��t�����ҵ��ٽڵ�
					Node adjNode=getNodeByName(adjName, T);
					//����ٽ��ĳ��ȴ��ڼ�������ĳ���
					if(distance<adjNode.getDistance()){
						adjNode.setDistance(distance);
						adjNode.setBackpointer(bp);
					}
				}
			}
			S.add(n);
		}
		//s������key�������ڵ�����·���ڵ�
		//��ÿһ������ʼ�ڵ����̽ڵ�������������ʼ�ڵ�+Ŀ�Ľڵ�+��������·��
		//key ����ʼ�ڵ㣬n��Ŀ�Ľڵ�
		for(Node n:S){
			Text t=new Text(key.toString()+":"+n.name+"("+n.getBackpointer()+")");
			context.write(t, new Text(""));
		}
	
	}

}
