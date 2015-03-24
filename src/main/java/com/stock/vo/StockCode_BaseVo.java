package com.stock.vo;



public class StockCode_BaseVo {

	private long id;
	
	private String code;
	
	private String name;
	
	private String place;
	
	
	public StockCode_BaseVo(){
		
	}
	public StockCode_BaseVo(String code,String name){
		this.code=code;
		this.name=name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}

	
	 
	
}
