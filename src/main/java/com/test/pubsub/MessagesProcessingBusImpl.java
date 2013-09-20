package com.test.pubsub;

import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;

/**
 * a message delivery bus for deliver the message to all registered listeners.
 * message order is preserved on each of the subscriber.
 * This class contains two inner classes, one is the Dispatcher, the other is
 * the DispatchWorker. Dispatcher is the monitoring thread whose responsibility
 * is to submit each message to a thread pool DispatchWorker thread to invoke
 * the listeners.
 * 
 * @author henry liang
 * 
 */
public class MessagesProcessingBusImpl implements MessageProcessingBus {
	private static final int MAX_POOL_SIZE = 10;
	private static final ExecutorService pool = Executors
			.newFixedThreadPool(MAX_POOL_SIZE);

	private final int maxSize;
	private final FixLengthConcurrentQueue<String> histories;
	private final CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<String>();
	private final CopyOnWriteArrayList<MessageListener> listeners = new CopyOnWriteArrayList<MessageListener>();
	private final Dispatcher dispatcher;

	/**
	 * construct a message processing bus, specify the maximum history size.
	 * 
	 * @param maxSize
	 *            the maximum history size
	 */
	public MessagesProcessingBusImpl(int maxSize) {
		this.maxSize = maxSize > 0 ? maxSize : 0;
		this.histories = new FixLengthConcurrentQueue<String>(this.maxSize);
		this.dispatcher = new Dispatcher();
		dispatcher.startDispatchIfNotStarted();
	}

	/**
	 * construct a message processing bus and set the maximum history size and
	 * deliver interval.
	 * 
	 * @param maxSize
	 *            the supported maximum history size.
	 * @param deliverInterval
	 *            time in second
	 */
	public MessagesProcessingBusImpl(int maxSize, int deliverInterval) {
		this.maxSize = maxSize > 0 ? maxSize : 0;
		this.histories = new FixLengthConcurrentQueue<String>(this.maxSize);
		this.dispatcher = new Dispatcher();
		this.dispatcher.setDeliverInterval(deliverInterval);
		dispatcher.startDispatchIfNotStarted();
	}

	@Override
	public void subscribe(MessageListener listener) {
		CopyOnWriteArrayList<MessageListener> listeners = this.getListeners();
		listeners.add(listener);
	}

	@Override
	public void subscribe(MessageListener listener, int historyCount) {
		if (historyCount < 1) {
			this.subscribe(listener);
			return;
		}

		if (historyCount > this.maxSize) {
			throw new IllegalArgumentException("history count is too large.");
		}

		Object[] obs = this.histories.toArray();
		int eleLength = obs.length;
		String[] elements = new String[eleLength];
		
		for(int i =0; i < obs.length; i++)
		{
			String element = (String)obs[i];
			elements[i] = element;
		}

		for (int i = 0; i < Math.min(eleLength,historyCount); i++) {
			String latestMsg = elements[(eleLength - 1 - i)];
            if(StringUtils.isNotBlank(latestMsg))
            {
            	listener.onMessage(latestMsg);
            }
		}
		this.subscribe(listener);
	}

	@Override
	public void publish(String message) {
		if(StringUtils.isBlank(message))
		{
			throw new IllegalArgumentException("no message to publish.");
		}
		this.messages.add(message);
		this.histories.add(message);
	}

	public int getMaxSize() {
		return maxSize;
	}

	public FixLengthConcurrentQueue<String> getHistories() {
		return histories;
	}

	public CopyOnWriteArrayList<String> getMessages() {
		return messages;
	}	

	public CopyOnWriteArrayList<MessageListener> getListeners() {
		return listeners;
	}


	/**
	 * A worker thread to invoke the registered listeners with a message.
	 * 
	 * @author henry liang
	 * 
	 */
	@SuppressWarnings("rawtypes")
	class DispatchWorker implements Callable {
		private CopyOnWriteArrayList<String> messages;
		private MessageListener listener;

		public DispatchWorker(CopyOnWriteArrayList<String> messages,
				MessageListener listener) {
			this.messages = messages;
			this.listener = listener;
		}

		@Override
		public Object call() throws Exception {
			try {
				for (String msg : messages) {
					try {
						listener.onMessage(msg);
					} catch (Exception e) {
						System.out
								.println("message listner fail to process the msg.");
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				System.out.println("dispatch fail.");
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * A Thread that dispatch the message to the message listener
	 * 
	 */
	class Dispatcher implements Runnable {
		private final ReadWriteLock stateLock = new ReentrantReadWriteLock();

		private long deliverIntervalMillis = 5;

		private boolean running = false;

		private final Thread dispatchThread;

		public Dispatcher() {
			dispatchThread = new Thread(this, "MessageBus-Dispatcher");
			dispatchThread.setDaemon(true);
		}

		public void run() {

			while (running) {
				dispatch();
				try {
					Thread.sleep(deliverIntervalMillis);
				} catch (InterruptedException e) {

					System.out.println("dispatcher thread interrupted.");
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("unchecked")
		private void dispatch() {
			try {
				CopyOnWriteArrayList<String> temp = (CopyOnWriteArrayList<String>)messages.clone();;
				for(MessageListener listener : listeners)
				{
					pool.submit(new DispatchWorker(temp, listener));
				}
				messages.removeAll(temp);
			} catch (Exception e) {
				System.out.println("dispatch fail.");
				e.printStackTrace();
			}
		}

		/**
		 * Kick off this thread to deliver the message.
		 * 
		 */
		public void startDispatch() {
			stateLock.writeLock().lock();

			try {
				if (!running) {
					running = true;
					dispatchThread.start();
				}
			} finally {
				stateLock.writeLock().unlock();
			}
		}

		/**
		 * If this thread has not started, then start it. Otherwise just return;
		 */
		public void startDispatchIfNotStarted() {
			stateLock.readLock().lock();
			try {
				if (running) {
					return;
				}
			} finally {
				stateLock.readLock().unlock();
			}

			stateLock.writeLock().lock();
			try {
				if (!running) {
					running = true;
					dispatchThread.start();
				}
			} finally {
				stateLock.writeLock().unlock();
			}
		}

		/**
		 * Get the interval of delivering.
		 * 
		 * @return The time in seconds.
		 */
		public int getDeliverInterval() {
			stateLock.readLock().lock();

			try {
				return (int) deliverIntervalMillis / 1000;
			} finally {
				stateLock.readLock().unlock();
			}
		}

		/**
		 * Set the interval of message delivering.
		 * 
		 * @param deliverInterval
		 *            The time in seconds
		 */
		public void setDeliverInterval(long deliverInterval) {
			stateLock.writeLock().lock();

			try {
				this.deliverIntervalMillis = deliverInterval * 1000;
			} finally {
				stateLock.writeLock().unlock();
			}
		}
	}

}
