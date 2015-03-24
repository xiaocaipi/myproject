package org.spring.mr.hadooppic;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;
//主要负责监听端口，实例化server端的IPCTestIMpl对象
public class IPCServer {
	public static final long IPC_VER = 1L;
	//远程调用是通过tcpid 来的所以要有一个远程的端口
	public static final int IPC_PORT = 32121;
	
	//启动server端
	public static void main(String[] args) throws IOException, InterruptedException{
		IPCTestImpl service = new IPCTestImpl();
		//   通过RPC.getServer 来创建一个tcpid的server 第二个是ip地址 这里是全0 表明在当前主机的所有ip上，都监听这个端口
		//5 是指5个handler 线程，是指调用5个handler 线程来 处理请求
		Server s = RPC.getServer(service, "0.0.0.0", IPC_PORT, 5, false, new Configuration());
		//start 是异步的方法，调用之后会立刻返回
		s.start();
		//需要在server 中对当前的线程做死循环
		while(true){
			Thread.sleep(10000000);
		}
		//在真实情况下还需要调用s.stop() 来停止
	}
}
