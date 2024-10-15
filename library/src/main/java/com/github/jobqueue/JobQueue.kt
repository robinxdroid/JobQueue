package com.github.jobqueue

import android.os.Handler
import android.os.Looper
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Robin on 2016/6/27 17:19.
 */
class JobQueue<T : Job<T>> @JvmOverloads constructor(
    threadPoolSize: Int,
    interceptor: Interceptor<T>? = DefaultInterceptor<T>()
) {
    private val mJobQueue = PriorityBlockingQueue<T>()

    private val mJobDispatchers: Array<JobDispatcher<T>?>

    private val mInterceptor: Interceptor<T>?

    private val mDelivery: Delivery<T>

    private val mWaitingJobQueues: MutableMap<Any?, Queue<T>?> = HashMap()

    private val mCurrentJobs: MutableSet<T> = HashSet()

    private val mJobHandlerListener: MutableList<JobHandlerListener<T>> = ArrayList()

    private val mSequenceGenerator = AtomicInteger()

    private var mJobHandlerCallback: JobHandlerListener<T>? = null

    @JvmOverloads
    constructor(interceptor: Interceptor<T>? = DefaultInterceptor<T>()) : this(
        DEFAULT_THREAD_POOL_SIZE, interceptor
    )

    init {
        mJobDispatchers = arrayOfNulls<JobDispatcher<T>?>(threadPoolSize)
        this.mInterceptor = interceptor
        mDelivery = DefaultDelivery(Handler(Looper.getMainLooper()))
    }

    fun start() {
        stop()

        val threadPoolExecutor =
            Executors.newFixedThreadPool(mJobDispatchers.size) as ThreadPoolExecutor
        for (i in mJobDispatchers.indices) {
            val savedDispatcher =
                JobDispatcher(mJobQueue, mInterceptor, mDelivery, mJobHandlerListener)
            mJobDispatchers[i] = savedDispatcher
            threadPoolExecutor.submit(savedDispatcher)
        }
    }

    fun stop() {
        for (i in mJobDispatchers.indices) {
            mJobDispatchers[i]?.quit()
        }
    }

    fun add(element: T): JobQueue<T> {
        CLog.i("add to queue")
        element.sequence(sequenceNumber)

        synchronized(mCurrentJobs) {
            mCurrentJobs.add(element)
        }

        if (mJobHandlerCallback == null) {
            mJobHandlerCallback = object : JobHandlerListenerAdapter<T>() {
                override fun onCancel(job: T) {
                    finish(job)
                }

                override fun onFinish(job: T) {
                    finish(job)
                }
            }
            addListener(mJobHandlerCallback!!)
        }

        val repeatTag = element.repeatFilterKey
        if (repeatTag != null) {
            synchronized(mWaitingJobQueues) {
                if (mWaitingJobQueues.containsKey(repeatTag)) {
                    var stagedRequests = mWaitingJobQueues[repeatTag]
                    if (stagedRequests == null) {
                        stagedRequests = LinkedList()
                    }
                    stagedRequests.add(element)
                    mWaitingJobQueues[repeatTag] = stagedRequests
                    CLog.i("Job for filterTag =$repeatTag is in flight, putting on hold.")
                } else {
                    mWaitingJobQueues[repeatTag] = null
                    mJobQueue.add(element)
                }
                return this
            }
        }

        mJobQueue.add(element)

        return this
    }

    fun finish(element: T) {
        synchronized(mCurrentJobs) {
            mCurrentJobs.remove(element)
        }

        var repeatTag = element.repeatFilterKey
        if (repeatTag != null) {
            synchronized(mWaitingJobQueues) {
                val waitingJobQueue = mWaitingJobQueues.remove(repeatTag)
                if (waitingJobQueue != null) {
                    CLog.i("Releasing " + waitingJobQueue.size + " waiting jobs for filterTag=" + repeatTag)
                    mJobQueue.addAll(waitingJobQueue)
                }
            }
        } else if (mWaitingJobQueues.size > 0) {  //Releasing all
            synchronized(mWaitingJobQueues) {
                val queueIterator: Iterator<Map.Entry<Any?, Queue<T>?>> =
                    mWaitingJobQueues.entries.iterator()
                while (queueIterator.hasNext()) {
                    val entry = queueIterator.next()
                    repeatTag = entry.key
                    val waitingJobQueue = mWaitingJobQueues.remove(repeatTag)
                    if (waitingJobQueue != null) {
                        CLog.i("Releasing " + waitingJobQueue.size + " waiting jobs for filterTag=" + repeatTag)
                        mJobQueue.addAll(waitingJobQueue)
                    }
                }
            }
        }
    }

    fun action(action: Action<T>?): JobQueue<T> {
        for (i in mJobDispatchers.indices) {
            mJobDispatchers[i]?.setAction(action)
        }
        return this
    }

    fun addListener(listener: JobHandlerListener<T>): JobQueue<T> {
        if (mJobHandlerListener.contains(listener)) {
            return this
        }
        mJobHandlerListener.add(listener)
        return this
    }

    private val sequenceNumber: Int
        get() = mSequenceGenerator.incrementAndGet()

    interface JobFilter<T> {
        fun apply(job: T): Boolean
    }

    fun cancelAll(filter: JobFilter<T>) {
        synchronized(mCurrentJobs) {
            for (job in mCurrentJobs) {
                if (filter.apply(job)) {
                    job.cancel()
                }
            }
        }
    }

    fun cancelAll(tag: Any?) {
        requireNotNull(tag) { "Cannot cancelAll with a null tag" }
        cancelAll(object : JobFilter<T> {
            override fun apply(job: T): Boolean {
                return job.tag === tag
            }
        })
    }

    companion object {
        const val DEFAULT_THREAD_POOL_SIZE: Int = 4
    }
}
