package org.bigfenbushi.security;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.thoughtworks.xstream.core.util.Base64Encoder;

//服务提供方对摘要信息进行校验
public class DigestServiceProvider extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Map params = req.getParameterMap();
		String requestDigest = req.getHeader("digest");
		boolean validateResult = false;
		try {
			validateResult = validate(params,requestDigest);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(!validateResult){
			resp.getWriter().write("validate fail!");
			return;
		}
		//基本参数
		String servicename = req.getParameter("service");
		String format = req.getParameter("format");
		
		Map parammeters = req.getParameterMap();
		//生成json 结果
		JsonResult jsonresult = new JsonResult();
		jsonresult.setMessage("success");
		//这里不写service 直接返回map
		jsonresult.setResult(parammeters);
		jsonresult.setResultCode(200);
		String digest = "";
		String json = JsonUtil.getJson(jsonresult);
		try {
			digest = getDigest(json);
		} catch (Exception e) {
			// TODO: handle exception
		}
		resp.setHeader("digest", digest);
		resp.getWriter().write(json);
		
		
	}
	
	
	private boolean validate(Map params,String digest) throws Exception{
		String secret = "abcdefjhijklmn";
		Set<String> keySet = params.keySet();
		// 使用treeset
		TreeSet<String> sortSet = new TreeSet<String>();
		sortSet.addAll(keySet);
		String keyvalueStr = "";
		Iterator<String> it = sortSet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String [] values = (String[])params.get(key);
			keyvalueStr += key + values[0];
		}
		keyvalueStr += secret;
		String base64Str = byte2base64(getMd5(keyvalueStr));
		if(base64Str.equals(digest)){
			return true;
		}else{
			return false;
		}
		
	}
	
	private String getDigest(String content) throws Exception{
		String secret = "abcdefjhijklmn";
		content += secret;
		String base64Str = byte2base64(getMd5(content));
		return base64Str;
	}
	
	public static byte[] getMd5(String content) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bytes=md.digest(content.getBytes("utf-8"));
		return bytes;
	}
	
	private static String byte2base64(byte [] bytes){
		Base64Encoder base64Encoder = new Base64Encoder();
		return base64Encoder.encode(bytes);
	}
	
	private static byte [] base642byte(String base64){
		Base64Encoder base64Encoder = new Base64Encoder();
		return base64Encoder.decode(base64);
	}
}
