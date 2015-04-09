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

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.stock.dao.ProxyDao;
import com.stock.dao.StockProjectDao;
import com.stock.service.ProxyService;
import com.stock.service.StockGetDataService;
import com.stock.util.GetStockCode;
import com.stock.util.NetUtil;
import com.stock.util.TigerUtil;
import com.stock.util.yahoointerface.GetDataFromYahooUtil;
import com.stock.vo.ProxyVo;
import com.stock.vo.StockCode_BaseVo;
import com.stock.vo.StockData;
import com.stock.vo.StockTigerVo;
import com.stock.vo.Stock_StrongTigerVo;

@Service("proxyService")
public class ProxyServiceImpl implements ProxyService {

	 @Autowired(required = false)
	  @Qualifier("proxyDao")
	private ProxyDao proxyDao;

	@Override
	public void crawlerproxyinxici(HashMap<String, Object> hashMap) {
		Document doc=null;
		List<ProxyVo> proxylist=new ArrayList<ProxyVo>();
		try {
			doc=NetUtil.goFetch("http://www.xici.net.co/", doc, hashMap);
			Element ip_list=doc.getElementById("ip_list");
			Elements alltr= ip_list.getElementsByTag("tr");
			for(Element trdata:alltr){
				ProxyVo proxyvo=new ProxyVo();
				Elements alltd =trdata.getElementsByTag("td");
				for(int i=0;i<alltd.size();i++){
					Element td=alltd.get(i);
					if(i==0){
						String country=td.toString().indexOf("Cn")>-1?"cn":"other";
						proxyvo.setCountry(country);
					}else if(i==1){
						String ip=td.text();
						proxyvo.setIp(ip);
					}else if(i==2){
						String port=td.text();
						proxyvo.setPort(port);
					}else if(i==3){
						String proxyposition=td.text();
						proxyvo.setProxyposition(proxyposition);
					}
				}
				if(StringUtils.isNotEmpty(proxyvo.getIp())){
					proxylist.add(proxyvo);
				}
				
			}
			proxyDao.insertProxy(proxylist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testProxy(HashMap<String, Object> hashMap) {
		
		
		
		List<ProxyVo>proxylist=proxyDao.getProxy(hashMap);
		for(ProxyVo vo :proxylist){
			try {
				hashMap.put("proxy", vo);
				Document doc=null;
				String id=vo.getId();
				hashMap.put("timeout", "20000");
				doc=NetUtil.goFetch("http://esf.fang.com/housing/", doc, hashMap);
				if(doc==null){
					vo.setStatus("-1");
					System.out.println(id+"是无效的ip");
				}else{
					vo.setStatus("1");
					System.out.println(id+"是可以的ip");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		proxyDao.updateProxy(proxylist);
		
	}
	
	

}
