package com.scott.example.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.utils.Contacts;
import com.scott.transer.ITaskCmd;
import com.scott.transer.TaskBuilder;
import com.scott.transer.TaskCmdBuilder;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.manager.dynamicproxy.ProcessorDynamicProxyFactory;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CreateTaskActivity extends BaseActivity {

    @BindView(R.id.edit_source)
    EditText editPath;

    @BindView(R.id.edit_dest)
    EditText editUrl;

    @BindView(R.id.rg_type)
    RadioGroup radioGroup;

    private TaskType task_type = TaskType.TYPE_HTTP_UPLOAD;

    final String NAME = "test.zip";
    final String DOWNLOAD_PATH = Contacts.LOCAL_STORAGE.getBaseSavePath() + File.separator + NAME;
    final String DOWNLOAD_URL = Contacts.API.getUrl(Contacts.API.DOWNLOAD_URL);
    ;
    final String UPLOAD_PATH = DOWNLOAD_PATH;
    final String UPLOAD_URL = Contacts.API.getUrl(Contacts.API.UPLOAD_URL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);

        switch (task_type) {
            case TYPE_HTTP_DOWNLOAD:
                editPath.setText(DOWNLOAD_PATH);
                editUrl.setText(DOWNLOAD_PATH);
                break;
            case TYPE_HTTP_UPLOAD:
                editPath.setText(Contacts.LOCAL_STORAGE.getBaseSavePath() + File.separator + NAME);
                editUrl.setText(Contacts.API.getUrl(Contacts.API.UPLOAD_URL));
                break;
        }
        setTitle(getString(R.string.create_task));
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

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_DOWNLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onAddTask() {
        Toast.makeText(this,"下载任务添加成功!",Toast.LENGTH_SHORT).show();
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_UPLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onAddUploadTask() {
        Toast.makeText(this,"上传任务添加成功!",Toast.LENGTH_SHORT).show();
    }

    @OnCheckedChanged(R.id.rb_upload)
    public void uploadCheck(boolean checked) {
        if(checked) {
            editPath.setText(UPLOAD_PATH);
            editUrl.setText(UPLOAD_URL);
        }
    }

    @OnCheckedChanged(R.id.rb_download)
    public void downloadCheck(boolean checked) {
        if(checked) {
            editUrl.setText(DOWNLOAD_URL);
            editPath.setText(DOWNLOAD_PATH);
        }
    }

    @OnClick(R.id.btn_create_task)
    public void createTaskByEventBus() {

        ITask task = createTask();
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(task_type)
                .setProcessType(ProcessType.TYPE_ADD_TASK)
                .setTask(task)
                .build();

        TaskEventBus.getDefault().execute(cmd);
    }

    @OnClick(R.id.btn_create_task_proxy)
    public void createTaskByDynamicProxy() {
        ITask task = createTask();
        ProcessorDynamicProxyFactory
                .getInstance()
                .create()
                .addTask(task);
    }

    private ITask createTask() {
        String source = null;
        String dest = null;

        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_upload) {
            task_type = TaskType.TYPE_HTTP_UPLOAD;
        } else {
            task_type = TaskType.TYPE_HTTP_DOWNLOAD;
        }

        switch (task_type) {
            case TYPE_HTTP_UPLOAD:
                source = editPath.getText().toString();
                dest = editUrl.getText().toString();
                break;
            case TYPE_HTTP_DOWNLOAD:
                dest = editPath.getText().toString();
                source = editUrl.getText().toString();
                break;
        }


        ITask task = new TaskBuilder()
                .setTaskType(task_type)
                .setSourceUrl(source)
                .setDestUrl(dest)
                .setName(NAME)
                .build();
        return task;
    }
}
