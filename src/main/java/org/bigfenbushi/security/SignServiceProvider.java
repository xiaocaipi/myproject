package org.bigfenbushi.security;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
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
public class SignServiceProvider extends HttpServlet {
	
	private String consuerPrivateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAzhTS2qwqx2tvY610omvw2nUxOQ5tnjxRuddOszVm8GXjpGyasMNQNEneM+kIWtLZ9MIks5YEFVxUliERqsXgWQIDAQABAkB9qSDxii4Dr7UaWLhYGe2hp/g9zFh3Nly3L1yj1hpQ1xffl/oKj/jnuQ8sfJbZwEzdI4ANcWqoyV9RVLPTyNsBAiEA7189i/uh9t6VuCed0zgWoF/yzhnd2ROZamegNkUX2SkCIQDcZZLuQDgYDwgw03Kb9sg52pu5JUkppcHJYMPvIztDsQIgfi2kEc/41lsKbOJlLVvZgXxSTxYPfUf5jL9HEpRlN9ECIEencmSpVlNkF7qgFEPmsOQ2UaiZGbdDn8RUBH+sCamRAiADWxuxvXzmk//0SANwfosxe7dCCgJkJ6jDIC42HH9SwA==";
	
	private String providerPublicKey ="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM4U0tqsKsdrb2OtdKJr8Np1MTkObZ48UbnXTrM1ZvBl46RsmrDDUDRJ3jPpCFrS2fTCJLOWBBVcVJYhEarF4FkCAwEAAQ==";
	
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
			digest = getSign(json);
		} catch (Exception e) {
			// TODO: handle exception
		}
		resp.setHeader("digest", digest);
		resp.getWriter().write(json);
		
		
	}
	
	
	private boolean validate(Map params,String digest) throws Exception{
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
		
		String base64Str = byte2base64(getMd5(keyvalueStr));
		PublicKey publicKey = string2Publickey(providerPublicKey);
		byte [] decryptBytes = publicDecrypt(base642byte(digest), publicKey);
		String decryptDigest= byte2base64(decryptBytes);
		if(base64Str.equals(decryptDigest)){
			return true;
		}else{
			return false;
		}
		
	}
	
	private String getSign(String content) throws Exception{
		PrivateKey privateKey = string2Privatekey(consuerPrivateKey);
		Signature signature = Signature.getInstance("Md5withRSA");
		signature.initSign(privateKey);
		signature.update(content.getBytes());
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
	public static PrivateKey string2Privatekey(String privateStr) throws Exception{
		byte [] keypairByte  = base642byte(privateStr);
		PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(keypairByte);
		KeyFactory factory= KeyFactory.getInstance("RSA");
		return factory.generatePrivate(keyspec);
	}
	
	public static PublicKey string2Publickey(String pubstr) throws Exception{
		byte [] keypairByte  = base642byte(pubstr);
		X509EncodedKeySpec keyspec = new X509EncodedKeySpec(keypairByte);
		KeyFactory factory= KeyFactory.getInstance("RSA");
		return factory.generatePublic(keyspec);
	}
	
	public static byte[] publicDecrypt(byte [] content,PublicKey publicKey) throws Exception{
		Cipher cipher =Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte [] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	public static byte[] privateEncrypt(byte [] content,PrivateKey privateKey) throws Exception{
		Cipher cipher =Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte [] bytes = cipher.doFinal(content);
		return bytes;
	}
}
