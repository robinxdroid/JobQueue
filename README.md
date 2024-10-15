# JobQueue
任务队列
## Support

* 基本的队列与线程配合，形成流水线式处理
* 优先级，任务处理有先后
* 多队列配合切换，把任务从一个队列抛出到另一个队列进行处理
* 拦截器，拦截处理任务，自定义拦截条件
* 任务取消，需要自由取消已经已经放入队列中的任务与正在执行的任务
* 重复任务过滤，后续重复任务进入等待队列等待，直至当前任务执行完毕后释放队列，并按序列执行

## Usage
###1.初始化一个处理器###

```kotlin
mCacheJobHandler = JobHandler.Builder<TestJob>()
        .threadPoolSize(1)
        .interceptor(CacheJobInterceptor())
        .build()
```

### 2.定义一个任务

继承`Job<T>`复写`getRepeatFilterKey()`函数

```kotlin
class TestJob : Job<TestJob>() {
        var count: Int = 0
        private set
        
        fun setCount(count: Int): TestJob {
            this.count = count
            return this
        }
        
        override val repeatFilterKey: Any
        get() = count
    }
```
### 3.任务进入队列执行

```kotlin
mCacheJobHandler?.enqueue(testJob)
                ?.action(cacheTaskAction)
                ?.addListener(mCacheJobHandlerListener)
```
---
## 其他用法
### 优先级

分四档，优先级越高，执行越靠前

```kotlin
testJob.priority(Priority.HIGH)
```

### 拦截器

```kotlin
class CacheJobInterceptor : Interceptor<TestJob> {
    override fun interceptCondition(job: TestJob): Boolean {
        if (job.count == 5) {
            return true
        }
        return false
    }

    override fun onIntercept(job: TestJob) {
        // 拦截后，做一些操作，例如将任务切换到其他队列执行
        mNetworkJobHandler?.enqueue(job)
            ?.action(networkTaskAction)
            ?.addListener(mNetworkJobHandlerListener)
    }
}
```
### 取消任务

```kotlin
mCacheJobHandler.cancelAll(tag)
```
