#JobQueue
任务队列
##Support

* 基本的队列与线程配合，形成流水线式处理
* 优先级，任务处理有先后
* 多队列配合切换，把任务从一个队列抛出到另一个队列进行处理
* 拦截器，拦截处理任务，自定义拦截条件
* 任务取消，需要自由取消已经已经放入队列中的任务与正在执行的任务
* 重复任务过滤，后续重复任务进入等待队列等待，直至当前任务执行完毕后释放队列，并按序列执行

##Usage
###1.初始化一个处理器

```java
mCacheJobHandler = new JobHandler.Builder<TestJob>()
       .threadPoolSize(1) //处理队列对应线程数量
       .interceptor(new CacheJobInterceptor()) //拦截器，用于拦截当前任务的处理
       .build();
```

###2.定义一个任务

继承`Job<T>`复写`getRepeatFilterKey()`函数

```java
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
    public Object getRepeatFilterKey() {  //重复任务过滤依据
        return mCount;
    }
}
```
###3.任务进入队列执行

```java
			mCacheJobHandler.enqueue(testJob)
                        .action(new Action<TestJob>() {
                            @Override
                            public void call(TestJob element) {
                                
                                CLog.i("具体操作");
                            }
                        })
                        .addListener(mCacheJobHandlerListener); //执行进度监听
```
---
##其他用法
###优先级

分四档，优先级越高，执行越靠前

```java
testJob.priority(Priority.HIGH);
```

###拦截器

```java
public class CacheJobInterceptor implements net.robinx.queue.Interceptor<TestJob>{

        @Override
        public boolean interceptCondition(TestJob job) { //拦截条件
            if (job.getCount() == 5) {
                return true;
            }
            return false;
        }

        @Override
        public void onIntercept(final TestJob job) {  //拦截后切换到其他队列，根据自己需求做相应动作
            mNetworkJobHandler.enqueue(job)
                    .action(new Action<TestJob>() {
                        @Override
                        public void call(TestJob element) {
                            
                            CLog.i("其他操作");
                        }
                    })
                    .addListener(mNetworkJobHandlerListener);

        }
    }
```
###取消任务

```java
mCacheJobHandler.cancelAll(tag);
```

##更多
你可以看 [这篇博客](http://robinx.net/2016/12/06/优雅的责任链模式/)

#About me
Email:735506404@robinx.net<br>
Blog:[www.robinx.net](http://www.robinx.net)