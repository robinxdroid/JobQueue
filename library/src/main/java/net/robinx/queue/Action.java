package net.robinx.queue;

/**
 * Created by Robin on 2016/6/28 17:00.
 */
public interface Action<T extends Job<T>> {
    void call(T element);
}
