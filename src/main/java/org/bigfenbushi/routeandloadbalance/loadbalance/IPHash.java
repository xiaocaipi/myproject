package org.bigfenbushi.routeandloadbalance.loadbalance;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bigfenbushi.routeandloadbalance.Server;

/**
 * 权重随机调度
 * 
 * @author Andy
 * 
 */
public class IPHash {

        private List<Server> serverList; // 服务器集合
        
        private static List<Integer> clientIpList;
        
        Map<Integer, Server> map =new HashMap<Integer, Server>();
        
        public void setServerList(List<Server> serverList) {
                this.serverList = serverList;
        }
        

        
        private Random random = new Random();
        public Server getBestServer(int clientip) {
        	Server server =null;
        	if(map.get(clientip) ==null){
        		server =serverList.get(random.nextInt(serverList.size()));
        		map.put(clientip, server);
        	}else{
        		server = map.get(clientip);
        	}
                return server;
        }


		public void init() {
                Server s1 = new Server("192.168.0.100", 0);// 3
                Server s2 = new Server("192.168.0.101", 0);// 2
                List<Server> sl = new ArrayList<Server>();
                sl.add(s1);
                sl.add(s2);
                setServerList(sl);
                clientIpList = new ArrayList<Integer>();
                clientIpList.add("0.0.0.1".hashCode());
                clientIpList.add("0.0.0.2".hashCode());
                clientIpList.add("0.0.0.3".hashCode());
                clientIpList.add("0.0.0.4".hashCode());
                
                
                
        }

        public Server getServer(int i) {
                if (i < serverList.size()) {
                        return serverList.get(i);
                }
                return null;
        }

        public static void main(String[] args) {
                IPHash obj = new IPHash();
                obj.init();
                
                List<String> logList = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                	for(int clientip :clientIpList){
                		Server s = obj.getBestServer(clientip);
                        String log = "ip:" + s.ip + ";client:" + clientip;
                        logList.add(log);
                	}
                        
                }
                for (String log :logList) {
                        System.out.println(log);
                }
        }
}


