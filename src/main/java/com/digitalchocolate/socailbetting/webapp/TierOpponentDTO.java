package com.digitalchocolate.socailbetting.webapp;

import java.util.List;

public class TierOpponentDTO implements Comparable<TierOpponentDTO>{
	private Integer tier;
	private List<BetOpponentDTO> candidates;
	public Integer getTier() {
		return tier;
	}
	public void setTier(Integer tier) {
		this.tier = tier;
	}
	public List<BetOpponentDTO> getCandidates() {
		return candidates;
	}
	public void setCandidates(List<BetOpponentDTO> candidates) {
		this.candidates = candidates;
	}
	@Override
	public int compareTo(TierOpponentDTO o) {
		if(o == null)
		{
			return -1;
		}else
		{
			Integer otherTier = o.getTier();
			if(this.tier == null)
			{
				if(otherTier == null)
				{
					return 0;
				}else
				{
					return 1;
				}
			}else
			{
				if(otherTier == null)
				{
					return -1;
				}else
				{
					return this.tier.compareTo(otherTier);
				}
			}
		}
	}
}
