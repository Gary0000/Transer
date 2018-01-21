package com.scott.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.ITaskCmd;
import com.scott.transer.TaskCmdBuilder;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleTaskListActivity extends AppCompatActivity {

    private TaskType taskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_task_list);
        ButterKnife.bind(this);

        taskType = (TaskType) getIntent().getSerializableExtra(TaskFragment.EXTRA_TASK_TYPE);

        TaskFragment taskFragment = new TaskFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskFragment.EXTRA_TASK_TYPE, taskType);
        taskFragment.setArguments(bundle);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.rl_container,taskFragment)
                .commit();
    }

    @OnClick(R.id.btn_stop_all)
    public void stopAll() {
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(taskType)
                .setProcessType(ProcessType.TYPE_STOP_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }


    @OnClick(R.id.btn_start_all)
    public void startAll() {
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(taskType)
                .setProcessType(ProcessType.TYPE_START_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }

    @OnClick(R.id.btn_delete_all)
    public void deleteAll() {
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(taskType)
                .setProcessType(ProcessType.TYPE_DELETE_TASKS_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }
}
