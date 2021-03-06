package com.sxt.chat.base;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sxt.chat.R;

/**
 * Created by sxt on 2018/4/19.
 */

public class HeaderActivity extends BaseActivity {
    private View RootView;
    private TextView title;
    private FrameLayout rightContainer;
    protected AppBarLayout appBarLayout;
    protected Toolbar toolbar;
    private FrameLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        RootView = LayoutInflater.from(this).inflate(R.layout.activity_header, null);
        container = ((FrameLayout) this.RootView.findViewById(R.id.content_frameLayout));
        appBarLayout = (AppBarLayout) RootView.findViewById(R.id.app_bar_layout);
        setToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onGoBack(null);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(View view) {
        if (view != null) {
            if (this.container != null) {
                container.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                super.setContentView(this.RootView);
            }
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view != null) {
            if (this.container != null) {
                container.addView(view, params);
                super.setContentView(this.RootView, params);
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (this.container != null) {
            View view = LayoutInflater.from(this).inflate(layoutResID, null);
            container.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            super.setContentView(this.RootView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public void setToolbarNavigationIcon(int resId) {
        if (this.toolbar != null) {
            this.toolbar.setNavigationIcon(resId);
        }
    }

    public void setToolbarNavigationIcon(Drawable drawable) {
        if (this.toolbar != null) {
            this.toolbar.setNavigationIcon(drawable);
        }
    }

    public void setToolBarBackground(int resId) {
        try {
            if (this.toolbar != null) {
                this.toolbar.setBackgroundColor(ContextCompat.getColor(this, resId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ValueAnimator valueAnimator;

    public void showToolbar(boolean show) {
        appBarLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (!show && container != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) container.getLayoutParams();
            layoutParams.topMargin = 0;
            container.setLayoutParams(layoutParams);
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) RootView.findViewById(R.id.toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);
        rightContainer = (FrameLayout) toolbar.findViewById(R.id.right_container);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setSupportActionBar(toolbar);//代替原来的ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏ToolBar自带的Title
        setToolbarNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    public void replaceToolbarView(View toolbarView, Toolbar toolbar) {
        if (toolbar != null) {
            if (this.toolbar != null) {
                appBarLayout.removeView(this.toolbar);
            }
            appBarLayout.addView(toolbarView, new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
            this.toolbar = toolbar;
            title = (TextView) toolbar.findViewById(R.id.title);
            rightContainer = (FrameLayout) toolbar.findViewById(R.id.right_container);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            setSupportActionBar(toolbar);//代替原来的ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏ToolBar自带的Title
            setToolbarNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    public void showToolbarBack(boolean showBack) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
        }
    }

    public void setTitle(String title) {
        if (this.title != null) {
            this.title.setText(title);
            toolbar.setTitle("");
        }
    }

    public void setToolbarTitle(String title) {
        if (this.title != null) {
            this.title.setText("");
        }
        toolbar.setTitle(title);
    }

    public void setTitle(int resId) {
        if (this.title != null) {
            this.title.setText(resId);
            toolbar.setTitle("");
        }
    }

    public void setToolbarTitle(int resId) {
        if (this.title != null) {
            this.title.setText("");
        }
        toolbar.setTitle(resId);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (this.title != null) {
            this.title.setText(title);
            toolbar.setTitle("");
        }
    }

    public void setToolbarTitle(CharSequence title) {
        if (this.title != null) {
            this.title.setText("");
        }
        toolbar.setTitle(title);
    }

    public void setRightContainer(View rightContainer) {
        if (this.rightContainer != null) {
            if (this.rightContainer.getChildCount() > 0) {
                this.rightContainer.removeAllViews();
            }
            this.rightContainer.addView(rightContainer);
            this.rightContainer.setOnClickListener(v -> {
                for (int i = 0; i < HeaderActivity.this.rightContainer.getChildCount(); i++) {
                    HeaderActivity.this.rightContainer.getChildAt(i).performClick();
                }
            });
        }
    }

    public void onGoBack(View view) {
        finish();
    }
}
