package com.test.pubsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

/**
 * A default implementation for topic subscriber. the default max history size
 * is 10.
 * 
 * @author henry liang
 * 
 */
public class DefaultTopicSubscriberImpl implements TopicSubscriber {
	private static final int MAX_HISTORY = 10;
	private Map<String, MessageProcessingBus> topics = new ConcurrentHashMap<String, MessageProcessingBus>();

	@Override
	public void subscribe(String topic, MessageListener listener) {
		MessageProcessingBus bus = this.validate(topic, listener, true);
		bus.subscribe(listener);
	}

	@Override
	public void subscribe(String topic, MessageListener listener,
			int histroyCount) {
		MessageProcessingBus bus = this.validate(topic, listener, true);
		bus.subscribe(listener, histroyCount);
	}

	@Override
	public void publish(String topic, String message) {
		MessageProcessingBus bus = this.validate(topic, null, false);
		bus.publish(message);
	}

	private synchronized MessageProcessingBus validate(String topic,
			MessageListener listener, boolean listenerRequired) {
		if (StringUtils.isBlank(topic)) {
			throw new IllegalArgumentException("topic is blank.");
		}
		if (listenerRequired && listener == null) {
			throw new IllegalArgumentException("message listener is required.");
		}
		MessageProcessingBus bus = this.topics.get(topic);
		if (bus == null) {
			bus = new MessagesProcessingBusImpl(MAX_HISTORY);
			this.topics.put(topic, bus);
		}
		return bus;
	}
}
