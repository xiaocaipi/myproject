package org.bigfenbushi.base;
import java.sql.ResultSet;  
import java.sql.SQLException;  
import java.util.Random;
public class fenbiaoQuery {
	static String sql = null;  
    static DBHelper db1 = null;  
    static ResultSet ret = null;  
	
	public static void main(String[] args) { 
		
		int tableNum=2;
		Random random = new Random();
		
		//插入数据  分了2个表 ,根据userid  分表插入
		for(int i=0;i<100;i++){
			String tableName = getTableName(tableNum, i);
			sql = "insert into "+tableName+"(user_id,auction_id) value ("+i+","+random.nextInt(100)+")";
			db1 = new DBHelper(sql);
			try {
				db1.pst.execute();
				db1.close();//关闭连接  
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//分表查询  
		int user_id = random.nextInt(99);
	    String queryTableName = getTableName(tableNum, user_id);
        sql = "select *from "+queryTableName+" where user_id ="+user_id;//SQL语句  
        db1 = new DBHelper(sql);//创建DBHelper对象  
  
        try {  
            ret = db1.pst.executeQuery();//执行语句，得到结果集  
            while (ret.next()) {  
                String user_id_tmp = ret.getString(2);  
                String auction_id_tmp = ret.getString(3);  
                System.out.println(user_id_tmp + "\t" + auction_id_tmp );  
            }//显示数据  
            ret.close();  
            db1.close();//关闭连接  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }

	private static String getTableName(int tableNum, int i) {
		String tableName ="order";
		int tablefeix=(i%tableNum)+1;
		tableName = tableName+tablefeix;
		return tableName;
	}  
}
