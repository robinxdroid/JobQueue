package com.github.jobqueue

/**
 * Created by Robin on 2016/6/28 17:40.
 */
class JobHandler<T : Job<T>> private constructor(builder: Builder<T>) {
    private val mJobQueue: JobQueue<T>

    private var mThreadPoolSize: Int

    private var mInterceptor: Interceptor<T>?

    init {
        this.mThreadPoolSize = builder.threadPoolSize
        this.mInterceptor = builder.interceptor

        if (mThreadPoolSize == 0) {
            mThreadPoolSize = JobQueue.DEFAULT_THREAD_POOL_SIZE
        }
        if (mInterceptor == null) {
            mInterceptor = DefaultInterceptor()
        }

        mJobQueue = JobQueue<T>(mThreadPoolSize, mInterceptor)
        mJobQueue.start()
    }

    fun enqueue(element: T): JobQueue<T> {
        return mJobQueue.add(element)
    }

    fun <O> cancelAll(tag: O): JobQueue<T> {
        mJobQueue.cancelAll(tag)
        return mJobQueue
    }

    class Builder<T : Job<T>> {
        internal var threadPoolSize: Int = 0
        var interceptor: Interceptor<T>? = null

        fun threadPoolSize(threadPoolSize: Int): Builder<T> {
            this.threadPoolSize = threadPoolSize
            return this
        }

        fun interceptor(interceptor: Interceptor<T>?): Builder<T> {
            this.interceptor = interceptor
            return this
        }

        fun build(): JobHandler<T> {
            return JobHandler(this)
        }
    }
}
