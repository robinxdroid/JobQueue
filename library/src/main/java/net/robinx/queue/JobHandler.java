package net.robinx.queue;

/**
 * Created by Robin on 2016/6/28 17:40.
 */
public class JobHandler<T extends Job<T>> {

    private JobQueue<T> mJobQueue;

    private int mThreadPoolSize;

    private Interceptor<T> mInterceptor;

    private JobHandler(Builder<T> builder){
        this.mThreadPoolSize = builder.threadPoolSize;
        this.mInterceptor = builder.interceptor;

        if (mThreadPoolSize == 0) {
            mThreadPoolSize = JobQueue.DEFAULT_THREAD_POOL_SIZE;
        }
        if (mInterceptor == null) {
            mInterceptor = new DefaultInterceptor<>();
        }

        mJobQueue = new JobQueue(mThreadPoolSize,mInterceptor);
        mJobQueue.start();
    }

    public JobQueue<T> enqueue(T element){

        return mJobQueue.add(element);
    }

    public <O> JobQueue<T> cancelAll(O tag){
        mJobQueue.cancelAll(tag);
        return mJobQueue;
    }

    public static class Builder<T extends Job<T>>{
        private int threadPoolSize;
        private Interceptor<T> interceptor;

        public Builder threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder interceptor(Interceptor<T> interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public JobHandler<T> build(){
            return new JobHandler<>(this);
        }
    }
}
