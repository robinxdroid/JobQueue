package net.robinx.queue;

import java.util.List;

/**
 * Created by Robin on 2016/12/6 15:36.
 */

public interface Delivery<T extends Job<T>> {
    void deliveryPrepare(List<JobHandlerListener<T>> listeners, T job);

    void deliveryCancel(List<JobHandlerListener<T>> listeners, T job);

    void deliveryFinish(List<JobHandlerListener<T>> listeners, T job);
}
