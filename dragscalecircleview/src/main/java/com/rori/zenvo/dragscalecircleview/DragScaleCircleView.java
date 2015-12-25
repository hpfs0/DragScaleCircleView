/**
 * Copyright 2015, Zenvo, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.rori.zenvo.dragscalecircleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/*
 * Custom View that provides dragged and scaled.
 *
 * @author hpfs0
 */
public class DragScaleCircleView extends ImageView implements View.OnTouchListener {

    // The screen width.
    protected int mScreenWidth;

    // The screen height.
    protected int mScreenHeight;

    // The last point X.
    protected int mLastX;

    // The last point Y.
    protected int mLastY;

    // The circle's center point coordinate X.
    private int mCenterPointX;

    // The circle's center point coordinate X.
    private int mCenterPointY;

    // The radius of the circle
    private int mRadius;

    // The direction that drag the circle view.
    private int mDragDirection;

    // Offset from the screen during initialization.
    private int mOffset;

    // The direction that drag side ward the circle view.
    private static final int SIDE = 0x10;

    // The direction that drag center the circle view.
    private static final int CENTER = 0x20;

    private static final int HANDLE_DOWN = (1 << 0);

    private static final int HANDLE_MOVE = (1 << 1);

    private static final int HANDLE_UP = (1 << 2);

    // the paint that can be used to draw.
    private final Paint mPaint = new Paint();

    // the paint that show outline circle on touch.
    private final Paint mHandlePaint = new Paint();

    // the paint that draw guide line.
    private final Paint mGuideLinePaint = new Paint();

    // outline circle radius.
    private float mHandleRadius;

    // handle mode.
    private int mHandleMode;

    // the circle view's border color.
    protected int mBorderColor;

    // -------------------------------------------------------------
    //                            constructor
    // -------------------------------------------------------------
    public DragScaleCircleView(Context context) {
        super(context);
        init(context, null);
        setOnTouchListener(this);
        initResources(context);
    }

    public DragScaleCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setOnTouchListener(this);
        initResources(context);
    }

    public DragScaleCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        setOnTouchListener(this);
        initResources(context);
    }
    // -------------------------------------------------------------
    //                            constructor
    // -------------------------------------------------------------

    /**
     * Initialization obtain the screen width and height.
     */
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            mScreenHeight = getResources().getDisplayMetrics().heightPixels - 40 - 128;
        } else {
            mScreenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        }
        mCenterPointX = mScreenWidth / 2;
        mCenterPointY = mScreenHeight / 2;
        mOffset = 20;
        mRadius = (Math.min(mScreenWidth, mScreenHeight) - mOffset) / 2;
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setStyle(Paint.Style.STROKE);

        mHandlePaint.setColor(Color.RED);
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setAntiAlias(true);
        mHandleRadius = 12f;

        mGuideLinePaint.setColor(Color.RED);
        mGuideLinePaint.setAlpha(100);
        mGuideLinePaint.setStyle(Paint.Style.STROKE);
        mHandlePaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2.0f);
    }

    /**
     * Initialization the application resources.
     *
     * @param context
     */
    protected void initResources(Context context) {
        mBorderColor = ContextCompat.getColor(context, R.color.border);
    }

    /**
     * drawing the view.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setStyle(Paint.Style.STROKE);

        mHandlePaint.setColor(Color.RED);
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setAntiAlias(true);
        mHandleRadius = 12f;

        mGuideLinePaint.setColor(Color.RED);
        mGuideLinePaint.setAlpha(100);
        mGuideLinePaint.setStyle(Paint.Style.STROKE);
        mHandlePaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2.0f);

        canvas.drawCircle(mCenterPointX, mCenterPointY, mRadius, mPaint);
        switch (mHandleMode) {
            case HANDLE_DOWN:
                if (mDragDirection == SIDE) {
                    drawHandles(canvas);
                    drawGuideLine(canvas);
                } else if (mDragDirection == CENTER) {
                    drawGuideLine(canvas);
                }
                break;
            case HANDLE_MOVE:
                if (mDragDirection == SIDE) {
                    drawHandles(canvas);
                    drawGuideLine(canvas);
                } else if (mDragDirection == CENTER) {
                    drawGuideLine(canvas);
                }
                break;
            case HANDLE_UP:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            setHandleMode(HANDLE_DOWN);
            mLastY = (int) event.getRawY();
            mLastX = (int) event.getRawX();
            mDragDirection = getDragDirection((DragScaleCircleView) v, (int) event.getX(), (int) event.getY());
        } else if (action == MotionEvent.ACTION_MOVE) {
            setHandleMode(HANDLE_MOVE);
            drag((DragScaleCircleView) v, event, action);

        } else if (action == MotionEvent.ACTION_UP) {
            setHandleMode(HANDLE_UP);
            drag((DragScaleCircleView) v, event, action);
        }
        invalidate();
        return true;
    }

    private void setHandleMode(int mode) {
        this.mHandleMode = mode;
    }

    private void drawHandles(Canvas canvas) {
        canvas.drawCircle(mCenterPointX - mRadius, mCenterPointY, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX, mCenterPointY + mRadius, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX, mCenterPointY - mRadius, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX + mRadius, mCenterPointY, mHandleRadius, mHandlePaint);
    }

    private void drawGuideLine(Canvas canvas) {
        float offset = (float) Math.sqrt(Math.pow(mRadius, 2) / 2);
        // top left
        float tlpointX = mCenterPointX - offset;
        float tlpointY = mCenterPointY - offset;

        // top right
        float trpointX = mCenterPointX + offset;
        float trpointY = tlpointY;

        // bottom left
        float blpointX = tlpointX;
        float blpointY = mCenterPointY + offset;

        // bottom right
        float brpointX = trpointX;
        float brpointY = blpointY;

        canvas.drawLine(tlpointX, tlpointY, trpointX, trpointY, mGuideLinePaint);
        canvas.drawLine(mCenterPointX - mRadius, mCenterPointY, mCenterPointX + mRadius, mCenterPointY, mGuideLinePaint);
        canvas.drawLine(blpointX, blpointY, brpointX, brpointY, mGuideLinePaint);

        canvas.drawLine(tlpointX, tlpointY, blpointX, blpointY, mGuideLinePaint);
        canvas.drawLine(mCenterPointX, mCenterPointY - mRadius, mCenterPointX, mCenterPointY + mRadius, mGuideLinePaint);
        canvas.drawLine(trpointX, trpointY, brpointX, brpointY, mGuideLinePaint);

    }

    /**
     * get the drag direction side or center
     *
     * @param v the circle view
     * @param x the touch point X
     * @param y the touch point Y
     * @return side or center
     */
    protected int getDragDirection(DragScaleCircleView v, int x, int y) {
        double d = Math.sqrt(Math.pow(x - v.mCenterPointX, 2) + Math.pow(y - v.mCenterPointY, 2));
        // touch point at the circle side
        if (d >= mRadius - mOffset / 2 && d <= mRadius + mOffset / 2) {
            return SIDE;
        }
        if (d < mRadius - mOffset / 2) {
            return CENTER;
        }
        return 0;
    }

    /**
     * drag the circle view.
     *
     * @param v      the circle view
     * @param event  event
     * @param action action
     */
    protected void drag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                int touchX = (int) event.getX();
                int touchY = (int) event.getY();
                switch (mDragDirection) {
                    case CENTER:
                        center((DragScaleCircleView) v, dx, dy);
                        break;
                    case SIDE:
                        side((DragScaleCircleView) v, touchX, touchY, touchX + dx, touchY + dy);
                        break;

                }
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                mDragDirection = 0;
                break;
        }
    }

    /**
     * move the circle view.
     *
     * @param v  the circle view
     * @param dx move X
     * @param dy move Y
     */
    private void center(DragScaleCircleView v, int dx, int dy) {
        if (mCenterPointX + dx - mRadius < mOffset / 2) {
            return;
        }
        if (mScreenWidth - (mCenterPointX + dx + mRadius) < mOffset / 2) {
            return;
        }
        if (mCenterPointY + dy - mRadius < mOffset / 2) {
            return;
        }
        if (mScreenHeight - (mCenterPointY + dy + mRadius) < mOffset / 2) {
            return;
        }
        mCenterPointX += dx;
        mCenterPointY += dy;
    }

    /**
     * touch on side scale in or out process.
     *
     * @param v     the circle view
     * @param lastX touch point X
     * @param lastY touch point Y
     * @param rawX  touch move point X
     * @param rawY  touch move point Y
     */
    private void side(DragScaleCircleView v, int lastX, int lastY, int rawX, int rawY) {
        // action_up: the touch point to the distance of the center on action up
        double rawDistance = Math.sqrt(Math.pow(rawX - mCenterPointX, 2) + Math.pow(rawY - mCenterPointY, 2));
        // get max radius of this context
        int maxRadius = Math.min(Math.min(mCenterPointX, mScreenWidth - mCenterPointX), Math.min(mCenterPointY, mScreenHeight - mCenterPointY)) - mOffset / 2;
        if (rawDistance <= maxRadius && rawDistance >= 50) {
            if (mRadius < maxRadius && mRadius > 50) {
                mRadius = (int) rawDistance;
            } else if (mRadius == maxRadius) {
                // only scale in can be done
                if (lastX > mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top right
                    if (rawX < lastX || rawY > lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
                if (lastX > mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom right
                    if (rawX < lastX || rawY < lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top left
                    if (rawX > lastX || rawY > lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom left
                    if (rawX > lastX || rawY < lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
            } else if (mRadius == 50) {
                // only scale out can be done
                if (lastX > mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top right
                    if (rawX > lastX || rawY < lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
                if (lastX > mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom right
                    if (rawX > lastX || rawY > lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top left
                    if (rawX < lastX || rawY < lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom left
                    if (rawX < lastX || rawY > lastY) {
                        mRadius = (int) rawDistance;
                    }
                }
            }
        }
    }

}
