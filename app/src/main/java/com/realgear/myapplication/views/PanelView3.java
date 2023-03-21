package com.realgear.myapplication.views;

import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.COLLAPSED;
import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.SLIDE_VERTICAL;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.realgear.multislidinguppanel.BasePanelView;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;
import com.realgear.myapplication.R;

import java.util.Random;

public class PanelView3 extends BasePanelView {

    public PanelView3(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context);

        getContext().setTheme(R.style.Theme_SampleMultiPanel);
        LayoutInflater.from(getContext()).inflate(R.layout.panel_layout_2, this, true);
    }

    @Override
    public void onCreateView() {
        this.setPanelState(COLLAPSED);
        this.setSlideDirection(SLIDE_VERTICAL);

        this.setPeakHeight(120);
    }

    @Override
    public void onBindView() {

    }

    @Override
    public void onPanelStateChanged(int panelSate) {

    }
}
