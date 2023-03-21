package com.realgear.multislidinguppanel;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.util.List;

public class Adapter {
    private MultiSlidingUpPanelLayout mSlidingUpPanelLayout;
    private List<Class<?>> mItems;

    private Context mContext;

    public  Adapter(Context context, List<Class<?>> items) {
        this.mContext = context;
        this.mItems = items;
    }

    void setSlidingUpPanelLayout(MultiSlidingUpPanelLayout panelLayout) {
        this.mSlidingUpPanelLayout = panelLayout;
    }

    public MultiSlidingUpPanelLayout getSlidingUpPanelLayout() {
        return this.mSlidingUpPanelLayout;
    }

    public int getItemCount() {
        return this.mItems.size();
    }

    @NonNull
    public IPanel<View> onCreateSlidingPanel(int position) {
        try {
            Constructor<?> c = this.mItems.get(position).getDeclaredConstructor(Context.class, MultiSlidingUpPanelLayout.class);
            BasePanelView panel = (BasePanelView) c.newInstance(this.mContext, this.mSlidingUpPanelLayout);
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
}
