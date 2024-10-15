package com.github.jobqueue

import android.os.Process
import java.util.concurrent.BlockingQueue
import kotlin.concurrent.Volatile

/**
 * Created by Robin on 2016/6/27 17:21.
 */
class JobDispatcher<T : Job<T>>(
    private val mJobQueue: BlockingQueue<T>,
    private val mInterceptor: Interceptor<T>?,
    private val mDelivery: Delivery<T>,
    jobHandlerListeners: List<JobHandlerListener<T>>?
) : Thread() {
    private var mJobHandlerListeners: List<JobHandlerListener<T>> = ArrayList()

    private var mAction: Action<T>? = null

    @Volatile
    private var mQuit = false

    init {
        if (jobHandlerListeners != null) {
            this.mJobHandlerListeners = jobHandlerListeners
        }
    }


    override fun run() {
        CLog.v("start new dispatcher:%s", name)
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)

        while (true) {
            try {
                val element = mJobQueue.take()
                CLog.d("job-queue-take,current thread:%s", name)
                if (element == null) {
                    CLog.d("element is null,current thread:%s", name)
                    continue
                }

                if (element.isCancel) {
                    CLog.e("job is cancel when run")
                    CLog.d("element is canceled,current thread:%s", name)
                    mDelivery.deliveryCancel(mJobHandlerListeners, element)
                    continue
                }

                if (mInterceptor != null && mInterceptor.interceptCondition(element)) {
                    mInterceptor.onIntercept(element)
                    continue
                }

                mDelivery.deliveryPrepare(mJobHandlerListeners, element)

                if (this.mAction == null) {
                    CLog.d("action is null,current thread:%s", name)
                    continue
                }
                CLog.d("job run start,current thread:%s", name)
                mAction?.call(element)
                CLog.d("job run finish,current thread:%s", name)

                element.finish()

                CLog.d("element is finished,current thread:%s", name)
                mDelivery.deliveryFinish(mJobHandlerListeners, element)
            } catch (e: InterruptedException) {
                if (mQuit) {
                    return
                }
                continue
            }
        }
    }

    fun setAction(action: Action<T>?) {
        this.mAction = action
    }

    fun quit() {
        mQuit = true
        interrupt()
    }
}
