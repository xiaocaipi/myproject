package com.stock.service;


import java.util.HashMap;
import java.util.Map;

public interface StockAutoService {
	void autoStockTiger(HashMap<String, String> paraMap)throws Exception;

	void autoFetchBaseStock(HashMap<String, String> hashMap);
}
