package com.github.jobqueue

/**
 * Created by Robin on 2016/6/28 18:12.
 */
open class JobHandlerListenerAdapter<T : Job<T>> : JobHandlerListener<T> {
    override fun onPrepare(job: T) {
    }

    override fun onCancel(job: T) {
    }

    override fun onFinish(job: T) {
    }
}
