package com.realgear.myapplication.views;

import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.COLLAPSED;
import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.SLIDE_VERTICAL;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.realgear.multislidinguppanel.BasePanelView;
import com.realgear.multislidinguppanel.IPanel;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;
import com.realgear.myapplication.R;

import java.util.Random;

public class PanelView1 extends BasePanelView {

    public PanelView1(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context, panelLayout);

        getContext().setTheme(R.style.Theme_SampleMultiPanel);
        LayoutInflater.from(getContext()).inflate(R.layout.panel_layout_0, this, true);
    }

    @Override
    public void onCreateView() {
        this.setPanelState(COLLAPSED);
        this.setSlideDirection(SLIDE_VERTICAL);

        this.setPeakHeight(120);
        this.setPanelExpandedHeightOffset(this.mParentSlidingPanel.getPaddingTop());
    }

    @Override
    public void onBindView() {

    }

    @Override
    public void onPanelStateChanged(int panelSate) {

    }

    @Override
    public void onSliding(@NonNull IPanel<View> panel, int top, int dy, float slidingOffset) {
        super.onSliding(panel, top, dy, slidingOffset);


        // We are using the sliding progress as alpha
        // this.setAlpha(slidingOffset);
    }
}
