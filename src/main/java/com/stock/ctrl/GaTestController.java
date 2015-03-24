package com.stock.ctrl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

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
@RequestMapping("/gatest")
public class GaTestController extends MultiActionController {

	@Autowired(required = false)
	@Qualifier("stockService")
	private StockGetDataService stockService;


	/**
	 * 创建索引测试用
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(params = "method=getPara")
	public ModelAndView fetchData(HttpServletRequest request,
			HttpServletResponse response) {

		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = (String) enu.nextElement();
			System.out
					.println(paraName + ": " + request.getParameter(paraName));
		}
		try {

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

}
