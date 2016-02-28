/**
 * Copyright 2015~2016, hpfs0.
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
import android.widget.ImageView;

import com.rori.zenvo.util.PaintUtil;

/*
 * Custom View that provides dragged and scaled.
 *
 * @author hpfs0
 */
public class DragScaleCircleView extends ImageView {

    @SuppressWarnings("unused")
    private static final String TAG = DragScaleCircleView.class.getName();

    // The min radius of circle window can be scale.
    private static final int MIN_CIRCLE_WINDOW_RADIUS = 30;

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

    // The direction that drag side ward the circle view.
    private static final int SIDE = 0x10;

    // The direction that drag center the circle view.
    private static final int CENTER = 0x20;

    /* The mode of touch. */
    private static final int HANDLE_DOWN = (1 << 0);
    private static final int HANDLE_MOVE = (1 << 1);
    private static final int HANDLE_UP = (1 << 2);

    // The bounding box around the bitmap that it can be cropped.
    @NonNull
    private RectF mBitmapRect = new RectF();

    // The paint that can be used to draw.
    private Paint mBoarderPaint;

    // The paint used to darken the surrounding areas outside the crop area.
    private Paint mSurroundingAreaOverlayPaint;

    // The paint that show outline circle on touch.
    private Paint mHandlePaint;

    // The paint that draw guide line.
    private Paint mGuideLinePaint;

    // The outline circle radius.
    private float mHandleRadius;

    // The Handle mode.
    private int mHandleMode;

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

    // the mask tools to draw the semitransparent mask
    private Bitmap maskBitmap;
    private Canvas maskCanvas;

    // -------------------------------------------------------------
    //                       constructor
    // -------------------------------------------------------------
    public DragScaleCircleView(Context context) {
        super(context);
        init(context, null);
    }

    public DragScaleCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DragScaleCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    // -------------------------------------------------------------
    //                       constructor
    // -------------------------------------------------------------

    /**
     * Get the value of global mHasGuideLine.
     *
     * @return mHasGuideLine
     */
    public Boolean getHasGuideLine() {
        return mHasGuideLine;
    }

    /**
     * Set the value of global mHasGuideLine.
     *
     * @param hasGuideLine
     */
    public void setHasGuideLine(Boolean hasGuideLine) {
        this.mHasGuideLine = hasGuideLine;
    }

    /**
     * Get the value of global mGuideLineSize.
     *
     * @return mGuideLineSize
     */
    public float getGuideLineSize() {
        return mGuideLineSize;
    }

    /**
     * Set the value of global mGuideLineSize.
     *
     * @param guideLineSize
     */
    public void setGuideLineSize(float guideLineSize) {
        this.mGuideLineSize = guideLineSize;
    }

    /**
     * Get the value of global mGuideLineColor.
     *
     * @return
     */
    public int getGuideLineColor() {
        return mGuideLineColor;
    }

    /**
     * Set the value of global mGuideLineColor.
     *
     * @param guideLineColor
     */
    public void setGuideLineColor(int guideLineColor) {
        this.mGuideLineColor = guideLineColor;
    }

    /**
     * Get the value of global mBorderSize.
     *
     * @return mBorderSize
     */
    public float getBorderSize() {
        return mBorderSize;
    }

    /**
     * Set the value of global mBorderSize.
     *
     * @param borderSize
     */
    public void setBorderSize(float borderSize) {
        this.mBorderSize = borderSize;
    }

    /**
     * Get the value of global mBorderColor.
     *
     * @return mBorderColor
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * Set the value of global mBorderColor.
     *
     * @param borderColor
     */
    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
    }

    /**
     * Set the color of guide line paint
     *
     * @param color color
     */
    public void setGuideLinePaintColor(int color) {
        this.mGuideLinePaint.setColor(color);
    }

    /**
     * Set the width of guide line
     *
     * @param width width
     */
    public void setGuideLineStrokeWidth(int width) {
        this.mGuideLinePaint.setStrokeWidth(width);
    }

    /**
     * Set the color of border paint
     *
     * @param color color
     */
    public void setBorderPaintColor(int color) {
        this.mBoarderPaint.setColor(color);
        invalidate();
    }

    /**
     * Get cropped circle bitmap of selected part of image
     *
     * @return cropped image
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

        float dstImageSize = rectSrc.width();
        final RectF rectDst = new RectF(0, 0, dstImageSize, dstImageSize);
        float dstRect = dstImageSize / 2.0f;
        Bitmap outputBitmap = Bitmap.createBitmap((int) dstImageSize, (int) dstImageSize, Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(outputBitmap);
        mCanvas.drawCircle(dstRect, dstRect, dstRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mCanvas.drawBitmap(bitmap, rectSrc, rectDst, paint);

        return outputBitmap;
    }

    /**
     * Get displayed image scale (image displayed on screen has different size than bitmap assigned to ImageView)
     *
     * @return displayed image scale
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
     *
     * @return rect for cropped part of image
     */
    private RectF getCroppedRect() {
        float left = mCenterPointX - mRadius;
        float top = mCenterPointY - mRadius;
        float right = mCenterPointX + mRadius;
        float bottom = mCenterPointY + mRadius;
        return new RectF(left, top, right, bottom);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    /**
     * Initialization obtain the screen width and height.
     */
    protected void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        // custom attr
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragScaleCircleView);
        try {
            mHasGuideLine = typedArray.getBoolean(R.styleable.DragScaleCircleView_hasGuideLine, true);
            mGuideLineSize = typedArray.getFloat(R.styleable.DragScaleCircleView_guideLineSize, getResources().getDimension(R.dimen.guideline_width));
            mGuideLineColor = typedArray.getInt(R.styleable.DragScaleCircleView_guideLineColor, getResources().getColor(R.color.guideline));
            mBorderSize = typedArray.getFloat(R.styleable.DragScaleCircleView_borderSize, getResources().getDimension(R.dimen.border_width));
            mBorderColor = typedArray.getInt(R.styleable.DragScaleCircleView_borderColor, getResources().getColor(R.color.border));
        } finally {
            typedArray.recycle();
        }

        final Resources resources = context.getResources();
        mScreenWidth = resources.getDisplayMetrics().widthPixels;
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (hasBackKey && hasHomeKey) {
            mScreenHeight = getResources().getDisplayMetrics().heightPixels - 40 - 128;
        } else {
            mScreenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        }
        mBoarderPaint = PaintUtil.newBoarderPaint(mBorderSize, mBorderColor);
        mSurroundingAreaOverlayPaint = PaintUtil.newSurroundingAreaOverlayPaint();
        mHandlePaint = PaintUtil.newHandlerPaint(resources);
        mHandleRadius = resources.getDimension(R.dimen.corner_width);
        mGuideLinePaint = PaintUtil.newGuideLinePaint(mGuideLineSize, mGuideLineColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // fix call CircleCropWindow() after crop button click
        if (changed) {
            mBitmapRect = getBitmapRect();
            initCircleCropWindow(mBitmapRect);
        }
    }

    /**
     * drawing the view.
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDarkenSurroundingArea(canvas);
        drawCircleBorder(canvas);
        if (mHandleMode == HANDLE_DOWN || mHandleMode == HANDLE_MOVE) {
            if (mDragDirection == SIDE) {
                drawHandles(canvas);
            }
            if (mHasGuideLine && (mDragDirection == SIDE || mDragDirection == CENTER)) {
                drawGuideLine(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If this view is not enabled, touch event must return false.
        if (!isEnabled()) {
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            setHandleMode(HANDLE_DOWN);
            mLastY = (int) event.getRawY();
            mLastX = (int) event.getRawX();
            mDragDirection = getDragDirection((int) event.getX(), (int) event.getY());
            invalidate();
        } else if (action == MotionEvent.ACTION_MOVE) {
            setHandleMode(HANDLE_MOVE);
            drag(event, action);
            invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            setHandleMode(HANDLE_UP);
            drag(event, action);
            invalidate();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return true;
    }

    private void setHandleMode(int mode) {
        this.mHandleMode = mode;
    }

    /**
     * Draw a corner on the crop circle window.
     *
     * @param canvas canvas
     */
    private void drawHandles(@NonNull Canvas canvas) {
        canvas.drawCircle(mCenterPointX - mRadius, mCenterPointY, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX, mCenterPointY + mRadius, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX, mCenterPointY - mRadius, mHandleRadius, mHandlePaint);
        canvas.drawCircle(mCenterPointX + mRadius, mCenterPointY, mHandleRadius, mHandlePaint);
    }

    /**
     * Draw crop circle window.
     *
     * @param canvas canvas
     */
    private void drawCircleBorder(@NonNull Canvas canvas) {
        canvas.drawCircle(mCenterPointX, mCenterPointY, mRadius, mBoarderPaint);
    }

    /**
     * Draw the darken surrounding area on canvas.
     *
     * @param canvas canvas
     */
    private void drawDarkenSurroundingArea(@NonNull Canvas canvas) {
        maskCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        maskCanvas.drawColor(getResources().getColor(R.color.surrounding_area));
        maskCanvas.drawCircle(mCenterPointX, mCenterPointY, mRadius, mSurroundingAreaOverlayPaint);
        canvas.drawBitmap(maskBitmap, 0, 0, null); 
    }

    /**
     * Draw a guideline on canvas
     *
     * @param canvas canvas
     */
    private void drawGuideLine(@NonNull Canvas canvas) {
        float offset = (float) (mRadius / Math.sqrt(2));

        float topLeftPointX = mCenterPointX - offset;
        float topLeftPointY = mCenterPointY - offset;
        float topRightPointX = mCenterPointX + offset;
        float bottomLeftPointY = mCenterPointY + offset;

        canvas.drawLine(topLeftPointX, topLeftPointY, topRightPointX, topLeftPointY, mGuideLinePaint);
        canvas.drawLine(mCenterPointX - mRadius, mCenterPointY, mCenterPointX + mRadius, mCenterPointY, mGuideLinePaint);
        canvas.drawLine(topLeftPointX, bottomLeftPointY, topRightPointX, bottomLeftPointY, mGuideLinePaint);

        canvas.drawLine(topLeftPointX, topLeftPointY, topLeftPointX, bottomLeftPointY, mGuideLinePaint);
        canvas.drawLine(mCenterPointX, mCenterPointY - mRadius, mCenterPointX, mCenterPointY + mRadius, mGuideLinePaint);
        canvas.drawLine(topRightPointX, topLeftPointY, topRightPointX, bottomLeftPointY, mGuideLinePaint);
    }

    /**
     * Get the drag direction side or center
     *
     * @param x the touch point X
     * @param y the touch point Y
     * @return side or center
     */
    protected int getDragDirection(int x, int y) {
        double d = Math.sqrt(Math.pow(x - mCenterPointX, 2) + Math.pow(y - mCenterPointY, 2));
        // touch point at the circle side
        if (d >= mRadius - mOffset / 2.0f && d <= mRadius + mOffset / 2.0f) {
            return SIDE;
        }
        // touch point at the circle center
        if (d < mRadius - mOffset / 2.0f) {
            return CENTER;
        }
        return 0;
    }

    /**
     * Drag the circle view.
     *
     * @param event  event
     * @param action action
     */
    protected void drag(MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - mLastX;
                float dy = event.getRawY() - mLastY;
                float touchX = event.getX();
                float touchY = event.getY();
                switch (mDragDirection) {
                    case CENTER:
                        center(dx, dy);
                        break;
                    case SIDE:
                        side(touchX, touchY, touchX + dx, touchY + dy);
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
     * Gets the bounding rectangle of the bitmap within the imageView.
     *
     * @return rect of the bitmap within the imageView
     */
    private RectF getBitmapRect() {

        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return new RectF();
        }

        final Rect drawableBounds = drawable.getBounds();
        final Bitmap src = ((BitmapDrawable) drawable).getBitmap();

        if (drawableBounds.right > mScreenWidth) {
            final float scale = mScreenWidth / drawableBounds.right;
            final Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            setImageBitmap(Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true));
        } else if (drawable.getBounds().bottom > mScreenHeight) {
            final float scale = mScreenHeight / drawableBounds.bottom;
            final Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            setImageBitmap(Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true));
        }

        // Calculate the dimensions as seen on screen.
        final int drawableDisplayWidth = src.getWidth();
        final int drawableDisplayHeight = src.getHeight();

        // Get the Rect of the displayed image within the ImageView.
        final float left = 0, top = 0;
        final float right = left + drawableDisplayWidth;
        final float bottom = top + drawableDisplayHeight;

        return new RectF(left, top, right, bottom);
    }

    /**
     * Move the circle view.
     *
     * @param dx move X
     * @param dy move Y
     */
    private void center(float dx, float dy) {
        if ((mCenterPointX + dx - mRadius < 0)
                || (mCenterPointY + dy - mRadius < 0)
                || (Math.min(mDrawableWidth, mScreenWidth) - (mCenterPointX + dx + mRadius) < 0)
                || (Math.min(mDrawableHeight, mScreenHeight) - (mCenterPointY + dy + mRadius) < 0)) {
            return;
        }
        mCenterPointX += dx;
        mCenterPointY += dy;
    }

    /**
     * Touch on side scale in or out process.
     *
     * @param lastX touch point X
     * @param lastY touch point Y
     * @param rawX  touch move point X
     * @param rawY  touch move point Y
     */
    private void side(float lastX, float lastY, float rawX, float rawY) {
        // action_up: the touch point to the distance of the center on action up
        float rawDistance = (float) Math.sqrt(Math.pow(rawX - mCenterPointX, 2) + Math.pow(rawY - mCenterPointY, 2));
        // get max radius of this context
        float maxRadius = Math.min(Math.min(mCenterPointX, mDrawableWidth - mCenterPointX), Math.min(mCenterPointY, mDrawableHeight - mCenterPointY));
        if (rawDistance <= maxRadius && rawDistance >= MIN_CIRCLE_WINDOW_RADIUS) {
            if (mRadius < maxRadius && mRadius > MIN_CIRCLE_WINDOW_RADIUS) {
                mRadius = rawDistance;
            } else if (mRadius == maxRadius) {
                // only scale in can be done.(touch point at top/bottom right || top/bottom left)
                if ((lastX > mCenterPointX && lastY != mCenterPointY && (rawX < lastX || rawY != lastY))
                        || lastX < mCenterPointX && lastY != mCenterPointY && (rawX > lastX || rawY != lastY)) {
                    mRadius = rawDistance;
                }
            } else if (mRadius == MIN_CIRCLE_WINDOW_RADIUS) {
                // only scale out can be done.(touch point at top/bottom right || top/bottom right)
                if ((lastX > mCenterPointX && lastY != mCenterPointY && (rawX > lastX || rawY != lastY))
                        || lastX < mCenterPointX && lastY != mCenterPointY && (rawX < lastX || rawY != lastY)) {
                    mRadius = rawDistance;
                }
            }
        }
    }
}