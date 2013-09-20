package com.digitalchocolate.socailbetting.webapp;

import java.util.List;

import com.digitalchocolate.socailbetting.utils.BetTypeEnum;

public class TiersOddsRequest extends BaseRequestDTO{
    private BetTypeEnum betType;
	private List<Integer> tiers;
	private Boolean isRelative;
	
	@Override
	public String getRequestContent() {
		String bt = betType == null? "":betType.value();
		String ir = isRelative == null? "" : isRelative+"";
		return new StringBuilder(bt).append(ir).append(super.getOpponentId()).append(super.getPlatformUserId()).append(super.getProjectId()).toString();

	}

	public List<Integer> getTiers() {
		return tiers;
	}

	public void setTiers(List<Integer> tiers) {
		this.tiers = tiers;
	}

	public BetTypeEnum getBetType() {
		return betType;
	}

	public void setBetType(BetTypeEnum betType) {
		this.betType = betType;
	}

	public Boolean getIsRelative() {
		return isRelative;
	}

	public void setIsRelative(Boolean isRelative) {
		this.isRelative = isRelative;
	}

}
