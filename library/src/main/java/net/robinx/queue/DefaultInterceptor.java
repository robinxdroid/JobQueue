package net.robinx.queue;

/**
 * Created by Robin on 2016/12/5 10:57.
 */

public class DefaultInterceptor<T extends Job<T>> implements Interceptor<T> {

    @Override
    public boolean interceptCondition(T job) {
        return false;
    }

    @Override
    public void onIntercept(T job) {
    }
}
