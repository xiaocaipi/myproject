package org.rubby;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseIf {
	
	//configuration是链接hbase数据库的配置信息，由于链接的是本地所以不与要配置内容
	Configuration conf;
	
	//利用单例的开发模式减少对象
	public static HbaseIf gbase=null;
	
	HbaseIf(){
		//默认不写的是自动去连本地的zookeeper
		conf=HBaseConfiguration.create();
		//不是单机的话，就要设置zookeeper的地址，前面参数是链接参数，后面是一个地址的列表，按照顺序链接
		//conf.set("hbase.zookeeper.quorum", "zk1.foo.com,zk2.foo.com");
	}
	
	public static HbaseIf getInstance(){
		if(gbase==null){
			gbase=new HbaseIf();
		}
		return gbase;
	}

	//创建一个工具方法来创建只有一个列族的表
	//表明 列族名字  可以存放的版本
	public void create_table(String name,String col,int version)  throws Exception{
		HBaseAdmin admin=new HBaseAdmin(conf);
		if(admin.tableExists(name)){
			//删除表先disable 后delete
			admin.disableTable(name);
			admin.deleteTable(name);
		}
		//表的描述用来创建表
		HTableDescriptor tabledes=new HTableDescriptor(name);
		//列族的描述用来创建列族
		HColumnDescriptor hd=new HColumnDescriptor(col);
		//提供元素中最大的version
		hd.setMaxVersions(version);
		tabledes.addFamily(hd);
		admin.createTable(tabledes);
		
	}
	
	/*要创建3个表
	 * tab_global   param：userid   列族是param  列名是param：userid 
	 * tab_user2id  info：id
	 * tab_id2user  info:username   info:password
	 * 
	 * 
	 */
	public void creteTables() throws Exception{
		//在创建表的时候之需要创建列族，在插入数据的时候在会制定列
		create_table("tab_global", "param", 1);
		//申明一个put对象来插入数据,里面需要传入一个rowkey的byte
		//hbase存储的数据都是二进制的数据
		//Bytes是hbase 转换数据的工具累
		//put相当于hbase中的一行
		Put put= new Put(Bytes.toBytes("row_userid"));
		long id=0;
		//往行里添加列,第一个是列族，第二个是列名，第三个是数据
		put.add(Bytes.toBytes("param"),Bytes.toBytes("userid"),Bytes.toBytes(id));
		//创建一个表的对象,第一个是conf，第二个是表明
		HTable ht=new HTable(conf, "tab_global");
		ht.put(put);
		create_table("tab_user2id", "info", 1);
		create_table("tab_id2user", "info", 1);
		
		/*
		 * tab_follow  rowkey:userid  
		 * CF:name:userid =>username
		 * version=>1
		 */
		create_table("tab_follow", "name", 1);
		

		/*
		 * tab_followed  rowkey:userid_{user_id}  
		 * CF:name:userid =>userid 当只需要一列的时候可以只定义一个列族就可以了，hbase会自动为列名
		 * version=>1
		 */
		create_table("tab_followed", "userid", 1);
		
		/*
		 * tab_post  rowkey:postid  
		 * CF:post:username  post:content post:ts
		 * version=>1
		 */
		create_table("tab_post", "post", 1);
		//tab_global 创建微薄的起始值
		 put= new Put(Bytes.toBytes("row_postid"));
		 id=0;
		put.add(Bytes.toBytes("param"),Bytes.toBytes("postid"),Bytes.toBytes(id));
		ht.put(put);
		
		/*
		 * tab_inbox  rowkey:userid+postid  
		 * CF:postid
		 * version=>1
		 */
		create_table("tab_inbox", "postid", 1);
		ht.close();
	}
	
	//发微薄
	public boolean post(String username,String content) throws Exception{
		HTable tab_global=new HTable(conf, "tab_global");
		HTable tab_post=new HTable(conf, "tab_post");
		long id=tab_global.incrementColumnValue(Bytes.toBytes("row_postid"), 
				Bytes.toBytes("param"),
				Bytes.toBytes("postid"),1);
		byte[] postid=Bytes.toBytes(id);
		
		//插入 tab_post
		Put put= new Put(postid);
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=df.format(new Date());
		put.add(Bytes.toBytes("post"),Bytes.toBytes("username"),username.getBytes());
		put.add(Bytes.toBytes("post"),Bytes.toBytes("content"),content.getBytes());
		put.add(Bytes.toBytes("post"),Bytes.toBytes("ts"),time.getBytes());
		tab_post.put(put);
		tab_post.close();
		tab_global.close();
		
		//发送微薄
		long senderid=this.getIdByUsername(username);
		//获得tab_followed 这表中发送者开头的起始值，tab_followed的rowkey是由userid+followedid组成的
		byte[] beigin=Bytes.add(Bytes.toBytes(senderid),Bytes.toBytes(Long.MAX_VALUE-Long.MAX_VALUE));//最小值是0 ，这样写可以取得long这个类型
		byte[] end=Bytes.add(Bytes.toBytes(senderid),Bytes.toBytes(Long.MAX_VALUE));
		Scan s=new Scan();
		s.setStartRow(beigin);
		s.setStopRow(end);
		HTable tab_followed=new HTable(conf, "tab_followed");
		HTable tab_inbox=new HTable(conf, "tab_inbox");
		
		ResultScanner ss=tab_followed.getScanner(s);
		//插入自己的收件箱 利用htable中put一个list的方法
		List<Put> list=new ArrayList<Put>();
		put=new Put(Bytes.add(Bytes.toBytes(senderid),postid ));
		put.add(Bytes.toBytes("postid"),null,postid);
		list.add(put);
		for(Result rs:ss){
			byte [] did=rs.getValue(Bytes.toBytes("userid"), null);
			put=new Put(Bytes.add(did, postid));
			put.add(Bytes.toBytes("postid"),null,postid);
			list.add(put);
		}
		tab_inbox.put(list);
		tab_inbox.close();
		 tab_followed.close();
		
		
		return true;
	}
	
	
	//获取微薄因为获取微薄有顺序的所以用list,set没有顺序
	public List<Post> getPost(String username) throws Exception{
		List<Post> list=new ArrayList<Post>();
		long id=this.getIdByUsername(username);
		//扫描 inbox表 设置开始和结束
		//byte[] begin=Bytes.add(Bytes.toBytes(id), Bytes.toBytes(Long.MAX_VALUE-Long.MAX_VALUE));
		//byte[] end=Bytes.add(Bytes.toBytes(id), Bytes.toBytes(Long.MAX_VALUE));
		
		//或者begin和end可以这么写，因为scan是>= begin <end 所以用+1的是不会被取得的
		byte[] begin=Bytes.toBytes(id);
		byte[] end=Bytes.toBytes(id+1);
		
		Scan s=new Scan();
		s.setStartRow(begin);
		s.setStartRow(end);
		HTable tab_post=new HTable(conf, "tab_post");
		HTable tab_inbox=new HTable(conf, "tab_inbox");
		ResultScanner ss=tab_inbox.getScanner(s);
		Get get=null;
		Post post=null;
		for(Result r:ss){
			//得到inbox表中的postId，根据postid去获得post表中的内容
			byte[] postid=r.getValue(Bytes.toBytes("postid"), null);
			get=new Get(postid);
			Result rs=tab_post.get(get);
			String name=Bytes.toString(rs.getValue(Bytes.toBytes("post"), Bytes.toBytes("username")));
			String content=Bytes.toString(rs.getValue(Bytes.toBytes("post"), Bytes.toBytes("content")));
			String ts=Bytes.toString(rs.getValue(Bytes.toBytes("post"), Bytes.toBytes("ts")));
			post=new Post(name, content, ts);
			System.out.println(name+"-"+content+"-"+ts);
			//插在最前面
			list.add(0,post);
			
		}
		return list;
	}
	
	
	//创建用户
	/*
	 * 
	 */
	public long getIdByUsername(String username){
		try {
			HTable tab_user2id=new HTable(conf, "tab_user2id");
			//result里面是一个keyvalue的list,result对应table中的一行
			Result result=tab_user2id.get(new Get(username.getBytes()));
			//一行会有多个版本的记录，获得最新的版本的记录
			KeyValue kv=result.getColumnLatest(Bytes.toBytes("info"), Bytes.toBytes("id"));
			byte[] bid=kv.getValue();
			return Bytes.toLong(bid);
		} catch (Exception e) {
			//用户名不存在就会返回一个0，因为result这个对象为空，就直接走异常
			return 0;
		}
		
	}
	public String getNameByUserid(long id){
		try {
			HTable tab_id2user=new HTable(conf, "tab_id2user");
			Result result=tab_id2user.get(new Get(Bytes.toBytes(id)));
			KeyValue kv=result.getColumnLatest(Bytes.toBytes("info"), Bytes.toBytes("username"));
			return Bytes.toString(kv.getValue());
		} catch (Exception e) {
			return "";
			// TODO: handle exception
		}
	}
	
	public long checkPassword(String username,String password) throws Exception{
		long id=getIdByUsername(username);
		if(id==0){
			return 0;
		}
		HTable tab_id2user=new HTable(conf, "tab_id2user");
		Result result=tab_id2user.get(new Get(Bytes.toBytes(id)));
		KeyValue kv=result.getColumnLatest(Bytes.toBytes("info"), Bytes.toBytes("password"));
		String passwordIndb=Bytes.toString(kv.getValue());
		if(passwordIndb.equals(password)){
			return id;
		}
		return 0;
	}
	public boolean createNewUser(String username,String password) throws Exception{
		//1检查用户是否存在
		
		HTable tab_user2id=new HTable(conf, "tab_user2id");
		HTable tab_global=new HTable(conf, "tab_global");
		HTable tab_id2user=new HTable(conf, "tab_id2user");
		//get也相当于一个行，一个行的初始化需要传入一个rowkey，table_user2id表的rowkey是username
		if(tab_user2id.exists(new Get(username.getBytes())))
			return false;
		
		//2获得一个新的id
		//incrementColumnValue方法自动加一.第四个参数是指要加几
		//不同地方同时调用不会返回2个一样的id 
		long id=tab_global.incrementColumnValue(Bytes.toBytes("row_userid"),
				Bytes.toBytes("param"), 
				Bytes.toBytes("userid"),
				1);
		Put put= new Put(username.getBytes());
		put.add(Bytes.toBytes("info"),Bytes.toBytes("id") , Bytes.toBytes(id));
		//3插入tab_user2id
		tab_user2id.put(put);
		
		//4插入tab_id2user
		put=new Put(Bytes.toBytes(id));
		put.add(Bytes.toBytes("info"),Bytes.toBytes("username"),username.getBytes());
		put.add(Bytes.toBytes("info"),Bytes.toBytes("password"),password.getBytes());
		tab_id2user.put(put);
		tab_user2id.close();
		tab_global.close();
		tab_id2user.close();
		return true;
	}
	//关注用户
	public boolean follow(String oname,String dname) throws Exception{
		long oid=this.getIdByUsername(oname);
		long did=this.getIdByUsername(dname);
		if(oid==0 || did==0 || oid==did) {
			return false;
		}
		//tab_follow
		HTable tab_follow=new HTable(conf, "tab_follow");
		Put put=new Put(Bytes.toBytes(oid));
		put.add(Bytes.toBytes("name"),Bytes.toBytes(did),dname.getBytes());
		tab_follow.put(put);
		tab_follow.close();
		//tab_followed
		HTable tab_followed=new HTable(conf, "tab_followed");
		//bytes提供的将2个id进行add
		 put=new Put(Bytes.add(Bytes.toBytes(did), Bytes.toBytes(oid)));
		 // 当只需要一列的时候可以只定义一个列族就可以了，hbase会自动为列名
		 put.add(Bytes.toBytes("userid"),null,Bytes.toBytes(oid));
		 tab_followed.put(put);
		 tab_followed.close();
		 
		 return true;
		
	}
	
	public boolean unfollow(String oname,String dname) throws Exception{
		long oid=this.getIdByUsername(oname);
		long did=this.getIdByUsername(dname);
		if(oid==0 || did==0 || oid==did) {
			return false;
		}
		HTable tab_follow=new HTable(conf, "tab_follow");
		//定义del对象用户删除 ,也是传入rowkey
		Delete del=new Delete(Bytes.toBytes(oid));
		//delete 对象有delcolumn 删除某一个version 可以传入timestamp 如果不传入就删除最新的一个
		//deletecolumns 是删除某一个列的所有verisons
		del.deleteColumns(Bytes.toBytes("name"), Bytes.toBytes(did));
		tab_follow.delete(del);
		tab_follow.close();
		HTable tab_followed=new HTable(conf, "tab_followed");
		del=new Delete(Bytes.add(Bytes.toBytes(did), Bytes.toBytes(oid)));
		//删除整行所以不需要制定列
		tab_followed.delete(del);
		tab_followed.close();
		return true;
	}
	
	//获取所有用户
	public Set<String>getAllUser() throws Exception{
		Set<String>set=new HashSet<String>();
		HTable tab_user2id=new HTable(conf, "tab_user2id");
		//scan对象用于扫描表可以传入开始rowkey和结束rowkey
		Scan s=new Scan();
		//得到resultScanner 对象,shi result的集合，就可以遍历表中所有的数据
		ResultScanner rs=tab_user2id.getScanner(s);
		//result对应table中的一行，里面有一个keyvalue的list
		for(Result r:rs){
			//这里只要得到一行就可以，不需要得到列
			String name=new String(r.getRow());
			set.add(name);
			System.out.println(name);
		}
		return set;
	}
	
	//获得关注的用户
	public Set<String> getFollow(String username)  throws Exception {
		long id=this.getIdByUsername(username);
		Set<String>set=new HashSet<String>();
		HTable tab_follow=new HTable(conf, "tab_follow");
		Get get=new Get(Bytes.toBytes(id));
		Result rs=tab_follow.get(get);
		//获得所有列
		for(KeyValue kv:rs.raw()){
			String s=new String(kv.getValue());
			set.add(s);
		}
		return set;
	}
	
	public static void main(String[] args) throws Exception {
	HbaseIf h=new HbaseIf();
	h.creteTables();
//	h.createNewUser("kaka1", "123456");
//	h.createNewUser("kaka2", "123456");
//	h.createNewUser("kaka3", "123456");
//	h.createNewUser("kaka4", "123456");
//	h.createNewUser("kaka5", "123456");
//	h.follow("kaka1", "kaka2");
//	h.follow("kaka1", "kaka3");
//	h.follow("kaka1", "kaka4");
//	h.post("kaka2", "test11111");
//	h.unfollow("kaka1", "kaka2");
//	h.getFollow("kaka1");
//	h.getPost("kaka1");
}
}
