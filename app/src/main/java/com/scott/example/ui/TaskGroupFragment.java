package com.scott.example.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseFragment;
import com.scott.example.R;
import com.scott.example.adapter.TaskGroupAdapter;
import com.scott.example.adapter.TaskListRecyclerAdapter;
import com.scott.example.moudle.TaskChildItem;
import com.scott.example.moudle.TaskGroupItem;
import com.scott.example.utils.Contacts;
import com.scott.transer.TaskCmd;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.ITaskHolder;
import com.scott.transer.utils.Debugger;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2018-03-21 13:12</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class TaskGroupFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<MultiItemEntity> mDatas = new ArrayList<>();

    private TaskGroupAdapter mAdapter;

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
        mAdapter = new TaskGroupAdapter(mDatas);
        mListView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        TaskEventBus.getDefault().regesit(this);

        TaskCmd cmd = new TaskCmd.Builder()
                .setUserId(Contacts.USER_ID)
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

        List<MultiItemEntity> items = convertItems(tasks);
        Debugger.error(TAG,"thread ==== " + Thread.currentThread().getName());
        mDatas.clear();
        mDatas.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    private List<MultiItemEntity> convertItems(List<ITask> tasks) {

        List<MultiItemEntity> items = new ArrayList<>();
        if(tasks == null) {
            return items;
        }
        for(int i = 0; i < tasks.size(); i++) {
            ITask task = tasks.get(i);

            /*
                如果当前列表中存在相同Group的 Item,则检查该Item 的 subItem 是
                否为空，如果不为空，则将当前的task作为subitem插入
                如果为空，则将当前的Item作为 groupItem 插入
             */
            List<TaskChildItem> subItems = null;
            for(MultiItemEntity item : items) {
                ITaskHolder holder = (ITaskHolder) item;
                if(holder.getTask() != null
                        && TextUtils.equals(holder.getTask().getGroupId(),task.getGroupId())) {
                    subItems = ((TaskGroupItem)item).getSubItems();
                    if(subItems == null) {
                        subItems = new ArrayList<>();
                        ((TaskGroupItem)item).setSubItems(subItems);

                        TaskChildItem childItem = new TaskChildItem(task);
                        subItems.add(childItem);
                    }
                    break;
                }
            }

            if(subItems == null) {
                TaskGroupItem item = new TaskGroupItem(task);
                items.add(item);
            } else {
                TaskChildItem item = new TaskChildItem(task);
                subItems.add(item);
            }
        }
        return items;
    }

    @Override
    public void onRefresh() {
        TaskCmd cmd = new TaskCmd.Builder()
                .setTaskType(mTaskType)
                .setUserId(Contacts.USER_ID)
                .setProcessType(ProcessType.TYPE_QUERY_TASKS_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }
}
