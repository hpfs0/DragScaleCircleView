package com.rori.zenvo.dragscalecircleviewexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rori.zenvo.dragscalecircleview.DragScaleCircleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DragScaleCircleView(this));
    }
}
