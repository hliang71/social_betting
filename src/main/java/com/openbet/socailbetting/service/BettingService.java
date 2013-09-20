package com.openbet.socailbetting.service;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import com.openbet.socailbetting.webapp.BetHistoryResult;
import com.openbet.socailbetting.webapp.BetSettlementDTO;
import com.openbet.socailbetting.webapp.BettingEventsDTO;
import com.openbet.socailbetting.webapp.BettingResultDTO;
import com.openbet.socailbetting.webapp.GenerateBetRequestBean;
import com.openbet.socailbetting.webapp.HistoryRequestBean;
import com.openbet.socailbetting.webapp.OpponentsOddsRequest;
import com.openbet.socailbetting.webapp.OpponentsOddsResult;
import com.openbet.socailbetting.webapp.PlaceBetRequestBean;
import com.openbet.socailbetting.webapp.RuleDTO;
import com.openbet.socailbetting.webapp.RuleRequestBean;
import com.openbet.socailbetting.webapp.SearchRequestBean;
import com.openbet.socailbetting.webapp.TiersOddsRequest;
import com.openbet.socailbetting.webapp.TiersOddsResult;
import com.openbet.socailbetting.webapp.UpdateBetRequestBean;



@CrossOriginResourceSharing(
		allowAllOrigins = true, 
        allowCredentials = false, 
        maxAge = 1
)
public interface BettingService {
	@POST
	@Path("/getRules")
	@Consumes({"application/json"})
	@Produces({"application/json"})
    public RuleDTO getRules(RuleRequestBean request);
	
	@POST
	@Path("/searchBets")
	@Consumes({"application/json"})
	@Produces({"application/json"})
    public BettingEventsDTO searchBets(SearchRequestBean request);
	
	@POST
	@Path("/createBet")
	@Consumes({"application/json"})
	@Produces({"application/json"})
    public BettingResultDTO generateBets(GenerateBetRequestBean request);
	
	@POST
	@Path("/acceptBet")
	@Consumes({"application/json"})
	@Produces({"application/json"})
    public BettingResultDTO placeBets(PlaceBetRequestBean request);
	
	@POST
	@Path("/updateBets")
	@Consumes({"application/json"})
	@Produces({"application/json"})
    public BetSettlementDTO updateBets(UpdateBetRequestBean request);
	
	@POST
	@Path("/getBetHistory")
	@Consumes({"application/json"})
	@Produces({"application/json"})
    public BetHistoryResult getBetHistory(HistoryRequestBean request);
	
	@POST
	@Path("/listOddsByTiers")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	public TiersOddsResult findOddsByTiers(TiersOddsRequest request);
		
	@POST
	@Path("/listOddsByUsers")
	@Consumes({"application/json"})
	@Produces({"application/json"})
	public OpponentsOddsResult findOddsByUsers(OpponentsOddsRequest request);
	
}
