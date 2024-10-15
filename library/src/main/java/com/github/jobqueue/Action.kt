package com.github.jobqueue

/**
 * Created by Robin on 2016/6/28 17:00.
 */
fun interface Action<T : Job<T>> {
    fun call(element: T)
}
