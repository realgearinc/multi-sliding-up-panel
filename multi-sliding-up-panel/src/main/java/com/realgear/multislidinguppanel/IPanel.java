package com.realgear.multislidinguppanel;

import android.view.View;

import androidx.annotation.NonNull;

public interface IPanel<T extends View> {

    @NonNull
    T getPanelView();

    int getPanelExpandedHeight();
    int getPanelExpandedHeightOffset();
    int getPanelCollapsedHeight();
    int getPanelTopByPanelState(@MultiSlidingUpPanelLayout.PanelState int panelState);

    @MultiSlidingUpPanelLayout.PanelState
    int getPanelState();

    @MultiSlidingUpPanelLayout.SlideDirection
    int getPanelSlideDirection();

    void setPanelState(@MultiSlidingUpPanelLayout.PanelState int panelState);
    void setSlideDirection(@MultiSlidingUpPanelLayout.SlideDirection int slideDirection);
    void setSlidingUpPanelRoot(MultiSlidingUpPanelLayout rootSlidingUpPanel);

    void onSliding(@NonNull IPanel<View> panel, int top, int dy, float slidingOffset);
}
