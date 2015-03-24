package com.stock.ctrl;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.stock.service.StockGetDataService;


@Controller
@RequestMapping("/stock")
public class StockBaseController extends MultiActionController{

	 @Autowired(required = false)
	  @Qualifier("stockService")
	private StockGetDataService stockService;
	 
	
	
	 

	 
	 
	
	 /**
		 *创建索引测试用
		 * @param request
		 * @param response
		 * @return
	 * @throws Exception 
		 */
	//包括开始 不包括结束timestart=2014-08-02&timeend=2014-08-03  只有 02那天
	 @RequestMapping(params = "method=fetchData")
	public ModelAndView fetchData(HttpServletRequest request,HttpServletResponse response){
		
		String tmptimestart=CommonTool.obj2String(request.getParameter("timestart"));
		String tmptimeend=CommonTool.obj2String(request.getParameter("timeend"));
		HashMap<String, String> paraMap=new HashMap<String, String>();
		try {
			
			paraMap.put("starttime", tmptimestart);
			paraMap.put("endtime", tmptimeend);
//			stockService.autoStockTiger(paraMap);
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	
		return null;
	}
	 
	 
	 @RequestMapping(params = "method=prepareDateOneKey")
		public ModelAndView prepareDateOneKey(HttpServletRequest request,HttpServletResponse response){
			
//			String tmptimestart=CommonTool.obj2String(request.getParameter("timestart"));
//			
			HashMap<String, String> paraMap=new HashMap<String, String>();
			try {
				Map map=stockService.getStockTigerFormalMaxDate(paraMap);
				Date maxdate=(Date)map.get("date");
				Calendar calendar = new GregorianCalendar();
				Date date=new Date();
				 SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
				 calendar.setTime(date);
				int start=-1;
				long num=CommonTool.caculateTimeBetween(maxdate, date);
				int end=start-(int)num+1;
						 
				 
				long time1=System.currentTimeMillis();
				
				
				for(int i=start;i>end;i--){
					calendar.setTime(date);
					calendar.add(calendar.DATE, i);
					String date2=fmt.format(calendar.getTime());
					paraMap.put("url", "http://data.eastmoney.com/Stock/lhb/"+date2+".html");
					stockService.fetchStockTigerDate(paraMap);
					System.out.println(date2);
				}
				//去除重复数据
				stockService.removeRepeatData(paraMap);
				//计算导出数据到目录 给大智慧使用
				CommonTool.formatFlie("G:\\project\\stock\\data\\data1.txt","",true);
				CommonTool.formatFlie("G:\\project\\stock\\data\\data2.txt","",true);
//				stockService.dealWithStockTiger1(new HashMap<String, String>());
//				stockService.dealWithStockTiger2(new HashMap<String, String>());
				long time2=System.currentTimeMillis();
				System.out.println("用时"+(time2-time1)/1000);
			} catch (Exception e) {
				
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		
			return null;
		}
	 
	 public static void formatFlie(String fileName, String context) { 
			File f = new File(fileName); 
			if (!f.exists()) { 
			try { 
			f.createNewFile(); 
			} catch (IOException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
			} 
			} 
			BufferedWriter output = null; 
			try { 
			output = new BufferedWriter(new OutputStreamWriter( 
			new FileOutputStream(fileName, true))); 
			output.write(context); 
			} catch (Exception e) { 
			e.printStackTrace(); 
			} finally { 
			try { 
			output.close(); 
			} catch (IOException e) { 
			e.printStackTrace(); 
			} 
			} 
			} 
	
	 
	 public static void main(String[] args) {

			
			String tmptimestart="2014-08-01";
			String tmptimeend="2014-08-05";
			try {
				Calendar calendar = new GregorianCalendar();
				Date date=new Date();
				 SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
				 calendar.setTime(date);
				 Date startdate=fmt.parse(tmptimestart);
				 Date enddate=fmt.parse(tmptimeend);
				 long num=CommonTool.caculateTimeBetween(startdate, enddate);
				 long time1=System.currentTimeMillis();
				
				
				for(int i=0;i<num;i++){
					calendar.setTime(startdate);
					calendar.add(calendar.DATE, i);
					String date2=fmt.format(calendar.getTime());
					System.out.println(date2);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		
		
	}

}
