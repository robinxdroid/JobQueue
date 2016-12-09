package net.robinx.queue;

import android.os.Handler;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Robin on 2016/12/6 15:44.
 */

public class DefaultDelivery<T extends Job<T>> implements Delivery<T> {
    private final Executor mResponsePoster;

    public DefaultDelivery(final Handler handler) {
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    public DefaultDelivery(Executor executor) {
        mResponsePoster = executor;
    }

    @Override
    public void deliveryPrepare(final List<JobHandlerListener<T>> listeners, final T job) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                if (job.isCancel()) {
                    CLog.e("job is cancel when deliveryPrepare");
                    deliveryCancel(listeners,job);
                    return;
                }

                CLog.d("deliveryPrepare,current thread:%s", Thread.currentThread().getName());
                for (int i = 0, size = listeners.size(); i < size; i++) {
                    listeners.get(i).onPrepare(job);

                }
            }
        });
    }

    @Override
    public void deliveryCancel(final List<JobHandlerListener<T>> listeners, final T job) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                CLog.d("deliveryCancel,current thread:%s", Thread.currentThread().getName());
                for (int i = 0, size = listeners.size(); i < size; i++) {
                    listeners.get(i).onCancel(job);
                }
            }
        });
    }

    @Override
    public void deliveryFinish(final List<JobHandlerListener<T>> listeners, final T job) {
        if (job.isCancel()) {
            CLog.e("job is cancel when deliveryFinish");
            deliveryCancel(listeners,job);
            return;
        }

        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                CLog.d("deliveryFinish,current thread:%s", Thread.currentThread().getName());
                for (int i = 0, size = listeners.size(); i < size; i++) {
                    listeners.get(i).onFinish(job);
                }
            }
        });
    }
}
