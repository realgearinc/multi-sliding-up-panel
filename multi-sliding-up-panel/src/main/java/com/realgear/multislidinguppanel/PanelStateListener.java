package com.realgear.multislidinguppanel;

import android.view.View;

public class PanelStateListener {
    private final MultiSlidingUpPanelLayout mPanelLayout;

    public PanelStateListener(MultiSlidingUpPanelLayout panelLayout) {
        this.mPanelLayout = panelLayout;
    }

    public void onPanelSliding(IPanel<View> panel, float slidingOffset) { }

    @SuppressWarnings("unchecked")
    void onPanelCollapsed(IPanel<View> panel) {
        int count = this.mPanelLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            panel = (IPanel<View>) this.mPanelLayout.getChildAt(i);
            panel.setPanelState(MultiSlidingUpPanelLayout.COLLAPSED);
            panel.getPanelView().setEnabled(true);
        }
        this.mPanelLayout.requestLayout();
    }

    @SuppressWarnings("unchecked")
    void onPanelExpanded(IPanel<View> panel) {
        int count = this.mPanelLayout.getChildCount();

        for (int i = 1; i < count; i++) {
            IPanel<View> panel1 = (IPanel<View>) this.mPanelLayout.getChildAt(i);
            if(panel1 == panel) {
                panel1.getPanelView().setEnabled(false);
            }
            else {
                panel1.setPanelState(MultiSlidingUpPanelLayout.HIDDEN);
                panel1.getPanelView().setEnabled(true);
            }
        }
    }
    void onPanelHidden(IPanel<View> panel) {

    }
}
