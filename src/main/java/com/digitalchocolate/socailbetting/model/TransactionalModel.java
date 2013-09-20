package com.digitalchocolate.socailbetting.model;

import java.util.ArrayList;
import java.util.List;

public class TransactionalModel {
	
	private List<String> transactionIds = new ArrayList<String>();

	public List<String> getTransactionIds() {
		return transactionIds;
	}

	public void setTransactionIds(List<String> transactionIds) {
		this.transactionIds = transactionIds;
	}
	

}
