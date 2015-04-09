package com.stock.ctrl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;











import com.stock.service.StockAlertService;
import com.stock.service.StockGetDataService;
import com.stock.util.JsonUtil;
import com.stock.vo.StockAlertVo;
import com.stock.vo.StockRealTimeData;



@Controller
@RequestMapping("/stockrt")
public class StockRTController extends MultiActionController{

	 @Autowired(required = false)
	  @Qualifier("stockGetDataService")
	private StockGetDataService stockGetDataService;
	 
	 @Autowired(required = false)
	  @Qualifier("stockAlertService")
	private StockAlertService stockAlertService;
	 
	 
	 
	 
	@RequestMapping(params = "method=getStockRT")
	public ModelAndView getStockRT(HttpServletRequest request,
			HttpServletResponse response) {

		HashMap<String, String> paraMap = new HashMap<String, String>();
		try {
			List<StockRealTimeData> rtlist=stockGetDataService.getRTlist(paraMap);
			 request.setCharacterEncoding("UTF-8");
		     response.setContentType("text/xml;charset=utf-8");
		
			JsonUtil.outputJson(rtlist, response);
		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		

		return null;
	}
	 
		@RequestMapping(params = "method=getStockRTInt")
		public ModelAndView getStockRTInt(HttpServletRequest request,
				HttpServletResponse response) {

			HashMap<String, String> paraMap = new HashMap<String, String>();
			try {
			} catch (Exception e) {

				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return new ModelAndView("stock/stockRT");
		}
		
		@RequestMapping(params = "method=alterManager")
		public ModelAndView alterManager(HttpServletRequest request,
				HttpServletResponse response) {

			HashMap<String, String> paraMap = new HashMap<String, String>();
			try {
			} catch (Exception e) {

				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return new ModelAndView("stock/alterManager");
		}
		
		@RequestMapping(params = "method=addAlert")
		public ModelAndView addAlert(HttpServletRequest request,
				HttpServletResponse response) {

			HashMap<String, String> paraMap = new HashMap<String, String>();
			try {
			} catch (Exception e) {

				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return new ModelAndView("stock/addAlert");
		}
		
		@RequestMapping(params = "method=saveAlert")
		public ModelAndView saveAlert(HttpServletRequest request,
				HttpServletResponse response,StockAlertVo alertvo) {

			HashMap<String, String> paraMap = new HashMap<String, String>();
			try {
				stockAlertService.addAlert(alertvo);
			} catch (Exception e) {
				
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return new ModelAndView("stock/alterManager");
		}
		
		
		
		
	 


}
