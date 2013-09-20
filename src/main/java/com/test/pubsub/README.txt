This is the implements for Challenge 2. Implemented features include:
1. void subscribe(topic, listener)
2. void publish(topic, message)
3. Ensure the class is thread safe (if using a language that supports threading, e.g. Java)
4. Implement queueing and history (e.g. a listener may request to subscribe with a history of 2, meaning that the most recent two messages should be delivered and all future messages should also be delivered - e.g. pubsub.subscribe("foo", listener2, 4)

two major classes are DefaultTopicSubscriberImple which implement the publish/subscriber feature, 
and MessageProcessingBusImpl which implement the message delivery, threading and etc.

please refer to the simple unit test class PubSubTest.java for reference on how to use the TopicSubscriber interface.

PubSubTest.java is a stand alone java program which can be run in any java runtime environment once it is compiled.

 