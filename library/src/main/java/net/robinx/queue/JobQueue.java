package net.robinx.queue;

import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Robin on 2016/6/27 17:19.
 */
public class JobQueue<T extends Job<T>> {

    public static final int DEFAULT_THREAD_POOL_SIZE = 4;

    private final PriorityBlockingQueue<T> mJobQueue = new PriorityBlockingQueue<T>();

    private JobDispatcher<T>[] mJobDispatchers;

    private Interceptor<T> mInterceptor;

    private Delivery<T> mDelivery;

    private final Map<Object, Queue<T>> mWaitingJobQueues = new HashMap<Object, Queue<T>>();

    private final Set<T> mCurrentJobs = new HashSet<T>();

    private List<JobHandlerListener<T>> mJobHandlerListener = new ArrayList<>();

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private JobHandlerListener<T> mJobHandlerCallback;

    public JobQueue() {
        this(new DefaultInterceptor<T>());
    }

    public JobQueue(int threadPoolSize) {
        this(threadPoolSize, new DefaultInterceptor<T>());
    }

    public JobQueue(Interceptor interceptor) {
        this(DEFAULT_THREAD_POOL_SIZE, interceptor);
    }

    public JobQueue(int threadPoolSize, Interceptor interceptor) {
        mJobDispatchers = new JobDispatcher[threadPoolSize];
        this.mInterceptor = interceptor;
        mDelivery = new DefaultDelivery<T>(new android.os.Handler(Looper.getMainLooper()));
    }

    public void start() {
        stop();

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(mJobDispatchers.length);
        for (int i = 0; i < mJobDispatchers.length; i++) {
            JobDispatcher<T> savedDispatcher = new JobDispatcher<T>(mJobQueue, mInterceptor, mDelivery, mJobHandlerListener);
            mJobDispatchers[i] = savedDispatcher;
            threadPoolExecutor.submit(savedDispatcher);
        }
    }

    public void stop() {
        for (int i = 0; i < mJobDispatchers.length; i++) {
            if (mJobDispatchers[i] != null) {
                mJobDispatchers[i].quit();
            }
        }
    }

    public JobQueue add(T element) {
        CLog.i("add to queue");
        element.sequence(getSequenceNumber());

        synchronized (mCurrentJobs) {
            mCurrentJobs.add(element);
        }

        if (mJobHandlerCallback == null) {
            mJobHandlerCallback= new JobHandlerListenerAdapter<T>() {

                @Override
                public void onCancel(T job) {
                    finish(job);
                }

                @Override
                public void onFinish(T job) {
                    finish(job);
                }
            };
            addListener(mJobHandlerCallback);
        }

        Object repeatTag = element.getRepeatFilterKey();
        if (repeatTag != null) {
            synchronized (mWaitingJobQueues) {
                if (mWaitingJobQueues.containsKey(repeatTag)) {
                    Queue<T> stagedRequests = mWaitingJobQueues.get(repeatTag);
                    if (stagedRequests == null) {
                        stagedRequests = new LinkedList<T>();
                    }
                    stagedRequests.add(element);
                    mWaitingJobQueues.put(repeatTag, stagedRequests);
                    CLog.i("Job for filterTag =" + repeatTag + " is in flight, putting on hold.");
                } else {
                    mWaitingJobQueues.put(repeatTag, null);
                    mJobQueue.add(element);
                }
                return this;
            }
        }

        mJobQueue.add(element);

        return this;
    }

    public void finish(T element) {
        synchronized (mCurrentJobs) {
            mCurrentJobs.remove(element);
        }

        Object repeatTag = element.getRepeatFilterKey();
        if (repeatTag != null) {
            synchronized (mWaitingJobQueues) {
                Queue<T> waitingJobQueue = mWaitingJobQueues.remove(repeatTag);
                if (waitingJobQueue != null) {
                    CLog.i("Releasing " + waitingJobQueue.size() + " waiting jobs for filterTag=" + repeatTag);
                    mJobQueue.addAll(waitingJobQueue);
                }
            }
        } else if (mWaitingJobQueues.size() > 0) {  //Releasing all
            synchronized (mWaitingJobQueues) {
                Iterator<Map.Entry<Object, Queue<T>>> queueIterator = mWaitingJobQueues.entrySet().iterator();
                while (queueIterator.hasNext()) {
                    Map.Entry<Object, Queue<T>> entry = queueIterator.next();
                    repeatTag = entry.getKey();
                    Queue<T> waitingJobQueue = mWaitingJobQueues.remove(repeatTag);
                    if (waitingJobQueue != null) {
                        CLog.i("Releasing " + waitingJobQueue.size() + " waiting jobs for filterTag=" + repeatTag);
                        mJobQueue.addAll(waitingJobQueue);
                    }
                }

            }
        }
    }

    public JobQueue action(Action<T> action) {
        for (int i = 0; i < mJobDispatchers.length; i++) {
            mJobDispatchers[i].setAction(action);
        }
        return this;
    }

    public JobQueue addListener(JobHandlerListener listener) {
        if (mJobHandlerListener.contains(listener)) {
            return this;
        }
        mJobHandlerListener.add(listener);
        return this;
    }

    private int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    public interface JobFilter<T> {
        boolean apply(T job);
    }

    public void cancelAll(JobFilter<T> filter) {
        synchronized (mCurrentJobs) {
            for (T job : mCurrentJobs) {
                if (filter.apply(job)) {
                    job.cancel();
                }
            }
        }
    }

    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new JobFilter<T>() {
            @Override
            public boolean apply(T job) {
                return job.getTag() == tag;
            }
        });
    }

}
