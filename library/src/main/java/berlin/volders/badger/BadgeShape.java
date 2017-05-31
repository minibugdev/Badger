/*
 * Copyright (C) 2016 Christian Schmitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package berlin.volders.badger;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.Dimension;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;

/**
 * The {@code BadgeShape} is a simple {@link Shape} like structure to bypass limitation drawing
 * shapes with hardware acceleration and customize its behavior for {@code Badger}.
 */
public abstract class BadgeShape {

    private final Rect badgeRect  = new Rect();
    private final Rect borderRect = new Rect();

    private final float scale;
    private final float aspectRatio;
    private final int   gravity;

    /**
     * @param scale       of the badge shape relative to the size of the canvas
     * @param aspectRatio width to height of the badge shape
     * @param gravity     to place the badge shape on the canvas
     */
    protected BadgeShape(@FloatRange(from = 0, to = 1) float scale, float aspectRatio, int gravity) {
        this.scale = scale;
        this.aspectRatio = aspectRatio;
        this.gravity = gravity;
    }

    /**
     * @param canvas          to draw on
     * @param bounds          of the canvas
     * @param badgePaint      to draw with
     * @param borderPaint     to draw with
     * @param layoutDirection for gravity mapping
     * @return the region the badge is drawn in
     */
    public Rect draw(@NonNull Canvas canvas, @NonNull Rect bounds, @NonNull Paint badgePaint, @NonNull Paint borderPaint, @Dimension int borderSize, int layoutDirection) {
        float width = bounds.width() * scale;
        float height = bounds.height() * scale;
        if (width < height * aspectRatio) {
            height = width / aspectRatio;
        } else {
            width = height * aspectRatio;
        }

        applyGravity((int) width, (int) height, bounds, layoutDirection);

        if(borderSize > 0){
            applyBorderSize(borderSize);
            onDraw(canvas, badgeRect, borderRect, badgePaint, borderPaint);
        } else {
            onDrawWithoutBorder(canvas, badgeRect, badgePaint);
        }

        return badgeRect;
    }

    private void applyGravity(int width, int height, Rect bounds, int layoutDirection) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Gravity.apply(gravity, width, height, bounds, badgeRect);
        } else {
            Gravity.apply(gravity, width, height, bounds, badgeRect, layoutDirection);
        }

    }

    private void applyBorderSize(int borderSize) {
        borderRect.set(badgeRect.left - borderSize, badgeRect.top - borderSize, badgeRect.right + borderSize, badgeRect.bottom + borderSize);
    }

    /**
     * @param canvas       to draw on
     * @param badgeRegion  to draw badge in
     * @param badgePaint   to draw with
     */
    private void onDrawWithoutBorder(@NonNull Canvas canvas, @NonNull Rect badgeRegion, @NonNull Paint badgePaint) {
        onDraw(canvas, badgeRegion, null, badgePaint, null);
    }

    /**
     * @param canvas       to draw on
     * @param badgeRegion  to draw badge in
     * @param borderRegion to draw border in
     * @param badgePaint   to draw badge with
     * @param borderPaint  to draw border with
     */
    protected abstract void onDraw(@NonNull Canvas canvas, @NonNull Rect badgeRegion, @Nullable Rect borderRegion, @NonNull Paint badgePaint, @Nullable Paint borderPaint);

    /**
     * @param scale   of the badge shape relative to the size of the canvas
     * @param gravity to place the badge shape on the canvas
     * @return a {@code BadgeShape} drawing a circle
     * @see #oval(float, float, int)
     */
    public static BadgeShape circle(@FloatRange(from = 0, to = 1) float scale, int gravity) {
        return oval(scale, 1, gravity);
    }

    /**
     * @param scale       of the badge shape relative to the size of the canvas
     * @param aspectRatio width to height of the badge shape
     * @param gravity     to place the badge shape on the canvas
     * @return a {@code BadgeShape} drawing a circle
     */
    public static BadgeShape oval(@FloatRange(from = 0, to = 1) float scale, float aspectRatio, int gravity) {
        return new BadgeShape(scale, aspectRatio, gravity) {
            private final RectF borderRegion = new RectF();
            private final RectF badgeRegion = new RectF();

            @Override
            protected void onDraw(@NonNull Canvas canvas, @NonNull Rect badgeRegion, @Nullable Rect borderRegion, @NonNull Paint badgePaint, @Nullable Paint borderPaint) {
                if (borderRegion != null && borderPaint != null) {
                    this.borderRegion.set(borderRegion);
                    canvas.drawOval(this.borderRegion, borderPaint);
                }

                this.badgeRegion.set(badgeRegion);
                canvas.drawOval(this.badgeRegion, badgePaint);
            }
        };
    }

    /**
     * @param scale       of the badge shape relative to the size of the canvas
     * @param aspectRatio width to height of the badge shape
     * @param gravity     to place the badge shape on the canvas
     * @return a {@code BadgeShape} drawing a rect
     */
    public static BadgeShape rect(@FloatRange(from = 0, to = 1) float scale, float aspectRatio, int gravity) {
        return new BadgeShape(scale, aspectRatio, gravity) {

            @Override
            protected void onDraw(@NonNull Canvas canvas, @NonNull Rect badgeRegion, @Nullable Rect borderRegion, @NonNull Paint badgePaint, @Nullable Paint borderPaint) {
                if (borderRegion != null && borderPaint != null) {
                    canvas.drawRect(borderRegion, borderPaint);
                }

                canvas.drawRect(badgeRegion, badgePaint);
            }
        };
    }

    /**
     * @param scale        of the badge shape relative to the size of the canvas
     * @param aspectRatio  width to height of the badge shape
     * @param gravity      to place the badge shape on the canvas
     * @param radiusFactor of the oval used to round the corners
     * @return a {@code BadgeShape} drawing a round-rect
     */
    public static BadgeShape rect(@FloatRange(from = 0, to = 1) float scale, float aspectRatio, int gravity,
                                  @FloatRange(from = 0, to = 1) final float radiusFactor) {
        if (radiusFactor == 0) {
            return rect(scale, aspectRatio, gravity);
        }
        return new BadgeShape(scale, aspectRatio, gravity) {
            private final RectF borderRegion = new RectF();
            private final RectF badgeRegion = new RectF();

            @Override
            protected void onDraw(@NonNull Canvas canvas, @NonNull Rect badgeRegion, @Nullable Rect borderRegion, @NonNull Paint badgePaint, @Nullable Paint borderPaint) {
                if (borderRegion != null && borderPaint != null) {
                    this.borderRegion.set(borderRegion);
                    float borderR = 0.5f * Math.min(borderRegion.width(), borderRegion.height()) * radiusFactor;
                    canvas.drawRoundRect(this.borderRegion, borderR, borderR, borderPaint);
                }

                this.badgeRegion.set(badgeRegion);
                float badgeR = 0.5f * Math.min(badgeRegion.width(), badgeRegion.height()) * radiusFactor;
                canvas.drawRoundRect(this.badgeRegion, badgeR, badgeR, badgePaint);
            }
        };
    }

    /**
     * @param scale   of the badge shape relative to the size of the canvas
     * @param gravity to place the badge shape on the canvas
     * @return a {@code BadgeShape} drawing a square
     * @see #rect(float, float, int)
     */
    public static BadgeShape square(@FloatRange(from = 0, to = 1) float scale, int gravity) {
        return rect(scale, 1, gravity);
    }

    /**
     * @param scale        of the badge shape relative to the size of the canvas
     * @param gravity      to place the badge shape on the canvas
     * @param radiusFactor of the oval used to round the corners
     * @return a {@code BadgeShape} drawing a round-square
     * @see #rect(float, float, int, float)
     */
    public static BadgeShape square(@FloatRange(from = 0, to = 1) float scale, int gravity,
                                    @FloatRange(from = 0, to = 1) float radiusFactor) {
        return rect(scale, 1, gravity, radiusFactor);
    }
}
