package com.stock.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.stock.dao.ProxyDao;
import com.stock.dao.StockProjectDao;
import com.stock.util.BaseDao;
import com.stock.vo.ProxyVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;


@Repository("proxyDao")
public class ProxyDaoImpl  extends BaseDao implements ProxyDao{

	
	private final static String NAME_SPACE = "SpringKms/src/com/lvmama/stock/dao/sql/ProxySql";


	@Override
	public void insertProxy(List<ProxyVo> proxylist) {
		this.insertObjectByBatch(NAME_SPACE + ".insertProxy",proxylist);
		
	}
//	@Override
//	public void insertStockCode(List<StockCode_BaseVo> list) {
//		this.insertObjectByBatch(NAME_SPACE + ".insertStockCode",list);
//		
//	}
//
//	@Override
//	public List<StockCode_BaseVo> getStockCode(HashMap<String, String> paraMap) {
//		
//		return this.getObjectForList(NAME_SPACE + ".getStockCode",paraMap);
//	}
//
//	@Override
//	public synchronized void insertStrocBase(List<StockData> stockList) {
//		this.insertObjectByBatch(NAME_SPACE + ".insertStrocBase",stockList);
//		
//	}
//
//	@Override
//	public void insertStockTiger(List<StockTigerVo> tigerList) {
//		this.insertObjectByBatch(NAME_SPACE + ".insertStockTiger",tigerList);
//		
//	}
//
//	@Override
//	public List<Map> getStrongTigerWithTime(HashMap<String, String> hashMap) {
//		return this.getObjectForList(NAME_SPACE + ".getStrongTigerWithTime",hashMap);
//	}
//
//	@Override
//	public Map getStockTigerFormalMaxDate(HashMap<String, String> paraMap) {
//		// TODO Auto-generated method stub
//		return (Map)this.getForObject(NAME_SPACE + ".getStockTigerFormalMaxDate",paraMap);
//	}
//
//	@Override
//	public void removeRepeatData(HashMap<String, String> paraMap) {
//		
//		this.removeObject(NAME_SPACE + ".removeRepeatData",paraMap);
//		
//	}


	@Override
	public List<ProxyVo> getProxy(HashMap<String, Object> hashMap) {
		// TODO Auto-generated method stub
		return this.getObjectForList(NAME_SPACE + ".getProxy",hashMap);
	}


	@Override
	public void updateProxy(List<ProxyVo> proxylist) {
		
		this.updateObjectByBatch(NAME_SPACE + ".updateProxy",proxylist);
		
	}


	

}
