package com.scott.example;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseActivity extends AppCompatActivity {

    private ViewGroup mContentView;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        mContentView = findViewById(R.id.base_content_view);
        mToolBar = findViewById(R.id.tool_bar);
        mToolBar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        mToolBar.setTitleTextColor(Color.WHITE);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID,mContentView,true);
    }

    @Override
    public void setTitle(CharSequence title) {
        mToolBar.setTitle(title);
    }
}
