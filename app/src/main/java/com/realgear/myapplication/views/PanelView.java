package com.realgear.myapplication.views;

import static com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout.*;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.realgear.multislidinguppanel.*;

import com.realgear.myapplication.R;

import java.util.Random;

public class PanelView extends BasePanelView {

    public PanelView(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context, panelLayout);

        getContext().setTheme(R.style.Theme_SampleMultiPanel);
        LayoutInflater.from(getContext()).inflate(R.layout.panel_layout, this, true);
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
        Random r = new Random();

        int color = Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
        setBackgroundColor(color);

        this.setOnClickListener(v -> {
            //hidePanel();
            Log.i("Panel", "Panel was Clicked");
        });
    }

    @Override
    public void onPanelStateChanged(int panelSate) {

    }
}
