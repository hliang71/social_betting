package com.digitalchocolate.socailbetting.webapp;

import java.util.ArrayList;
import java.util.List;

public class OpponentsOddsResult {
	private List<OpponentsOdds> results = new ArrayList<OpponentsOdds>();
	private Boolean success = true;
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public List<OpponentsOdds> getResults() {
		return results;
	}
}
