package com.scott.example.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.adapter.FileInfoAdapter;
import com.scott.example.http.IRequestService;
import com.scott.example.http.RetrofitSingleton;
import com.scott.example.moudle.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FileListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private List<FileInfo> mDatas = new ArrayList<>();
    private FileInfoAdapter mAdapter;
    //private ListView mListView;
    private RecyclerView mListView;
    private boolean isLocal;
    public static String EXTRA_IS_LOCAL = "LOCAL_NET";
    public static String EXTRA_ROOT_PATH = "PATH";
    private String mRootPath;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean isLoadMore = false;

    private final class MyScriber extends Subscriber<List<FileInfo>> {

        @Override
        public void onCompleted() {
            mRefreshLayout.setRefreshing(false);
            if(!isLocal && false) {
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
        }

        @Override
        public void onNext(List<FileInfo> fileInfos) {
            if(fileInfos == null) {
                if(isLoadMore) {
                    mAdapter.loadMoreEnd();
                }
                return;
            }
            mDatas.addAll(fileInfos);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        isLocal = getIntent().getBooleanExtra(EXTRA_IS_LOCAL,false);
        if(isLocal) {
            setTitle(getString(R.string.local_file_list));
        } else {
            setTitle(getString(R.string.server_file_list));
        }

        mRootPath = getIntent().getStringExtra(EXTRA_ROOT_PATH);
        if(isLocal && TextUtils.isEmpty(mRootPath)) {
            mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if(TextUtils.isEmpty(mRootPath)){
            mRootPath = "root";
        }

        initViews();
        initDatas();
    }

    private void initDatas() {
        if(isLocal) {
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
                    fileInfo.type = f.isDirectory() ? 0 : 1;
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
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FileInfo info = mDatas.get(position);
        if(info.type == 0) {
            Intent intent = new Intent(this,FileListActivity.class);
            intent.putExtra(EXTRA_ROOT_PATH,info.path);
            intent.putExtra(EXTRA_IS_LOCAL,isLocal);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        isLoadMore = false;
        mDatas.clear();
        initDatas();
    }
}
