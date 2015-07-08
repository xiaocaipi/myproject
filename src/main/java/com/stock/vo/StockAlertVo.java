package com.stock.vo;

public class StockAlertVo extends StockData {

	private Double overprice;
	
	private Double lowprice;
	
	private Double zhangfu;
	
	private Double diefu;

	public Double getOverprice() {
		return overprice;
	}

	public void setOverprice(Double overprice) {
		this.overprice = overprice;
	}

	public Double getLowprice() {
		return lowprice;
	}

	public void setLowprice(Double lowprice) {
		this.lowprice = lowprice;
	}

	public Double getZhangfu() {
		return zhangfu;
	}

	public void setZhangfu(Double zhangfu) {
		this.zhangfu = zhangfu;
	}

	public Double getDiefu() {
		return diefu;
	}

	public void setDiefu(Double diefu) {
		this.diefu = diefu;
	}
	
}
