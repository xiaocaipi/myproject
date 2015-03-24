package org.bigfenbushi.base;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestLucence2 {

	public static List<Map<String, String>> getData() throws Exception{
		List<Map<String, String>> returnList = new ArrayList<Map<String,String>>();
		String sql ="select c.order_num,c.event_time create_time,p.event_time pick_time from order_create c LEFT JOIN order_picket p on c.order_num = p.order_number";
		DBHelper db1 = new DBHelper(sql);
		ResultSet ret=null;
		 try {  
	            ret = db1.pst.executeQuery();//执行语句，得到结果集  
	            while (ret.next()) {  
	            	Map<String, String> map = new HashMap<String, String>();
	                String order_num = ret.getString(1);  
	                String create_time = ret.getString(2); 
	                String pick_time = ret.getString(3); 
	                System.out.println(order_num + "\t" + create_time+ "\t" + pick_time  );  
	                map.put("order_num", order_num);
	                map.put("create_time", create_time);
	                map.put("pick_time", pick_time);
	                returnList.add(map);
	            }//显示数据  
	            ret.close();  
	            db1.close();//关闭连接  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        } 
		 return returnList;
	}
	
	private static String indexPath = "G:\\project\\lucence";
	private static Analyzer analyzer= new IKAnalyzer();
	
	public static void createIndex(List<Map<String, String>> list) throws Exception{
		File dir= new File(indexPath);
		Directory directory=FSDirectory.open(dir);
		IndexWriterConfig indexWriterConfig= new IndexWriterConfig(Version.LUCENE_CURRENT,analyzer );
		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		
		IndexWriter indexWriter=null;
		indexWriter=new IndexWriter(directory,indexWriterConfig);
		for(Map<String, String> map :list){
			Document doc= new Document();
			doc.add(new Field("order_num",map.get("order_num"),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("create_time",map.get("create_time"),Field.Store.YES,Field.Index.ANALYZED));
			doc.add(new Field("pick_time",map.get("pick_time")==null?"":map.get("pick_time"),Field.Store.YES,Field.Index.ANALYZED));
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	public static void main(String[] args) throws Exception {
		 List<Map<String, String>> data =getData();
		 createIndex(data);
	}
}
