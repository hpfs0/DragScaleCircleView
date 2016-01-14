package com.rori.zenvo.dragscalecircleviewexample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.rori.zenvo.dragscalecircleview.DragScaleCircleView;

public class MainActivity extends AppCompatActivity {

    DragScaleCircleView mDragScaleCircleView;
    Switch guideLineSwitch;
    Button getCroppedImage;
    ImageView croppedImage;

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
        croppedImage = (ImageView) findViewById(R.id.croppedImage);
        getCroppedImage = (Button) findViewById(R.id.getCroppedImage);
        getCroppedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap croppedBitmap = mDragScaleCircleView.getCroppedCircleBitmap();
                croppedImage.setImageBitmap(croppedBitmap);

            }
        });
    }
}
