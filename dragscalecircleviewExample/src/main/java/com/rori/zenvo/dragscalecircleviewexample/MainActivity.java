package com.rori.zenvo.dragscalecircleviewexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.rori.zenvo.dragscalecircleview.DragScaleCircleView;

public class MainActivity extends AppCompatActivity {

    DragScaleCircleView mDragScaleCircleView;
    Switch guideLineSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragScaleCircleView = (DragScaleCircleView) findViewById(R.id.dragScaleCircleView);
        guideLineSwitch = (Switch) findViewById(R.id.guideLineSwitch);
        guideLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDragScaleCircleView.setmHasGuideLine(true);
                } else {
                    mDragScaleCircleView.setmHasGuideLine(false);
                }
            }
        });
    }
}
