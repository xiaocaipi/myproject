/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stock.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;

/**
 * BaseDao,Dao需继承此Dao
 * @author 2012
 * since 2011-3-3 下午02:52:36
 */ 
public class BaseDao extends SqlMapClientDaoSupport {
	
	private static final Logger logger=Logger.getLogger(BaseDao.class);

    @Resource(name = "sqlMapClient")
    private SqlMapClient sqlMapClient;
    
    @PostConstruct
    public void initSqlMapClient() {  
        super.setSqlMapClient(sqlMapClient);
    } 
    
    
    public Pager pageMapQuery(String statementName, Pager pager,Map<String,Object> arg) {  
  	   Number totalCount =(Number)this.getForObject(statementName+"Count", arg);  
  	   pager.setTotallRecord(totalCount.intValue()); 
  	   List<T> pageList=(List<T>)this.getObjectForList(statementName, arg);
  	   pager.setPageList(pageList);
        return pager;
     }  
    
    public Pager pageQuery(String statementName, Pager pager) { 
 	   Number totalCount =(Number)this.getForObject(statementName+"Count", pager);  
 	   pager.setTotallRecord(totalCount.intValue()); 
 	   List<T> pageList=(List<T>)this.getObjectForList(statementName, pager);
 	   pager.setPageList(pageList);
       return pager;
    }  
    
    
	
	public Object getForObject(String statementName, Object para){ 
		return getSqlMapClientTemplate().queryForObject(statementName, para);
	}

	public List getObjectForList(String statementName, Object para) { 
		return getSqlMapClientTemplate().queryForList(statementName, para);
	}

	public Object insertObject(String statementName, Object para) {
		Object newKey = getSqlMapClientTemplate().insert(statementName, para);
		return newKey;
	}

	public void insertObjectByBatch(final String statementName, final List paraList) {
		SqlMapClientCallback callback = new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				executor.startBatch();
				for (int i = 0, n = paraList.size(); i < n; i++) {
					executor.insert(statementName, paraList.get(i));
					if (i % 5000 == 0) {
						executor.executeBatch();
					}
				}
				executor.executeBatch();
				return null;
			}
		};
		getSqlMapClientTemplate().execute(callback);
	}
	
	public void updateObjectByBatch(final String statementName, final List paraList) { 
		SqlMapClientCallback callback = new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {
				executor.startBatch();
				for (int i = 0, n = paraList.size(); i < n; i++) { 
					executor.update(statementName, paraList.get(i));
					if (i % 5000 == 0) {
						executor.executeBatch();
					}
				}
				executor.executeBatch();
				return null;
			}
		};
		getSqlMapClientTemplate().execute(callback);
	}
	
	public void removeObjectByBatch(String statementName, Object para) {
	}

	public int removeObject(String statementName, Object para) {
		int rows = getSqlMapClientTemplate().delete(statementName, para);
		return rows;
	}

	public int updateObject(String statementName, Object para) {
		
		int rows = getSqlMapClientTemplate().update(statementName, para);
		return rows;
	}
    
    
    
}
