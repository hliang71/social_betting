package com.openbet.socailbetting.service;

import java.util.List;

import com.openbet.socailbetting.model.TransactionParticipantModel;
import com.openbet.socailbetting.model.TransactionalModel;

public interface BettingMessageProducer {
	public void atomicSave(List<TransactionParticipantModel> lightweightObject, TransactionalModel event, boolean isInsert);
}
