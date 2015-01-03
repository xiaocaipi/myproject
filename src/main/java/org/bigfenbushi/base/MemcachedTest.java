package org.bigfenbushi.base;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedTest {

	static{
		 /**  
		   
	        * 初始化SockIOPool，管理memcached的连接池  
	   
	        * */  
	   
	       String[] servers = { "192.168.1.11:11211" };  
	   
	       SockIOPool pool = SockIOPool.getInstance();  
	   
	       pool.setServers(servers);  
	   
	       pool.setFailover(true);  
	   
	       pool.setInitConn(10);  
	   
	       pool.setMinConn(5);  
	   
	       pool.setMaxConn(250);  
	   
	       pool.setMaintSleep(30);  
	   
	       pool.setNagle(false);  
	   
	       pool.setSocketTO(3000);  
	   
	       pool.setAliveCheck(true);  
	   
	       pool.initialize();  
	}
	
	public static void main(String[] args) {  
		   
	       /**  
	   
	        * 建立MemcachedClient实例  
	   
	        * */  
	   
	       MemCachedClient memCachedClient = new MemCachedClient();  
	       memCachedClient.set("name", "menu");
	       Object value = memCachedClient.get("name");
	       System.out.println("first="+value);
	       memCachedClient.delete("name");
	       value = memCachedClient.get("name");
	       System.out.println("seconde="+value);
	   
	    }  
	   
	
}
