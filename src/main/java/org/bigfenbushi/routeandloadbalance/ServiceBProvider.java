package org.bigfenbushi.routeandloadbalance;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ServiceBProvider {
	
	private String serviceName = "service-B";
	private int port = 1235;

	//想zookeeper 注册服务
	public void init() throws Exception{

		
		String zkServerList = "192.168.1.105:2181";
		String PATH ="/configcenter"; //服务节点路径
		ZkClient zkClinet = new ZkClient(zkServerList);
				
		boolean rootExists = zkClinet.exists(PATH);
		if(!rootExists){
			zkClinet.createPersistent(PATH);
		}
		
		//zookeeper的根节点去创建这个服务  服务名称的节点是一个持久节点
		boolean serviceExists = zkClinet.exists(PATH +"/"+serviceName);
		
		if(!serviceExists){
			zkClinet.createPersistent(PATH +"/"+serviceName);
		}
		
		//注册当前服务器和权重
		InetAddress addr = InetAddress.getLocalHost();
		String ip =addr.getHostAddress().toString();//获得本机ip
		
		//把服务的ip地址增加到服务的名称后面，那么服务的消费者在调用服务的时候就可以查到服务提供者的地址
		//服务的机器 ip地址，并不是一个持久节点，是一个非持久节点，因为服务上下线，宕机的时候，该节点会产生响应的变化
		//因为这里只有一台机器，所以写不同端口来实现
		zkClinet.createEphemeral(PATH +"/"+serviceName +"/"+ip+"_"+port);
		
	
	}
	
	//提供服务
		public void provide() throws Exception{
			ServerSocket server = new ServerSocket(port);
			while(true){
				Socket socket = server.accept();
				
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				output.writeObject("this is serviceB");
			}
		}
	public static void main(String[] args) throws Exception {
		ServiceBProvider a = new ServiceBProvider();
		a.init();
		a.provide();
		Thread.sleep(1000*60*60*24);
	}
}
