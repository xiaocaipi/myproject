package org.bigfenbushi.http.testprotocol;

//http://www.cnblogs.com/happyPawpaw/archive/2013/04/09/3009553.html  java枚举
public enum Encode {
	
	GBK((byte)0), UTF8((byte)1);
	
	private byte value = 1;

	public byte getValue() {
		return value;
	}
	private Encode (byte value){
		this.value = value;
	}
	
	
}
