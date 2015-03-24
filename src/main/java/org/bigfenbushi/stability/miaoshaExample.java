package org.bigfenbushi.stability;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mysql.jdbc.Statement;

public class miaoshaExample {
	
	public static final String url = "jdbc:mysql://192.168.1.30/dianshang";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = ""; 
    
    public static void main(String[] args) throws Exception {
    	 Class.forName(name);
    	 Connection conn =DriverManager.getConnection(url, user, password);
    	 conn.setAutoCommit(false);
    	 Statement stat = (Statement) conn.createStatement();
    	 CountDownLatch countDown = new CountDownLatch(1000);
    	 ExecutorService ex = Executors.newFixedThreadPool(1000);
    	 //启动1000个线程去秒杀
    	 for(int i=0;i<1000;i++){
    		 ex.execute(new Job(stat, countDown, conn));
    	 }
    	 countDown.await();
    	 
    	 ex.shutdown();
    	 
	}
    
    static class Job implements Runnable{
		private CountDownLatch countDown;
		private Statement stat;
		private Connection conn;
		public Job(Statement stat,CountDownLatch countDown,Connection conn){
			this.stat = stat;
			this.countDown = countDown;
			this.conn = conn;
		}
		@Override
		public void run() {
			
			try {
				 //下一次单  插入订单表一条记录
				 int insertOrder = stat.executeUpdate("insert into `order` (itemid,time)  value ('111','2015-03-01')");
				 //更新库存表  这里规定每个 订单只能买一件东西，初始值是1000
		    	 int updateItem = stat.executeUpdate("update item set num = num -1 where item = 111");
		    	 if(insertOrder>0 && updateItem>0 ){
		    		 conn.commit();
		    	 }else{
		    		 conn.rollback();
		    	 }
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
			countDown.countDown();
		}
	}

}
