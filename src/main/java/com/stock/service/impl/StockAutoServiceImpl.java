package com.stock.service.impl;


import java.io.IOException;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.stock.dao.StockProjectDao;
import com.stock.service.StockAnalysisService;
import com.stock.service.StockAutoService;
import com.stock.service.StockGetDataService;
import com.stock.util.CommonTool;
import com.stock.util.CommonUtil;
import com.stock.util.GetStockCode;
import com.stock.util.NetUtil;
import com.stock.util.SohuStockUtil;
import com.stock.util.TigerUtil;
import com.stock.util.yahoointerface.GetDataFromYahooUtil;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;

@Service("stockAutoService")
public class StockAutoServiceImpl implements StockAutoService {

	 @Autowired(required = false)
	  @Qualifier("stockProjectDao")
	private StockProjectDao stockProjectDao;
	 
	 @Autowired(required = false)
	  @Qualifier("stockGetDataService")
	private StockGetDataService stockGetDataService;
	 
	 @Autowired(required = false)
	  @Qualifier("stockAnalysisService")
	private StockAnalysisService stockAnalysisService;

	
	 @Override
		public void autoStockTiger(HashMap<String, String> paraMap) throws Exception {
			
			Map map=stockGetDataService.getStockTigerFormalMaxDate(new HashMap<String, String>());
			SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
			Date maxdate=(Date)map.get("date");
			String starttime=fmt.format(maxdate);
			String endtime=fmt.format(new Date());
			if(StringUtils.isNotEmpty(paraMap.get("starttime")) && StringUtils.isNotEmpty(paraMap.get("endtime"))){
				starttime=paraMap.get("starttime");
				endtime=paraMap.get("endtime");
			}
			List<String> dates=CommonUtil.getDatesByDay(starttime, endtime);
			for(String tradedate:dates ){
				HashMap<String, String> paramap=new HashMap<String, String>();
				paramap.put("url", "http://data.eastmoney.com/Stock/lhb/"+tradedate+".html");
				paramap.put("tradedate",tradedate);
//				1先爬取http://data.eastmoney.com/Stock/lhb/2014-09-02.html 龙虎榜日期页面，把每个股票的url 记录到数据库里面
				stockGetDataService.fetchStockTigerDate(paramap);
				//2爬取每一个股票的龙虎榜信息
				stockGetDataService.fetchStockTiger(paramap);
				//3对stock_tiger_Date错误进行重新爬取
				stockGetDataService.fetchStockTigerWrongDate(paramap);
				//4对stock_tiger错误进行重新爬取
				stockGetDataService.fetchStockTigerWrong(paramap);
				
			}
			//5生成数据
//			CommonTool.formatFlie("/data/app/tomcat/data/data1.txt","",true);		
			CommonTool.formatFlie(CommonUtil.path+"data1.txt","",true);		
			stockAnalysisService.dealWithStockTiger1(new HashMap<String, String>());
//			CommonTool.formatFlie(CommonUtil.path+"data2.txt","",true);		
//			stockAnalysisService.dealWithStockTiger2(new HashMap<String, String>());
			CommonTool.formatFlie(CommonUtil.path+"data3.txt","",true);		
			stockAnalysisService.dealWithStockTiger3(new HashMap<String, String>());
			
		}


	@Override
	public void autoFetchBaseStock(HashMap<String, String> hashMap) {
		stockGetDataService.fetchData(hashMap);
		
	}	

}
