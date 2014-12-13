package org.bigfenbushi.http.tesrtcprpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
   
	public static void main(String[] args) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ServerSocket server = new ServerSocket(1234);
		while(true){
			Socket socket = server.accept();
			
			//读取服务信息
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			String interfacename = input.readUTF();//接口的名称
			String methodName = input.readUTF();//方法名称
			Class<?> [] parameterTypes= (Class<?> [])input.readObject(); //参数类型
			Object[] arguments= (Object[])input.readObject();//参数对象
			
			//执行调用
			Class serviceinterfaceclass = Class.forName(interfacename);//得到接口的class
			Object service = services.get(interfacename);//取得服务实现的对象
			Method method = serviceinterfaceclass.getMethod(methodName, parameterTypes);//获得要调用的方法
			Object result =method.invoke(service, arguments);
			
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(result);
		}
	}
}
