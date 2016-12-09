package net.robinx.queue;

/**
 * Created by Robin on 2016/6/28 18:12.
 */
public class JobHandlerListenerAdapter<T extends Job<T>> implements JobHandlerListener<T> {
    @Override
    public void onPrepare(T job) {

    }

    @Override
    public void onCancel(T job) {

    }

    @Override
    public void onFinish(T job) {

    }
}
