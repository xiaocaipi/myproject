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

import com.stock.dao.StockAlertDao;
import com.stock.dao.StockProjectDao;
import com.stock.service.StockAlertService;
import com.stock.service.StockAnalysisService;
import com.stock.service.StockAutoService;
import com.stock.service.StockGetDataService;
import com.stock.util.CommonUtil;
import com.stock.util.GetStockCode;
import com.stock.util.NetUtil;
import com.stock.util.SohuStockUtil;
import com.stock.util.TigerUtil;
import com.stock.util.yahoointerface.GetDataFromYahooUtil;
import com.stock.vo.CrawlerWrongVo;
import com.stock.vo.StockAlertVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerDateVo;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;

@Service("stockAlertService")
public class StockAlertServiceImpl implements StockAlertService {

	 @Autowired(required = false)
	  @Qualifier("stockProjectDao")
	private StockProjectDao stockProjectDao;
	 
	 @Autowired(required = false)
	  @Qualifier("stockGetDataService")
	private StockGetDataService stockGetDataService;
	 
	 @Autowired(required = false)
	  @Qualifier("stockAnalysisService")
	private StockAnalysisService stockAnalysisService;
	 
	 @Autowired(required = false)
	  @Qualifier("stockAlertDao")
	private StockAlertDao stockAlertDao;

	@Override
	public void addAlert(StockAlertVo alertvo) {
		stockAlertDao.addAlert(alertvo);
	}


}
