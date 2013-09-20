package com.test.pubsub;

public interface TopicSubscriber {
	public void subscribe(String topic, MessageListener listener);

	public void subscribe(String topic, MessageListener listener,
			int histroyCount);

	public void publish(String Topic, String message);
}
