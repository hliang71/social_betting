package com.openbet.socailbetting.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.openbet.socailbetting.dao.AbstractEventDao;
import com.openbet.socailbetting.dao.EventDao;
import com.openbet.socailbetting.exception.SocialBettingInternalException;
import com.openbet.socailbetting.model.BettingEvent;
import com.openbet.socailbetting.utils.BetHistoryStatusEnum;
import com.openbet.socailbetting.utils.BetTypeEnum;
import com.openbet.socailbetting.utils.HashUtils;
import com.openbet.socailbetting.utils.StatusEnum;

@Repository("eventDao")
public class EventDaoImpl  extends AbstractEventDao implements EventDao{
	private static final Logger log = Logger.getLogger(EventDaoImpl.class);
	private static final int HASH_SIZE=30;
	
	@Override
	public List<BettingEvent> findBets(Long projectId, String opponentId, Integer expectedLevel, BetTypeEnum betType, StatusEnum status, boolean friendEnable,String contextId, Integer limit, Integer begin)
	{
		
		if(log.isDebugEnabled()) log.debug("projectId:"+projectId+"opponentId:"+opponentId+" expectedLevel"+expectedLevel+"betType"+betType.value()+" status:"+status.value()+" friendEnable:"+friendEnable);
		List<BettingEvent> events = null;
		try
		{
			if(StringUtils.isNotBlank(contextId))
			{
				events = eventTemplate.find(new Query(Criteria.where("contextIdentifier").is(contextId).and("projectId").is(projectId).and("betType").is(betType.value()).and("status").is(status.value()).and("opponents.opponentId").ne(opponentId)).skip(begin).limit(limit), BettingEvent.class);
			    return events;
			}
			Criteria c2 =new Criteria().orOperator(Criteria.where("expectedLevels").is(expectedLevel), Criteria.where("friends.friendId").is(opponentId) );
            Criteria c1 =Criteria.where("projectId").is(projectId).and("betType").is(betType.value()).and("status").is(status.value());
            Criteria cri = c1.andOperator(c2);
            cri = cri.and("opponents.opponentId").ne(opponentId);
			if(!friendEnable)
			{
				events = eventTemplate.find(new Query(cri).skip(begin).limit(limit), BettingEvent.class);
			}else
			{
				events = eventTemplate.find(new Query(Criteria.where("projectId").is(projectId).and("betType").is(betType.value()).and("status").is(status.value()).and("friends.friendId").is(opponentId).and("opponents.opponentId").ne(opponentId)).skip(begin).limit(limit), BettingEvent.class);
			}
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to find the bet, ", e);
		}
		return events;
	}
	
	@Override
	public String saveEvent(BettingEvent event) {
		return this.saveEvent(event, true);
	}
	public String updateEvent(BettingEvent event)
	{
		return this.saveEvent(event, false);
	}
	private String saveEvent(BettingEvent event, boolean isInsert)
	{
		try
		{
			if(isInsert)
			{
				eventTemplate.insert(event);
			}else
			{
				event.setProjectId(null);
				event.setBetType(null);
				event.setDay(null);
				//event.setId(null);
				eventTemplate.save(event);
			}
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to create a new event.", e);
		}
		return event.getId();
	}
	@Override
	public BettingEvent getEventById(String eventId)
	{
		String projectId = this.getProjectIdFromEventId(eventId);
		String betType = this.getBetTypeFromEventId(eventId);
		try
		{
			Long pId = Long.valueOf(projectId);
			return this.getEventByIds(pId, betType, eventId);
		}catch(Exception e)
		{
			if(e instanceof SocialBettingInternalException)
			{
				SocialBettingInternalException se = (SocialBettingInternalException) e;
				throw se;
			}else
			{
				throw new SocialBettingInternalException("projectId is not a number.", e);
			}
		}
	}
	/**public BettingEvent getEventById(String eventId) {
		BettingEvent event = null;
		try
		{
			event = eventTemplate.findOne(new Query(Criteria.where("id").is(eventId)), BettingEvent.class);
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to get event by id:", e);
		}
		return event;
	}*/
	@Override
	public BettingEvent getEventByIds(Long projectId, String betType, String eventId) {
		BettingEvent event = null;
		try
		{  
			if(StringUtils.isNotBlank(eventId))
			{
				int idHash = HashUtils.hash(eventId, HASH_SIZE);
				event = eventTemplate.findOne(new Query(Criteria.where("projectId").is(projectId).and("betType").is(betType).and("day").is(idHash).and("id").is(eventId)), BettingEvent.class);
			}
			
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to get event by id:", e);
		}
		return event;
	}
	@Override
	public List<BettingEvent> getBetHistory(String opponentId, Long projectId, BetTypeEnum betType, String status, Date startDate, Date endDate, Integer begin,
			Integer limit) {
		List<BettingEvent> events = null;
		int beg = (begin == null) ? 0 : begin;
		try
		{	
			Criteria c2 = null;
			if(startDate != null && endDate != null)
			{                       // cri.and(
				c2 =new Criteria().orOperator(Criteria.where("createdDate").gte(startDate).lte(endDate), Criteria.where("matchedDate").gte(startDate).lte(endDate));
			}else if(startDate != null)
			{
				c2 = new Criteria().orOperator(Criteria.where("createdDate").gte(startDate), Criteria.where("matchedDate").gte(startDate));
			}
			
			Criteria cri = Criteria.where("projectId").is(projectId).and("betType").is(betType.value());
			if(StringUtils.isNotBlank(status))
			{
				if(status.equals(BetHistoryStatusEnum.LIVE.value()))
				{
					cri = cri.and("status").in(StatusEnum.WAIT_OPPONENT.value(), StatusEnum.LIVE.value());
				}else
				{
					
					cri = cri.and("status").is(status);
				}
				

			}
			cri = cri.and("opponents.opponentId").is(opponentId);
			if(c2 != null)
			{
				cri = c2.andOperator(cri);
			}
			
			Query q = new Query(cri);
			q = q.with(new Sort(Sort.Direction.DESC, "createdDate"));
			//q.sort().on("id", Order.DESCENDING);
			
			events = eventTemplate.find(q.skip(beg).limit(limit), BettingEvent.class);
			
		}catch(Exception e)
		{
			throw new SocialBettingInternalException("failed to get the bet history:", e);
		}
		return events;
	}
}
