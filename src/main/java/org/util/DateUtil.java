package org.util;



public class DateUtil {
	
	public static void convertMonthAndDay(Object[] date_list) {
		for (int i=0; i<date_list.length;i++) {
			if(date_list[i]!=null&&date_list[i] instanceof String )
				date_list[i] = ((String)date_list[i]).substring(5).replace('-', '/');
		}
	}

	public static void main(String[] args) {
		String[] ss = new String[]{"2014-03.23","2014-03.23"};
		DateUtil.convertMonthAndDay(ss);
		System.out.println(ss[0]+ss[1]);
	}

}
