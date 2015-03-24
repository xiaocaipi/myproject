package org.spring.mr.shortestpath;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Reducer;
//����һ���ڵ㣬���ж��·����ʱ���ҵ���̵���һ��
public class Reduce extends Reducer<Text, Text, Text, Text> {

	private Text outValues=new Text();
	//���Ŀ�Ľڵ�����
    private String targetNode;
    
    //����һ��enum ��counterʹ��
    public static enum PathCounter{
    	TARGET_NODE_FOUND
    }
    
    //����reduce ��setup, ���ᱻmapreduce�Զ����ã�ÿ��reduce���������һ��
     @Override
    protected void setup(Context context)
    		throws IOException, InterruptedException {
    	
    	 //ͨ��context ���Եõ���configuration�������õ�targetnode
    	 targetNode=context.getConfiguration().get(Main.TARGET_NODE);
    }
	
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Context context)
			throws IOException, InterruptedException {
		int minDistance=Integer.MAX_VALUE;
		Node shortestAdjacentNode=null;
		Node originalNode=null;
		//ͬһ��key node������values����
		for(Text textValue:values){
			Node node=Node.fromMR(textValue.toString());
			if(node.containAjacentNodes()){
				//Ҫ����originalNode ����Ϊ map��2�������1��ֱ��������Ǵ���ڵ�ģ�����һ����forѭ��������ǲ�����ڵ�ģ�����reduce�������Ҳ��Ҫ����ڵ�ģ�����Ҫֱ������ļ�¼
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
		
		//������㵽��targetnode ���������targernode�ĳ��Ȳ��ǳ�ʼ��int.max ��ֵ����Ϊmap���ÿ���ڵ㶼���������   �Ϳ��Խ���ѭ������,�������ѭ��֪���Ѿ��ҵ��ˡ�
		if(targetNode.equals(key.toString())&& minDistance!=Integer.MAX_VALUE){
			//��reduce�д��ݲ�����ȥ������ʹ��mapreduce��counter��ʹ��counter����Ҫ����enum
			//ͨ��context����õ�ǰ��counter,������������Ǹ�enum����countername
			Counter counter=context.getCounter(PathCounter.TARGET_NODE_FOUND);
			counter.increment(minDistance);//�����·�����ý�ȥPathCounter
		}
		
	}
}
