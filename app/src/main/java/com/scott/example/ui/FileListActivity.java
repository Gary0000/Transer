package com.scott.example.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskSubscriber;
import com.scott.annotionprocessor.TaskType;
import com.scott.annotionprocessor.ThreadMode;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.adapter.FileInfoAdapter;
import com.scott.example.http.IRequestService;
import com.scott.example.http.RetrofitSingleton;
import com.scott.example.moudle.FileInfo;
import com.scott.example.utils.Contacts;
import com.scott.transer.Task;
import com.scott.transer.TaskCmd;
import com.scott.transer.event.TaskEventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FileListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,MenuItem.OnMenuItemClickListener {

    private List<FileInfo> mDatas = new ArrayList<>();
    private FileInfoAdapter mAdapter;
    //private ListView mListView;
    private RecyclerView mListView;
    private SELECT_TYPE mSelectType;
    public static String EXTRA_SELECT_TYPE = "LOCAL_NET";
    public static String EXTRA_ROOT_PATH = "PATH";
    private String mRootPath;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean isLoadMore = false;
    final static String BUNDLE_DATA_KEY = "data";
    private Stack<String> mPathStack = new Stack<>();

    public enum SELECT_TYPE {
        TYPE_NET_NOT_RETURN(1),
        TYPE_LOCAL_NOT_RETURN(2),
        TYPE_NET(3),
        TYPE_LOCAL(4);

        int value = 0;

        SELECT_TYPE(int value){
            this.value = value;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_upload_tasks:
                createTasks(TaskType.TYPE_HTTP_UPLOAD);
                break;
            case R.id.btn_download_tasks:
                createTasks(TaskType.TYPE_HTTP_DOWNLOAD);
                break;
        }
        mAdapter.cancelSelect();
        return false;
    }

    @TaskSubscriber(processType = ProcessType.TYPE_ADD_TASKS,taskType = TaskType.TYPE_HTTP_UPLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onAddUploadTasks() {
        Toast.makeText(this,"添加任务成功!",Toast.LENGTH_SHORT).show();
    }

    @TaskSubscriber(processType = ProcessType.TYPE_ADD_TASKS,taskType = TaskType.TYPE_HTTP_DOWNLOAD,threadMode = ThreadMode.MODE_MAIN)
    public void onAddDownloadTasks() {
        Toast.makeText(this,"添加任务成功!",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TaskEventBus.getDefault().unregesit(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TaskEventBus.getDefault().regesit(this);
    }

    private void createTasks(TaskType taskType) {
        List<ITask> tasks = new ArrayList<>();

        String groupId = System.currentTimeMillis() + "";
        String groupName = mAdapter.getCheckedItems().get(0).name;

        for(FileInfo info : mAdapter.getCheckedItems()) {
            Task.Builder builder = new Task.Builder()
                    .setUserId(Contacts.USER_ID)
                    .setTaskType(taskType)
                    .setGroupId(groupId)
                    .setGroupName(groupName)
                    .setName(info.name);
            if(taskType == TaskType.TYPE_HTTP_DOWNLOAD) {
                builder.setSourceUrl(Contacts.API.getUrl(Contacts.API.DOWNLOAD_URL))
                        .setSourcePath(info.path)
                        .setDestUrl(Contacts.LOCAL_STORAGE.getBaseSavePath());

            } else {
                builder.setSourceUrl(info.path)
                        .setDestPath("upload")
                        .setDestUrl(Contacts.API.getUrl(Contacts.API.UPLOAD_URL));
            }
            ITask task = builder.build();
            tasks.add(task);
        }

//        ITaskProcessor processor = ProcessorDynamicProxyFactory
//                .getInstance()
//                .create();
//        processor.addTasks(tasks);
        TaskCmd cmd = new TaskCmd.Builder()
                .setTaskType(mSelectType.value % 2 == 0 ? TaskType.TYPE_HTTP_UPLOAD : TaskType.TYPE_HTTP_DOWNLOAD)
                .setTasks(tasks)
                .setProcessType(ProcessType.TYPE_ADD_TASKS)
                .setUserId(Contacts.USER_ID)
                .build();
        TaskEventBus.getDefault().execute(cmd);
    }

    private final class MyScriber extends Subscriber<List<FileInfo>> {

        @Override
        public void onCompleted() {
            mRefreshLayout.setRefreshing(false);
            if(mSelectType.value % 2 != 0 && false) {
                mAdapter.setEnableLoadMore(true);
            }
            if(isLoadMore) {
                mAdapter.loadMoreComplete();
            }
        }

        @Override
        public void onError(Throwable e) {
            if(isLoadMore) {
                mAdapter.loadMoreFail();
            }
            e.printStackTrace();
        }

        @Override
        public void onNext(List<FileInfo> fileInfos) {
            sortFileList(fileInfos);
            if(fileInfos == null) {
                if(isLoadMore) {
                    mAdapter.loadMoreEnd();
                }
                return;
            }
            mDatas.clear();
            mDatas.addAll(fileInfos);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void sortFileList(List<FileInfo> fileInfos) {
        Collections.sort(fileInfos, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if(o1.type == FileInfo.FILE_TYPE.DIRECTORY &&
                        o2.type == FileInfo.FILE_TYPE.FILE) {
                    return -1;
                }

                if(o1.type == FileInfo.FILE_TYPE.FILE &&
                        o2.type == FileInfo.FILE_TYPE.DIRECTORY) {
                    return 1;
                }

                if(o1.type == o2.type) {
                    return o1.name.compareTo(o2.name);
                }
                return 0;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        mSelectType = (SELECT_TYPE) getIntent().getSerializableExtra(EXTRA_SELECT_TYPE);
        if(mSelectType == null) {
            return;
        }
        if(mSelectType.value % 2 == 0) {
            setTitle(getString(R.string.local_file_list));
        } else {
            setTitle(getString(R.string.server_file_list));
        }

        mRootPath = getIntent().getStringExtra(EXTRA_ROOT_PATH);
        if(mSelectType.value % 2 == 0 && TextUtils.isEmpty(mRootPath)) {
            mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if(TextUtils.isEmpty(mRootPath)){
            mRootPath = "";
        }

        initViews();
        initDatas();
    }

    @Override
    public void onBackPressed() {
        if(mPathStack.empty()) {
            super.onBackPressed();
        } else {
            mRootPath = mPathStack.pop();
            initDatas();
        }
    }

    private void initDatas() {
        if(mSelectType.value % 2 == 0) {
            loadFromLocal();
        } else {
            loadFromNet();
        }
    }

    private void loadFromLocal() {
        Observable.fromCallable(new Callable<List<FileInfo>>() {

            @Override
            public List<FileInfo> call() throws Exception {
                File file = new File(mRootPath);
                File[] files = file.listFiles();
                List<FileInfo> fileInfos = new ArrayList<>();
                for(File f : files) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.name = f.getName();
                    fileInfo.type = f.isDirectory() ? FileInfo.FILE_TYPE.DIRECTORY : FileInfo.FILE_TYPE.FILE;
                    fileInfo.date = f.lastModified();
                    fileInfo.length = f.length();
                    fileInfo.path = f.getAbsolutePath();
                    fileInfos.add(fileInfo);
                }
                return fileInfos;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyScriber());
    }

    private void loadFromNet() {
        RetrofitSingleton
                .getInstance()
                .getRetrofit()
                .create(IRequestService.class)
                .getFileList(mRootPath,1000)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyScriber());
    }

    private void initViews() {
        mListView = findViewById(R.id.list_view);
        mAdapter = new FileInfoAdapter(R.layout.item_file_info,mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isLoadMore = true;
                initDatas();
            }
        }, mListView);

        mAdapter.setEnableLoadMore(false);
        mRefreshLayout = findViewById(R.id.swipeLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));

        getSelectView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals(((TextView)v).getText(),getString(R.string.select))) {
                    mAdapter.setSelectMode(true);
                    getSubmitView().setVisibility(View.VISIBLE);
                    getSelectView().setText(R.string.select_all);
                    getSubmitView().setText(R.string.cancel);
                } else if(TextUtils.equals(((TextView)v).getText(),getString(R.string.select_all))) {
                    mAdapter.selectAll();
                    getSubmitView().setText(R.string.completed);
                    getSelectView().setText(R.string.cancel_select_all);
                } else if(TextUtils.equals(((TextView)v).getText(),getString(R.string.cancel_select_all))) {
                    mAdapter.cancelSelect();
                    getSubmitView().setText(R.string.cancel);
                    getSelectView().setText(R.string.select_all);
                }
            }
        });

        getSubmitView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectView().setText(R.string.select);
                mAdapter.setSelectMode(false);
                getSubmitView().setVisibility(View.INVISIBLE);

                if(TextUtils.equals(((TextView)v).getText(),getString(R.string.cancel))) {
                    mAdapter.cancelSelect();
                } else {
                    onSelectCompleted();
                }
            }
        });

        getSelectView().setVisibility(View.VISIBLE);

        mAdapter.setOnSelectChangeListenner(new FileInfoAdapter.OnSelectChangeListenner() {
            @Override
            public void onChanged(int selectOut) {
                if(selectOut == 0) {
                    getSubmitView().setText(R.string.cancel);
                } else {
                    getSubmitView().setText(R.string.completed);
                }
            }
        });
    }

    private void onSelectCompleted() {
        if(mSelectType == SELECT_TYPE.TYPE_LOCAL ||
                mSelectType == SELECT_TYPE.TYPE_NET) {

            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_DATA_KEY,mAdapter.getCheckedItems());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(0,intent);
            finish();
            return;
        }
        getSubmitView().setOnCreateContextMenuListener(this);
        getSubmitView().showContextMenu();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_menu, menu);

        MenuItem upload = menu.findItem(R.id.btn_upload_tasks);
        upload.setOnMenuItemClickListener(this);

        MenuItem download = menu.findItem(R.id.btn_download_tasks);
        download.setOnMenuItemClickListener(this);

        if(mSelectType.value % 2 == 0) {
            download.setEnabled(false);
        } else {
            upload.setEnabled(false);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FileInfo info = mDatas.get(position);
        if(info.type == FileInfo.FILE_TYPE.DIRECTORY) {
            mPathStack.push(mRootPath);
            mRootPath = info.path;
            initDatas();
        }
    }

    @Override
    public void onRefresh() {
        isLoadMore = false;
        mDatas.clear();
        initDatas();
    }
}
