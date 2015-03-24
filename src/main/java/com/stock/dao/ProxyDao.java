package com.stock.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stock.vo.ProxyVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;


public interface ProxyDao {

	void insertProxy(List<ProxyVo> proxylist);

	List<ProxyVo> getProxy(HashMap<String, Object> hashMap);

	void updateProxy(List<ProxyVo> proxylist);



	

}
