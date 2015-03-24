package com.stock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 得到几天前的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static String getDateBefore(Object d, int day) {
		Calendar now = Calendar.getInstance();
		try {
			if(d.getClass().getName().equals("java.util.Date")){
				Date time=(Date)d;
				now.setTime(time);
			}else if(d.getClass().getName().equals("java.lang.String")){
				String time=String.valueOf(d);
				now.setTime(fmt.parse(time));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return fmt.format(now.getTime());
	}

	public static void main(String[] args) {
		System.out.println(getDateBefore(new Date(), 0));
	}
}
