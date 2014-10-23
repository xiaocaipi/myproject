package org.rubby;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HbaseIf  hbase=new HbaseIf();
	long id=	hbase.getIdByUsername("kaka");
	System.out.println(id);
		System.out.println(11);

	}

}
