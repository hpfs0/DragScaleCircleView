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
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
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

    // The screen width.
    protected float mScreenWidth;

    // The screen height.
    protected float mScreenHeight;

    // The last point X.
    protected int mLastX;

    // The last point Y.
    protected int mLastY;

    // The circle's center point coordinate X/Y.
    private float mCenterPointX, mCenterPointY;

    // The radius of the circle
    private float mRadius;

    // The direction that drag the circle view.
    private int mDragDirection;

    // Offset from the screen during initialization.
    private float mOffset;

    private float mScaleX, mScaleY;

    private float mTransX, mTransY;

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

    // The Handle mode.
    private int mHandleMode;

    // The bitmap used to make surrounding area.
    private Bitmap mBitmap;

    // The canvas used to load bitmap.
    private Canvas mCanvas;

    // -------------------------------------------------------------
    //                       custom style
    // -------------------------------------------------------------
    // The custom attr that make guideline display.
    private Boolean mHasGuideLine;

    // The custom attr that guideline size.
    private float mGuideLineSize;

    // The custom attr tha guideline color.
    private int mGuideLineColor;

    // The custom attr tha circle window border size.
    private float mBorderSize;

    // The custom attr tha circle window border color..
    private int mBorderColor;

    // -------------------------------------------------------------
    //                       constructor
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
    //                       constructor
    // -------------------------------------------------------------

    /**
     * Get the value of global mHasGuideLine.
     * @return mHasGuideLine
     */
    public Boolean getmHasGuideLine() {
        return mHasGuideLine;
    }

    /**
     * Set the value of global mHasGuideLine.
     * @param mHasGuideLine
     */
    public void setmHasGuideLine(Boolean mHasGuideLine) {
        this.mHasGuideLine = mHasGuideLine;
    }

    /**
     * Get the value of global mGuideLineSize.
     * @return mGuideLineSize
     */
    public float getmGuideLineSize() {
        return mGuideLineSize;
    }

    /**
     * Set the value of global mGuideLineSize.
     * @param mGuideLineSize
     */
    public void setmGuideLineSize(float mGuideLineSize) {
        this.mGuideLineSize = mGuideLineSize;
    }

    /**
     * Get the value of global mGuideLineColor.
     * @return
     */
    public int getmGuideLineColor() {
        return mGuideLineColor;
    }

    /**
     * Set the value of global mGuideLineColor.
     * @param mGuideLineColor
     */
    public void setmGuideLineColor(int mGuideLineColor) {
        this.mGuideLineColor = mGuideLineColor;
    }

    /**
     * Get the value of global mBorderSize.
     * @return mBorderSize
     */
    public float getmBorderSize() {
        return mBorderSize;
    }

    /**
     * Set the value of global mBorderSize.
     * @param mBorderSize
     */
    public void setmBorderSize(float mBorderSize) {
        this.mBorderSize = mBorderSize;
    }

    /**
     * Get the value of global mBorderColor.
     * @return mBorderColor
     */
    public int getmBorderColor() {
        return mBorderColor;
    }

    /**
     * Set the value of global mBorderColor.
     * @param mBorderColor
     */
    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    /**
     * Get cropped circle bitmap of selected part of imagge
     * @return
     */
    public Bitmap getCroppedCircleBitmap() {

        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();

        final Paint paint = new Paint();
        final RectF croppedRect = getCroppedRect();

        croppedRect.top -= mBitmapRect.top;
        croppedRect.left -= mBitmapRect.left;
        croppedRect.bottom -= mBitmapRect.top;
        croppedRect.right -= mBitmapRect.left;

        float scale = getScale();
        setRectScale(croppedRect, scale);

        final Rect rectSrc = new Rect();
        rectSrc.set((int) croppedRect.left, (int) croppedRect.top, (int) croppedRect.right, (int) croppedRect.bottom);

        int dstImageSize = rectSrc.width();
        final Rect rectDst = new Rect(0, 0, dstImageSize, dstImageSize);
        float dstRect = (float) dstImageSize / 2f;
        Bitmap mOutputBitmap = Bitmap.createBitmap(dstImageSize, dstImageSize, Bitmap.Config.ARGB_4444);

        Canvas mCanvas = new Canvas(mOutputBitmap);
        mCanvas.drawARGB(0, 0, 0, 0);
        mCanvas.drawCircle(dstRect, dstRect, dstRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mCanvas.drawBitmap(bitmap, rectSrc, rectDst, paint);

        return mOutputBitmap;
    }

    /**
     * Get displayed image scale (image displayed on screen has different size than bitmap assigned to ImageView)
     * @return
     */
    private float getScale() {
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        return bitmap.getWidth() / mBitmapRect.width();
    }

    private void setRectScale(RectF rect, float scale) {
        rect.top = rect.top * scale;
        rect.left = rect.left * scale;
        rect.bottom = rect.bottom * scale;
        rect.right = rect.right * scale;
    }

    /**
     * Get rect for cropped part of image
     * @return
     */
    private RectF getCroppedRect() {
        float x1 = mCenterPointX - mRadius;
        float y1 = mCenterPointY - mRadius;
        float x2 = mCenterPointX + mRadius;
        float y2 = mCenterPointY + mRadius;
        return new RectF(x1, y1, x2, y2);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    /**
     * Initialization obtain the screen width and height.
     */
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragScaleCircleView);
        mHasGuideLine = typedArray.getBoolean(R.styleable.DragScaleCircleView_hasGuideLine, true);
        mGuideLineSize = typedArray.getFloat(R.styleable.DragScaleCircleView_guideLineSize, getResources().getDimension(R.dimen.guideline_width));
        mGuideLineColor = typedArray.getInt(R.styleable.DragScaleCircleView_guideLineColor, getResources().getColor(R.color.guideline));
        mBorderSize = typedArray.getFloat(R.styleable.DragScaleCircleView_borderSize, getResources().getDimension(R.dimen.border_width));
        mBorderColor = typedArray.getInt(R.styleable.DragScaleCircleView_borderColor, getResources().getColor(R.color.border));
        typedArray.recycle();

        final Resources resources = context.getResources();
        mScreenWidth = resources.getDisplayMetrics().widthPixels;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            mScreenHeight = getResources().getDisplayMetrics().heightPixels - 40 - 128;
        } else {
            mScreenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        }
        mBoarderPaint = PaintUtil.newBoarderPaint(resources, mBorderSize, mBorderColor);
        mSurroundingAreaOverlayPaint = PaintUtil.newSurroundingAreaOverlayPaint(resources);
        mHandlePaint = PaintUtil.newHandlerPaint(resources);
        mHandleRadius = resources.getDimension(R.dimen.corner_width);
        mGuideLinePaint = PaintUtil.newGuideLinePaint(resources, mGuideLineSize, mGuideLineColor);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

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
        drawDarkenSurroundingArea(canvas);
        drawCircleBorder(canvas);
        switch (mHandleMode) {
            case HANDLE_DOWN:
                if (mDragDirection == SIDE) {
                    drawHandles(canvas);
                    if(mHasGuideLine) {
                        drawGuideLine(canvas);
                    }
                } else if (mDragDirection == CENTER) {
                    if(mHasGuideLine){
                        drawGuideLine(canvas);
                    }
                }
                break;
            case HANDLE_MOVE:
                if (mDragDirection == SIDE) {
                    drawHandles(canvas);
                    if(mHasGuideLine) {
                        drawGuideLine(canvas);
                    }
                } else if (mDragDirection == CENTER) {
                    if(mHasGuideLine) {
                        drawGuideLine(canvas);
                    }
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

    /**
     * Draw a corner on the crop circle window.
     * @param canvas canvas
     */
    private void drawHandles(Canvas canvas) {
        canvas.drawCircle(mCenterPointX - mRadius, mCenterPointY, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX, mCenterPointY + mRadius, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX, mCenterPointY - mRadius, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX + mRadius, mCenterPointY, mHandleRadius, mHandlePaint);
    }

    /**
     * Draw crop circle window.
     * @param canvas
     */
    private void drawCircleBorder(@NonNull Canvas canvas) {
        canvas.drawCircle(mCenterPointX, mCenterPointY, mRadius, mBoarderPaint);
    }

    /**
     * Draw the darken surrounding area on canvas.
     * @param canvas
     */
    private void drawDarkenSurroundingArea(@NonNull Canvas canvas) {
        mBitmap = Bitmap.createBitmap(canvas.getWidth(), (canvas.getHeight()), Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.TRANSPARENT);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(getResources().getColor(R.color.surrounding_area));
        mCanvas.drawCircle(mCenterPointX, mCenterPointY, mRadius, mSurroundingAreaOverlayPaint);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * Draw a guideline on canvas
     * @param canvas canvas
     */
    private void drawGuideLine(Canvas canvas) {
        float offset = (float) Math.sqrt(Math.pow(mRadius, 2) / 2);
        // top left
        float topLeftPointX = mCenterPointX - offset;
        float topLeftpointY = mCenterPointY - offset;

        // top right
        float topRightPointX = mCenterPointX + offset;
        float topRightPpointY = topLeftpointY;

        // bottom left
        float bottomLeftPointX = topLeftPointX;
        float bottomLeftpointY = mCenterPointY + offset;

        // bottom right
        float bottomRightPointX = topRightPointX;
        float bottomRightPointY = bottomLeftpointY;

        canvas.drawLine(topLeftPointX, topLeftpointY, topRightPointX, topRightPpointY, mGuideLinePaint);
        canvas.drawLine(mCenterPointX - mRadius, mCenterPointY, mCenterPointX + mRadius, mCenterPointY, mGuideLinePaint);
        canvas.drawLine(bottomLeftPointX, bottomLeftpointY, bottomRightPointX, bottomRightPointY, mGuideLinePaint);

        canvas.drawLine(topLeftPointX, topLeftpointY, bottomLeftPointX, bottomLeftpointY, mGuideLinePaint);
        canvas.drawLine(mCenterPointX, mCenterPointY - mRadius, mCenterPointX, mCenterPointY + mRadius, mGuideLinePaint);
        canvas.drawLine(topRightPointX, topRightPpointY, bottomRightPointX, bottomRightPointY, mGuideLinePaint);

    }

    /**
     * Get the drag direction side or center
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
     * Drag the circle view.
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
        mDrawableHeight = bitmapRect.height() / mScaleY;
        mCenterPointX = mDrawableWidth / 2.0f;
        mCenterPointY = mDrawableHeight / 2.0f;
        mRadius = (Math.min(bitmapRect.width(), bitmapRect.height()) - mOffset) / 2.0f;
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
        mScaleX = matrixValues[Matrix.MSCALE_X];
        mScaleY = matrixValues[Matrix.MSCALE_Y];
        mTransX = matrixValues[Matrix.MTRANS_X];
        mTransY = matrixValues[Matrix.MTRANS_Y];

        // Get the width and height of the original bitmap.
        final int drawableIntrinsicWidth = drawable.getIntrinsicWidth();
        final int drawableIntrinsicHeight = drawable.getIntrinsicHeight();

        // Calculate the dimensions as seen on screen.
        final int drawableDisplayWidth = Math.round(drawableIntrinsicWidth * mScaleX);
        final int drawableDisplayHeight = Math.round(drawableIntrinsicHeight * mScaleY);

        // Get the Rect of the displayed image within the ImageView.
        final float left =Math.max(mTransX, 0);
        final float top = Math.max(mTransY, 0);
        final float right = left + drawableDisplayWidth;
        final float bottom = top + drawableDisplayHeight ;

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
        if (Math.min(mDrawableWidth, mScreenWidth) - (mCenterPointX + dx + mRadius) < 0) {
            return;
        }
        if (mCenterPointY + dy - mRadius < 0) {
            return;
        }
        if (Math.min(mDrawableHeight, mScreenHeight) - (mCenterPointY + dy + mRadius) < 0) {
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
                mRadius = (float) rawDistance;
            } else if (mRadius == maxRadius) {
                // only scale in can be done
                if (lastX > mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top right
                    if (rawX < lastX || rawY > lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
                if (lastX > mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom right
                    if (rawX < lastX || rawY < lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top left
                    if (rawX > lastX || rawY > lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom left
                    if (rawX > lastX || rawY < lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
            } else if (mRadius == 30) {
                // only scale out can be done
                if (lastX > mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top right
                    if (rawX > lastX || rawY < lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
                if (lastX > mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom right
                    if (rawX > lastX || rawY > lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY < mCenterPointY) {
                    // touch point at top left
                    if (rawX < lastX || rawY < lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
                if (lastX < mCenterPointX && lastY > mCenterPointY) {
                    // touch point at bottom left
                    if (rawX < lastX || rawY > lastY) {
                        mRadius = (float) rawDistance;
                    }
                }
            }
        }
    }

}
