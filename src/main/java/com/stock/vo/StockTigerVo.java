package com.stock.vo;



public class StockTigerVo {

	
	private String id;
	
	private String date;
	
	private String code;
	//是否有机构 1  是 0 不是 
	private String isorg;
	//1是买入 2是卖出
	private String flag;
	//序号
	private int num;
	
	
	private String tradename;
	
	private double tradebuymoney;
	
	private double tradebuyper;
	
	private double tradesellmoney;
	
	private double tradesellper;
	
	private double diff;
	
	private String stock_date_id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIsorg() {
		return isorg;
	}

	public void setIsorg(String isorg) {
		this.isorg = isorg;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getTradename() {
		return tradename;
	}

	public void setTradename(String tradename) {
		this.tradename = tradename;
	}

	public double getTradebuymoney() {
		return tradebuymoney;
	}

	public void setTradebuymoney(double tradebuymoney) {
		this.tradebuymoney = tradebuymoney;
	}

	public double getTradebuyper() {
		return tradebuyper;
	}

	public void setTradebuyper(double tradebuyper) {
		this.tradebuyper = tradebuyper;
	}

	public double getTradesellmoney() {
		return tradesellmoney;
	}

	public void setTradesellmoney(double tradesellmoney) {
		this.tradesellmoney = tradesellmoney;
	}

	public double getTradesellper() {
		return tradesellper;
	}

	public void setTradesellper(double tradesellper) {
		this.tradesellper = tradesellper;
	}

	public double getDiff() {
		return diff;
	}

	public void setDiff(double diff) {
		this.diff = diff;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getStock_date_id() {
		return stock_date_id;
	}

	public void setStock_date_id(String stock_date_id) {
		this.stock_date_id = stock_date_id;
	}


	
	
}
