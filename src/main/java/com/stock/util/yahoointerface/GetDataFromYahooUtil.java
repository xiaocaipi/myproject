package com.stock.util.yahoointerface;


/**
 * yahoo股票数据接口工具类
 * @author 祁丛生 2013.01.02
 * @version 1.0
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.stock.vo.StockData;

public class GetDataFromYahooUtil {

	public static final String YAHOO_FINANCE_URL = "http://table.finance.yahoo.com/table.csv?";
	public static final String YAHOO_FINANCE_URL_TODAY = "http://download.finance.yahoo.com/d/quotes.csv?";

	/**
	 * 根据 股票编码、开始日期、结束日期 获取股票数据
	 * 
	 * @author 祁丛生
	 * @param stockName
	 *            沪市：000000.ss 深市：000000.sz
	 * @param fromDate
	 *            开始日期
	 * @param toDate
	 *            结束日期
	 * @return List<StockData>
	 */
	public static List<StockData> getStockCsvData(String stockName,
			String fromDate, String toDate) {
		List<StockData> list = new ArrayList<StockData>();
		String[] datefromInfo = fromDate.split("-");
		String[] toDateInfo = toDate.split("-");
		String code = stockName.substring(0, 6);
		;

		String a = (Integer.valueOf(datefromInfo[1]) - 1) + "";// a – 起始时间，月
		String b = datefromInfo[2];// b – 起始时间，日
		String c = datefromInfo[0];// c – 起始时间，年
		String d = (Integer.valueOf(toDateInfo[1]) - 1) + "";// d – 结束时间，月
		String e = toDateInfo[2];// e – 结束时间，日
		String f = toDateInfo[0];// f – 结束时间，年

		String params = "&a=" + a + "&b=" + b + "&c=" + c + "&d=" + d + "&e="
				+ e + "&f=" + f;
		String url = YAHOO_FINANCE_URL + "s=" + stockName + params;

		URL MyURL = null;
		URLConnection con = null;
		InputStreamReader ins = null;
		BufferedReader in = null;
		try {
			MyURL = new URL(url);
			con = MyURL.openConnection();
			ins = new InputStreamReader(con.getInputStream(), "UTF-8");
			in = new BufferedReader(ins);

			String newLine = in.readLine();// 第一行为标题行，不取数据

			while ((newLine = in.readLine()) != null) {
				String stockInfo[] = newLine.trim().split(",");
				StockData sd = new StockData();
				sd.setCode(code);
				sd.setDate(stockInfo[0]);
				sd.setOpen(Float.valueOf(stockInfo[1]));
				sd.setHigh(Float.valueOf(stockInfo[2]));
				sd.setLow(Float.valueOf(stockInfo[3]));
				sd.setClose(Float.valueOf(stockInfo[4]));
				sd.setVolume(Float.valueOf(stockInfo[5]));
				sd.setAdj(Float.valueOf(stockInfo[6]));
				if(sd.getVolume()!=0){
					list.add(sd);
				}
				
			}

		} catch (Exception ex) {
			return null; // 无交易数据
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		}
		return list;
	}
	
	
	
	
	public static void main(String[] args){
		GetDataFromYahooUtil stockUtil = new GetDataFromYahooUtil();
		List<StockData> sd = stockUtil.getStockCsvData("002727.sz", "2014-09-17","2014-09-17");
		System.out.println(sd.size());
		}

}