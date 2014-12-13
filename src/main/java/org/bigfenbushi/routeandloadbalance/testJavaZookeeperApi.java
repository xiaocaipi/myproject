package org.bigfenbushi.routeandloadbalance;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;

public class testJavaZookeeperApi {
	
	public static void main(String[] args) {
		String zkServerList = "192.168.1.105:2181";
		ZkClient zkclient = new ZkClient(zkServerList);
		String PATH ="/test";
		boolean testExist = zkclient.exists(PATH);
		if(!testExist){
			//创建
			zkclient.createPersistent(PATH);
		}
		printznode(zkclient);
		
		//删除
		zkclient.delete(PATH);
		System.out.println("--------------------");
		printznode(zkclient);

	}

	private static void printznode(ZkClient zkclient) {
		List<String> childs =zkclient.getChildren("/");
		for(String child :childs){
			System.out.println(child);
		}
	}
	
	

}
