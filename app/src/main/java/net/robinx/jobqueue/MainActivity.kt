package net.robinx.jobqueue

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.jobqueue.Action
import com.github.jobqueue.CLog
import com.github.jobqueue.Interceptor
import com.github.jobqueue.Job
import com.github.jobqueue.JobHandler
import com.github.jobqueue.JobHandlerListener
import com.github.jobqueue.Priority

class MainActivity : AppCompatActivity() {
    private var mCount = 0

    private var mCacheJobHandler: JobHandler<TestJob>? = null
    private var mNetworkJobHandler: JobHandler<TestJob>? = null

    private var tvTaskState: TextView? = null

    private val cacheTaskAction = Action<TestJob> { element ->
            try {
                Thread.sleep(1500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            CLog.i("执行缓存队列任务" + element.count)
            runOnUiThread {
                tvTaskState?.text =
                    tvTaskState?.text.toString() + "\n" + "执行缓存队列任务" + element.count
            }
        }

    private val networkTaskAction = Action<TestJob> { element ->
            try {
                Thread.sleep(1500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            CLog.i("执行网络队列任务" + element.count)
            runOnUiThread {
                tvTaskState?.text =
                    tvTaskState?.text.toString() + "\n" + "执行网络队列任务" + element.count
            }
        }


    private val mCacheJobHandlerListener: JobHandlerListener<TestJob> =
        object : JobHandlerListener<TestJob> {
            override fun onPrepare(job: TestJob) {
                CLog.i("读缓存准备" + job.count)
            }

            override fun onCancel(job: TestJob) {
                CLog.i("读缓存取消" + job.count)
            }

            override fun onFinish(job: TestJob) {
                CLog.i("读缓存完成" + job.count)
            }
        }

    private val mNetworkJobHandlerListener: JobHandlerListener<TestJob> =
        object : JobHandlerListener<TestJob> {
            override fun onPrepare(job: TestJob) {
                CLog.i("请求网络准备" + job.count)
            }

            override fun onCancel(job: TestJob) {
                CLog.i("请求网络取消" + job.count)
            }

            override fun onFinish(job: TestJob) {
                CLog.i("请求网络完成" + job.count)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTaskState = findViewById(R.id.tv_task_state)

        CLog.allowD = false

        mCacheJobHandler = JobHandler.Builder<TestJob>()
            .threadPoolSize(1)
            .interceptor(CacheJobInterceptor())
            .build()

        mNetworkJobHandler = JobHandler.Builder<TestJob>()
            .build()

        findViewById<View>(R.id.btn_add_job).setOnClickListener {
            val testJob = TestJob()
            if (mCount == 2) {
                testJob.priority(Priority.LOW)
            }

            if (mCount == 3) {
                testJob.priority(Priority.NORMAL)
            }

            if (mCount == 7) {
                testJob.priority(Priority.HIGH)
            }

            if (mCount == 8) {
                testJob.priority(Priority.IMMEDIATE)
            }

            testJob.setCount(mCount)
            testJob.tag(this@MainActivity)

            mCacheJobHandler?.enqueue(testJob)
                ?.action(cacheTaskAction)
                ?.addListener(mCacheJobHandlerListener)
            mCount++
        }

        findViewById<View>(R.id.btn_add_repeat_job).setOnClickListener {
            val testJob = TestJob()
            testJob.setCount(mCount - 1)
            testJob.tag(this@MainActivity)
            mCacheJobHandler?.enqueue(testJob)
                ?.action(cacheTaskAction)
                ?.addListener(mCacheJobHandlerListener)
        }

        findViewById<View>(R.id.btn_cancel_job).setOnClickListener {
            mCacheJobHandler?.cancelAll(this@MainActivity)
            mNetworkJobHandler?.cancelAll(this@MainActivity)
        }
    }

    inner class CacheJobInterceptor : Interceptor<TestJob> {
        override fun interceptCondition(job: TestJob): Boolean {
            if (job.count == 5) {
                return true
            }
            return false
        }

        override fun onIntercept(job: TestJob) {
            mNetworkJobHandler?.enqueue(job)
                ?.action(networkTaskAction)
                ?.addListener(mNetworkJobHandlerListener)
        }
    }

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

}
