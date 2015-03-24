package org.bigfenbushi.base;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class TestRedis {

	public static void main(String[] args) {
		Jedis redis = new Jedis("192.168.1.11",6379);
		System.out.println("-------------keys----------");
		Set<String>keys = redis.keys("name*");//取以name开始的key,这个memcached很难做到
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			System.out.println(key);
		}
		System.out.println("-------------keys----------");
		
		
		System.out.println("-------------String----------");
		redis.set("name", "menu");//设置key-value
		redis.setex("content", 5, "hello");//设置有效期5秒
		redis.mset("class","a","age","22");//一次设置多个key-value
		redis.append("content", "lucy");//给字符串追加内容
		String content = redis.get("content");
		System.out.println("content:   "+content);
		redis.del("content");
		content = redis.get("content");
		System.out.println("content:   "+content);
//		List<String> list = redis.mget("class","age");//一次取多个key
//		for(String tmpcontent:list){
//			System.out.println(tmpcontent);
//		}
		System.out.println("-------------String----------");
		
		
	}
}
