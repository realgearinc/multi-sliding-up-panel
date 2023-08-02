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

    boolean isUserHidden();
    boolean isUserHiddenModeEnabled();
    void disableUserHiddenMode();

    @MultiSlidingUpPanelLayout.PanelState
    int getPanelState();

    @MultiSlidingUpPanelLayout.PanelState
    int getPrevPanelState();

    @MultiSlidingUpPanelLayout.SlideDirection
    int getPanelSlideDirection();

    void resetPanelRealHeight();

    MultiSlidingUpPanelLayout getMultiSlidingUpPanel();

    void setPanelState(@MultiSlidingUpPanelLayout.PanelState int panelState);
    void setSlideDirection(@MultiSlidingUpPanelLayout.SlideDirection int slideDirection);
    void setSlidingUpPanelRoot(MultiSlidingUpPanelLayout rootSlidingUpPanel);

    void onSliding(@NonNull IPanel<View> panel, int top, int dy, float slidingOffset);
}
