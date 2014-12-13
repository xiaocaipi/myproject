package org.bigfenbushi.routeandloadbalance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.bigfenbushi.http.tesrtcprpc.SayHelloService;
import org.bigfenbushi.http.tesrtcprpc.SayHelloServiceImpl;

public class ServiceAProvider {
	
	private String serviceName = "service-A";
	private int port = 1234;

	//想zookeeper 注册服务
	public void init() throws Exception{

		
		String zkServerList = "192.168.1.105:2181";
		String PATH ="/configcenter"; //服务节点路径
		ZkClient zkClinet = new ZkClient(zkServerList);
				
		boolean rootExists = zkClinet.exists(PATH);
		if(!rootExists){
			zkClinet.createPersistent(PATH);
		}
		
		//zookeeper的根节点去创建这个服务
		boolean serviceExists = zkClinet.exists(PATH +"/"+serviceName);
		
		if(!serviceExists){
			zkClinet.createPersistent(PATH +"/"+serviceName);
		}
		
		//注册当前服务器和权重
		InetAddress addr = InetAddress.getLocalHost();
		String ip =addr.getHostAddress().toString();//获得本机ip
		
		//把服务的ip地址增加到服务的名称后面，那么服务的消费者在调用服务的时候就可以查到服务提供者的地址
		zkClinet.createEphemeral(PATH +"/"+serviceName +"/"+ip+"_"+port);
		
	
	}
	
	//提供服务
	public void provide() throws Exception{
		ServerSocket server = new ServerSocket(port);
		while(true){
			Socket socket = server.accept();
			
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject("this is serviceA");
		}
	}
	public static void main(String[] args) throws Exception {
		ServiceAProvider a = new ServiceAProvider();
		a.init();
		a.provide();
		Thread.sleep(1000*60*60*24);
	}
}
