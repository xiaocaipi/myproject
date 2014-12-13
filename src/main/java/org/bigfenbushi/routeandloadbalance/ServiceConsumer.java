package org.bigfenbushi.routeandloadbalance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ServiceConsumer {

	private  List<String> serverList = new ArrayList<String>();
	Map<String, String> map = new HashMap<String, String>();
	
	String zkServerList = "192.168.1.105:2181";//zookeeper 服务器列表
	String SERVICE_PATH ="/configcenter"; //服务节点路径
	ZkClient zkClinet = new ZkClient(zkServerList);
	
	//初始化服务地址信息
	public void init(){
		
		
				
		boolean serviceExists = zkClinet.exists(SERVICE_PATH);
		if(serviceExists){
			serverList = zkClinet.getChildren(SERVICE_PATH); 
		}else{
			throw new RuntimeException("service not exist");
		}
		for(String server :serverList){
			String ip_port=zkClinet.getChildren(SERVICE_PATH+"/"+server).get(0);
			map.put(server, ip_port);
		}
		
		//注册事件监听
		zkClinet.subscribeChildChanges(SERVICE_PATH, new IZkChildListener() {
			
			@Override
			//当服务上线，下线，会触发监听器，去更新服务列表
			public void handleChildChange(String arg0, List<String> arg1)
					throws Exception {
				//监听对应服务的节点，得到服务的列表
				serverList = arg1;
				map = new HashMap<String, String>();
				for(String server :serverList){
					String ip_port=zkClinet.getChildren(SERVICE_PATH+"/"+server).get(0);
					map.put(server, ip_port);
				}
				
			}
		});
		
	}
	
	//消费服务
	public void consume() throws Exception{
		//通过负载均衡算法，找到一台服务器进行调用

		//这里通过round robin 来调用不同的服务，只有一台机器
		//round robin 通过循环依次调用
		for(String serveradress:serverList){
//			System.out.println(serveradress+map.get(serveradress));
			String port = map.get(serveradress).split("_")[1];
			Socket socket = new Socket("127.0.0.1",Integer.parseInt(port));
			 ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			 Object result = input.readObject();
			 System.out.println("serverName:"+serveradress+"server_ip_port:"+map.get(serveradress)+"sever_content:"+result);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		ServiceConsumer consumer = new ServiceConsumer();
		consumer.init();
		for(int i =0;i<10;i++){
			consumer.consume();
		}
		Thread.sleep(1000*60*60*24);
		
	}
	
}
