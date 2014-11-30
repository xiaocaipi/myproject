package org.bigfenbushi.http.tesrtcprpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.bigfenbushi.http.testprotocol.Encode;
import org.bigfenbushi.http.testprotocol.ProtocolUtil;
import org.bigfenbushi.http.testprotocol.Request;
import org.bigfenbushi.http.testprotocol.Response;

public class Provider {
	
	//所有的对外服务 都放在map里面
	private static Map<String, Object> services = new HashMap<String, Object>();
	
	static{
		services.put(SayHelloService.class.getName(), new SayHelloServiceImpl());
	}
   
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(1234);
		while(true){
			Socket socket = server.accept();
			
			//读取服务信息
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			String interfacename = input.readUTF();//接口的名称
			String methodName = input.readUTF();//方法名称
			
			
			
		}
	}
}
