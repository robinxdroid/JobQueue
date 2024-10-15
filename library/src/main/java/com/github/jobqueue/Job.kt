package com.github.jobqueue

/**
 * Created by Robin on 2016/6/28 16:52.
 */
abstract class Job<T : Job<T>> : Comparable<T> {
    var tag: Any? = null
        protected set

    var isCancel: Boolean = false
        protected set
    var isFinish: Boolean = false
        protected set

    var sequence: Int? = null
        protected set
    var priority: Priority = Priority.NORMAL
        protected set

    fun tag(tag: Any?): Job<*> {
        this.tag = tag
        return this
    }

    fun priority(priority: Priority): Job<*> {
        this.priority = priority
        return this
    }

    fun cancel(): Job<*> {
        isCancel = true
        return this
    }

    fun finish(): Job<*> {
        isFinish = true
        return this
    }

    fun sequence(sequence: Int?): Job<*> {
        this.sequence = sequence
        return this
    }

    override fun compareTo(another: T): Int {
        val left = this.priority
        val right = another!!.priority
        return if (left == right) sequence!! - another.sequence!! else right.ordinal - left.ordinal
    }

    abstract val repeatFilterKey: Any?
}
