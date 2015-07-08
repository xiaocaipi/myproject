package com.stock.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class BaseDto implements Serializable{

	private static final long serialVersionUID = 1562217804742502260L;
	
	
	private Long recordCounts;//数据记录总数
	

	//�ܼ�¼��
	private int recordCount;
	//��ǰ�û�
	private String usersId;
	
	private Timestamp enterDate;
	//��ѯ��ʼ��
	private int startRowNo = 0;
	//��ѯ��ֹ��
	private int endRowNo  = 0;
	
	private int pageIndex;//��ǰ��ѡ���ҳ��

	private String orderByClause;
	
	
	private String pageBarStr;// JSPҳ���ҳ


	private int totalPage;// ��ҳ��
	
	
	public String getPageBarStr() {
		return pageBarStr;
	}

	public void setPageBarStr(String pageBarStr) {
		this.pageBarStr = pageBarStr;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
 
	public void setEnterDate(Timestamp enterDate) {
		this.enterDate = enterDate;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public String getUsersId() {
		return usersId;
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public int getStartRowNo() {
		return startRowNo;
	}

	public void setStartRowNo(int startRowNo) {
		this.startRowNo = startRowNo;
	}

	public int getEndRowNo() {
		return endRowNo;
	}

	public void setEndRowNo(int endRowNo) {
		this.endRowNo = endRowNo;
	}

	public Timestamp getEnterDate() {
		return enterDate;
	}

	public void initEnterDate() {
		this.enterDate = new Timestamp(System.currentTimeMillis());
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}
	public Long getRecordCounts() {
		return recordCounts;
	}

	public void setRecordCounts(Long recordCounts) {
		this.recordCounts = recordCounts;
	}
	
	
}
