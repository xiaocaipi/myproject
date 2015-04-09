package com.stock.util;
import java.util.List;


public class Pager<E> {
	/**
	 * 当前页
	 */
	private int currentPage=1;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 总记录数
	 */
	private int totallRecord; 
	/**
	 * 分页显示条数
	 */
	private int pageSize =10;
	/**
	 * 分页对象
	 */
	private List<E> pageList;
	/**
	 * 当前记录开始位置
	 */
	private int pageIndex;
	/**
	 * 排序对象
	 */
	private String  orderby; 
	/**
	 * 分组
	 */
	private String  groupby; 
	
    
    /**
     * 分页
     * @return
     */
    private String pageBarStr;
    
    /**
     * 
     * @return
     */
    
    private static int pPage =3;
    
	private static int nPage =2;
    
	
	public String getPageBarStr() {
		String pageBarStr = "";
		int sCount = 1;
		int eCount = 1;
		if(totalPage>=6){
			if(currentPage <= pPage+1){
				eCount = pPage+nPage+1;
			}else if(currentPage >= totalPage - nPage){
				sCount = totalPage-pPage-nPage;
				eCount = totalPage;
			}else{
				sCount = currentPage-pPage;
				eCount = currentPage+nPage;
			}
			
		}else{
			eCount = totalPage; 
		}
		
		for(int i=sCount;i<=eCount;i++){
			pageBarStr = pageBarStr + i + ",";
		}
		
		if(pageBarStr.length()>1){
			pageBarStr = pageBarStr.substring(0,pageBarStr.length()-1);
		}
		
		return pageBarStr;
	}

	public void setPageBarStr(String pageBarStr) {
		this.pageBarStr = pageBarStr;
	}


	public Pager(){
		
	}
	
	public Pager(List<E> pageList, int pageSize, int currentPage,int totallRecord) {
		this.pageSize = pageSize;
		this.currentPage = currentPage;
		this.totallRecord = totallRecord;
		this.pageList = pageList;
		// 当前记录开始位置
		if(currentPage>=1){
		this.pageIndex = (currentPage - 1) * pageSize;
		}else{
		this.pageIndex =0;	
		}
		//总页数
		if (this.totallRecord % this.pageSize == 0) {
			this.totalPage = this.totallRecord / this.pageSize;
		} else {
			this.totalPage = this.totallRecord / this.pageSize + 1;
		}
	}

	
	
	
	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getGroupby() {
		return groupby;
	}

	public void setGroupby(String groupby) {
		this.groupby = groupby;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {  
		this.currentPage = currentPage;
		if(currentPage>=1){
			this.pageIndex = (currentPage - 1) * pageSize;
			}else{
			this.pageIndex =0;	
			}
		 
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) { 
		this.totalPage = totalPage;
	}

	public int getTotallRecord() {
		return totallRecord;
	}

	public void setTotallRecord(int totallRecord) {
		this.totallRecord = totallRecord;
		//总页数
		if (this.totallRecord % this.pageSize == 0) {
			this.totalPage = this.totallRecord / this.pageSize;
		} else {
			this.totalPage = this.totallRecord / this.pageSize + 1;
		} 
	} 
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<E> getPageList() {
		return pageList;
	}

	public void setPageList(List<E> pageList) {
		this.pageList = pageList;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		// 当前记录开始位置
		this.pageIndex =pageIndex; 
	}
	
 

}
