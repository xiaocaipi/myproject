package com.stock.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockAlertVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;


public interface StockAlertDao {

	void addAlert(StockAlertVo alertvo);


}
