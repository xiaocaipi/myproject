package com.stock.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.stock.service.StockGetDataService;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockData;

public class StockUtil {
	//判断当天是否涨停
	public static boolean iszhangting(StockData stockdata){
		if(stockdata.getZhangdiefudu()>9.81 &&stockdata.getClose()==stockdata.getHigh()){
			return true;
		}else{
			return false;
		}
		
	}
	
	// 判断第二天是否有买点  就是最低价比昨天的收盘价低于1个点就可以
	public static boolean isTommorrowBuy1(StockData stockdata0,StockData stockdata1) {
		if(stockdata1.getLow()/stockdata0.getClose()<1.01){
			return true;
		}else {
			return false;
		}

	}
	
	// 获取第二天最高价比第一天收盘价的涨幅
		public static double getHigh2close1(StockData stockdata0,StockData stockdata1) {
			return stockdata1.getHigh()/stockdata0.getClose();

		}
	//生成查询参数
		public static HashMap<String, String> generateQueryMap(HashMap<String, String> map,String fudumin,String fudumax,String datebegin,String dateend,boolean isNotZhiShu ) {
			if(map==null){
				map = new HashMap<String, String>();
			}
			//默认查询涨幅在0-10
			if(!StringUtils.isEmpty(fudumax)){
				map.put("fudumin", fudumin);
			}
			if(!StringUtils.isEmpty(fudumin)){
				map.put("fudumax", fudumax);
			}
			//默认查询一个月的数据
			if(StringUtils.isEmpty(datebegin)){
				datebegin = DateUtil.getDateBefore(new Date(), 30);
			}
			if(StringUtils.isEmpty(dateend)){
				dateend = DateUtil.getDateBefore(new Date(), 0);
			}
			if(isNotZhiShu){
				map.put("isnotzhishu", "1");
			}
			map.put("datebegin", datebegin);
			map.put("dateend", dateend);
			return map;
		}
		
		public static List<StockData> getZhishuList(StockGetDataService stockGetDataService,String code){
			List<StockData> returnlist =null;
			HashMap<String, String> zhishuparamap =new HashMap<String, String>();
			if(code.equals("000001")){
				zhishuparamap.put("code", "000001");
				zhishuparamap.put("name", "上证指数");
				returnlist=stockGetDataService.getStockBase(zhishuparamap);
			}else if(code.equals("399006")){
				zhishuparamap.put("code", "399006");
				zhishuparamap.put("name", "创业板指");
				returnlist=stockGetDataService.getStockBase(zhishuparamap);
			}
			return returnlist;
		}
		//map存的是日期，和对应的涨幅+涨跌幅点数
		public static Map<String, String> getZhishuMap(StockGetDataService stockGetDataService,String code){
			Map<String, String> returnmap=new HashMap<String, String>();
			List<StockData> list=getZhishuList(stockGetDataService, code);
			for(StockData stockData :list){
				returnmap.put(stockData.getDate(), stockData.getZhangdiefudu()+":::"+stockData.getZhangdie());
			}
			return returnmap;
		}
		/**
		 * 生成股票基本信息的map  map的key是 code value 是这个code   List<StockData>  排过序的
		 * 参数查出来的数据
		 * @return
		 */
		public static Map<String, List<StockData>> formatStockDataMap1(List<StockData>  baseList) {
			Map<String, List<StockData>> returnMap = new HashMap<String, List<StockData>>();
			List<StockData> tmpList= new ArrayList<StockData>();
			String refcode="";
			//baselist 最后加一行  防止最后一个代码不加到returnMap里面去
			baseList.add(new StockData());
			for(StockData stockData : baseList){
				String code =stockData.getCode();
				if(!refcode.equals(code)){
					returnMap.put(refcode, tmpList);
					refcode=code;
					tmpList= new ArrayList<StockData>();
				}
				tmpList.add(stockData);
			}
			return returnMap;
		}
		/**
		 * 获取n天前的stockdata list 是排序过的从大到小
		 * 
		 * @return
		 */
		public static StockData getRefNData(List<StockData> list, String date,
				int n) {
			int num=0;
			for(int i=0;i<list.size();i++){
				StockData data=list.get(i);
				if(date.equals(data.getDate())){
					num=n+i;
				}
			}
			if(num>=list.size()-1){
				return null;
			}else {
				return list.get(num);
			}
		}
		
	
}
