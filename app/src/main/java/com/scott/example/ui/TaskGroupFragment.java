package com.scott.example.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.IExpandable;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseFragment;
import com.scott.example.R;
import com.scott.example.adapter.TaskGroupAdapter;
import com.scott.example.adapter.TaskGroupExpandAdapter;
import com.scott.example.adapter.TaskListRecyclerAdapter;
import com.scott.example.moudle.TaskChildItem;
import com.scott.example.moudle.TaskGroupItem;
import com.scott.example.utils.Contacts;
import com.scott.transer.TaskCmd;
import com.scott.transer.TaskState;
import com.scott.transer.event.TaskEventBus;
import com.scott.transer.handler.ITaskHolder;
import com.shilec.xlogger.XLogger;

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

    //private TaskGroupAdapter mAdapter;
    //private RecyclerView mListView;

    private ExpandableListView mListView;
    private TaskGroupExpandAdapter mAdapter;

    private final String TAG = TaskFragment.class.getSimpleName();
    private TaskType mTaskType;
    public static final String EXTRA_TASK_TYPE = "task_type";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onStart() {
        super.onStart();
        mTaskType = (TaskType) getArguments().get(EXTRA_TASK_TYPE);
        TaskEventBus.getDefault().regesit(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_task_expand_list, container, false);

        mSwipeRefreshLayout = root.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = root.findViewById(R.id.rcy_tasks);
        //mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //mTaskAdapter = new TaskListAdapter(mTasks);
        //mAdapter = new TaskGroupAdapter(mDatas);
        mAdapter = new TaskGroupExpandAdapter(mDatas);
        mListView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        TaskEventBus.getDefault().unregesit(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        TaskCmd cmd = new TaskCmd.Builder()
                .setUserId(Contacts.USER_ID)
                .setTaskType(mTaskType)
                .setProcessType(ProcessType.TYPE_QUERY_TASKS_ALL)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_DOWNLOAD, threadMode = ThreadMode.MODE_MAIN)
    public void onDownloadTasksChange(final List<ITask> tasks) {

        if (mTaskType != TaskType.TYPE_HTTP_DOWNLOAD) return;
        //Debugger.error(TAG,tasks.toString());
        onTasksChange(tasks);
    }

    @TaskSubscriber(taskType = TaskType.TYPE_HTTP_UPLOAD, threadMode = ThreadMode.MODE_MAIN)
    public void onUploadTaskChange(final List<ITask> tasks) {
        if (mTaskType != TaskType.TYPE_HTTP_UPLOAD) return;
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

        XLogger.getDefault().e(TAG, "thread ==== " + Thread.currentThread().getName());
        mDatas.clear();
        mDatas.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 将当前任务列表转换成可以分组显示的数据
     * @param tasks
     * @return
     */
    private List<MultiItemEntity> convertItems(List<ITask> tasks) {

        List<MultiItemEntity> items = new ArrayList<>();
        if (tasks == null) {
            return items;
        }
        for (int i = 0; i < tasks.size(); i++) {
            ITask task = tasks.get(i);

            MultiItemEntity group = null;
            for (MultiItemEntity item : items) {
                ITask task1 = ((ITaskHolder) item).getTask();
                if (TextUtils.equals(task.getGroupId(), task1.getGroupId())) {
                    group = item;
                    break;
                }
            }

            //每个分组有一个child 列表
            if (group == null) {
                group = new TaskGroupItem(task);
                ((TaskGroupItem) group).setSubItems(new ArrayList<TaskChildItem>());
                items.add(group);
            }

            TaskGroupItem groupItem = (TaskGroupItem) group;
            groupItem.getSubItems().add(new TaskChildItem(task));
            groupItem.setAllCount(groupItem.getSubItems().size());
            groupItem.setAllSize(groupItem.getAllSize() + task.getLength());
            groupItem.setLeaveCount(groupItem.getLeaveCount() +
                    (task.getState() == TaskState.STATE_FINISH ? 0 : 1));
            groupItem.setCompleteSize(groupItem.getCompleteSize() + task.getCompleteLength());
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
