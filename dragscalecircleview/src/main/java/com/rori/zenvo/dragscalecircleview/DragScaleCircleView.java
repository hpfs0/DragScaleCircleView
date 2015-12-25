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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.rori.zenvo.util.PaintUtil;

/*
 * Custom View that provides dragged and scaled.
 *
 * @author hpfs0
 */
public class DragScaleCircleView extends ImageView implements View.OnTouchListener {

    @SuppressWarnings("unused")
    private static final String TAG = DragScaleCircleView.class.getName();

    // The drawable width.
    protected float mDrawableWidth;

    // The drawable height.
    protected float mDrawableHeight;

    // The last point X.
    protected int mLastX;

    // The last point Y.
    protected int mLastY;

    // The circle's center point coordinate X.
    private float mCenterPointX;

    // The circle's center point coordinate X.
    private float mCenterPointY;

    // The radius of the circle
    private float mRadius;

    // The direction that drag the circle view.
    private int mDragDirection;

    // Offset from the screen during initialization.
    private float mOffset;

    // The direction that drag side ward the circle view.
    private static final int SIDE = 0x10;

    // The direction that drag center the circle view.
    private static final int CENTER = 0x20;

    private static final int HANDLE_DOWN = (1 << 0);

    private static final int HANDLE_MOVE = (1 << 1);

    private static final int HANDLE_UP = (1 << 2);

    // The bounding box around the bitmap that it can be cropped.
    @NonNull
    private RectF mBitmapRect = new RectF();

    // The paint that can be used to draw.
    private Paint mBoarderPaint;

    // The paint used to darken the surrounding areas outside the crop area.
    private  Paint mSurroundingAreaOverlayPaint;

    // The paint that show outline circle on touch.
    private Paint mHandlePaint;

    // The paint that draw guide line.
    private Paint mGuideLinePaint;

    // The outline circle radius.
    private float mHandleRadius;

    // Handle mode.
    private int mHandleMode;

    // The circle view's border color.
    protected int mBorderColor;

    // -------------------------------------------------------------
    //                            constructor
    // -------------------------------------------------------------
    public DragScaleCircleView(Context context) {
        super(context);
        init(context, null);
        setOnTouchListener(this);
    }

    public DragScaleCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setOnTouchListener(this);
    }

    public DragScaleCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        setOnTouchListener(this);
    }
    // -------------------------------------------------------------
    //                            constructor
    // -------------------------------------------------------------


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    /**
     * Initialization obtain the screen width and height.
     */
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        final Resources resources = context.getResources();
        mBoarderPaint = PaintUtil.newBoarderPaint(resources);
        mSurroundingAreaOverlayPaint = PaintUtil.newSurroundingAreaOverlayPaint(resources);
        mHandlePaint = PaintUtil.newHandlerPaint(resources);
        mHandleRadius = resources.getDimension(R.dimen.corner_width);
        mGuideLinePaint = PaintUtil.newGuideLinePaint(resources);
        mBitmapRect = getBitmapRect();
        initCircleCropWindow(mBitmapRect);
    }

    /**
     * drawing the view.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDarkeSurroundingArea(canvas);
        drawCircleBorder(canvas);
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

    private void drawCircleBorder(@NonNull Canvas canvas) {
        canvas.drawCircle(mCenterPointX, mCenterPointY, mRadius, mBoarderPaint);
    }

    private void drawDarkeSurroundingArea(@NonNull Canvas canvas) {

        final RectF bitmapRect = mBitmapRect;

        final float left = mCenterPointX - bitmapRect.left - mRadius;
        final float top = mCenterPointY - mRadius;
        final float right = mCenterPointX - bitmapRect.left + mRadius;
        final float bottom = mCenterPointY + mRadius;

        /*-
          -------------------------------------
          |                top                |
          -------------------------------------
          |      |                    |       |
          |      |                    |       |
          | left |                    | right |
          |      |                    |       |
          |      |                    |       |
          -------------------------------------
          |              bottom               |
          -------------------------------------
         */

        // Draw "top", "bottom", "left", then "right" quadrants according to diagram above.
        canvas.drawRect(bitmapRect.left, bitmapRect.top, bitmapRect.right, top, mSurroundingAreaOverlayPaint);
        canvas.drawRect(bitmapRect.left, bottom, bitmapRect.right, bitmapRect.bottom, mSurroundingAreaOverlayPaint);
        canvas.drawRect(bitmapRect.left, top, left, bottom, mSurroundingAreaOverlayPaint);
        canvas.drawRect(right, top, bitmapRect.right, bottom, mSurroundingAreaOverlayPaint);
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
     * Initialize the crop window by setting the proper values.
     * <p/>
     * If fixed aspect ratio is turned off, the initial crop window will be set to the displayed
     * image with 10% margin.
     */
    private void initCircleCropWindow(@NonNull RectF bitmapRect) {

        // Initialize circle crop window to have 10% padding of min width/height to Drawable's bounds.
        mOffset = 0.1f * Math.min(bitmapRect.width(), bitmapRect.height());
        mDrawableWidth = bitmapRect.width();
        mDrawableHeight = bitmapRect.height();
        mCenterPointX = mDrawableWidth / 2.0f;
        mCenterPointY = mDrawableHeight / 2.0f;
        mRadius = (Math.min(mDrawableWidth, mDrawableHeight) - mOffset) / 2.0f;
    }

    /**
     * gets the bounding rectangle of the bitmap within the ImageView.
     */
    private RectF getBitmapRect() {

        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return new RectF();
        }

        // Get image matrix values and place them in an array.
        final float[] matrixValues = new float[9];
        getImageMatrix().getValues(matrixValues);

        // Extract the scale and translation values from the matrix.
        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        // Get the width and height of the original bitmap.
        final int drawableIntrinsicWidth = drawable.getIntrinsicWidth();
        final int drawableIntrinsicHeight = drawable.getIntrinsicHeight();

        // Calculate the dimensions as seen on screen.
        final int drawableDisplayWidth = Math.round(drawableIntrinsicWidth * scaleX);
        final int drawableDisplayHeight = Math.round(drawableIntrinsicHeight * scaleY);

        // Get the Rect of the displayed image within the ImageView.
        final float left = Math.max(transX, 0);
        final float top = Math.max(transY, 0);
        final float right = left + drawableDisplayWidth;
        final float bottom = top + drawableDisplayHeight;

        return new RectF(left, top, right, bottom);
    }

    /**
     * move the circle view.
     *
     * @param v  the circle view
     * @param dx move X
     * @param dy move Y
     */
    private void center(DragScaleCircleView v, int dx, int dy) {
        if (mCenterPointX + dx - mRadius < 0) {
            return;
        }
        if (mDrawableWidth - (mCenterPointX + dx + mRadius) < 0) {
            return;
        }
        if (mCenterPointY + dy - mRadius < 0) {
            return;
        }
        if (mDrawableHeight - (mCenterPointY + dy + mRadius) < 0) {
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
        float maxRadius = Math.min(Math.min(mCenterPointX, mDrawableWidth - mCenterPointX), Math.min(mCenterPointY, mDrawableHeight - mCenterPointY));
        if (rawDistance <= maxRadius && rawDistance >= 30) {
            if (mRadius < maxRadius && mRadius > 30) {
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
            } else if (mRadius == 30) {
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
