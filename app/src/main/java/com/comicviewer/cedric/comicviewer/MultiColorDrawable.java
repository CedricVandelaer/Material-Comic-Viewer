package com.comicviewer.cedric.comicviewer;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by CV on 3/07/2015.
 */
public class MultiColorDrawable extends Drawable {

    public enum Orientation {VERTICAL, HORIZONTAL}

    private int[] themeColors;
    private Orientation mOrientation;

    public MultiColorDrawable(int[] themeColors, Orientation orientation) {
        this.themeColors = themeColors;
        mOrientation = orientation;
    }

    @Override
    public void draw(Canvas canvas) {

        // get drawable dimensions
        Rect bounds = getBounds();

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        // draw background gradient
        Paint backgroundPaint = new Paint();
        int barWidth = width / themeColors.length;
        int barWidthRemainder = width % themeColors.length;

        int barHeight = height / themeColors.length;
        int barHeightRemainder = height % themeColors.length;

        for (int i = 0; i < themeColors.length; i++) {
            backgroundPaint.setColor(themeColors[i]);

            if (mOrientation == Orientation.HORIZONTAL)
                canvas.drawRect(i * barWidth, 0, (i + 1) * barWidth, height, backgroundPaint);
            else
                canvas.drawRect(0, i* barHeight, width, (i + 1) * barHeight, backgroundPaint);
        }

        // draw remainder, if exists
        if (barWidthRemainder > 0) {
            if (mOrientation == Orientation.HORIZONTAL)
                canvas.drawRect(themeColors.length * barWidth, 0, themeColors.length * barWidth + barWidthRemainder, height, backgroundPaint);
            else
                canvas.drawRect(0, themeColors.length * barHeight, width, themeColors.length * barWidth + barHeightRemainder , backgroundPaint);
        }

    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

}