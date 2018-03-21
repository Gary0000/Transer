package com.scott.example.ui;

import android.os.Bundle;

import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskType;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.transer.TaskCmd;
import com.scott.transer.event.TaskEventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleTaskListActivity extends BaseActivity {

    private TaskType taskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_task_list);
        ButterKnife.bind(this);

        taskType = (TaskType) getIntent().getSerializableExtra(TaskFragment.EXTRA_TASK_TYPE);
        if(taskType == TaskType.TYPE_HTTP_UPLOAD) {
            setTitle(R.string.upload_tasks_list);
        } else {
            setTitle(R.string.download_tasks_list);
        }

        TaskFragment taskFragment = new TaskFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskFragment.EXTRA_TASK_TYPE, taskType);
        taskFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rl_container,taskFragment)
                .commit();
    }

    @OnClick(R.id.btn_stop_all)
    public void stopAll() {
        TaskCmd cmd = new TaskCmd.Builder()
                .setTaskType(taskType)
                .setProcessType(ProcessType.TYPE_STOP_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }


    @OnClick(R.id.btn_start_all)
    public void startAll() {
        TaskCmd cmd = new TaskCmd.Builder()
                .setTaskType(taskType)
                .setProcessType(ProcessType.TYPE_START_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }

    @OnClick(R.id.btn_delete_all)
    public void deleteAll() {
        TaskCmd cmd = new TaskCmd.Builder()
                .setTaskType(taskType)
                .setProcessType(ProcessType.TYPE_DELETE_TASKS_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }
}
