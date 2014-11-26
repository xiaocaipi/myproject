package org.bigfenbushi.http.testprotocol;

public class ByteUtil {
	
	//字节到整数的转换需要注意的是  java是大端的字节序，大小端的字节序需要注意下
	//http://blog.csdn.net/cyh1111/article/details/5129976   java字节序
	public static int bytes2Int(byte[] bytes){
		int num = bytes[3] & 0xFF;
		num |= ((bytes[2] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[2] << 24) & 0xFF000000);
		return num;
	}
	
	public static byte[] int2ByteArray(int n){
		byte[] b = new byte[4];  
		  b[3] = (byte) (n & 0xff);  
		  b[2] = (byte) (n >> 8 & 0xff);  
		  b[1] = (byte) (n >> 16 & 0xff);  
		  b[0] = (byte) (n >> 24 & 0xff);  
		  return b;  
	}
}
