package org.rubby;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;


public class HbaseApi {

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		HBaseAdmin ha = new HBaseAdmin(conf);
//		ha.disableTable("test_table");
//		ha.deleteTable("test_table");
		
		//query
//		UserTagDTO dto = new UserTagDTO();
//		dto  = (UserTagDTO)queryHbase("584811", dto, "act_user", "cf_login");
//		 Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//	        String message = gson.toJson(dto);
//	        System.out.println(message);

		//query2
//		HTable table = new HTable(conf, "act_user");
//		Scan scan = new Scan();
//		ResultScanner scanner = table.getScanner(scan);
//		Result resultInScan = null;
//		while ((resultInScan = scanner.next()) != null) {
//			String key = Bytes.toString(resultInScan.getRow());
//			System.out.println("key:" + key);
//		}
//		scanner.close();
		
		//create
//		TableName name = TableName.valueOf(Bytes.toBytes("test_table")); 
//		HTableDescriptor desc = new HTableDescriptor(name);
//		HColumnDescriptor family = new HColumnDescriptor(Bytes.toBytes("testfml")); 
//		family.setMaxVersions(Integer.MAX_VALUE);
//		family.setTimeToLive(1000 * 24 * 60 * 60);
//		desc.addFamily(family);
//		ha.createTable(desc);
//		ha.close();
		
		HTable table = new HTable(conf, "act_user");
//		
//		for(int i =0;i<1;i++){
//			String key = "121312_2015-07-26 19:20:34";
////			String key = "testkey2";
//			Put put = new Put(Bytes.toBytes(key));
//			put.add(Bytes.toBytes("testfml"), Bytes.toBytes("testcl"), Bytes.toBytes(i));
//			table.put(put);
//			table.flushCommits();
//			System.out.println(i);
//		}
		
		//query 1
//		Get get = new Get(Bytes.toBytes("testkey2"));
//		get.setMaxVersions();
//		get.setTimeRange(1435226551065l, 1435226551069l);
//		Result result = table.get(get);
//		byte [] aa = result.getValue(Bytes.toBytes("testfml"), Bytes.toBytes("testcl"));
//		List<KeyValue> list = result.list(); 
//         for(final KeyValue v:list){
//             System.out.println(Bytes.toInt(v.getValue())+"  ts "+v.getTimestamp());
//         }
		
//		System.out.println(new Date().getTime());
		
//		Scan s=new Scan();
//		s.setStartRow(Bytes.toBytes("121312_2015-06-25 19:18:00"));
//		s.setStopRow(Bytes.toBytes("121312_2015-06-25 19:19:00"));
//		ResultScanner ss=table.getScanner(s);
//		int i = 0;
//		for(Result r:ss){
//			byte [] aa = r.getValue(Bytes.toBytes("testfml"), Bytes.toBytes("testcl"));
//			System.out.println(Bytes.toString(r.getRow()));
//			System.out.println(i++);
//			
//		}
		
		
		
	}

	// hbase mapping dto
	public static Object queryHbase(String rowkey, Object originObject,
			String tableTmp, String familyTmp) throws IOException {

		String tableName = tableTmp;
		String tableFamily = familyTmp;

		Configuration conf = HBaseConfiguration.create();

		HTable table = new HTable(conf, tableName);
		Get get = new Get(Bytes.toBytes(rowkey));
		Result rs = table.get(get);

		Field[] fields = originObject.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			Field field = fields[i];
			String filedName = field.getName();
			Class<?> type = field.getType();
			String fieldSimpleTypeName = type.getSimpleName();
			Object fieldValue = null;
			byte[] fieldValueByte = rs.getValue(Bytes.toBytes(tableFamily),
					Bytes.toBytes(filedName));
			if (fieldSimpleTypeName.equals("Long") && fieldValueByte != null) {
				fieldValue = Bytes.toLong(fieldValueByte);
			} else if (fieldSimpleTypeName.equals("String")
					&& fieldValueByte != null) {
				fieldValue = Bytes.toString(fieldValueByte);
			}
			setter(originObject, filedName, fieldValue, type);
		}
		return originObject;
	}

	public static void setter(Object obj, String att, Object value,
			Class<?> type) {
		try {
			Method method = obj.getClass().getMethod("set" + captureName(att),
					type);
			method.invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public static String captureName(String name) {
	        name = name.substring(0, 1).toUpperCase() + name.substring(1);
	        return  name;

	    }

}
