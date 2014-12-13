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
public class RoundRobinScheduling {

        private List<Server> serverList; // 服务器集合
        
        public void setServerList(List<Server> serverList) {
                this.serverList = serverList;
        }
        

        
        public Server getBestServer(int i) {
        	Server server =serverList.get(i%serverList.size());
        	 return server;
        }


		public void init() {
                Server s1 = new Server("192.168.0.100", 0);// 3
                Server s2 = new Server("192.168.0.101", 0);// 2
                Server s3 = new Server("192.168.0.102", 0);// 6
                Server s4 = new Server("192.168.0.103", 0);// 4
                Server s5 = new Server("192.168.0.104", 0);// 1
                Server s6 = new Server("192.168.0.105", 0);// 0
                Server s7 = new Server("192.168.0.106", 0);// 0
                Server s8 = new Server("192.168.0.107", 0);// 0
                Server s9 = new Server("192.168.0.108", 0);// 0
                List<Server> sl = new ArrayList<Server>();
                sl.add(s1);
                sl.add(s2);
                sl.add(s3);
                sl.add(s4);
                sl.add(s5);
                sl.add(s6);
                sl.add(s7);
                sl.add(s8);
                sl.add(s9);
                
                setServerList(sl);
        }

        public Server getServer(int i) {
                if (i < serverList.size()) {
                        return serverList.get(i);
                }
                return null;
        }

        public static void main(String[] args) {
        	RoundRobinScheduling obj = new RoundRobinScheduling();
                obj.init();
                
                Map<String, Integer> countResult = new HashMap<String, Integer>();
                for (int i = 0; i < 1000; i++) {
                        Server s = obj.getBestServer(i);
                        String log = "ip:" + s.ip + ";weight:" + s.weight;
                        if (countResult.containsKey(log)) {
                                countResult.put(log, countResult.get(log) + 1);
                        } else {
                                countResult.put(log, 1);
                        }
                }
                for (Entry<String, Integer> map : countResult.entrySet()) {
                        System.out.println("服务器 " + map.getKey() + " 请求次数： " + map.getValue());
                }
        }
}


