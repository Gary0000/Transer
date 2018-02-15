package com.scott.example;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    private ViewGroup mContentView;
    private TextView mTvTitle;
    private TextView mTvSelect;
    private TextView mTvSubmit;

    protected TextView getSelectView() {
        return mTvSelect;
    }

    protected TextView getSubmitView() {
        return mTvSubmit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.activity_base);

        mContentView = findViewById(R.id.base_content_view);
        mTvSelect = findViewById(R.id.tv_select);
        mTvTitle = findViewById(R.id.tv_title);
        mTvSubmit = findViewById(R.id.tv_submit);

        mTvSelect.setVisibility(View.INVISIBLE);
        mTvSubmit.setVisibility(View.INVISIBLE);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID,mContentView,true);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTvTitle.setText(title);
    }
}
