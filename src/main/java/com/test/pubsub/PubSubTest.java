package com.test.pubsub;

public class PubSubTest {

	/**
	 * this method is to test an error condition when publish an empty message,
	 * the expected result is "PASSED"
	 */
	public void testPublishWhenMessageIsEmpty() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();

		try {
			pubsub.publish("foo", null);
		} catch (IllegalArgumentException e) {
			String m = e.getMessage();
			if (m.equals("no message to publish.")) {
				System.out.println("PASSED");
			} else {
				System.out.println("FAIL");
			}
		}

	}

	/**
	 * this method is to test an error condition when historyCount parameter is
	 * larger than the maximum supported histroy count, the expected result is
	 * "PASSED"
	 */
	public void testErrorConditionWhenHistoryCountIsTooLarge() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();
		MessageListener listener1 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener1:" + msg);

			}
		};

		pubsub.publish("foo", "hello");
		pubsub.publish("foo", "world");
		try {
			pubsub.subscribe("foo", listener1, 100);
		} catch (IllegalArgumentException e) {
			String m = e.getMessage();
			if (m.equals("history count is too large.")) {
				System.out.println("PASSED");
			} else {
				System.out.println("FAIL");
			}
		}

	}

	/**
	 * this method is to test multiple listeners subscribe at different time
	 * with history support, the message received by each listener are
	 * different, the expected result is listener 1 displays "Listener1: hello",
	 * "Listener1: world", "Listener1: from", "Listener1: me". listener 2
	 * displays "Listener2: world","Listener2: from", "Listener2: me".
	 */
	public void testTwoDifferentListenerSubscribAtDifferentTimeWithHistorySupport() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();
		MessageListener listener1 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener1:" + msg);

			}
		};
		MessageListener listener2 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener2:" + msg);
			}

		};
		pubsub.subscribe("foo", listener1);
		pubsub.publish("foo", "hello");
		pubsub.publish("foo", "world");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pubsub.subscribe("foo", listener2, 1);
		pubsub.publish("foo", "from");
		pubsub.publish("foo", "me");
	}

	/**
	 * this method is to test multiple listeners subscribe at different time so
	 * the message received by each listener are different, the expected result
	 * is listener 1 displays "Listener1: hello", "Listener1: world",
	 * "Listener1: from", "Listener1: me". listener 2 displays
	 * "Listener2: from", "Listener2: me".
	 */
	public void testTwoDifferentListenerSubscribAtDifferentTime() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();
		MessageListener listener1 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener1:" + msg);

			}
		};
		MessageListener listener2 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener2:" + msg);
			}

		};
		pubsub.subscribe("foo", listener1);
		pubsub.publish("foo", "hello");
		pubsub.publish("foo", "world");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pubsub.subscribe("foo", listener2);
		pubsub.publish("foo", "from");
		pubsub.publish("foo", "me");
	}

	/**
	 * this method is to test single listener listening to a topic with 2 latest
	 * history messages displayed, the expected result is "Listener1: me",
	 * "Listener1 from", "Listener1: hello", "Listener1: world"
	 */
	public void testMessageDeliveryWithHistorySupport() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();
		MessageListener listener1 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener1:" + msg);

			}
		};

		pubsub.publish("foo", "HELLO");
		pubsub.publish("foo", "hi");
		pubsub.publish("foo", "from");
		pubsub.publish("foo", "me");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pubsub.subscribe("foo", listener1, 2);
		pubsub.publish("foo", "hello");
		pubsub.publish("foo", "world");
	}

	/**
	 * this method is to test two message listeners listening to the same topic
	 * the expected result is each listener print "hello", "world", "from", "me"
	 * in order
	 */
	public void testOrderedMessageDelivery() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();
		MessageListener listener1 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener1:" + msg);

			}
		};
		MessageListener listener2 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener2:" + msg);
			}

		};
		pubsub.subscribe("foo", listener1);
		pubsub.subscribe("foo", listener2);

		pubsub.publish("foo", "hello");
		pubsub.publish("foo", "world");
		pubsub.publish("foo", "from");
		pubsub.publish("foo", "me");

	}

	/**
	 * this method is to test two different listeners listening on two different
	 * topics, the expected result is listener 1 print "Listener1: hello"
	 * listener 2 print "Listener 2: hello", "Listener2: world"
	 */
	public void testNormalCaseWithTwoListener() {
		TopicSubscriber pubsub = new DefaultTopicSubscriberImpl();
		MessageListener listener1 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener1:" + msg);

			}
		};
		MessageListener listener2 = new MessageListener() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Listener2:" + msg);
			}

		};
		pubsub.subscribe("foo", listener1);
		pubsub.subscribe("foo", listener2);
		pubsub.subscribe("bar", listener2);
		pubsub.publish("foo", "hello");
		pubsub.publish("bar", "world");

	}

	public static final void main(String[] args) {

		PubSubTest pubTest = new PubSubTest();
		System.out
				.println("========testNormalCaseWithTwoListener================");
		System.out
				.println("expected result is listener 1 print \"Listener1: hello\" ");
		System.out
				.println("listener 2 print \"Listener 2: hello\", \"Listener2: world\", order is not applicable.");
		System.out
				.println("===============================================================");

		pubTest.testNormalCaseWithTwoListener();
		try {
			Thread.sleep(10); // this statement is to syn. up the thread output
								// so that it is possible to read.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println("=====================testOrderedMessageDelivery================");
		System.out
				.println("expected result is each  listener print \"hello\", \"world\", \"from\", \"me\" in order ");
		System.out
				.println("===============================================================");
		pubTest.testOrderedMessageDelivery();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println("=====================testMessageDeliveryWithHistorySupport================");
		System.out
				.println("expected result is \"Listener1: me\", \"Listener1 from\", \"Listener1: hello\", \"Listener1: world\"");
		System.out
				.println("==========================================================================");

		pubTest.testMessageDeliveryWithHistorySupport();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println("=====================testTwoDifferentListenerSubscribAtDifferentTime================");
		System.out
				.println("expected result is listener 1 displays \"Listener1: hello\", \"Listener1: world\", \"Listener1: from\", \"Listener1: me\".");
		System.out
				.println("listener 2 displays \"Listener2: from\", \"Listener2: me\".");
		System.out
				.println("==========================================================================");
		pubTest.testTwoDifferentListenerSubscribAtDifferentTime();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println("=====================testTwoDifferentListenerSubscribAtDifferentTimeWithHistorySupport================");
		System.out
				.println("expected result is: listener 1 displays \"Listener1: hello\", \"Listener1: world\", \"Listener1: from\", \"Listener1: me\".");
		System.out
				.println("listener 2 displays \"Listener2: world\",\"Listener2: from\", \"Listener2: me\".");
		System.out
				.println("==========================================================================");

		pubTest.testTwoDifferentListenerSubscribAtDifferentTimeWithHistorySupport();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println("=====================testErrorConditionWhenHistoryCountIsTooLarge================");
		System.out.println("expected result is: \"PASSED\"");
		System.out
				.println("==========================================================================");

		pubTest.testErrorConditionWhenHistoryCountIsTooLarge();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out
				.println("=====================testPublishWhenMessageIsEmpty================");
		System.out.println("expected result is: \"PASSED\"");
		System.out
				.println("==========================================================================");
		pubTest.testPublishWhenMessageIsEmpty();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
