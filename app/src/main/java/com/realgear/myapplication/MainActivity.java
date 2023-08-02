package com.realgear.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.realgear.multislidinguppanel.MultiSlidingPanelAdapter;
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout;
import com.realgear.multislidinguppanel.PanelStateListener;
import com.realgear.myapplication.views.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MultiSlidingUpPanelLayout panelLayout = findViewById(R.id.multiSlidingUpPanel);

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