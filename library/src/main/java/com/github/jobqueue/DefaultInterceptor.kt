package com.github.jobqueue

/**
 * Created by Robin on 2016/12/5 10:57.
 */
class DefaultInterceptor<T : Job<T>> : Interceptor<T> {
    override fun interceptCondition(job: T): Boolean {
        return false
    }

    override fun onIntercept(job: T) {
    }
}
