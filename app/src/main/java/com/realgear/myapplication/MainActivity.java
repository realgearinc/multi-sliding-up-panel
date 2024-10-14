package com.realgear.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.realgear.multislidinguppanel.MultiSlidingPanelAdapter;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;
import com.realgear.multislidinguppanel.PanelStateListener;
import com.realgear.myapplication.views.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static int getStatusBarHeight(Context context) {
        int status_bar_height = 0;

        int status_r_id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(status_r_id > 0)
            status_bar_height = context.getResources().getDimensionPixelSize(status_r_id);

        return  status_bar_height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        MultiSlidingUpPanelLayout panelLayout = findViewById(R.id.multiSlidingUpPanel);

        panelLayout.setPadding(0, getStatusBarHeight(this.getBaseContext()), 0, 0);
        panelLayout.setClipToPadding(false);
        panelLayout.enableNoLimitsLayout(true);

        List<Class<?>> items = new ArrayList<>();

        items.add(PanelView.class);
        items.add(PanelView2.class);
        items.add(PanelView3.class);

        panelLayout.setPanelStateListener(new PanelStateListener(panelLayout));
        panelLayout.setAdapter(new MultiSlidingPanelAdapter(this, items));

        ((Button)panelLayout.findViewById(R.id.btn_expand)).setOnClickListener(v -> {
            panelLayout.getAdapter().getItem(PanelView3.class).expandPanel();
        });

        ((Button)panelLayout.findViewById(R.id.btn_collapse)).setOnClickListener(v -> {
            panelLayout.getAdapter().getItem(PanelView3.class).collapsePanel();
        });

        ((Button)panelLayout.findViewById(R.id.btn_hide)).setOnClickListener(v -> {
            panelLayout.getAdapter().getItem(PanelView3.class).hidePanel();
        });
    }
}