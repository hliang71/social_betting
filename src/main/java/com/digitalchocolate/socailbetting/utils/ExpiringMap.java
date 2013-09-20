package com.digitalchocolate.socailbetting.utils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


/**
 * A map with expiration.  This class contains a worker thread that will 
 * periodically check this class in order to determine if any objects 
 * should be removed based on the provided time-to-live value.
 *
 */
public class ExpiringMap<K, V> implements Map<K, V>, Serializable{
   
	private static final long serialVersionUID = -716098486898234979L;
	private static final Logger log = Logger.getLogger(ExpiringMap.class);
    public static final int DEFAULT_TIME_TO_LIVE = 60;
    public static final int MAX_PROCESS_SIZE=6000000;
    private static final int MAX_POOL_SIZE=30;
	private static final ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL_SIZE);
	
   
    public static final int DEFAULT_EXPIRATION_INTERVAL = 1;

    private static volatile int expirerCount = 1;

    private final ConcurrentMap<K, ExpiringObject> delegate;

    private final CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners;

    private final Expirer expirer;
    
    private String identifier;

    /**
     * Creates a new instance of ExpiringMap using the default values 
     *
     */
    public ExpiringMap() {
        this(DEFAULT_TIME_TO_LIVE, DEFAULT_EXPIRATION_INTERVAL);
    }

    /**
     * Creates a new instance of ExpiringMap using the supplied 
     * time-to-live value and the default value for DEFAULT_EXPIRATION_INTERVAL
     *
     * @param timeToLive
     *  The time-to-live value (seconds)
     */
    public ExpiringMap(int timeToLive) {
        this(timeToLive, DEFAULT_EXPIRATION_INTERVAL);
    }

    /**
     * Creates a new instance of ExpiringMap using the supplied values and 
     * a ConcurrentHashMap for the internal data structure.
     *
     * @param timeToLive
     *  The time-to-live value (seconds)
     * @param expirationInterval
     *  The time between checks to see if a value should be removed (seconds)
     */
    public ExpiringMap(int timeToLive, int expirationInterval) {
        this(new ConcurrentHashMap<K, ExpiringObject>(),
                new CopyOnWriteArrayList<ExpirationListener<V>>(), timeToLive,
                expirationInterval);
    }

    
    public ExpiringMap(int timeToLive, int expirationInterval, String identifier) {
        this(new ConcurrentHazelcastMap<K, ExpiringObject>(identifier+timeToLive),
                new CopyOnWriteArrayList<ExpirationListener<V>>(), timeToLive,
                expirationInterval);
        this.identifier = identifier;
    }
    
    public ExpiringMap(int timeToLive, int expirationInterval, String identifier, ApplicationContext ctx) {
        this(new ConcurrentRedisMap<K, ExpiringObject>(identifier+timeToLive, ctx, timeToLive),
                new CopyOnWriteArrayList<ExpirationListener<V>>(), timeToLive,
                expirationInterval);
        this.identifier = identifier;
    }
    
    private ExpiringMap(ConcurrentRedisMap<K, ExpiringObject> delegate,
            CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners,
            int timeToLive, int expirationInterval) {
        this.delegate = delegate;
        this.expirationListeners = expirationListeners;

        this.expirer = new Expirer();
        expirer.setTimeToLive(timeToLive);
        expirer.setExpirationInterval(expirationInterval);
    }
    private ExpiringMap(ConcurrentHazelcastMap<K, ExpiringObject> delegate,
            CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners,
            int timeToLive, int expirationInterval) {
        this.delegate = delegate;
        this.expirationListeners = expirationListeners;

        this.expirer = new Expirer();
        expirer.setTimeToLive(timeToLive);
        expirer.setExpirationInterval(expirationInterval);
    }
    private ExpiringMap(ConcurrentHashMap<K, ExpiringObject> delegate,
            CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners,
            int timeToLive, int expirationInterval) {
        this.delegate = delegate;
        this.expirationListeners = expirationListeners;

        this.expirer = new Expirer();
        expirer.setTimeToLive(timeToLive);
        expirer.setExpirationInterval(expirationInterval);
    }

    public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public V put(K key, V value) {
        ExpiringObject<K,V> answer = delegate.put(key, new ExpiringObject<K,V>(key,
                value, System.currentTimeMillis()));
        if (answer == null) {
            return null;
        }

        return answer.getValue();
    }
    
    
    public V get(Object key) {
    	ExpiringObject<K,V> object = delegate.get(key);

        if (object != null) {

            return object.getValue();
        }

        return null; 
    }
    /** not touch the access time.
    public V get(Object key) {
        ExpiringObject object = delegate.get(key);

        if (object != null) {
            object.setLastAccessTime(System.currentTimeMillis());

            return object.getValue();
        }

        return null;
    }
    */
    public V remove(Object key) {
        ExpiringObject<K,V> answer = delegate.remove(key);
        if (answer == null) {
            return null;
        }

        return answer.getValue();
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public void clear() {
        delegate.clear();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public void putAll(Map<? extends K, ? extends V> inMap) {
        for (Entry<? extends K, ? extends V> e : inMap.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public void addExpirationListener(ExpirationListener<V> listener) {
        expirationListeners.add(listener);
    }

    public void removeExpirationListener(
            ExpirationListener<V> listener) {
        expirationListeners.remove(listener);
    }

    public Expirer getExpirer() {
        return expirer;
    }

    public int getExpirationInterval() {
        return expirer.getExpirationInterval();
    }

    public int getTimeToLive() {
        return expirer.getTimeToLive();
    }

    public void setExpirationInterval(int expirationInterval) {
        expirer.setExpirationInterval(expirationInterval);
    }

    public void setTimeToLive(int timeToLive) {
        expirer.setTimeToLive(timeToLive);
    }

    
    @SuppressWarnings("rawtypes")
	public class ExpirerWorker implements Callable
    {
    	private ConcurrentMap<K, ExpiringObject> delegate;
    	private CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners;
    	private ExpiringObject<K,V> o;
    	private long timeToLiveMillis;
        
        public ExpirerWorker (ConcurrentMap<K, ExpiringObject> delegate, CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners, ExpiringObject<K,V> o, long timeToLiveMillis) {
            this.delegate = delegate;
            this.expirationListeners = expirationListeners;
            this.o = o;
            this.timeToLiveMillis = timeToLiveMillis;
        }
        
        
		@Override
		public Object call() throws Exception {
			try
    		{
        		this.delegate.remove(o.getKey());
                for (ExpirationListener<V> listener : expirationListeners) {
                	try{
                		listener.expired(o.getValue());
                	}catch(Exception e)
                	{
                		log.error("expiring but failed. put it back to expiring map:"+ e);
                		long resetTime = timeToLiveMillis - 3* 1000;
                		long currentTimeMillis = System.currentTimeMillis();
    					o.setLastAccessTime(currentTimeMillis-resetTime);				
                		//o.setLastAccessTime(System.currentTimeMillis());
                		delegate.put(o.getKey(), o);
                	}
                }
    		}catch(Exception e)
    		{
    			log.error("expiring worker thread run expire failed:", e);
    		}
			return null;
		}

    }
    /**
     * A Thread that monitors an ExpiringMap and will remove
     * elements that have passed the threshold.
     *
     */ 
    public class Expirer implements Runnable {
        private final ReadWriteLock stateLock = new ReentrantReadWriteLock();
       

        private long timeToLiveMillis;

        private long expirationIntervalMillis;

        private boolean running = false;

        private final Thread expirerThread;

        /**
         * Creates a new instance of Expirer.  
         *
         */
        public Expirer() {
            expirerThread = new Thread(this, "ExpiringMapExpirer-"
                    + expirerCount++);
            expirerThread.setDaemon(true);
        }

        public void run() {
            while (running) {
                processExpires();

                try {
                    Thread.sleep(expirationIntervalMillis);
                } catch (InterruptedException e) {
                }
            }
        }

        private void processExpires() {
        	if(log.isDebugEnabled())log.debug("#######process expire###########");
        	try
        	{
	            long timeNow = System.currentTimeMillis();
	            List<Future> list = new ArrayList<Future>();
	            Collection<ExpiringObject> obs = delegate.values();
	            for (ExpiringObject o : obs) {
	                if(o != null)
	                {
		                if (timeToLiveMillis <= 0) {
		                    continue;
		                }
		
		                long timeIdle = timeNow - o.getLastAccessTime();
		
		                if (timeIdle >= timeToLiveMillis) {
		                    
		                	Future fu = pool.submit(new ExpirerWorker (delegate, expirationListeners, o, timeToLiveMillis) );
		                	list.add(fu);
		                	
		                }
	                }
	            }
	           for(Future f : list)
	            {
	            	try
	            	{
	            		f.get();
	            		if(!f.isDone())
	            		{
	            			log.error("task execution failed.");
	            		}
	            	}catch(Exception e)
	            	{
	            		log.info("processing expired failed."+e);
	            	}
	            }
	            if(!list.isEmpty())
	            {
	            	log.info("########################## process expire done###########################");
	            }
        	}catch(Exception e)
        	{
        		log.error("process expire through error:", e);
        	}
        }

        /**
         * Kick off this thread which will look for old objects and remove them.
         *
         */
        public void startExpiring() {
            stateLock.writeLock().lock();

            try {
                if (!running) {
                    running = true;
                    expirerThread.start();
                }
            } finally {
                stateLock.writeLock().unlock();
            }
        }

        /**
         * If this thread has not started, then start it.  
         * Otherwise just return;
         */
        public void startExpiringIfNotStarted() {
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
                    expirerThread.start();
                }
            } finally {
                stateLock.writeLock().unlock();
            }
        }

        /**
         * Stop the thread from monitoring the map.
         */
        public void stopExpiring() {
            stateLock.writeLock().lock();

            try {
                if (running) {
                    running = false;
                    expirerThread.interrupt();
                }
            } finally {
                stateLock.writeLock().unlock();
            }
        }

        /**
         * Checks to see if the thread is running
         *
         * @return
         *  If the thread is running, true.  Otherwise false.
         */
        public boolean isRunning() {
            stateLock.readLock().lock();

            try {
                return running;
            } finally {
                stateLock.readLock().unlock();
            }
        }

        /**
         * Returns the Time-to-live value.
         *
         * @return
         *  The time-to-live (seconds)
         */
        public int getTimeToLive() {
            stateLock.readLock().lock();

            try {
                return (int) timeToLiveMillis / 1000;
            } finally {
                stateLock.readLock().unlock();
            }
        }

        /**
         * Update the value for the time-to-live
         *
         * @param timeToLive
         *  The time-to-live (seconds)
         */
        public void setTimeToLive(long timeToLive) {
            stateLock.writeLock().lock();

            try {
                this.timeToLiveMillis = timeToLive * 1000;
            } finally {
                stateLock.writeLock().unlock();
            }
        }

        /**
         * Get the interval in which an object will live in the map before
         * it is removed.
         *
         * @return
         *  The time in seconds.
         */
        public int getExpirationInterval() {
            stateLock.readLock().lock();

            try {
                return (int) expirationIntervalMillis / 1000;
            } finally {
                stateLock.readLock().unlock();
            }
        }

        /**
         * Set the interval in which an object will live in the map before
         * it is removed.
         *
         * @param expirationInterval
         *  The time in seconds
         */
        public void setExpirationInterval(long expirationInterval) {
            stateLock.writeLock().lock();

            try {
                this.expirationIntervalMillis = expirationInterval * 1000;
            } finally {
                stateLock.writeLock().unlock();
            }
        }
    }
}


