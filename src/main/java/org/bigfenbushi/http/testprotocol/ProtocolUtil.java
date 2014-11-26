package org.bigfenbushi.http.testprotocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtocolUtil {
	
	public static Request readRequest(InputStream input) throws IOException{
		
		//读入编码  从输入里面读取一个字节
		byte [] encodeByte = new byte[1];
		input.read(encodeByte);
		//并把这个字节转换编码
		byte encode = encodeByte[0];
		
		//读取4个字节  读取命令长度
		byte[] commandLengthBytes = new byte[4];
		input.read(commandLengthBytes);
		int commandLength = ByteUtil.bytes2Int(commandLengthBytes);
		
		//读取命令
		byte [] commandBytes = new byte[commandLength];
		input.read(commandBytes);
		String command ="";
		if(Encode.GBK.getValue() == encode){
			command = new String(commandBytes,"GBK");
		}else{
			command = new String(commandBytes,"UTF8");
		}
		
		//构建一个请求返回
		Request request = new Request();
		request.setCommand(command);
		request.setEncode(encode);
		request.setCommandLength(commandLength);
		return request;
	}
	//响应的读取也类似
public static Response readResponse(InputStream input) throws IOException{
		
		//读取编码  
		byte [] encodeByte = new byte[1];
		input.read(encodeByte);
		byte encode = encodeByte[0];
		
		//读取响应长度
		byte[] responseLengthBytes = new byte[4];
		input.read(responseLengthBytes);
		int responseLength = ByteUtil.bytes2Int(responseLengthBytes);
		
		//读取响应内容
		byte [] responseBytes = new byte[responseLength];
		input.read(responseBytes);
		String responseStr ="";
		if(Encode.GBK.getValue() == encode){
			responseStr = new String(responseBytes,"GBK");
		}else{
			responseStr = new String(responseBytes,"UTF8");
		}
		
		//组装响应
		Response response = new Response();
		response.setEncode(encode);
		response.setResponse(responseStr);
		response.setResponseLength(responseLength);
		return response;
	}

//响应的输出

	public static void writeResponse(OutputStream output,Response response) throws IOException{
		//将response 响应返回给客户端
		output.write(response.getEncode());
		output.write(ByteUtil.int2ByteArray(response.getResponseLength()));
		if(Encode.GBK.getValue() == response.getEncode()){
			output.write(response.getResponse().getBytes("GBK"));
		}else{
			output.write(response.getResponse().getBytes("UTF8"));
		}
		output.flush();
	}
	
	public static void writeRequest(OutputStream output,Request request) throws IOException{
		//将response 响应返回给客户端
		output.write(request.getEncode());
		output.write(ByteUtil.int2ByteArray(request.getCommandLength()));
		if(Encode.GBK.getValue() == request.getEncode()){
			output.write(request.getCommand().getBytes("GBK"));
		}else{
			output.write(request.getCommand().getBytes("UTF8"));
		}
		output.flush();
	}
	
	

}
