package net.robinx.jobqueue;

import net.robinx.queue.Job;

/**
 * Created by Robin on 2016/6/28 17:29.
 */
public class TestJob extends Job<TestJob> {
    private int mCount;

    public int getCount() {
        return mCount;
    }

    public TestJob setCount(int count) {
        mCount = count;
        return this;
    }

    @Override
    public Object getRepeatFilterKey() {
        return mCount;
    }
}
