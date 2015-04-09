package com.stock.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.stock.dao.StockAlertDao;
import com.stock.dao.StockProjectDao;
import com.stock.util.BaseDao;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockAlertVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;


@Repository("stockAlertDao")
public class StockAlertDaoImpl  extends BaseDao implements StockAlertDao{

	
	private final static String NAME_SPACE = "SpringKms/src/com/lvmama/stock/dao/sql/stockalertsql";

	@Override
	public void addAlert(StockAlertVo alertvo) {
		 
		this.insertObject(NAME_SPACE+".addAlert", alertvo);
		
	}



	
	
//
//	@Override
//	public MyWisdomStatisticsDto statis(MyWisdomStatisticsDto myWisdomStatisticsDto) {
//		
//		return (MyWisdomStatisticsDto)getForObject(NAME_SPACE+".statis", myWisdomStatisticsDto);
//
//	}
	

}
