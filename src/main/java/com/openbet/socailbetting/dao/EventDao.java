package com.openbet.socailbetting.dao;

import java.util.Date;
import java.util.List;

import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.StatusEnum;

public interface EventDao {
	public List<BettingEvent> findBets(Long projectId, String opponentId,
			Integer expectedLevel, BetTypeEnum betType,
			StatusEnum status, boolean friendEnable,String contextId, Integer limit, Integer begin);

	public String saveEvent(BettingEvent event);

	public String updateEvent(BettingEvent event);
	public BettingEvent getEventById(String eventId);
	public BettingEvent getEventByIds(Long projectId, String betType, String eventId);
	public List<BettingEvent> getBetHistory(String opponentId, Long projectId, BetTypeEnum betType, String status, Date startDate, Date endDate, Integer begin, Integer limit);
	
}
