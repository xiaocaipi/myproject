package org.spring.mr.shortestpath;


import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
//�ڵ㣬û�е�ǰ�ڵ�����֣��ǲ���Ҫ�ģ���mapreduce��key���ǵ�ǰ�ڵ�����
public class Node {

	private String backpointer;
	
	//�ٽ��
	private String [] adjacentNodeNames;
	
	//�ָ���
	public static final char fieldSeparator='\t';
	
	private int distance=Integer.MAX_VALUE;

	public String getBackpointer() {
		return backpointer;
	}
	//����set����ֱ�ӷ���node����
	public Node setBackpointer(String backpointer) {
		this.backpointer = backpointer;
		return this;
	}

	public String[] getAdjacentNodeNames() {
		return adjacentNodeNames;
	}

	public Node setAdjacentNodeNames(String[] adjacentNodeNames) {
		this.adjacentNodeNames = adjacentNodeNames;
		return this;
	}

	public int getDistance() {
		return distance;
	}

	public Node setDistance(int distance) {
		this.distance = distance;
		return this;
	}
	
	//backpointer �����ʱ����������
	public String constructBackPointer(String name){
		StringBuffer backpointers=new StringBuffer();
		//�жϵ�ǰ��backponter�Ƿ�Ϊ��,�����Ϊ�գ����ڵ�ǰbackpointer����Ӹ���
		if(StringUtils.trimToNull(getBackpointer())!=null){
			backpointers.append(getBackpointer()).append(":");
		}
		//�ѵ�ǰ�Ľڵ����ּ��ں���
		backpointers.append(name);
		return backpointers.toString();
	}
	//�жϵ�ǰ�Ľڵ��Ƿ����ٽڵ�
	public boolean containAjacentNodes(){
		
		//��adjacentNodeNames ������鲻Ϊ�վ�˵�����ٽ��
		return adjacentNodeNames!=null;
	}
	//�Ƿ�·�������ù�
	public boolean  isDistanceSet(){
		//��������max ��ʱ��˵�������ù�
		return distance != Integer.MAX_VALUE;
	}
	
	@Override
	//Ҫ���سɳ���+bp+�ٽ��
	public String toString() {
		//���õķ�ʽ�����ֵ��õķ�ʽ����Ϊʲô��set ����Ϊ��ǰ���õ�ԭ��,��������append֮�󣬻᷵�ص�ǰstringbuilder��������á���˿����ٺ����������append
		StringBuilder sb=new StringBuilder();
		sb.append(distance)
		.append(fieldSeparator)
		.append(backpointer);
		if(containAjacentNodes()){
			sb.append(fieldSeparator);
			//��adjacentNodeNames �������\t��ƴ��һ���ַ�������stringutils��������һ�����������飬�ڶ����Ƿָ���
			sb.append(StringUtils.join(getAdjacentNodeNames(),fieldSeparator));
		}
		return sb.toString();
	}
	
	//ÿһ��node  ����һ���ڵ㣬����input��txt �е�һ�У�Ҫ��һ��ת����node ���൱�ڶ�������л�
	//����ʹ�õ��Ǿ�̬�ķ��������е��඼�����ͬ���ķ�������Ϊ��������������淵��һ���µĽڵ�
	//�����������ļ��е�һ��Lily	0		Vincent	Steve
	public static Node fromMR(String value){
		String [] parts=StringUtils.splitPreserveAllTokens(value,fieldSeparator);
		Node node= new Node().setDistance(Integer.parseInt(parts[0]))
				//����Ƿ�Ϊ��
				.setBackpointer(StringUtils.trimToNull(parts[1]));
		//˵�����ٽ��
		if(parts.length>2){
			//arrays��copyOfRange ��ָ�������ָ����Χ���Ƶ�һ�������顣
			node.setAdjacentNodeNames(Arrays.copyOfRange(parts, 2, parts.length));
		}
		return node;
				
	}
	

}
