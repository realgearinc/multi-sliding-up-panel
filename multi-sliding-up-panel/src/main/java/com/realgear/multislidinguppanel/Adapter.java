package com.realgear.multislidinguppanel;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;

public class Adapter {
    private MultiSlidingUpPanelLayout mSlidingUpPanelLayout;
    private List<Class<?>> mItems;

    private AppCompatActivity mActivity;

    public  Adapter(AppCompatActivity activity, List<Class<?>> items) {
        this.mActivity = activity;
        this.mItems = items;
    }

    void setSlidingUpPanelLayout(MultiSlidingUpPanelLayout panelLayout) {
        this.mSlidingUpPanelLayout = panelLayout;
    }

    public int getItemCount() {
        return this.mItems.size();
    }

    public AppCompatActivity getAppCompatActivity() {
        return this.mActivity;
    }

    @NonNull
    public IPanel<View> onCreateSlidingPanel(int position) {
        try {
            Constructor<?> c = this.mItems.get(position).getDeclaredConstructor(Context.class, MultiSlidingUpPanelLayout.class);
            BasePanelView panel = (BasePanelView) c.newInstance(this.mActivity.getBaseContext(), this.mSlidingUpPanelLayout);
            panel.setFloor(position);
            panel.onCreateView();
            panel.setEnabled(true);
            return panel;
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void onBindView(IPanel<View> panel, int position) {
        if (getItemCount() == 0)
            return;

        BasePanelView panelView = (BasePanelView) panel;
        panelView.onBindView();
    }

    @SuppressWarnings("unchecked")
    public IPanel<View> getItem(int position) {
        int childCount = this.mSlidingUpPanelLayout.getChildCount();
        if (getItemCount() == 0 || childCount <= 1 || (position + 1) > childCount)
            return null;

        View child = this.mSlidingUpPanelLayout.getChildAt(position + 1);

        if (!(child instanceof IPanel))
            return null;

        return (IPanel<View>) child;
    }

    public BasePanelView getItem(Class<?> panelType) {
        int childCount = this.mSlidingUpPanelLayout.getChildCount();
        if (getItemCount() == 0 || childCount <= 1)
            return null;

        int index = this.mItems.indexOf(panelType);
        if ((index + 1) > childCount)
            return null;

        View child = this.mSlidingUpPanelLayout.getChildAt(index + 1);

        if (!(child instanceof BasePanelView))
            return null;

        return (BasePanelView) child;
    }
}
