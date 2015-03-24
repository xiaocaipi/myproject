package com.stock.util;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.stock.service.StockGetDataService;

public class FetchStockDataThread implements Runnable{

	StockGetDataService stockService;
	HashMap<String, String>paraMap;
	public FetchStockDataThread(HashMap<String, String> paraMap,StockGetDataService stockService){
		this.paraMap=paraMap;
		this.stockService=stockService;
	}
	@Override
	public void run() {
		System.out.println(111);
		stockService.fetchData(paraMap);
		System.out.println(222);
	}

	
	
}
