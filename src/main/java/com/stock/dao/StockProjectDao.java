package com.stock.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;


public interface StockProjectDao {


	void insertStockCode(List<StockCode_BaseVo> list);

	List<StockCode_BaseVo> getStockCode(HashMap<String, String> paraMap);

	 void insertStrocBase(List<StockData> stockList);

	void insertStockTiger(List<StockTigerVo> tigerList);


	List<Map> getStrongTigerWithTime(HashMap<String, String> hashMap);

	Map getStockTigerFormalMaxDate(HashMap<String, String> paraMap);

	void removeRepeatData(HashMap<String, String> paraMap);

	void insertCrawlerWrong(CrawlerWrongVo wrongvo);

	void insertStockTigerDate(List<StockTigerDateVo> tigerdatelist);

	List<StockTigerDateVo> getStockTigerDate(HashMap<String, String> paramap);

	int getStockTigerDateCount(HashMap<String, String> paraMap);

	List<CrawlerWrongVo> getCrawlerWrongList(HashMap<String, String> paramap);

	void deleteCrawlerWrong(HashMap<String, String> paramap);

	List<Map> getStockTiger3(HashMap<String, String> hashMap);

	List<StockData> getStockBase(HashMap<String, String> hashMap);

	void deleteStockCode(HashMap<String, String> hashMap);

	

}
