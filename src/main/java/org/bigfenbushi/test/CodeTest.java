package org.bigfenbushi.test;

public class CodeTest {
	
	public static void main(String[] args) {
		int i = 2;
		i = Math.abs(i);
		i = i |0x80;
		System.out.println(i);
	}

}
