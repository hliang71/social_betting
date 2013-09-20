package com.openbet.socailbetting.webapp;

import java.util.ArrayList;
import java.util.List;

public class TiersResult {
	private Integer tier;
	private Double offeredOdds;
	private List<OddsUid> uids = new ArrayList<OddsUid>();
	
	public Double getOfferedOdds() {
		return offeredOdds;
	}
	public void setOfferedOdds(Double offeredOdds) {
		this.offeredOdds = offeredOdds;
	}
	public List<OddsUid> getUids() {
		return uids;
	}
	public Integer getTier() {
		return tier;
	}
	public void setTier(Integer tier) {
		this.tier = tier;
	}
	
	
	
}
