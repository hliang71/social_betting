package com.test.pubsub;

public interface MessageProcessingBus {
	
	public void subscribe(MessageListener listener);
	public void subscribe(MessageListener listener, int histroyCount);
	public void publish(String message);

}
