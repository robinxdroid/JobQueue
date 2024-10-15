package com.github.jobqueue

/**
 * Created by Robin on 2016/12/5 10:44.
 */
interface Interceptor<T : Job<T>> {
    fun interceptCondition(job: T): Boolean
    fun onIntercept(job: T)
}
