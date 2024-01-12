package com.realgear.multislidinguppanel;

import android.view.View;

public class PanelStateListener {
    private final MultiSlidingUpPanelLayout mPanelLayout;

    public PanelStateListener(MultiSlidingUpPanelLayout panelLayout) {
        this.mPanelLayout = panelLayout;
    }

    public void onPanelSliding(IPanel<View> panel, float slidingOffset) {}

    @SuppressWarnings("unchecked")
    void onPanelCollapsed(IPanel<View> panel) {
        int count = this.mPanelLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            IPanel<View> temp_panel = (IPanel<View>) this.mPanelLayout.getChildAt(i);

            if (panel.isUserHidden()) {
                panel.disableUserHiddenMode();
                temp_panel.resetPanelRealHeight();
            }

            if (!temp_panel.isUserHidden()) {
                temp_panel.setPanelState(MultiSlidingUpPanelLayout.COLLAPSED);
            }

            temp_panel.getPanelView().setEnabled(true);
        }
        this.mPanelLayout.requestLayout();
    }

    @SuppressWarnings("unchecked")
    void onPanelExpanded(IPanel<View> panel) {
        int count = this.mPanelLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            IPanel<View> temp_panel = (IPanel<View>) this.mPanelLayout.getChildAt(i);
            if(temp_panel == panel) {
                temp_panel.getPanelView().setEnabled(false);
            }
            else {
                if (panel.isUserHidden()) {
                    panel.disableUserHiddenMode();
                    temp_panel.resetPanelRealHeight();
                }
                temp_panel.setPanelState(MultiSlidingUpPanelLayout.HIDDEN);
                temp_panel.getPanelView().setEnabled(true);
            }
        }
    }

    void onPanelHidden(IPanel<View> panel) {
        int count = this.mPanelLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            IPanel<View> temp_panel = (IPanel<View>) this.mPanelLayout.getChildAt(i);
            if (panel.isUserHidden() && panel != temp_panel) {
                temp_panel.resetPanelRealHeight();
            };
        }
        this.mPanelLayout.requestLayout();
    }
}
