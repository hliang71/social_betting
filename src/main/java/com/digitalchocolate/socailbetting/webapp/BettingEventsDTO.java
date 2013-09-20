package com.digitalchocolate.socailbetting.webapp;

import java.util.ArrayList;
import java.util.List;

import com.digitalchocolate.socailbetting.utils.BetTypeEnum;

public class BettingEventsDTO {
	private List<TierOpponentDTO> results = new ArrayList<TierOpponentDTO>();
	private BetTypeEnum betType;
	
	public List<TierOpponentDTO> getResults() {
		return results;
	}
	public void setResults(List<TierOpponentDTO> results) {
		this.results = results;
	}
	public BetTypeEnum getBetType() {
		return betType;
	}
	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}
	
}
