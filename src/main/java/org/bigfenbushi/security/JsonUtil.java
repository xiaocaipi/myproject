package org.bigfenbushi.security;

import net.sf.json.JSONObject;

public class JsonUtil {
	   public static Object jsonToObject(String json, Class class1) {
	        try {
	            return JSONObject.toBean(JSONObject.fromObject(json), class1);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	   
	   public static String getJson(Object obj){
		   return JSONObject.fromObject(obj).toString();
	   }
}
