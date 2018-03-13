package com.scott.transer.handler;

import android.os.Looper;
import android.text.TextUtils;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ITaskEventDispatcher;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.Task;
import com.scott.transer.TaskErrorCode;
import com.scott.transer.TaskState;
import com.scott.transer.utils.Debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-14 15:31</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      如果实现其他的传输器需要继承自该类
 * </p>
 */
public abstract class BaseTaskHandler implements ITaskHandler {

    protected ITaskHandlerCallback mListenner;
    private volatile boolean isExit = false;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;
    private ThreadPoolExecutor mTaskHandleThreadPool;
    private volatile Task mTask;
    private HandleRunnable mHandleRunnable;
    private final long  MAX_DELAY_TIME = 1000;
    private final String TAG = BaseTaskHandler.class.getSimpleName();

    private long mLastCompleteLength = 0;
    private StateRunnable mStateRunnable;
    private Thread mStateThread;
    protected final int DEFAULT_PICE_SIZE = 1 * 1024 * 1024;

    private long mLastCaluteTime = 0;
    private long mLastCalculateLength = 0;

    private long mLastPiceSuccessfulTime = 0;

    //每片大小
    protected int getPiceBuffSize() {
        return DEFAULT_PICE_SIZE;
    }

    public interface SPEED_LIMIT_SIZE {
        long SPEED_100KB = 100 * 1024;
        long SPEED_200KB = 200 * 1024;
        long SPEED_300KB = 300 * 1024;
        long SPEED_500KB = 500 * 1024;
        long SPEED_1MB = 1 * 1024 * 1024;
        long SPEED_2MB = 2 * 1024 * 1024;
        long SPEED_5MB = 5 * 1024 * 1024;
        long SPEED_10MB = 10 * 1024 * 1024;
        long SPEED_UNLIMITED = -1;
    }

    public BaseTaskHandler() {
        mStateRunnable = new StateRunnable();
        mHandleRunnable = new HandleRunnable();
    }

    @Override
    public void setHandlerListenner(ITaskHandlerCallback l) {
        mListenner = l;
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    @Override
    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    @Override
    public void setThreadPool(ThreadPoolExecutor threadPool) {
        mTaskHandleThreadPool = threadPool;
    }


    //判断一片是否发送或接受成功
    protected abstract boolean isPiceSuccessful();

    //判断任务是否成功
    protected abstract boolean isSuccessful();

    //从数据源中读取一片
    protected abstract byte[] readPice(Task task) throws Exception;

    //写入一片到目标
    protected abstract void writePice(byte[] datas,Task task) throws Exception;

    //传输开始前
    protected abstract void prepare(ITask task) throws Exception;

    //当前这片从数据源中实际读取的大小
    protected abstract int getPiceRealSize();

    //文件大小，下载为服务器的文件大小 。 上传为本地的文件大小
    protected  abstract long fileSize();


    private void handle(ITask task) throws Exception {

        //mTask.setState(TaskState.STATE_RUNNING);
        mLastCompleteLength = task.getCompleteLength();
        //开始任务前准备任务数据，初始化源数据流
        prepare(task);

        if(fileSize() <= 0) {
            isExit = true;
            mListenner.onError(TaskErrorCode.ERROR_FILE_NOT_EXSIT,mTask);
            return;
        }
        //获取到的源数据大小设置到task
        mTask.setLength(fileSize());
        mListenner.onStart(mTask);
        Debugger.error(TAG,"start ============= length = " + task.getLength() + "" +
                ",completeLength = " + task.getCompleteLength() + ",startOffset = " + task.getStartOffset() + ",endOffset = " + task.getEndOffset());

        _handle(task);

        if(!isSuccessful()) { //判断整个任务是否成功
            mTask.setState(TaskState.STATE_ERROR);
            mListenner.onError(TaskErrorCode.ERROR_FINISH,mTask);
        } else {
            mTask.setCompleteLength(mTask.getLength());
            mTask.setCompleteTime(System.currentTimeMillis());
            mTask.setState(TaskState.STATE_FINISH);
            mListenner.onFinished(mTask);
        }

        release(); //释放资源
    }

    //限制下载速度
    protected long getLimitSpeed() {
        long limitSize = SPEED_LIMIT_SIZE.SPEED_UNLIMITED;
        return limitSize;
    }

    private void _handle(ITask task) throws Exception{
        while (!isExit) {

            mLastCaluteTime = System.currentTimeMillis();

            byte[] datas = readPice((Task) task); // 从源中读取一片数据
            int piceSize = getPiceRealSize(); //获取当前读取一片的实际大小

            //如果读取到源数据的末尾
            if(piceSize == -1 || piceSize == 0) {
                mTask.setCompleteLength(mTask.getLength());
                isExit = true;
                break;
            }
            //设置读取的结束偏移量
            ((Task) task).setEndOffset(task.getStartOffset() + piceSize);
            writePice(datas,(Task) task); //写入实际读入的大小
            long endCalculateTime = System.currentTimeMillis();

            mLastCalculateLength += getPiceRealSize();
            //如果当前传输速度大于限制的速度，则等待一段时间
            if(endCalculateTime - mLastCaluteTime < MAX_DELAY_TIME && mLastCalculateLength >= getLimitSpeed()
                    && getLimitSpeed() != SPEED_LIMIT_SIZE.SPEED_UNLIMITED) {
                long waitTime = 1000 - (endCalculateTime - mLastCaluteTime);
                Thread.sleep(waitTime);
                mLastCalculateLength = 0;
                Debugger.error(TAG,"wait time = " + waitTime + ",size = " + getPiceBuffSize() + ",realSize = " + getPiceRealSize() );
            }

            mTask.setCompleteLength(mTask.getEndOffset());
            mTask.setStartOffset(mTask.getEndOffset());
            Debugger.info(TAG,"length = " + task.getLength() + "" +
                    ",completeLength = " + task.getCompleteLength() + ",startOffset = " + task.getStartOffset() + ",endOffset = " + task.getEndOffset());

            if(isPiceSuccessful()) { //判断一片是否成功
                mTask.setState(TaskState.STATE_RUNNING);
                if(System.currentTimeMillis() - mLastPiceSuccessfulTime > MAX_DELAY_TIME) {
                    mLastPiceSuccessfulTime = System.currentTimeMillis();
                    mListenner.onPiceSuccessful(mTask);
                }
            } else {
                mTask.setState(TaskState.STATE_ERROR);
                mListenner.onError(TaskErrorCode.ERROR_PICE,mTask);
                isExit = true;
                break;
            }
            Debugger.error(TAG,"========= setState = " + mTask.getState());
        }

    }

    @Override
    public void start() {
        synchronized (this) {
            //如果任务已经开始或完成则不重复开始
            if(TaskState.STATE_READY == mTask.getState()
                    || mTask.getState() == TaskState.STATE_RUNNING) {
                //throw new IllegalStateException("current handler already started ...");
                Debugger.error(TAG,"current handler already started ...");
                return;
            }

            isExit = false;
            //如果设置了线程池则会在线程池中传输，否则会在当前线程中开始传输
            if (mTaskHandleThreadPool != null) {
                mTaskHandleThreadPool.execute(mHandleRunnable);
            } else {
                mHandleRunnable.run();
            }

            mTask.setState(TaskState.STATE_READY);
            //mTask.setLength(fileSize());
            mListenner.onReady(mTask);
            Debugger.error(TAG," ===== START =======");
        }
    }

    @Override
    public void stop() {

        //停止，完成,失败的任务不能停止
        if(TaskState.STATE_STOP == mTask.getState() ||
                TaskState.STATE_FINISH == mTask.getState() ||
                TaskState.STATE_ERROR == mTask.getState()) {
            return;
        }

        Debugger.error(TAG,"stop ============= length = " + mTask.getLength() + "" +
                ",completeLength = " + mTask.getCompleteLength() + ",startOffset = " + mTask.getStartOffset() + ",endOffset = " + mTask.getEndOffset());
        isExit = true;
        mTask.setState(TaskState.STATE_STOP);
        //mTaskHandleThreadPool.remove(mHandleRunnable);
//        Thread thread = Thread.currentThread();
//        if(thread.getId() != Looper.getMainLooper().getThread().getId()) {
//            thread.interrupt();
//        }

        if(mTaskHandleThreadPool != null) {
            mTaskHandleThreadPool.remove(mHandleRunnable);
        }
        mListenner.onStop(mTask);
        release();
    }

    protected void release() {
    }


    @Override
    public ITask getTask() {
        return mTask;
    }

    @Override
    public void setTask(ITask task) {
        mTask = (Task) task;
    }

    @Override
    public TaskType getType() {
        return mTask.getType();
    }

    private void checkParams() {
        if(TextUtils.isEmpty(getTask().getSourceUrl())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DataSource can not be a null value,");
            if(getType() == TaskType.TYPE_HTTP_DOWNLOAD) {
                stringBuilder.append("is the path of the file you want to download");
            } else {
                stringBuilder.append("is the path you want to upload files");
            }
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        if(TextUtils.isEmpty(getTask().getDestUrl())) {
            StringBuilder builder = new StringBuilder();
            builder.append("DestSource can not be a null value,");
            if(getType() == TaskType.TYPE_HTTP_DOWNLOAD) {
                builder.append("destSource is the path saved by the file you downloaded");
            } else {
                builder.append("destSource is the path you want to upload to the server");
            }
            throw new IllegalArgumentException(builder.toString());
        }

        if(TextUtils.isEmpty(getTask().getName())) {
            throw new IllegalArgumentException("name can not be a null value!");
        }
    }

    class HandleRunnable implements Runnable {

        @Override
        public void run() {
            try {
                Debugger.error(TAG," ===== START RUN =======");
                mStateThread = new Thread(mStateRunnable);
                mStateThread.setName("speed_" + getTask().getName() + "_thread");
                mStateThread.setDaemon(true);
                mStateThread.start();
                checkParams();
                handle(mTask);
            } catch (Exception e) {
                Debugger.error(TAG,e.getMessage());
                e.printStackTrace();
                if(mTask.getState() != TaskState.STATE_STOP) {
                    mTask.setState(TaskState.STATE_ERROR);
                    mListenner.onError(TaskErrorCode.ERROR_CODE_EXCEPTION, mTask);
                }
                isExit = true;
            }
        }
    }

    //当前实际完成的长度，这个数值是比较及时的，可以用来显示速度和进度的变化
    protected  long getCurrentCompleteLength() {
        return mTask.getCompleteLength();
    }

    class StateRunnable implements Runnable {

        @Override
        public void run() {
            while (!isExit) {
                try {
                    Thread.sleep(MAX_DELAY_TIME);
                    if(getCurrentCompleteLength() == mLastCompleteLength) continue;
                    mTask.setSpeed((long) ((getCurrentCompleteLength() - mLastCompleteLength) / ( MAX_DELAY_TIME / 1000f)));
                    mListenner.onSpeedChanged((long) ((getCurrentCompleteLength() - mLastCompleteLength) / ( MAX_DELAY_TIME / 1000f)), mTask);
                    mLastCompleteLength = getCurrentCompleteLength();
                    Debugger.error(TAG," ===== state = " + mTask.getState());
                    //Debugger.error(TAG,"speed = " + task.getSpeed());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected  abstract static class Builder<B extends Builder,T extends ITaskHandler> {

        private T mTarget;
        private Map<String,String> mHeaders;
        private Map<String,String> mParams;
        private ThreadPoolExecutor mThreadPool;
        private ITaskHandlerCallback mCallback;
        private ITask mTask;
        private int mCoreThreadSize;
        private ITaskEventDispatcher mDispatcher;
        private boolean isRunOnNewThread = false;

        public Builder() {

        }

        public Builder(T target) {
            mTarget = target;
        }

        public B setEventDispatcher(ITaskEventDispatcher dispatcher) {
            mDispatcher = dispatcher;
            return (B)this;
        }

        /**
         * http headers
         * @param headers
         * @return
         */
        public B setHeaders(Map<String,String> headers) {
            mHeaders = headers;
            return (B)this;
        }

        /***
         * http params,
         * 默认的上传 {@link DefaultHttpUploadHandler} method = post , mime-type 为 application/octet-stream,
         * 所以只能将自定义的参数放到 header中
         *
         * 默认的下载  {@link DefaultHttpDownloadHandler}method = GET ，所以会将该参数放置到url 之后。
         * @param params
         * @return
         */
        public B setParams(Map<String,String> params) {
            mParams = params;
            return (B)this;
        }


        public B addHeader(String k,String v) {
            if(mHeaders == null) {
                mHeaders = new HashMap<>();
            }
            mHeaders.put(k,v);
            return (B)this;
        }

        public B addParam(String k,String v) {
            if(mParams == null) {
                mParams = new HashMap<>();
            }
            mParams.put(k,v);
            return (B)this;
        }

        /***
         * 设置任务，必须设置。 创建task {@link com.scott.transer.TaskBuilder}
         * @param task
         * @return
         */
        public B setTask(ITask task) {
            mTask = task;
            return (B)this;
        }

        /***
         * 设置线程池，下载或上传将会在线程中执行 或者调用 runOnNewThread(int coreSize)
         * 设置默认的线程池，否则，上传/下载 将会在当前调用的线程内执行
         * @param executor
         * @return
         */
        public B setThreadPool(ThreadPoolExecutor executor) {
            mThreadPool = executor;
            return (B)this;
        }

        /**
         * 回掉
         * @param callback
         * @return
         */
        public B setCallback(ITaskHandlerCallback callback) {
            mCallback = callback;
            return (B)this;
        }

        /**
         * 在一个线程中去执行
         * @return
         */
        public B runOnNewThread() {
            isRunOnNewThread = true;
            return (B)this;
        }

        public T build() {
            if(mTarget == null) {
                mTarget = buildTarget();
            }

            if(mTarget == null) {
                throw new IllegalStateException("buildTarget() not impl!");
            }

            if(mTask == null) {
                throw new IllegalArgumentException("task is null,you must call setTask to set task!");
            }

            if(mCallback == null && mDispatcher != null) {
                mCallback = new EventDispacherAdapter(mDispatcher);
            }
            mTarget.setHandlerListenner(mCallback);
            mTarget.setTask(mTask);
            mTarget.setHeaders(mHeaders);
            mTarget.setParams(mParams);

            if(mThreadPool == null && isRunOnNewThread) {
                if(mCoreThreadSize <= 0) {
                    mCoreThreadSize = 1;
                }
                mThreadPool = new ThreadPoolExecutor(mCoreThreadSize,mCoreThreadSize,
                        6000, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(10000));
            }
            mTarget.setThreadPool(mThreadPool);
            return mTarget;
        }

        protected abstract T buildTarget();

        private final static class EventDispacherAdapter implements ITaskHandlerCallback {
            private ITaskEventDispatcher mDispacher;

            EventDispacherAdapter(ITaskEventDispatcher dispatcher) {
                mDispacher = dispatcher;
            }

            private void adaptDispatcher(ITask task) {
                List<ITask> taskList = new ArrayList<>();
                taskList.add(task);
                mDispacher.dispatchTasks(task.getType(),ProcessType.TYPE_DEFAULT,taskList);
            }

            @Override
            public void onReady(ITask task) {
                adaptDispatcher(task);
            }

            @Override
            public void onStart(ITask task) {
                adaptDispatcher(task);
            }

            @Override
            public void onStop(ITask task) {
                adaptDispatcher(task);
            }

            @Override
            public void onError(int code, ITask task) {
                adaptDispatcher(task);
            }

            @Override
            public void onSpeedChanged(long speed, ITask task) {
                adaptDispatcher(task);
            }

            @Override
            public void onPiceSuccessful(ITask task) {
                adaptDispatcher(task);
            }

            @Override
            public void onFinished(ITask task) {
                adaptDispatcher(task);
            }
        }
    }
 }
