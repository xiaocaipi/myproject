package com.stock.service;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stock.vo.StockData;
import com.stock.vo.StockRealTimeData;

public interface StockGetDataService {

	void fetchData(HashMap<String, String> paraMap);

	void insertStockConde();

	int fetchStockTigerDate(HashMap<String, String> paraMap)  throws InterruptedException;
	
	

	Map getStockTigerFormalMaxDate(HashMap<String, String> paraMap);

	void removeRepeatData(HashMap<String, String> paraMap);

	

	int fetchStockTiger(HashMap<String, String> paramap);

	void fetchStockTigerWrongDate(HashMap<String, String> paramap);

	void fetchStockTigerWrong(HashMap<String, String> paramap);

	List<StockData> getStockBase(HashMap<String, String> hashMap);

	List<StockData> getStockDataInDay(String tmpcode, String tmpdate, int i);

	List<StockRealTimeData> getRTlist(HashMap<String, String> paraMap);


	

	

}
