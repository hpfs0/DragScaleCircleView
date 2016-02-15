package com.rori.zenvo.dragscalecircleviewexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;

import com.rori.zenvo.dragscalecircleview.DragScaleCircleView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CAMERA_PATH = "/DCIM/Camera";
    private static final String CAMERA_CROP_IMAGE_PATH = "/DCIM/Camera/crop_image.jpg";

    DragScaleCircleView mDragScaleCircleView;
    Switch guideLineSwitch;
    Button getCroppedImage;
    DiscreteSeekBar guideLineSizeSeek, guideLineColorSeek, borderColorSeek;
    ImageView croppedImage, borderColor1, borderColor2, borderColor3, borderColor4, borderColor5;
    float touchX, touchY;
    List<Integer> colors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragScaleCircleView = (DragScaleCircleView) findViewById(R.id.dragScaleCircleView);

        colors.add(getResources().getColor(R.color.border));
        colors.add(getResources().getColor(android.R.color.holo_red_light));
        colors.add(getResources().getColor(android.R.color.holo_green_light));
        colors.add(getResources().getColor(android.R.color.holo_purple));
        colors.add(getResources().getColor(android.R.color.holo_blue_light));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));

        guideLineSwitch = (Switch) findViewById(R.id.guideLineSwitch);
        guideLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDragScaleCircleView.setHasGuideLine(true);
                } else {
                    mDragScaleCircleView.setHasGuideLine(false);
                }
            }
        });
        croppedImage = (ImageView) findViewById(R.id.croppedImage);
        croppedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = croppedImage.getDrawable();
                if (drawable != null) {
                    showDialog(drawable);
                }
            }
        });
        croppedImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchX = event.getRawX();
                touchY = event.getRawY();
                return false;
            }
        });
        croppedImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, croppedImage);
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.saveImage:
                                saveImage();
                                break;
                            case R.id.loadImage:
                                loadImage();
                                break;
                            case R.id.clearImage:
                                croppedImage.setImageDrawable(null);
                                break;
                            default:
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

        getCroppedImage = (Button) findViewById(R.id.getCroppedImage);
        getCroppedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap croppedBitmap = mDragScaleCircleView.getCroppedCircleBitmap();
                croppedImage.setImageBitmap(croppedBitmap);

            }
        });

        guideLineColorSeek = (DiscreteSeekBar) findViewById(R.id.guideLineColorSeek);
        guideLineColorSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                int selectedColor = colors.get(value);
                guideLineColorSeek.setThumbColor(selectedColor, selectedColor);
                guideLineColorSeek.setScrubberColor(selectedColor);
                setGuideLineColor(selectedColor);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        guideLineSizeSeek = (DiscreteSeekBar) findViewById(R.id.guideLineSizeSeek);
        guideLineSizeSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                mDragScaleCircleView.setGuideLineStrokeWidth(value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        borderColorSeek = (DiscreteSeekBar) findViewById(R.id.borderColorSeek);
        borderColorSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                int selectedColor = colors.get(value);
                borderColorSeek.setThumbColor(selectedColor, selectedColor);
                borderColorSeek.setScrubberColor(selectedColor);
                setBorderColor(selectedColor);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private void setGuideLineColor(int color) {
        mDragScaleCircleView.setGuideLinePaintColor(color);
    }

    private void setBorderColor(int color) {
        mDragScaleCircleView.setBorderPaintColor(color);
    }

    private void showDialog(final Drawable drawable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.image_dialog, null);
        ImageView image = (ImageView) dialogLayout.findViewById(R.id.imageDialog);
        image.setImageDrawable(drawable);

        dialog.setView(dialogLayout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogTheme);
        dialog.show();
    }

    private void saveImage() {
        Bitmap bitmap = ((BitmapDrawable) croppedImage.getDrawable()).getBitmap();
        File cameraPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + CAMERA_PATH);
        if (!cameraPath.exists()) {
            cameraPath.mkdirs();
        }
        File cachePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + CAMERA_CROP_IMAGE_PATH);
        FileOutputStream outputStream = null;
        try {
            if (cachePath.createNewFile()) {
                outputStream = new FileOutputStream(cachePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadImage() {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + CAMERA_CROP_IMAGE_PATH).copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap != null) {
            croppedImage.setImageDrawable(null);
            croppedImage.setImageBitmap(bitmap);
        }
    }
}
