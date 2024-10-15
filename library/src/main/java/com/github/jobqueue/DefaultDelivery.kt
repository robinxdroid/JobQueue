package com.github.jobqueue

import android.os.Handler
import java.util.concurrent.Executor

/**
 * Created by Robin on 2016/12/6 15:44.
 */
class DefaultDelivery<T : Job<T>> : Delivery<T> {
    private val mResponsePoster: Executor

    constructor(handler: Handler) {
        mResponsePoster = Executor { command -> handler.post(command) }
    }

    constructor(executor: Executor) {
        mResponsePoster = executor
    }

    override fun deliveryPrepare(listeners: List<JobHandlerListener<T>?>?, job: T) {
        mResponsePoster.execute(Runnable {
            if (job.isCancel) {
                CLog.e("job is cancel when deliveryPrepare")
                deliveryCancel(listeners, job)
                return@Runnable
            }
            CLog.d("deliveryPrepare,current thread:%s", Thread.currentThread().name)
            var i = 0
            val size = listeners!!.size
            while (i < size) {
                listeners[i]!!.onPrepare(job)

                i++
            }
        })
    }

    override fun deliveryCancel(listeners: List<JobHandlerListener<T>?>?, job: T) {
        mResponsePoster.execute {
            CLog.d("deliveryCancel,current thread:%s", Thread.currentThread().name)
            var i = 0
            val size = listeners!!.size
            while (i < size) {
                listeners[i]!!.onCancel(job)
                i++
            }
        }
    }

    override fun deliveryFinish(listeners: List<JobHandlerListener<T>?>?, job: T) {
        if (job.isCancel) {
            CLog.e("job is cancel when deliveryFinish")
            deliveryCancel(listeners, job)
            return
        }

        mResponsePoster.execute {
            CLog.d("deliveryFinish,current thread:%s", Thread.currentThread().name)
            var i = 0
            val size = listeners!!.size
            while (i < size) {
                listeners[i]!!.onFinish(job)
                i++
            }
        }
    }
}
