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

import net.sf.json.util.JSONUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.thoughtworks.xstream.core.util.Base64Encoder;

//服务消费者
public class DigestServiceConsumer extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//参数  服务消费者发起请求的参数
		String service = "com.http.sayhello";
		String format = "json";
		String arg1 = "hello";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", service);
		params.put("format", format);
		params.put("arg1", arg1);
		String digest = "";
		try {
			digest = this.getDigest(params);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String url = "http://localhost:8080//myproject/digestServiceProvider?"+"service="+service +"&format="+format+"&arg1="+arg1;
		
		//组装请求
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("digest",digest);

		//接受响应
		HttpResponse response = httpClient.execute(httpGet);
		
		HttpEntity entity = response.getEntity();
		byte [] bytes = EntityUtils.toByteArray(entity);
		String jsonresult = new String(bytes);
		
		//从header 里面接受摘要
		String serverResponseDigest = response.getLastHeader("digest").getValue();
		boolean validateResult = false;
		try {
			validateResult = validate(jsonresult,serverResponseDigest);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(validateResult){
			JsonResult result = (JsonResult)JsonUtil.jsonToObject(jsonresult, JsonResult.class);
			resp.getWriter().write(result.getResult().toString());
		}
	}
	
	
	private boolean validate(String responseContent,String digest) throws Exception{
		String secret = "abcdefjhijklmn";
		byte [] bytes = getMd5(responseContent + secret);
		String responseDigest = byte2base64(bytes);
		if(responseDigest.endsWith(digest)){
			return true;
		}else{
			return false;
		}
		
	}
	
	private String getDigest(Map<String, String> params) throws Exception{
		String secret = "abcdefjhijklmn";
		Set<String> keySet = params.keySet();
		//使用treeset
		TreeSet<String> sortSet = new TreeSet<String>();
		sortSet.addAll(keySet);
		String keyvalueStr = "";
		Iterator<String> it = sortSet.iterator();
		while(it.hasNext()){
			String key = it.next();
			String value= params.get(key);
			keyvalueStr += key+value;
		}
		keyvalueStr += secret;
		String base64Str = byte2base64(getMd5(keyvalueStr));
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
