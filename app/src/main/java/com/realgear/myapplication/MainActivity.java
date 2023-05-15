package com.realgear.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.realgear.multislidinguppanel.Adapter;
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

        items.add(PanelView1.class);
        items.add(PanelView2.class);
        items.add(PanelView3.class);

        panelLayout.setPanelStateListener(new PanelStateListener(panelLayout));
        panelLayout.setAdapter(new Adapter(this, items));
    }
}