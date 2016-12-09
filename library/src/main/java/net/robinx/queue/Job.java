package net.robinx.queue;

/**
 * Created by Robin on 2016/6/28 16:52.
 */
public abstract class Job<T extends Job<T>> implements java.lang.Comparable<T> {
    protected Object tag;

    protected boolean isCancel;
    protected boolean isFinish;

    protected Integer mSequence;
    protected Priority mPriority = Priority.NORMAL;

    public Object getTag() {
        return tag;
    }

    public Job tag(Object tag) {
        this.tag = tag;
        return this;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public Job priority(Priority priority) {
        mPriority = priority;
        return this;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public Job cancel() {
        isCancel = true;
        return this;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public Job finish() {
        isFinish = true;
        return this;
    }

    public Integer getSequence() {
        return mSequence;
    }

    public Job sequence(Integer sequence) {
        mSequence = sequence;
        return this;
    }

    @Override
    public int compareTo(T another) {
        Priority left = this.getPriority();
        Priority right = another.getPriority();
        return left == right ? this.mSequence - another.mSequence : right.ordinal() - left.ordinal();
    }

    public abstract Object getRepeatFilterKey();
}
