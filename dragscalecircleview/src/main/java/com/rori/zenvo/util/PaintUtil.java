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
package com.rori.zenvo.util;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;

import com.rori.zenvo.dragscalecircleview.R;

public class PaintUtil {

    // -------------------------------------------------------------
    //                        public method
    // -------------------------------------------------------------

    /**
     * create the paint object for drawing the crop window border.
     */
    public static Paint newBoarderPaint(float size, int color){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);

        return paint;
    }

    /**
     * creates the paint object for drawing the translucent overlay outside the crop window.
     *
     */
    public static Paint newSurroundingAreaOverlayPaint() {

        final Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setAntiAlias(true);

        return paint;
    }

    /**
     * create the paint object for drawing the crop window corner..
     */
    public static Paint newHandlerPaint(@NonNull Resources resources){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(resources.getColor(R.color.corner));
        paint.setStyle(Paint.Style.FILL);

        return paint;
    }

    /**
     * create the paint object for drawing the crop window corner..
     */
    public static Paint newGuideLinePaint(float size, int color){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size);

        return paint;
    }
}
