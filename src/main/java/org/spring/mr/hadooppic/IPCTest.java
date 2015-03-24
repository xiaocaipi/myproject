package org.spring.mr.hadooppic;


import org.apache.hadoop.ipc.VersionedProtocol;
//接口作为客户端和服务端的接口，在server端 和服务端都需要
//必须实现VersionedProtocol
public interface IPCTest extends VersionedProtocol{
	//接口的方法
	int add(int a, int b);
}
