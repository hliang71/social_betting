package com.digitalchocolate.socailbetting.service;

import java.util.List;

import com.digitalchocolate.socailbetting.model.TransactionParticipantModel;
import com.digitalchocolate.socailbetting.model.TransactionalModel;

public interface BettingMessageProducer {
	public void atomicSave(List<TransactionParticipantModel> lightweightObject, TransactionalModel event, boolean isInsert);
}
