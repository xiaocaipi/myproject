package org.spring.mr.shortestpath;


import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
//节点，没有当前节点的名字，是不需要的，在mapreduce中key就是当前节点名字
public class Node {

	private String backpointer;
	
	//临界点
	private String [] adjacentNodeNames;
	
	//分隔符
	public static final char fieldSeparator='\t';
	
	private int distance=Integer.MAX_VALUE;

	public String getBackpointer() {
		return backpointer;
	}
	//这里set函数直接返回node对象
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
	
	//backpointer 多个的时候来做处理
	public String constructBackPointer(String name){
		StringBuffer backpointers=new StringBuffer();
		//判断当前的backponter是否为空,如果不为空，则在当前backpointer后面加个：
		if(StringUtils.trimToNull(getBackpointer())!=null){
			backpointers.append(getBackpointer()).append(":");
		}
		//把当前的节点名字加在后面
		backpointers.append(name);
		return backpointers.toString();
	}
	//判断当前的节点是否有临节点
	public boolean containAjacentNodes(){
		
		//当adjacentNodeNames 这个数组不为空就说明有临界点
		return adjacentNodeNames!=null;
	}
	//是否路径被设置过
	public boolean  isDistanceSet(){
		//当不等于max 的时候说明被设置过
		return distance != Integer.MAX_VALUE;
	}
	
	@Override
	//要重载成长度+bp+临界点
	public String toString() {
		//调用的方式，这种调用的方式就是为什么把set 返回为当前引用的原因,首先用了append之后，会返回当前stringbuilder对象的引用。因此可以再后面继续调用append
		StringBuilder sb=new StringBuilder();
		sb.append(distance)
		.append(fieldSeparator)
		.append(backpointer);
		if(containAjacentNodes()){
			sb.append(fieldSeparator);
			//将adjacentNodeNames 数组根据\t来拼成一个字符串，用stringutils来做，第一个参数是数组，第二个是分隔符
			sb.append(StringUtils.join(getAdjacentNodeNames(),fieldSeparator));
		}
		return sb.toString();
	}
	
	//每一个node  都是一个节点，都是input。txt 中的一行，要把一行转换成node ，相当于对象的序列化
	//方法使用的是静态的方法，所有的类都会调用同样的方法，因为会在这个方法里面返回一个新的节点
	//参数是数据文件中的一行Lily	0		Vincent	Steve
	public static Node fromMR(String value){
		String [] parts=StringUtils.splitPreserveAllTokens(value,fieldSeparator);
		Node node= new Node().setDistance(Integer.parseInt(parts[0]))
				//检查是否为空
				.setBackpointer(StringUtils.trimToNull(parts[1]));
		//说明有临界点
		if(parts.length>2){
			//arrays。copyOfRange 将指定数组的指定范围复制到一个新数组。
			node.setAdjacentNodeNames(Arrays.copyOfRange(parts, 2, parts.length));
		}
		return node;
				
	}
	

}
