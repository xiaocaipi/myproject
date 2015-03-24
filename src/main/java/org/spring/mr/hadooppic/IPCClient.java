package org.spring.mr.hadooppic;


import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
//调用远程过程的方法
public class IPCClient {
	public static void main(String[] args) throws IOException{
		//创建远程tcpid的连接
		InetSocketAddress addr = new InetSocketAddress("localhost", IPCServer.IPC_PORT);
		//得到client 端ipctest 的对象
		IPCTest query = (IPCTest)RPC.getProxy(IPCTest.class, IPCServer.IPC_VER, addr, new Configuration());
		System.out.println(query.add(1, 1));
		RPC.stopProxy(query);
	}
}
