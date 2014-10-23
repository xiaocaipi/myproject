package org.rubby.cdr;

import java.util.List;
import java.util.Map;

import org.rubby.CdrPro;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class CdrDailyAction extends ActionSupport {
	Map<String, Integer> dailyreport;
	
	
	public Map<String, Integer> getDailyreport() {
		return dailyreport;
	}


	public void setDailyreport(Map<String, Integer> dailyreport) {
		this.dailyreport = dailyreport;
	}


	@Override
	public String execute() throws Exception {
		HbaseCdrIf hif=HbaseCdrIf.getInstance();
		dailyreport=hif.getDailyReport();
		return SUCCESS;
	}

}
