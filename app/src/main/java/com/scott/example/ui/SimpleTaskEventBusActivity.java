package com.scott.example.ui;

import android.os.Environment;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.utils.Contacts;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.TaskBuilder;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.DefaultHttpUploadHandler;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.utils.Debugger;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.scott.example.utils.TaskUtils.getFileSize;

public class SimpleTaskEventBusActivity extends BaseActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_complete_length)
    TextView tvCompleteLength;

    @BindView(R.id.tv_all_length)
    TextView tvAllLength;

    @BindView(R.id.progress_length)
    ProgressBar progressLength;

    @BindView(R.id.tv_md5)
    TextView tvMd5;

    @BindView(R.id.tv_md5_new)
    TextView tvNewMd5;

    @BindView(R.id.tv_equals)
    TextView tvEquals;

    @BindView(R.id.tv_speed)
    TextView tvSpeed;

    private ITaskHandler mHandler;
    private ITask task;

    final String URL = Contacts.API.getUrl(Contacts.API.UPLOAD_URL);
    final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "test.zip";
    final String TAG = SimpleUploadActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_download);

        ButterKnife.bind(this);

        task = new TaskBuilder()
                .setName("test.zip")
                .setTaskId("1233444")
                .setSessionId("123123123131")
                .setDataSource(FILE_PATH)
                .setDestSource(URL)
                .build();

        mHandler = new DefaultHttpUploadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setEventDispatcher(TaskEventBus.getDefault().getDispatcher()) //设置EventDispatcher,
                .runOnNewThread()
                .build();
        setTitle(getString(R.string.simple_upload));
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskEventBus.getDefault().regesit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskEventBus.getDefault().unregesit(this);
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_UPLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onTaskStateChanged(List<ITask> tasks) {
        if(tasks == null || tasks.isEmpty()) {
            return;
        }
        ITask task = tasks.get(0);
        switch (task.getState()) {
            case TaskState.STATE_RUNNING:
                onSpeedChanged(task);
                break;
            case TaskState.STATE_FINISH:
                onFinished(task);
                break;
            case TaskState.STATE_ERROR:
                onError(task);
                break;
        }
    }


    public void onFinished(final ITask task) {
        tvCompleteLength.setText(getFileSize(task.getCompleteLength()));
        tvAllLength.setText(getFileSize(task.getLength()));

        double progress = (double)task.getCompleteLength() / (double)task.getLength();
        progress = progress * 100f;
        progressLength.setProgress((int) progress);
        Debugger.error(TAG,"========onFinished============");
    }

    public void onSpeedChanged(final ITask params) {
        tvCompleteLength.setText(getFileSize(params.getCompleteLength()));
        tvAllLength.setText(getFileSize(params.getLength()));

        double progress = (double)params.getCompleteLength() / (double)params.getLength();
        progress = progress * 100f;
        progressLength.setProgress((int) progress);
        tvSpeed.setText(TaskUtils.getFileSize(task.getSpeed()));
        Debugger.error("OnlyDownloadActivity","speed = " + getFileSize(params.getSpeed()) + "/s");
    }

    public void onError(ITask params) {
        Debugger.error("SimpleUploadActivity","error ...");
    }

    @OnClick(R.id.btn_stop)
    public void stop() {
        mHandler.stop();
    }


    @OnClick(R.id.btn_start)
    public void start() {
        mHandler.start();
    }
}
