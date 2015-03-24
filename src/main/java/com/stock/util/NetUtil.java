package com.stock.util;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.stock.vo.ProxyVo;

public class NetUtil {

	
	public static Document goFetch(String districtUrl, Document doc,HashMap<String, Object> paraMap) throws Exception {
		ProxyVo proxyvo=(ProxyVo)paraMap.get("proxy");
		String timeout=(String)paraMap.get("timeout");
		if(timeout==null){
			timeout="2000";
		}
		int i=Math.abs((int)Math.round(Math.random()*10));
		
		if(proxyvo!=null){
			String ip=proxyvo.getIp();
			String port=proxyvo.getPort();
			System.setProperty("http.proxyHost",ip);
			System.setProperty("http.proxyPort", port);
		}
		
		try {
			doc = Jsoup.connect(districtUrl).timeout(Integer.parseInt(timeout)).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36").get() ;
		} catch (IOException e) {
			System.out.println("获取网页--"+districtUrl+"失败"+"失败的ip---"+ i);
		}
		Thread.sleep(1000);
		return doc;
	}
}
