package com.stock.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.spring.kms.util.CommonTool;
import com.stock.service.StockGetDataService;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockData;
import com.stock.vo.StockRealTimeData;

public class CommonUtil {
	
	
//	public static String path="/data/app/tomcat/data/";
	public static String path="G:\\project\\stock\\data\\";
	public static String codeshpath="G:\\project\\stock\\data\\SH.SNT";
	public static String codeszpath="G:\\project\\stock\\data\\SZ.SNT";
	public static String outputpath="G:\\project\\stock\\output\\";
	private static SimpleDateFormat fmtspecial=new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static CrawlerWrongVo getCrawlerWrongVo (String url,String type,String message){
		CrawlerWrongVo returnvo=new CrawlerWrongVo();
		returnvo.setMessage(message);
		returnvo.setUrl(url);
		returnvo.setType(type);
		return returnvo;
	}
	
	public static String getDateFromString (String data){
		String returndata="";
		Pattern pattern=Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
		Matcher matcher = pattern.matcher(data);
		while(matcher.find()){
			returndata=matcher.group(0);
		}
		return returndata;
	}
	//前闭后闭
	public static List<String> getDatesByDay(String tmptimestart,String tmptimeend){
//		String tmptimestart="2014-08-01";
//		String tmptimeend="2014-08-05";
		
		Date date=new Date();
		if(StringUtils.isEmpty(tmptimeend)){
			tmptimeend=fmt.format(date);
		}
		List<String> returnlist=new ArrayList<String>();
		try {
			Calendar calendar = new GregorianCalendar();
			
			 
			 calendar.setTime(date);
			 Date startdate=fmt.parse(tmptimestart);
			 Date enddate=fmt.parse(tmptimeend);
			 long num=CommonTool.caculateTimeBetween(startdate, enddate);
			 long time1=System.currentTimeMillis();
			
			
			for(int i=0;i<=num;i++){
				calendar.setTime(startdate);
				calendar.add(calendar.DATE, i);
				String date2=fmt.format(calendar.getTime());
				returnlist.add(date2);
//				System.out.println(date2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return returnlist;
	}

	
	
	
	public static void createDZHdata(HashMap<String, Integer> tmpmap,
			List<String> tmplist,String filename) {
		Calendar calendar = new GregorianCalendar();
		Date tmpdata2=new Date();
		SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
		
		//tmpmap 放的是符合的数据，但是大智慧工具里面要把不符合的日期也要设置为0 
		for(String tmpcode2:tmplist){
			for(int i=0;i>-1000;i--){
				calendar.setTime(tmpdata2);
				calendar.add(calendar.DATE, i);
				String date2=fmt.format(calendar.getTime());
				String codedate=tmpcode2+"-"+date2;
				if(tmpmap.get(codedate)==null){
					CommonTool.formatFlie(filename, tmpcode2 +"\t"+date2+"\t"+"0"+"\n",false);
				}else{
					CommonTool.formatFlie(filename, tmpcode2 +"\t"+date2+"\t"+"2"+"\n",false);
				}
			}
		}
	}
	
	public static String praseDateToString(Date date,boolean special){
		String returnDate="";
		if(special){
			returnDate=fmtspecial.format(date);
		}else{
			returnDate=fmt.format(date);
		}
		return returnDate;
	}
	
	public static void showStockData(List<StockData> list){
		
		for(StockData stockdata:list){
			stockdata.toString();
		}
	}
	
	public static void showStockDataWithZhiShu(List<StockData> list, Map<String, String> shangzhenzhiMap, Map<String, String> chuangyebanzhiMap ){
		
		for(StockData stockdata:list){
			String print=stockdata.getPrintString();
			String print2=shangzhenzhiMap.get(stockdata.getDate());
			String print3=chuangyebanzhiMap.get(stockdata.getDate());
			System.out.println(print+"##"+print2+"##"+print3);
			
		}
	}
	

	public static void showStockRealTimeData(StockRealTimeData realTimeData) {
		
		System.out.println(realTimeData.getCode()+"--"+realTimeData.getClose()+"--:"+realTimeData.getZhangdiefudu()+"--"+realTimeData.getDealnowShou());
	}
	
	public static void fileCommonList (List<Map<String, Object>> list) {
		String printline="";
		for(Map<String, Object> map:list){
			String tmpprintline1="";
			for(String s:map.keySet()){
//				String tmp1=(String)map.get(s);
				if(tmpprintline1.equals("")){
					tmpprintline1 =""+map.get(s);
				}else{
					tmpprintline1 =tmpprintline1+"\t"+map.get(s);
				}
				
			}
			printline = printline+"\n"+tmpprintline1;
		}
		try {
			FileMyUtil.print(printline, "test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 叠到map的顺序
	 * 
	 * @return
	 */
	public static Map<String, Object>  converMap(Map<String, Object> paraMap) {
		LinkedHashMap<String, Object> returnMap =new LinkedHashMap<String, Object>();
		for(String s:paraMap.keySet()){
			returnMap.put(s, paraMap.get(s));
		}
		return returnMap;
	}
	
	public static void main(String[] args) {
		
		List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();
		LinkedHashMap<String, Object> mapName = new LinkedHashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		mapName.put("todayzhangfu","todayzhangfu");
		mapName.put("ref1zhangfu","ref1zhangfu");
		mapName.put("todaySHzhangfu","todaySHzhangfu");
		mapName.put("ref1SHzhangfu","ref1SHzhangfu");
		mapName.put("todayCYBzhangfu","todayCYBzhangfu");
		mapName.put("ref1CYBzhangfu","ref1CYBzhangfu");
		converMap(mapName);
		list.add(mapName);
//		list.add(map2);
//		list.add(map3);
		
		fileCommonList(list);
		
		
	}

}
