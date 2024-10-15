package com.github.jobqueue

/**
 * Created by Robin on 2016/6/28 16:30.
 */
interface JobHandlerListener<T : Job<T>> {
    fun onPrepare(job: T)
    fun onCancel(job: T)
    fun onFinish(job: T)
}
