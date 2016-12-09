package net.robinx.queue;

/**
 * Created by Robin on 2016/12/5 10:44.
 */

public interface Interceptor<T extends Job<T>> {
    boolean interceptCondition(T job);
    void onIntercept(T job);
}
