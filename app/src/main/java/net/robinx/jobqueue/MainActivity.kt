package net.robinx.jobqueue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.robinx.queue.Action;
import net.robinx.queue.CLog;
import net.robinx.queue.JobHandler;
import net.robinx.queue.Priority;
import net.robinx.queue.JobHandlerListener;

public class MainActivity extends AppCompatActivity {

    private int mCount;

    private JobHandler<TestJob> mCacheJobHandler;
    private JobHandler<TestJob> mNetworkJobHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CLog.allowD = false;

        mCacheJobHandler = new JobHandler.Builder<TestJob>()
                .threadPoolSize(1)
                .interceptor(new CacheJobInterceptor())
                .build();

        mNetworkJobHandler = new JobHandler.Builder<TestJob>()
                .build();

        findViewById(R.id.btn_add_job).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TestJob testJob = new TestJob();
                if (mCount == 2) {
                    testJob.priority(Priority.LOW);
                }

                if (mCount == 3) {
                    testJob.priority(Priority.NORMAL);
                }

                if (mCount == 7) {
                    testJob.priority(Priority.HIGH);
                }

                if (mCount == 8) {
                    testJob.priority(Priority.IMMEDIATE);
                }

                testJob.setCount(mCount);
                testJob.tag(MainActivity.this);

                mCacheJobHandler.enqueue(testJob)
                        .action(new Action<TestJob>() {
                            @Override
                            public void call(TestJob element) {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                CLog.i("读缓存"+element.getCount());
                            }
                        })
                        .addListener(mCacheJobHandlerListener);

                mCount++;

            }
        });

        findViewById(R.id.btn_add_repeat_job).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestJob testJob = new TestJob();
                testJob.setCount(mCount-1);
                testJob.tag(MainActivity.this);

                mCacheJobHandler.enqueue(testJob)
                        .action(new Action<TestJob>() {
                            @Override
                            public void call(TestJob element) {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                CLog.i("读缓存"+element.getCount());
                            }
                        })
                        .addListener(mCacheJobHandlerListener);

            }
        });

        findViewById(R.id.btn_cancel_job).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCacheJobHandler.cancelAll(MainActivity.this);
                mNetworkJobHandler.cancelAll(MainActivity.this);
            }
        });

    }

    public class CacheJobInterceptor implements net.robinx.queue.Interceptor<TestJob>{

        @Override
        public boolean interceptCondition(TestJob job) {
            if (job.getCount() == 5) {
                return true;
            }
            return false;
        }

        @Override
        public void onIntercept(final TestJob job) {
            mNetworkJobHandler.enqueue(job)
                    .action(new Action<TestJob>() {
                        @Override
                        public void call(TestJob element) {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            CLog.i("请求网络"+element.getCount());
                        }
                    })
                    .addListener(mNetworkJobHandlerListener);

        }
    }

    private JobHandlerListener<TestJob> mCacheJobHandlerListener = new JobHandlerListener<TestJob>() {
        @Override
        public void onPrepare(TestJob job) {
            CLog.i("读缓存准备"+job.getCount());
        }

        @Override
        public void onCancel(TestJob job) {
            CLog.i("读缓存取消"+job.getCount());
        }

        @Override
        public void onFinish(TestJob job) {
            CLog.i("读缓存完成"+job.getCount());
        }

    };

    private JobHandlerListener<TestJob> mNetworkJobHandlerListener = new JobHandlerListener<TestJob>() {

        @Override
        public void onPrepare(TestJob job) {
            CLog.i("请求网络准备"+job.getCount());
        }

        @Override
        public void onCancel(TestJob job) {
            CLog.i("请求网络取消"+job.getCount());
        }

        @Override
        public void onFinish(TestJob job) {
            CLog.i("请求网络完成"+job.getCount());
        }

    };

}
