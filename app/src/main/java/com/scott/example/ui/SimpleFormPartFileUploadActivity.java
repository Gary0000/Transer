package com.scott.example.ui;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.moudle.FileInfo;
import com.scott.example.utils.Contacts;
import com.scott.example.utils.TaskUtils;
import com.scott.transer.Task;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.BaseTaskHandler;
import com.scott.transer.handler.ITaskHandler;
import com.scott.transer.handler.DefaultFormPartUploadHandler;
import com.shilec.xlogger.XLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.scott.example.utils.TaskUtils.getFileSize;

public class SimpleFormPartFileUploadActivity extends BaseActivity {

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

    final String TAG = SimpleUploadActivity.class.getSimpleName();
    FileInfo mFileInfo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ArrayList<FileInfo> fileInfos = (ArrayList<FileInfo>) data.getSerializableExtra(FileListActivity.BUNDLE_DATA_KEY);
        if (fileInfos == null || fileInfos.isEmpty()) {
            return;
        }
        mFileInfo = fileInfos.get(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_download);

        ButterKnife.bind(this);
        mHandler = new DefaultFormPartUploadHandler.Builder()
                .addHeader("session-id","123")
                .addParam("name","shilec")
                .addParam("path", "root")
                .setFileMediaType(MediaType.parse("multi-formpart"))
                .setEventDispatcher(TaskEventBus.getDefault().getDispatcher())
                .runOnNewThread()
                .build();
        setTitle(getString(R.string.task_upload_formpart));
    }

    private ITask getTask(FileInfo fileInfo) {
        ITask task = new Task.Builder()
                .setName(fileInfo.name)
                .setSourceUrl(fileInfo.path)
                .setLength(fileInfo.length)
                .setTaskType(TaskType.TYPE_HTTP_UPLOAD)
                .setDestUrl(Contacts.API.getUrl(Contacts.API.UPLOAD_FORMPART))
                .build();
        return task;
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
        XLogger.getDefault().e(TAG,"========onFinished============");
    }

    public void onSpeedChanged(final ITask params) {
        tvCompleteLength.setText(getFileSize(params.getCompleteLength()));
        tvAllLength.setText(getFileSize(params.getLength()));

        double progress = (double)params.getCompleteLength() / (double)params.getLength();
        progress = progress * 100f;
        progressLength.setProgress((int) progress);
        tvSpeed.setText(TaskUtils.getFileSize(params.getSpeed()));
        XLogger.getDefault().e("OnlyDownloadActivity","speed = " + getFileSize(params.getSpeed()) + "/s");
    }

    public void onError(ITask params) {
        XLogger.getDefault().e("SimpleUploadActivity","error ...");
    }

    @OnClick(R.id.btn_stop)
    public void stop() {
        if(mHandler != null) {
            mHandler.stop();
        }
    }


    @OnClick(R.id.btn_start)
    public void start() {

        if(mFileInfo == null) {
            Intent intent = new Intent(this,FileListActivity.class);
            intent.putExtra(FileListActivity.EXTRA_SELECT_TYPE, FileListActivity.SELECT_TYPE.TYPE_LOCAL);
            startActivityForResult(intent,1);
        } else {
            mHandler.setTask(getTask(mFileInfo));
            mHandler.start();
            mFileInfo = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TaskEventBus.getDefault().regesit(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TaskEventBus.getDefault().unregesit(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null) {
            mHandler.stop();
        }
    }
}
