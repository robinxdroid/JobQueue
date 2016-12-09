package net.robinx.queue;

import android.os.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Robin on 2016/6/27 17:21.
 */
public class JobDispatcher<T extends Job<T>> extends Thread {

    private final BlockingQueue<T> mJobQueue;

    private Interceptor<T> mInterceptor;

    private Delivery<T> mDelivery;

    private List<JobHandlerListener<T>> mJobHandlerListeners = new ArrayList<>();

    private Action<T> mAction;

    private volatile boolean mQuit = false;

    public JobDispatcher(BlockingQueue<T> jobQueue, Interceptor<T> interceptor, Delivery<T> delivery, List<JobHandlerListener<T>> jobHandlerListeners) {
        this.mJobQueue = jobQueue;
        this.mInterceptor = interceptor;
        this.mDelivery = delivery;
        if (jobHandlerListeners != null) {
            this.mJobHandlerListeners = jobHandlerListeners;
        }

    }


    @Override
    public void run() {
        CLog.v("start new dispatcher:%s", getName());
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        while (true) {
            try {
                T element = mJobQueue.take();
                CLog.d("job-queue-take,current thread:%s", getName());
                if (element == null) {
                    CLog.d("element is null,current thread:%s", getName());
                    continue;
                }

                if (element.isCancel()) {
                    CLog.e("job is cancel when run");
                    CLog.d("element is canceled,current thread:%s", getName());
                    mDelivery.deliveryCancel(mJobHandlerListeners, element);
                    continue;
                }

                if (mInterceptor != null && mInterceptor.interceptCondition(element)) {
                    mInterceptor.onIntercept(element);
                    continue;
                }

                mDelivery.deliveryPrepare(mJobHandlerListeners, element);

                if (this.mAction == null) {
                    CLog.d("action is null,current thread:%s", getName());
                    continue;
                }
                CLog.d("job run start,current thread:%s", getName());
                this.mAction.call(element);
                CLog.d("job run finish,current thread:%s", getName());

                element.finish();

                CLog.d("element is finished,current thread:%s", getName());
                mDelivery.deliveryFinish(mJobHandlerListeners, element);

            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }
        }
    }

    public void setAction(Action<T> action) {
        this.mAction = action;
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }
}
