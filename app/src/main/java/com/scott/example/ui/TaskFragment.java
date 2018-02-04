package com.scott.example.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseFragment;
import com.scott.example.R;
import com.scott.example.adapter.TaskListRecyclerAdapter;
import com.scott.transer.ITaskCmd;
import com.scott.transer.TaskCmdBuilder;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.utils.Debugger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2017/12/19</P>
 * <P>Email: shilec@126.com</p>
 */

public class TaskFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private List<ITask> mTasks = new ArrayList<>();
    //private TaskListAdapter mTaskAdapter;
    //private ListView mListView;
    private TaskListRecyclerAdapter mAdapter;
    private RecyclerView mListView;
    private final String TAG = TaskFragment.class.getSimpleName();
    private TaskType mTaskType;
    public static final String EXTRA_TASK_TYPE = "task_type";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onStart() {
        super.onStart();
        mTaskType = (TaskType) getArguments().get(EXTRA_TASK_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_task_list, container, false);

        mSwipeRefreshLayout = root.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = root.findViewById(R.id.rcy_tasks);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //mTaskAdapter = new TaskListAdapter(mTasks);
        mAdapter = new TaskListRecyclerAdapter(R.layout.item_task_item,mTasks);
        mListView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        TaskEventBus.getDefault().regesit(this);

        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(mTaskType)
                .setProcessType(ProcessType.TYPE_QUERY_TASKS_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskEventBus.getDefault().unregesit(this);
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_DOWNLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onDownloadTasksChange(final List<ITask> tasks) {

        if(mTaskType != TaskType.TYPE_HTTP_DOWNLOAD) return;
        //Debugger.error(TAG,tasks.toString());
        onTasksChange(tasks);
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_UPLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onUploadTaskChange(final List<ITask> tasks) {
        if(mTaskType != TaskType.TYPE_HTTP_UPLOAD) return;
        onTasksChange(tasks);
    }

    private void onTasksChange(final List<ITask> tasks) {

        mSwipeRefreshLayout.setRefreshing(false);
        //为了保持任务管理的一致。不能将ITask 转为 Task
        //只是用ITask 去获取任务信息，显示到UI
        if (tasks == null) {
            return;
        }
        Debugger.error(TAG,"thread ==== " + Thread.currentThread().getName());
        mTasks.clear();
        mTasks.addAll(tasks);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(mTaskType)
                .setProcessType(ProcessType.TYPE_QUERY_TASKS_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }
}
