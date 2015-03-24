package org.spring.mr.hadooppic;


import java.io.IOException;
//接口在server端的实现
public class IPCTestImpl implements IPCTest{

	@Override
	//这个是在VersionedProtocol 定义的方法，作用是为了保持server端和客户端的一致性
	//当某一端发生变化的时候，就会对版本发生变化
	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		// TODO Auto-generated method stub
		//返回客户端和server端一致的版本号
		return IPCServer.IPC_VER;
	}

	@Override
	//如果是多线程的化，就需要方法是线程安全的，不然要加sychonize关键字
	public int add(int a, int b) {
		// TODO Auto-generated method stub
		return a+b;
	}

}
