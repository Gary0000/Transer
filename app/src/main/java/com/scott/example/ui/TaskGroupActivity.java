package com.scott.example.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.TableLayout;

import com.scott.annotionprocessor.TaskType;
import com.scott.example.BaseActivity;
import com.scott.example.R;
import com.scott.example.adapter.TaskGroupViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskGroupActivity extends BaseActivity {

    @BindView(R.id.vp_content)
    public ViewPager viewPager;

    @BindView(R.id.tab_layout)
    public TabLayout tableLayout;

    private FragmentPagerAdapter mPagerAdapter;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_group);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        Fragment fragment = new TaskGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskGroupFragment.EXTRA_TASK_TYPE, TaskType.TYPE_HTTP_UPLOAD);
        fragment.setArguments(bundle);
        mFragments.add(fragment);

        fragment = new TaskGroupFragment();
        bundle = new Bundle();
        bundle.putSerializable(TaskGroupFragment.EXTRA_TASK_TYPE, TaskType.TYPE_HTTP_DOWNLOAD);
        fragment.setArguments(bundle);
        mFragments.add(fragment);

        mPagerAdapter = new TaskGroupViewPagerAdapter(getSupportFragmentManager(),mFragments,this);
        viewPager.setAdapter(mPagerAdapter);

        //tableLayout.setTabGravity(Gravity.CENTER);
        tableLayout.setTabMode(TabLayout.MODE_FIXED);
        tableLayout.setupWithViewPager(viewPager);
    }
}
