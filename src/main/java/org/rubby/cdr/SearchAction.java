package org.rubby.cdr;

import java.util.List;
import java.util.Map;

import org.rubby.CdrPro;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class SearchAction extends ActionSupport {
	String addr;
	List<CdrPro.SmCdr>cdrs;
	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public List<CdrPro.SmCdr> getCdrs() {
		return cdrs;
	}

	public void setCdrs(List<CdrPro.SmCdr> cdrs) {
		this.cdrs = cdrs;
	}
	@Override
	public String execute() throws Exception {
		HbaseCdrIf hif=HbaseCdrIf.getInstance();
		cdrs=hif.getSmCdr(addr);
		return SUCCESS;
	}

}
