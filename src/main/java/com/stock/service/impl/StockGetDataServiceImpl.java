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
import com.stock.service.StockGetDataService;
import com.stock.util.CommonUtil;
import com.stock.util.GetStockCode;
import com.stock.util.NetUtil;
import com.stock.util.SinaStockUtil;
import com.stock.util.SohuStockUtil;
import com.stock.util.TigerUtil;
import com.stock.util.yahoointerface.GetDataFromYahooUtil;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockRealTimeData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;

@Service("stockGetDataService")
public class StockGetDataServiceImpl implements StockGetDataService {

	 @Autowired(required = false)
	  @Qualifier("stockProjectDao")
	private StockProjectDao stockProjectDao;
	@Override
	public void fetchData(HashMap<String, String> paraMap) {
		SohuStockUtil stockUtil = new SohuStockUtil();
//		paraMap.put("start", "14216");
//		paraMap.put("end", "14218");
		String starttime=paraMap.get("starttime");
		String endtime=paraMap.get("endtime");
		if(StringUtils.isEmpty(starttime)|| StringUtils.isEmpty(endtime)){
			starttime=CommonUtil.praseDateToString(new Date(), true);
			endtime=CommonUtil.praseDateToString(new Date(), true);
		}
		List<StockCode_BaseVo> codelist=stockProjectDao.getStockCode(paraMap);
		for(StockCode_BaseVo codevo:codelist){
			String code=codevo.getCode();
			String name=codevo.getName();
			boolean iszhishu = false;
			if(codevo.getPlace().equals("zs")){
				iszhishu=true;
			}
			List<StockData> stockList = stockUtil.getStockData(code, starttime, endtime, iszhishu,name);
			if(stockList!=null){
				stockProjectDao.insertStrocBase(stockList);
			}
			
		}
		
		
	}
	@Override
	public void insertStockConde() {
		List<StockCode_BaseVo> codelist=stockProjectDao.getStockCode(new HashMap<String, String>());
		HashMap<String, String> codemap=new HashMap<String, String>();
		for(StockCode_BaseVo codebasevo:codelist){
			String codename=codebasevo.getCode()+"-"+codebasevo.getName();
			codemap.put(codename, "1");
		}
		List<StockCode_BaseVo> szlisttmp=GetStockCode.readTxtFile(CommonUtil.codeszpath);
		List<StockCode_BaseVo> szlist=new ArrayList<StockCode_BaseVo>();
		for(StockCode_BaseVo codebasevo:szlisttmp){
			String codename=codebasevo.getCode()+"-"+codebasevo.getName();
			if(codemap.get(codename)==null){
				szlist.add(codebasevo);
			}
			
		}
		stockProjectDao.insertStockCode(szlist);
		
		
		List<StockCode_BaseVo> shlisttmp=GetStockCode.readTxtFile(CommonUtil.codeshpath);
		List<StockCode_BaseVo> shlist=new ArrayList<StockCode_BaseVo>();
		for(StockCode_BaseVo codebasevo:shlisttmp){
			String codename=codebasevo.getCode()+"-"+codebasevo.getName();
			if(codemap.get(codename)==null){
				shlist.add(codebasevo);
			}
			
		}
		stockProjectDao.insertStockCode(shlist);
		
		//删除代码一样，名字不一样的指数
		stockProjectDao.deleteStockCode(new HashMap<String, String>());
		//插入指数 上证 深圳 和创业板
		List<StockCode_BaseVo> zslist=new ArrayList<StockCode_BaseVo>();
		StockCode_BaseVo zscodevo1=new StockCode_BaseVo("000001","上证指数");
		zscodevo1.setPlace("zs");
		StockCode_BaseVo zscodevo2=new StockCode_BaseVo("399001","深圳成指");
		zscodevo2.setPlace("zs");
		StockCode_BaseVo zscodevo3=new StockCode_BaseVo("399006","创业板指");
		zscodevo3.setPlace("zs");
		zslist.add(zscodevo1);
		zslist.add(zscodevo2);
		zslist.add(zscodevo3);
		stockProjectDao.insertStockCode(zslist);
		
	}
	@Override
	public int fetchStockTigerDate(HashMap<String, String> paraMap) throws InterruptedException {
		String url=paraMap.get("url");
		String tradedate=CommonUtil.getDateFromString(url);
		Document doc=null;
		List<StockTigerDateVo> tigerdatelist=new ArrayList<StockTigerDateVo>();
		String prefix="http://data.eastmoney.com";
		int stocktigercount=stockProjectDao.getStockTigerDateCount(paraMap);
		if(stocktigercount >0){
			System.out.println(paraMap.get("tradedate")+"的stock_tiger_date已经爬取过了");
			return 0;
		}
		try {
			doc=NetUtil.goFetch(url, doc, new HashMap<String, Object>());
			if(doc==null){
				CrawlerWrongVo wrongvo=CommonUtil.getCrawlerWrongVo(url, "1", "龙虎榜日期页面爬取错误");
				stockProjectDao.insertCrawlerWrong(wrongvo);
				return 0;
			}else{
				
				Elements alltrs=doc.getElementsByAttributeValueContaining("class", "all");
				Map<String, Integer> map=new HashMap<String, Integer>();
				for(Element onetr:alltrs){
					Elements onetd=onetr.getElementsByAttributeValueContaining("href","/stock/lhb");
					if(onetd.size()==0){
						continue;
					}
					String tmpstockurl=onetd.attr("href");
					if(map.get(tmpstockurl)==null){
						map.put(tmpstockurl, 1);
						String stockurl=prefix+tmpstockurl;
						StockTigerDateVo datevo=new StockTigerDateVo();
						datevo.setUrl(stockurl);
						datevo.setTradedate(tradedate);
						tigerdatelist.add(datevo);
					}
				}
				
				stockProjectDao.insertStockTigerDate(tigerdatelist);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		System.out.println(paraMap.get("tradedate")+"的stock_tiger_date爬取成功");
		return 1;
		
		
	}
	
	@Override
	public int fetchStockTiger(HashMap<String, String> paramap) {
//		paramap.put("id", "98");
		TigerUtil tigerutil =new TigerUtil();
		List<StockTigerDateVo> stockdateList=stockProjectDao.getStockTigerDate(paramap);
		for(StockTigerDateVo stocktigerdatevo:stockdateList){
			String url=stocktigerdatevo.getUrl();
			String id=stocktigerdatevo.getId();
			String tmpurls[]=url.split(",");
			String date=tmpurls[1];
			String code=tmpurls[2].replace(".html", "");
			Document doc=null;
			try {
				doc=NetUtil.goFetch(url, doc, new HashMap<String, Object>());
				if(doc==null){
					CrawlerWrongVo wrongvo=CommonUtil.getCrawlerWrongVo(url, "2", "龙虎榜股票单页爬取错误");
					stockProjectDao.insertCrawlerWrong(wrongvo);
					return 0;
				}else{
					List<StockTigerVo> tigerlist=new ArrayList<StockTigerVo>();
					Element divelement= doc.getElementById("cont1");
					if(doc.toString().indexOf("您访问的页面不存在")>-1){
						continue;
					}
					Element buytable=divelement.getElementsByAttributeValue("class", "tab2").get(0);
					extractPaserStockTiger(id, date, code, buytable,"1",tigerlist);
					Element selltable=divelement.getElementsByAttributeValue("class", "tab2").get(1);
					extractPaserStockTiger(id, date, code, selltable,"2",tigerlist);
					stockProjectDao.insertStockTiger(tigerlist);
				}
			} catch (Exception e) {
				CrawlerWrongVo wrongvo=CommonUtil.getCrawlerWrongVo(url, "2", "龙虎榜股票单页爬取错误");
				stockProjectDao.insertCrawlerWrong(wrongvo);
				e.printStackTrace();
				return 0;
			}
		}
		
		return 1;
		
		
	}
	private void extractPaserStockTiger(String id, String date, String code,
			Element buytable,String flag,List<StockTigerVo> tigerlist) {
		Element tbody=buytable.getElementsByTag("tbody").get(0);
		Elements trs=tbody.getElementsByTag("tr");
		for(Element tr:trs){
			StockTigerVo tigervo=new StockTigerVo();
			tigervo.setStock_date_id(id);
			tigervo.setDate(date);
			tigervo.setCode(code);
			tigervo.setFlag(flag);
			Elements tds=tr.getElementsByTag("td");
			if(tr.toString().indexOf("总合计")>-1){
				continue;
			}
			for(int i=0;i<tds.size();i++){
				Element td=tds.get(i);
				String tdcontent=td.text().replace("%", "");
				if(tdcontent.contains("-")){
					tdcontent = "0";
				}
				if(i==0){
					tigervo.setNum(Integer.parseInt(tdcontent));
				}else if(i==1){
					tigervo.setTradename(tdcontent);
				}else if(i==2){
					tigervo.setTradebuymoney(Double.parseDouble(tdcontent));;
				}else if(i==3){
					tigervo.setTradebuyper(Double.parseDouble(tdcontent));
				}else if(i==4){
					tigervo.setTradesellmoney(Double.parseDouble(tdcontent));
				}else if(i==5){
					tigervo.setTradesellper(Double.parseDouble(tdcontent));
				}else if(i==6){
					tigervo.setDiff(Double.parseDouble(tdcontent));
				}
			}
			tigerlist.add(tigervo);
		}
	}
	
	@Override
	public void fetchStockTigerWrongDate(HashMap<String, String> paramap) {
		paramap.put("type", "1"); 
		List<CrawlerWrongVo> wronglist=stockProjectDao.getCrawlerWrongList(paramap);
		if(wronglist!=null && wronglist.size()>0 ){
			stockProjectDao.deleteCrawlerWrong(paramap);
			try {
				int i=this.fetchStockTigerDate(paramap);
				if(i==0){
					fetchStockTigerWrongDate(paramap);
				}
				this.fetchStockTiger(paramap);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("type1没有错误");
		}
		
	}
	
	@Override
	public void fetchStockTigerWrong(HashMap<String, String> paramap) {

		paramap.put("type", "2"); 
		List<CrawlerWrongVo> wronglist=stockProjectDao.getCrawlerWrongList(paramap);
		if(wronglist!=null && wronglist.size()>0 ){
			stockProjectDao.deleteCrawlerWrong(paramap);
			try {
				int i=this.fetchStockTiger(paramap);
				if(i==0){
					fetchStockTigerWrong(paramap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("type2没有错误");
		}
		
	
		
	}
	
	
	
	@Override
	public Map getStockTigerFormalMaxDate(HashMap<String, String> paraMap) {
		
		return stockProjectDao.getStockTigerFormalMaxDate(paraMap);
	}
	@Override
	public void removeRepeatData(HashMap<String, String> paraMap) {
		stockProjectDao.removeRepeatData(paraMap);
		
	}
	@Override
	public List<StockData> getStockBase(HashMap<String, String> hashMap) {
		List<StockData> list=stockProjectDao.getStockBase(hashMap);
		return list;
	}
	@Override
	//获取 code 在 date 后  num天的信息  包括date当天的信息
	public List<StockData> getStockDataInDay(String code, String date, int num) {
		HashMap<String, String> hashMap=new HashMap<String, String>();
		hashMap.put("code", code);
		List<StockData> list =getStockBase(hashMap);
		List<StockData> returnlist =new ArrayList<StockData>();
		int count=-1;
		for(StockData stockData:list){
			String tmpdate=stockData.getDate();
			if(tmpdate.equals(date)){
				returnlist.add(stockData);
				count=0;
			}else if(count<num && count!=-1 ){
				count++;
				returnlist.add(stockData);
			}
		}
		return returnlist;
	}
	@Override
	public List<StockRealTimeData> getRTlist(HashMap<String, String> paraMap) {
		 String paracode ="sh600030";
		 SinaStockUtil sinautil=new SinaStockUtil();
		 List<StockRealTimeData> list1=sinautil.getRealTimeDataHasLast(paracode,null);
		 Map<String, StockRealTimeData> refMap =new HashMap<String, StockRealTimeData>();
		 for(StockRealTimeData realTimeData :list1){
			 String code=realTimeData.getCode();
			 refMap.put(code, realTimeData);
		 }
			 List<StockRealTimeData> list=sinautil.getRealTimeDataHasLast(paracode,refMap);
			 for(StockRealTimeData realTimeData :list){
				 String code=realTimeData.getCode();
				 refMap.put(code, realTimeData);
				 if(code.equals("600030"))
				 CommonUtil.showStockRealTimeData(realTimeData);
			 }
			 System.out.println();
			 try {
				Thread.sleep(3000l);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return list1;
	}
	
	

	

	

}
