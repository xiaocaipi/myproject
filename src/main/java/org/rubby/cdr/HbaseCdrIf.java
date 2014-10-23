package org.rubby.cdr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.rubby.CdrPro;
import org.rubby.Post;



public class HbaseCdrIf {

	HTablePool tablePool;
	
	//使用单例的模式来去的这个对象
	public static HbaseCdrIf gbase=null;
	
	public HbaseCdrIf(){
		tablePool=new HTablePool(HBaseConfiguration.create(),1000);
	}
	//典型的单例模式
	public static HbaseCdrIf getInstance(){
		if(gbase==null){
			gbase=new HbaseCdrIf();
		}
		return gbase;
	}
	
	//通过手机号码获得话单
	public List<CdrPro.SmCdr>getSmCdr(String addr) throws Exception{
		List<CdrPro.SmCdr> list=new ArrayList<CdrPro.SmCdr>();
		if(addr.length()!=11){
			return list;
		}
		
		HTable tab_cdr=(HTable)tablePool.getTable("tab_cdr");
		
		Scan s=new Scan();
		s.setStartRow(addr.getBytes());
		//加1是对最后一个字符的ASC码加1，因为在hbase中是针对字节来比较，它是不知道这是ASC还是其他
		byte[] end=addr.getBytes();
		//对最后一位加一
		end[end.length-1]+=1;
		s.setStopRow(end);
//		s.setStopRow(Bytes.toBytes("13900000038"));
		
		ResultScanner rs=tab_cdr.getScanner(s);
		Get get=null;
		Post p=null;
		for(Result r:rs){
			//得到protocobuf 解码前的子节流
			byte[] data=r.getValue(Bytes.toBytes("data"), null);
			//解码成Smcdr对象
			CdrPro.SmCdr sm=CdrPro.SmCdr.parseFrom(data);
			list.add(sm);
		}
				
		
		return list;
	}
	//得到daily 表的数据
	//用map 的好处是map会自动的排序。
	public Map<String,Integer> getDailyReport() throws Exception{
		//为了排序用LinkedHashMap
		Map<String, Integer> map=new LinkedHashMap<String, Integer>();
		HTable tab_cdr=(HTable)tablePool.getTable("tab_cdr_daily");
		Scan s=new Scan();
		ResultScanner rs=tab_cdr.getScanner(s);
		Get get=null;
		for(Result r:rs){
			String t=Bytes.toString(r.getRow());
			int n=Bytes.toInt(r.getValue(Bytes.toBytes("data"), null));
			map.put(t, n);
		}
		return map;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		HbaseCdrIf hif=HbaseCdrIf.getInstance();
		List<CdrPro.SmCdr> l=hif.getSmCdr("13900000037");
		for(CdrPro.SmCdr sm:l){
			System.out.println(sm.getType()+":"+sm.getOaddr()+":"+sm.getOareacode()+":"+sm.getDaddr()+":"+sm.getDareacode()+":"
					+sm.getTimestamp());
		}
	}

}
