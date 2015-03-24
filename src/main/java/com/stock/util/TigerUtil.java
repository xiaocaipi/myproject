package com.stock.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.stock.vo.StockTigerVo;


public class TigerUtil {

	String searchtiger = "/Stock/lhb.*?html";
	
	String prefix="http://data.eastmoney.com";
	String searchtigersecond="(<td).*?(</td>)";
	
	public List<StockTigerVo>  getTigerSecondList(String secondUrl,String content,String stock_date_id) throws Exception{
		List<StockTigerVo> returnList=new ArrayList<StockTigerVo>();
		
			// 讲编译的正则表达式对象赋给pattern
			Pattern pattern = Pattern.compile(searchtigersecond);
			// 对字符串content执行正则表达式
			Matcher matcher = pattern.matcher(content);
			String tmpsearch=">.*?<";
			Pattern pattern2 = Pattern.compile(tmpsearch);
			
			//获得代码和时间
			String tmpurls[]=secondUrl.split(",");
			String date=tmpurls[1];
			String code=tmpurls[2].replace(".html", "");
			if(code.equals("000159")){
				System.out.println();
			}
			
			// 获得匹配字符串的时候
			int i=0;
			int j=0;
			int k=0;
//			一条记录
//	序号		1--<td width="39px;">1</td>
//	营业部名字		2--<td width="355px;" class="tdtext"><a href="/Stock/lhb/yyb/011956.html">宏源证券股份有限公司大连金马路证券营业部</a></td>
//	买入金额		3--<td width="120px;" class="tdnumber"><span class="red">581.33</span></td>
//	买入比例		4--<td width="93px;" class="tdnumber"><span>37.15%</span></td>
//	卖出金额		5--<td width="120px;" class="tdnumber"><span class="">0.00</span></td>
//	卖出笔记		6--<td width="93px;" class="tdnumber"><span>0.00%</span></td>
//	差		7--<td class="tdnumber"><span class="red">581.33</span></td>
			StockTigerVo vo =null;
			while (matcher.find()) {
				
				String tmp=matcher.group(0);
				Matcher tmpmatcher = pattern2.matcher(tmp);
				System.out.println(i+"--"+tmp);
					if(tmp.contains("39px")){
						i++;
						vo=new StockTigerVo();
						vo.setStock_date_id(stock_date_id);
						while(tmpmatcher.find()){
							String tmpnum=removeSome(tmpmatcher.group(0));
							if(StringUtils.isNotEmpty(tmpnum)){
								System.out.println(tmpnum);
								vo.setNum(Integer.parseInt(tmpnum));
								vo.setCode(code);
								vo.setDate(date);
							}
							
						}
					}
					if(tmp.contains("355px")){
						while(tmpmatcher.find()){
							String tmpnum=removeSome(tmpmatcher.group(0));
							if(StringUtils.isNotEmpty(tmpnum)){
								System.out.println(tmpnum);
								vo.setTradename(tmpnum);
							}
						}
					}
					if(tmp.contains("120px")){
						//第一次出现是买入，第二次是卖出
						j++;
						while(tmpmatcher.find()){
							String tmpnum=removeSome(tmpmatcher.group(0));
							if(StringUtils.isNotEmpty(tmpnum)){
								if(j%2==1){
									System.out.println(tmpnum);
									vo.setTradebuymoney(Double.parseDouble(tmpnum));
								}else{
									System.out.println(tmpnum);
									vo.setTradesellmoney(Double.parseDouble(tmpnum));
								}
								
							}
						}
					}
					if(tmp.contains("93px")){
						//第一次出现是买入，第二次是卖出
						k++;
						while(tmpmatcher.find()){
							String tmpnum=removeSome(tmpmatcher.group(0));
							if(StringUtils.isNotEmpty(tmpnum)){
								if(k%2==1){
									System.out.println(tmpnum);
									vo.setTradebuyper(Double.parseDouble(tmpnum));
								}else{
									System.out.println(tmpnum);
									vo.setTradesellper(Double.parseDouble(tmpnum));
								}
								
							}
						}
					}
					if((tmp.contains("green")||tmp.contains("red")) && !tmp.contains("width")){
						while(tmpmatcher.find()){
							String tmpnum=removeSome(tmpmatcher.group(0));
							if(StringUtils.isNotEmpty(tmpnum)){
								double diff=Double.parseDouble(tmpnum);
								System.out.println(tmpnum);
								if(vo!=null){
									vo.setDiff(Double.parseDouble(tmpnum));
								}else {
									System.out.println(111);
								}
								if(diff>0){
									vo.setFlag("1");
								}else{
									vo.setFlag("2");
								}
							}
						}
						returnList.add(vo);
					}
					if(tmp.contains("总合计")){
						break;
					}
				
			}
		
		
		return returnList;
	}
	
	
	private String removeSome(String group) {
		
		return group.replace("<", "").replace(">", "").replace("%", "");
	}

	/**
	 * 获取当前网页的code
	 * @param httpUrl
	 * @return
	 * @throws IOException
	 */
	public String getHtmlCode(String httpUrl) throws IOException {
		//定义字符串content
		String content = "";
		//生成传入的URL的对象
		URL url = new URL(httpUrl);
		//获得当前url的字节流（缓冲）
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream(),"gbk"));

		String input;
		//当当前行存在数据时
		while ((input = reader.readLine()) != null) {
			//将读取数据赋给content
			content += input;
		}
		//关闭缓冲区
		reader.close();
		//返回content
		return content;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException  {
		TigerUtil util=new TigerUtil();
		System.out.println(util.goFetch("http://data.eastmoney.com/Stock/lhb/2014-05-29.html"));
//		String aa="http://data.eastmoney.com/Stock/lhb,2014-02-22,000159.html";
//		List list=null;
//		try {
//			list = util.getTigerSecondList(aa);
//		} catch (IOException e) {
//			
//			e.printStackTrace();
//		}
//		String tmp2[]=aa.split(",");
//		System.out.println(tmp2[1]);
//		System.out.println(tmp2[2]);
//		System.out.println(list.size());
//		List<String> firstList=util.getTigerFirstList("http://data.eastmoney.com/Stock/lhb/2014-02-22.html");
//		System.out.println(firstList.size());
		
		
		
	}
	
	public String goFetch(String districtUrl) throws InterruptedException {
		Document doc=null;
		try {
			doc = Jsoup.connect(districtUrl).timeout(200000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36").get() ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread.sleep(1000);
		return doc.toString();
	}




}
