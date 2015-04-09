package com.stock.service.impl;


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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.stock.dao.StockProjectDao;
import com.stock.service.StockAnalysisService;
import com.stock.service.StockGetDataService;
import com.stock.util.CommonTool;
import com.stock.util.CommonUtil;
import com.stock.util.DateUtil;
import com.stock.util.GetStockCode;
import com.stock.util.NetUtil;
import com.stock.util.SohuStockUtil;
import com.stock.util.StockUtil;
import com.stock.util.TigerUtil;
import com.stock.util.yahoointerface.GetDataFromYahooUtil;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;

@Service("stockAnalysisService")
public class StockAnalysisServiceImpl implements StockAnalysisService {

	 @Autowired(required = false)
	  @Qualifier("stockProjectDao")
	private StockProjectDao stockProjectDao;
	 
	 @Autowired(required = false)
	  @Qualifier("stockGetDataService")
	private StockGetDataService stockGetDataService;

	
	 @Override
		//获得有希望的-就是在一个月内有多次 机构买入
			//获得由希望的之后并且进行收益的计算
		public void dealWithStockTiger1(HashMap<String, String> hashMap) {
			List<Map> strongtiger1List=stockProjectDao.getStrongTigerWithTime(hashMap);
			HashMap<String, Integer>tmpmap=new HashMap<String, Integer>();
			List<String>tmplist=new ArrayList<String>();
			String code="";
			Date date=null;
			for(int i=0;i<strongtiger1List.size();i++){
				Map map=strongtiger1List.get(i);
				String tmpcode=(String)map.get("code");
				Date tmpdate=(Date)map.get("date");
				if(!code.equals(tmpcode)){
					code=tmpcode;
					date=tmpdate;
				}
				else{
					long num=CommonTool.caculateTimeBetween(date,tmpdate);
					if(num<11){
//						System.out.println(tmpcode +"\t"+tmpdate+"\t"+"1");
						tmpcode=CommonTool.setFormalCode(tmpcode);
//						CommonTool.formatFlie("G:\\project\\stock\\data\\data1.txt", tmpcode +"\t"+tmpdate+"\t"+"2"+"\n",false);
						tmpmap.put(tmpcode+"-"+tmpdate, 1);
						tmplist.add(tmpcode);
					}
					code=tmpcode;
					date=tmpdate;
				}
			}
			CommonUtil.createDZHdata(tmpmap, tmplist,CommonUtil.path+"data1.txt");
			
		}
		
		
		
		@Override
		//把每天机构净买入的都抽出来
		public void dealWithStockTiger2(HashMap<String, String> hashMap) {
			List<Map> strongtiger1List=stockProjectDao.getStrongTigerWithTime(hashMap);
			HashMap<String, Integer>tmpmap=new HashMap<String, Integer>();
			List<String>tmplist=new ArrayList<String>();
			String code="";
			Date date=null;
			for(int i=0;i<strongtiger1List.size();i++){
				Map map=strongtiger1List.get(i);
				String tmpcode=(String)map.get("code");
				Date tmpdate=(Date)map.get("date");
				if(!code.equals(tmpcode)){
					tmpcode=CommonTool.setFormalCode(tmpcode);
					tmpmap.put(tmpcode+"-"+tmpdate, 1);
					tmplist.add(tmpcode);
					code=tmpcode;
					date=tmpdate;
				}
			}
			CommonUtil.createDZHdata(tmpmap, tmplist,CommonUtil.path+"data2.txt");
		} 
		//机构买入数量大于2
		@Override
		public void dealWithStockTiger3(HashMap<String, String> hashMap) {
			List<Map> strongtiger3List=stockProjectDao.getStockTiger3(hashMap);
			HashMap<String, Integer>tmpmap=new HashMap<String, Integer>();
			List<String>tmplist=new ArrayList<String>();
			String code="";
			Date date=null;
			for(int i=0;i<strongtiger3List.size();i++){
				Map map=strongtiger3List.get(i);
				String tmpcode=(String)map.get("code");
				Date tmpdate=(Date)map.get("date");
				String tmpflag=(String)map.get("flag");
				Long sellnum=(Long)map.get("sellnum");
				Long buynum=(Long)map.get("buynum");
				if(buynum-sellnum>=2 && buynum>2){
					tmpcode=CommonTool.setFormalCode(tmpcode);
					tmpmap.put(tmpcode+"-"+tmpdate, 1);
					tmplist.add(tmpcode);
				}
					
			}
			CommonUtil.createDZHdata(tmpmap, tmplist,CommonUtil.path+"data3.txt");
			
		}

		
		@Override
		public void dealWithStockTiger4(HashMap<String, String> hashMap) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void analysisStockTiger3() {
			List<Map> strongtiger3List=stockProjectDao.getStockTiger3(new HashMap<String, String>());
			HashMap<String, Integer>tmpmap=new HashMap<String, Integer>();
			List<String>tmplist=new ArrayList<String>();
			int tongji1=0;
			int tongji2=0;
			int tongji3=0;
			//获取指数的信息
			Map<String, String> shangzhenzhiMap=StockUtil.getZhishuMap(stockGetDataService, "000001");
			Map<String, String> chuangyebanzhiMap=StockUtil.getZhishuMap(stockGetDataService, "399006");
			String code="";
			Date date=null;
			for(int i=0;i<strongtiger3List.size();i++){
				Map map=strongtiger3List.get(i);
				String tmpcode=(String)map.get("code");
				Date tmpdate=(Date)map.get("date");
				String tmpdate2=CommonUtil.praseDateToString(tmpdate, false);
				String tmpflag=(String)map.get("flag");
				Long sellnum=(Long)map.get("sellnum");
				Long buynum=(Long)map.get("buynum");
				if(buynum-sellnum>2){
					tongji3++;
					List<StockData> stockdata=stockGetDataService.getStockDataInDay(tmpcode, tmpdate2, 3);
					if(stockdata.size()<3){
						continue;
					}
					StockData stockdata0=stockdata.get(0);
					StockData stockdata1=stockdata.get(1);
					StockData stockdata2=stockdata.get(2);
					if(StockUtil.iszhangting(stockdata0) &&StockUtil.isTommorrowBuy1(stockdata0, stockdata1)){
						tongji1++;
						CommonUtil.showStockDataWithZhiShu(stockdata,shangzhenzhiMap,chuangyebanzhiMap);
						if(StockUtil.getHigh2close1(stockdata1, stockdata2)>1.02){
							tongji2++;
						}
						System.out.println(tongji3+"-----"+tongji1+"-------"+tongji2);
					}
					
					
				}
					
			}
			
		}



		@Override
		//生成数据，放到R里面去处理，或者放到mahout里面去处理
		public void outPutData(HashMap<String, String> hashMap) {
			
			//今天的涨幅（大于3个点）  昨天的涨幅  今天大盘1的涨幅  昨天大盘1的涨幅  今天大盘2的涨幅  昨天大盘2的涨幅   先用这些数据测试一下
			if(hashMap ==null){
				hashMap = new HashMap<String, String>();
			}
			List<Map<String, Object>> outputList = new ArrayList<Map<String,Object>>();
			hashMap =StockUtil.generateQueryMap(hashMap, null, null,null,null,true);
			List<StockData>  baseList = stockGetDataService.getStockBase(hashMap);
			//获取指数的信息
			Map<String, String> shangzhenzhiMap=StockUtil.getZhishuMap(stockGetDataService, "000001");
			Map<String, String> chuangyebanzhiMap=StockUtil.getZhishuMap(stockGetDataService, "399006");
			Map<String, List<StockData>> stockDataMap=StockUtil.formatStockDataMap1(baseList);
			LinkedHashMap<String, Object> mapName = new LinkedHashMap<String, Object>();
			mapName.put("todayzhangfu","todayzhangfu");
			mapName.put("ref1zhangfu","ref1zhangfu");
			mapName.put("todaySHzhangfu","todaySHzhangfu");
			mapName.put("ref1SHzhangfu","ref1SHzhangfu");
			mapName.put("todayCYBzhangfu","todayCYBzhangfu");
			mapName.put("ref1CYBzhangfu","ref1CYBzhangfu");
			
			outputList.add(mapName);
			for(int i=0;i<baseList.size();i++){
				StockData stockData = baseList.get(i);
				if(stockData.getZhangdiefudu()<3){
					continue;
				}
				LinkedHashMap<String, Object> maptmp = new LinkedHashMap<String, Object>();
				maptmp.put("todayzhangfu", stockData.getZhangdiefudu());
				String code = stockData.getCode();
				StockData ref1StockData = StockUtil.getRefNData(stockDataMap.get(code),stockData.getDate(),1);
				if(ref1StockData==null ){
					continue;
				}
				maptmp.put("ref1zhangfu",ref1StockData.getZhangdiefudu());
				maptmp.put("todaySHzhangfu", shangzhenzhiMap.get(stockData.getDate()).split(":::")[0]);
				maptmp.put("ref1SHzhangfu", shangzhenzhiMap.get(ref1StockData.getDate()).split(":::")[0]);
				maptmp.put("todayCYBzhangfu", chuangyebanzhiMap.get(stockData.getDate()).split(":::")[0]);
				maptmp.put("ref1CYBzhangfu", chuangyebanzhiMap.get(ref1StockData.getDate()).split(":::")[0]);
				outputList.add(maptmp);
			}
			
			CommonUtil.fileCommonList(outputList);
			
			
			
			
		}



	
	

}
