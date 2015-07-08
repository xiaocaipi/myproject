package com.stock.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.stock.dao.StockProjectDao;
import com.stock.util.BaseDao;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;


@Repository("stockProjectDao")
public class StockProjectDaoImpl  extends BaseDao implements StockProjectDao{

	
	private final static String NAME_SPACE = "SpringKms/src/com/lvmama/stock/dao/sql/StockProjectSql";


	@Override
	public void insertStockCode(List<StockCode_BaseVo> list) {
		this.insertObjectByBatch(NAME_SPACE + ".insertStockCode",list);
		
	}

	@Override
	public List<StockCode_BaseVo> getStockCode(HashMap<String, String> paraMap) {
		if(StringUtils.isEmpty(paraMap.get("start"))){
			paraMap.put("start","1");
			paraMap.put("end","10000000");
		}
		
		return this.getObjectForList(NAME_SPACE + ".getStockCode",paraMap);
	}

	@Override
	public synchronized void insertStrocBase(List<StockData> stockList) {
		this.insertObjectByBatch(NAME_SPACE + ".insertStrocBase",stockList);
		
	}

	@Override
	public void insertStockTiger(List<StockTigerVo> tigerList) {
		this.insertObjectByBatch(NAME_SPACE + ".insertStockTiger",tigerList);
		
	}

	@Override
	public List<Map> getStrongTigerWithTime(HashMap<String, String> hashMap) {
		return this.getObjectForList(NAME_SPACE + ".getStrongTigerWithTime",hashMap);
	}

	@Override
	public Map getStockTigerFormalMaxDate(HashMap<String, String> paraMap) {
		// TODO Auto-generated method stub
		return (Map)this.getForObject(NAME_SPACE + ".getStockTigerFormalMaxDate",paraMap);
	}

	@Override
	public void removeRepeatData(HashMap<String, String> paraMap) {
		
		this.removeObject(NAME_SPACE + ".removeRepeatData",paraMap);
		
	}

	@Override
	public void insertCrawlerWrong(CrawlerWrongVo wrongvo) {
		this.insertObject(NAME_SPACE + ".insertCrawlerWrong",wrongvo);
		
	}

	@Override
	public void insertStockTigerDate(List<StockTigerDateVo> tigerdatelist) {
		this.insertObjectByBatch(NAME_SPACE + ".insertStockTigerDate",tigerdatelist);
		
	}

	@Override
	public List<StockTigerDateVo> getStockTigerDate(
			HashMap<String, String> paramap) {
		return this.getObjectForList(NAME_SPACE + ".getStockTigerDate",paramap);
	}

	@Override
	public int getStockTigerDateCount(HashMap<String, String> paraMap) {
		// TODO Auto-generated method stub
		return (Integer)this.getForObject(NAME_SPACE + ".getStockTigerDateCount",paraMap);
	}

	@Override
	public List<CrawlerWrongVo> getCrawlerWrongList(
			HashMap<String, String> paramap) {
		return this.getObjectForList(NAME_SPACE + ".getCrawlerWrongList",paramap);
	}

	@Override
	public void deleteCrawlerWrong(HashMap<String, String> paramap) {
		
		this.removeObject(NAME_SPACE + ".deleteCrawlerWrong",paramap);
		
	}

	@Override
	public List<Map> getStockTiger3(HashMap<String, String> hashMap) {
		// TODO Auto-generated method stub
		return this.getObjectForList(NAME_SPACE + ".getStockTiger3",hashMap);
	}

	@Override
	public List<StockData> getStockBase(HashMap<String, String> hashMap) {
		// TODO Auto-generated method stub
		return this.getObjectForList(NAME_SPACE + ".getStockBase",hashMap);
	}

	@Override
	public void deleteStockCode(HashMap<String, String> hashMap) {
		
		this.removeObject(NAME_SPACE + ".deleteStockCode",hashMap);
		
	}


	
	
//
//	@Override
//	public MyWisdomStatisticsDto statis(MyWisdomStatisticsDto myWisdomStatisticsDto) {
//		
//		return (MyWisdomStatisticsDto)getForObject(NAME_SPACE+".statis", myWisdomStatisticsDto);
//
//	}
	

}
