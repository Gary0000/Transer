package com.scott.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class FileListActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private List<FileInfo> mDatas = new ArrayList<>();
    private FileInfoAdapter mAdapter;
    private ListView mListView;
    private boolean isLocal;
    public static String EXTRA_IS_LOCAL = "LOCAL_NET";
    public static String EXTRA_ROOT_PATH = "PATH";
    private String mRootPath;
    private Subscriber<List<FileInfo>> mAsyncScriber = new Subscriber<List<FileInfo>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<FileInfo> fileInfos) {
            if(fileInfos == null) return;
            mDatas.addAll(fileInfos);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        setTitle("FileListGet");

        isLocal = getIntent().getBooleanExtra(EXTRA_IS_LOCAL,false);
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
                .subscribe(mAsyncScriber);
    }

    private void loadFromNet() {
        RetrofitSingleton
                .getInstance()
                .getRetrofit()
                .create(IRequestService.class)
                .getFileList(mRootPath,1000)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mAsyncScriber);
    }

    private void initViews() {
        mListView = findViewById(R.id.list_view);
        mAdapter = new FileInfoAdapter(mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo info = mDatas.get(position);
        if(info.type == 0) {
            Intent intent = new Intent(this,FileListActivity.class);
            intent.putExtra(EXTRA_ROOT_PATH,info.path);
            intent.putExtra(EXTRA_IS_LOCAL,isLocal);
            startActivity(intent);
        }
    }
}
