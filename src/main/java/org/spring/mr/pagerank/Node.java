package org.spring.mr.pagerank;


import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
//����ڵ���Ϣ
public class Node {

	private double pageRank=0.25;//���峣��prֵ
	
	//�ٽ��
	private String [] adjacentNodeNames;//������ǰnode ���ٽ�㣬ָ���ⲿ�ڵ������
	
	//�ָ���
	public static final char fieldSeparator='\t';
	public String name;//�ڵ������
	
	public Node(){
		
	}

	public String[] getAdjacentNodeNames() {
		return adjacentNodeNames;
	}

	public Node setAdjacentNodeNames(String[] adjacentNodeNames) {
		this.adjacentNodeNames = adjacentNodeNames;
		return this;
	}



	public double getPageRank() {
		return pageRank;
	}


	public Node setPageRank(double pageRank) {
		this.pageRank = pageRank;
		return this;
	}



	//�ж��Ƿ�����ڵ�
	public boolean containAjacentNodes(){
		
		return adjacentNodeNames!=null;
	}
	
	@Override
	//tostring ��node���� �ַ���
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(pageRank);
		if(getAdjacentNodeNames()!=null){
			sb.append(fieldSeparator);
			//��adjacentNodeNames �������\t��ƴ��һ���ַ�������stringutils��������һ�����������飬�ڶ����Ƿָ���
			sb.append(StringUtils.join(getAdjacentNodeNames(),fieldSeparator));
		}
		return sb.toString();
	}
	
	//from ���ַ�תת��Ϊnode
	public static Node fromMR(String value) throws IOException{
		//���Ƚ��зָ�
		String [] parts=StringUtils.splitPreserveAllTokens(value,fieldSeparator);
		//һ���ж�
		if(parts.length<1){
			throw new IOException("expected 1 or more parts by received "+ parts.length);
		}
		//map�����ĵ�һ�������ǽڵ��prֵ������������ڵ���ٽڵ�
		Node node=new Node().setPageRank(Double.valueOf(parts[0]));
		//˵�����ٽ��
		if(parts.length>1){
			//arrays��copyOfRange ��ָ�������ָ����Χ���Ƶ�һ�������顣
			node.setAdjacentNodeNames(Arrays.copyOfRange(parts, 1, parts.length));
		
		}
		return node;
				
	}


	

}
