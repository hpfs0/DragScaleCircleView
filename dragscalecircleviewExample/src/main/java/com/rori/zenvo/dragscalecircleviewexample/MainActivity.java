package com.rori.zenvo.dragscalecircleviewexample;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
    ImageView croppedImage, guideLineColor1, guideLineColor2, guideLineColor3, guideLineColor4, guideLineColor5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragScaleCircleView = (DragScaleCircleView) findViewById(R.id.dragScaleCircleView);
        guideLineColor1 = (ImageView) findViewById(R.id.guideLineColor1);
        guideLineColor2 = (ImageView) findViewById(R.id.guideLineColor2);
        guideLineColor3 = (ImageView) findViewById(R.id.guideLineColor3);
        guideLineColor4 = (ImageView) findViewById(R.id.guideLineColor4);
        guideLineColor5 = (ImageView) findViewById(R.id.guideLineColor5);

        setGuideLineColor(guideLineColor1);
        setGuideLineColor(guideLineColor2);
        setGuideLineColor(guideLineColor3);
        setGuideLineColor(guideLineColor4);
        setGuideLineColor(guideLineColor5);

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

    private void setGuideLineColor(final ImageView target){
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragScaleCircleView.setGuideLinePaintColor(((ColorDrawable)target.getBackground()).getColor());
            }
        });
    }
}
