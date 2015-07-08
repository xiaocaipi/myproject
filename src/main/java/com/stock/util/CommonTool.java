package com.stock.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * @version 0.1
 * @author xiaozhendong
 * @purpose
 * @date 2010-9-26
 */
public class CommonTool {
	
	   //取得ip
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
	public static BigDecimal obj2BigDecimal(Object obj){
		if(obj == null || obj.equals("")){
			return new BigDecimal("0");
		}
		return new BigDecimal(obj.toString());
	}
	
	public static Integer obj2Integer(Object obj){
		if(obj == null || obj.equals("")){
			return 0;
		}
		return new Integer(obj.toString());
	}
	
	public static String obj2String(Object obj){
		if(obj == null){
			return "";
		}
		return obj.toString();
	}
	public static Integer obj2IntegerForPageIndex(Object obj){
		if(obj == null ||  obj.equals("") || "undefined".equals(obj)){
			return 1;
		}
		return obj2Integer(obj);
	}
	
	public static boolean isNull(Object obj){
		if(obj == null || obj.toString().equals("")){
			return true;
		}
		return false;
	}
	/**
	 * jspҳ���а�nullת��Ϊ��
	 * @param obj
	 * @return
	 */
	public static String convertNull(Object obj){
		if(obj == null ){
			return "";
		}
		return obj.toString().trim();
	}
	
	public static GregorianCalendar cleanTime(GregorianCalendar date) {
		date.set(Calendar.AM_PM, Calendar.AM);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}
	
	public static Date cleanTime(Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.set(Calendar.AM_PM, Calendar.AM);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		return gc.getTime();
	}
	
	public static String int2Str(int para){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.AM_PM, Calendar.AM);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		gc.add(Calendar.MILLISECOND, para);
		return sdf.format(gc.getTime());
	}
	public static String long2Str(long para){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.AM_PM, Calendar.AM);
		gc.set(Calendar.HOUR_OF_DAY, 0);
		gc.set(Calendar.MINUTE, 0);
		gc.set(Calendar.SECOND, 0);
		gc.set(Calendar.MILLISECOND, 0);
		gc.add(Calendar.MILLISECOND, (int)para);
		return sdf.format(gc.getTime());
	}
	
	public static BigDecimal obj2BigDecimal(Object obj,int i){
		if(obj == null || obj.equals("")){
			return new BigDecimal("0");
		}
		return new BigDecimal(obj.toString()).setScale(i, BigDecimal.ROUND_HALF_UP);
	}
	
	public static boolean compareStr(Object obj1,Object obj2){
		boolean b = true;
		if(!obj2String(obj1).trim().equals(obj2String(obj2).trim())){
			b = false;
		}
		return b;
	}
	
	public static String getDateStr(Date date){
		if(date == null){
			return "";
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc.get(Calendar.YEAR)+"��"+gc.get(Calendar.MONTH)+"��"+gc.get(Calendar.DAY_OF_MONTH)+"��";
	}
	
	public static void formatFlie(String fileName, String context,boolean exitIsdelete) { 
		File f = new File(fileName); 
		if(f.exists()){
			if(exitIsdelete){
				f.delete();
			}
		}
		if (!f.exists()) { 
		try { 
		f.createNewFile(); 
		} catch (IOException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} 
		} 
		
		BufferedWriter output = null; 
		try { 
		output = new BufferedWriter(new OutputStreamWriter( 
		new FileOutputStream(fileName, true))); 
		output.write(context); 
		} catch (Exception e) { 
		e.printStackTrace(); 
		} finally { 
		try { 
		output.close(); 
		} catch (IOException e) { 
		e.printStackTrace(); 
		} 
		} 
		}

	public static String setFormalCode(String tmpcode) {
		if(tmpcode.startsWith("0")){
			tmpcode="SZ"+tmpcode;
		}else if(tmpcode.startsWith("3")){
			tmpcode="SZ"+tmpcode;
		}else if(tmpcode.startsWith("6")){
			tmpcode="SH"+tmpcode;
		}else{
			tmpcode="SH"+tmpcode;
		}
		return tmpcode;
	} 
	
	public static long caculateTimeBetween(Date date, Date tmpdate) {
		long time1=date.getTime();
		long time2=tmpdate.getTime();
		long retuntime=(time2-time1)/(1000 * 60 * 60 *24) ;
		
		return retuntime;
	}
	
}
