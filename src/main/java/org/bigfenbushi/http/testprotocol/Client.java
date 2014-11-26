package org.bigfenbushi.http.testprotocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws IOException {
		
		//拼装一个request 请求
		Request request = new Request();
		request.setCommand("HELLO");
		request.setCommandLength(request.getCommand().length());
		request.setEncode(Encode.UTF8.getValue());
		
		System.out.println("commandlength: "+request.getCommandLength());
		System.out.println("command : "+request.getCommand());
		//练到服务端
		Socket client = new Socket("127.0.0.1",4567);
		
		OutputStream output = client.getOutputStream();
		
		//发送请求
		ProtocolUtil.writeRequest(output, request);
		
		//读取服务端响应数据
		InputStream input = client.getInputStream();
		Response response = ProtocolUtil.readResponse(input);
		
		System.out.println("responseLength :" + response.getResponseLength());
		System.out.println("response :" + response.getResponse());
	}
}
