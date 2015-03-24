package org.spring.mr.pagerank;


import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
//保存节点信息
public class Node {

	private double pageRank=0.25;//定义常量pr值
	
	//临界点
	private String [] adjacentNodeNames;//表明当前node 的临界点，指向外部节点的名字
	
	//分隔符
	public static final char fieldSeparator='\t';
	public String name;//节点的名字
	
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



	//判断是否有令节点
	public boolean containAjacentNodes(){
		
		return adjacentNodeNames!=null;
	}
	
	@Override
	//tostring 从node对象到 字符串
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(pageRank);
		if(getAdjacentNodeNames()!=null){
			sb.append(fieldSeparator);
			//将adjacentNodeNames 数组根据\t来拼成一个字符串，用stringutils来做，第一个参数是数组，第二个是分隔符
			sb.append(StringUtils.join(getAdjacentNodeNames(),fieldSeparator));
		}
		return sb.toString();
	}
	
	//from 从字符转转换为node
	public static Node fromMR(String value) throws IOException{
		//首先进行分割
		String [] parts=StringUtils.splitPreserveAllTokens(value,fieldSeparator);
		//一场判断
		if(parts.length<1){
			throw new IOException("expected 1 or more parts by received "+ parts.length);
		}
		//map函数的第一个输入是节点的pr值，后面是这个节点的临节点
		Node node=new Node().setPageRank(Double.valueOf(parts[0]));
		//说明有临界点
		if(parts.length>1){
			//arrays。copyOfRange 将指定数组的指定范围复制到一个新数组。
			node.setAdjacentNodeNames(Arrays.copyOfRange(parts, 1, parts.length));
		
		}
		return node;
				
	}


	

}
