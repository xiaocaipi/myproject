package com.stock.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stock.vo.StockData;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SohuStockUtil {
	public static void main(String args[]) {
		List<StockData> list=getStockData("002727", "20140917", "20140918", false,"一心堂");
		System.out.println(list.size());
		
	}
	//时间是前闭后闭
	public static List<StockData> getStockData(String code,String starttime,String endtime,boolean iszhishu,String name){

		URL ur = null;
		BufferedReader reader = null;
		List<StockData> returnlist=new ArrayList<StockData>();
		String url="http://q.stock.sohu.com/hisHq?code=cn_"+code+"&start="+starttime+"&end="+endtime+"&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp";
		if(iszhishu){
			 url="http://q.stock.sohu.com/hisHq?code=zs_"+code+"&start="+starttime+"&end="+endtime+"&stat=1&order=D&period=d&callback=historySearchHandler&rt=jsonp";
		}
		try {
			ur = new URL(url);
			HttpURLConnection uc = (HttpURLConnection) ur.openConnection();
			reader = new BufferedReader(new InputStreamReader(ur.openStream(),
					"GBK"));
			String line;
			while ((line = reader.readLine()) != null) {
				if(line.indexOf("hq")==-1){
					continue;
				}
				line=line.replaceAll("historySearchHandler", "");
				line=line.replace("([", "");
				line=line.replace("])", "");
				List list= JSONArray.toList(JSONObject.fromObject(line).getJSONArray("hq"));
				for(int i=0;i<list.size();i++){
					ArrayList tmp=(ArrayList)list.get(i);
					 StockData stockdatavo=new StockData();
					 stockdatavo.setCode(code);
					 stockdatavo.setName(name);
					for(int j=0;j<tmp.size();j++){
						 String tmp2=(String)tmp.get(j);
						 tmp2=tmp2.replace("%", "");
						if(j==0){
							stockdatavo.setDate(tmp2);
						}if(j==1){
							stockdatavo.setOpen(Double.parseDouble(tmp2));
						}if(j==2){
							stockdatavo.setClose(Double.parseDouble(tmp2));
						}if(j==3){
							stockdatavo.setZhangdie(Double.parseDouble(tmp2));
						}if(j==4){
							stockdatavo.setZhangdiefudu(Double.parseDouble(tmp2));
						}if(j==5){
							stockdatavo.setLow(Double.parseDouble(tmp2));
						}if(j==6){
							stockdatavo.setHigh(Double.parseDouble(tmp2));
						}if(j==7){
							stockdatavo.setVolume(Double.parseDouble(tmp2));
						}if(j==8){
							stockdatavo.setDealmoney(Double.parseDouble(tmp2));
						}
						if(j==9){
							if(iszhishu){
								stockdatavo.setHuanshoulv(0);
							}else{
								stockdatavo.setHuanshoulv(Double.parseDouble(tmp2));
							}
							
						}
					}
					returnlist.add(stockdatavo);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnlist;
	}
}
