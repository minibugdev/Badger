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

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.test.filters.SdkSuppress;
import android.view.Gravity;
import android.view.View;

import org.junit.Before;
import org.junit.Test;

public class BadgeShapeTest {

    final Rect bounds = new Rect(0, 0, 64, 64);

    final Paint paint = new Paint();
    final Paint borderPaint = new Paint();
    final Rect region = new Rect();
    final Rect borderRegion = new Rect();
    final int borderSize = 10;

    TestCanvas canvas;

    @Before
    public void setup() {
        canvas = new TestCanvas();
    }

    @Test
    public void draw() {
        region.set(0, 0, 48, 48);

        TestBadgeShape badgeShape = new TestBadgeShape(1, 1, 0);
        Rect rect = badgeShape.draw(canvas, region, paint, borderPaint, borderSize, 0);

        badgeShape.assertBadgeRegion(rect);
        badgeShape.assertBadgeRegion(region);
    }

    @Test
    public void draw_scale() {
        region.set(16, 16, 48, 48);

        TestBadgeShape badgeShape = new TestBadgeShape(0.5f, 1, Gravity.NO_GRAVITY);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, 0);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    public void draw_scale_center() {
        region.set(16, 16, 48, 48);

        TestBadgeShape badgeShape = new TestBadgeShape(0.5f, 1, Gravity.CENTER);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, 0);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    public void draw_scale_ratio_right_top() {
        region.set(32, 0, 64, 16);

        TestBadgeShape badgeShape = new TestBadgeShape(0.5f, 2, Gravity.RIGHT | Gravity.TOP);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, 0);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void draw_scale_ratio_end_center_ltr() {
        region.set(32, 24, 64, 40);

        TestBadgeShape badgeShape = new TestBadgeShape(0.5f, 2, Gravity.END | Gravity.CENTER);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, View.LAYOUT_DIRECTION_LTR);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void draw_scale_ratio_end_center_rtl() {
        region.set(0, 24, 32, 40);

        TestBadgeShape badgeShape = new TestBadgeShape(0.5f, 2, Gravity.END | Gravity.CENTER);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, View.LAYOUT_DIRECTION_RTL);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    public void draw_ratio_left() {
        region.set(0, 0, 32, 64);

        TestBadgeShape badgeShape = new TestBadgeShape(1, 0.5f, Gravity.LEFT);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, 0);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    public void draw_ratio() {
        region.set(16, 0, 48, 64);

        TestBadgeShape badgeShape = new TestBadgeShape(1, 0.5f, Gravity.NO_GRAVITY);
        badgeShape.draw(canvas, bounds, paint, borderPaint, borderSize, 0);

        badgeShape.assertBadgeRegion(region);
    }

    @Test
    public void circle() throws Exception {
        region.set(16, 16, 48, 48);

        BadgeShape.circle(0.5f, Gravity.CENTER).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.OVAL, region);
    }

    @Test
    public void oval() throws Exception {
        region.set(16, 24, 48, 40);

        BadgeShape.oval(0.5f, 2, Gravity.CENTER).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.OVAL, region);
    }

    @Test
    public void rect() throws Exception {
        region.set(16, 24, 48, 40);

        BadgeShape.rect(0.5f, 2, Gravity.CENTER).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.RECT, region);
    }

    @Test
    public void rect_without_radius() throws Exception {
        region.set(16, 24, 48, 40);

        BadgeShape.rect(0.5f, 2, Gravity.CENTER, 0).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.RECT, region);
    }

    @Test
    public void rect_with_radius() throws Exception {
        region.set(16, 24, 48, 40);
        float radius = 0.25f;
        Object[] params = {0.5f * region.height() * radius, 0.5f * region.height() * radius};

        BadgeShape.rect(0.5f, 2, Gravity.CENTER, radius).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.ROUND_RECT, region, params);
    }

    @Test
    public void square() throws Exception {
        region.set(16, 16, 48, 48);

        BadgeShape.square(0.5f, Gravity.CENTER).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.RECT, region);
    }

    @Test
    public void square_with_radius() throws Exception {
        region.set(16, 16, 48, 48);
        float radius = 0.25f;
        Object[] params = {0.5f * region.width() * radius, 0.5f * region.height() * radius};

        BadgeShape.square(0.5f, Gravity.CENTER, radius).onDraw(canvas, region, borderRegion, paint, borderPaint);

        canvas.assertDrawn(TestCanvas.Shape.ROUND_RECT, region, params);
    }
}