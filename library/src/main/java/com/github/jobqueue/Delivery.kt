package com.github.jobqueue

/**
 * Created by Robin on 2016/12/6 15:36.
 */
interface Delivery<T : Job<T>> {
    fun deliveryPrepare(listeners: List<JobHandlerListener<T>?>, job: T)

    fun deliveryCancel(listeners: List<JobHandlerListener<T>?>, job: T)

    fun deliveryFinish(listeners: List<JobHandlerListener<T>?>, job: T)
}
