package net.robinx.queue;

/**
 * Created by Robin on 2016/6/28 16:30.
 */
public interface JobHandlerListener<T extends Job<T>> {
    void onPrepare(T job);
    void onCancel(T job);
    void onFinish(T job);
}
