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

import org.w3c.dom.Text;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    private ViewGroup mContentView;
    private Toolbar mToolBar;
    private TextView mTvTitle;
    private TextView mTvSelect;
    private int mState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.activity_base);

        mContentView = findViewById(R.id.base_content_view);
        mToolBar = findViewById(R.id.tool_bar);
        mTvSelect = findViewById(R.id.tv_select);
        mTvTitle = findViewById(R.id.tv_title);

        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolBar.setTitleTextColor(Color.WHITE);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.onBackPressed();
            }
        });
        mToolBar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_menu_white_24dp));
        mTvSelect.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                } catch (Exception e) {
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID,mContentView,true);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTvTitle.setText(title);
    }

    protected void enableSelect() {
        mTvSelect.setVisibility(View.VISIBLE);
    }

    protected void disenableSelect() {
        mTvSelect.setVisibility(View.INVISIBLE);
    }
}
