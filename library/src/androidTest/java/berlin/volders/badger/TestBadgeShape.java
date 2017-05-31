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
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class TestBadgeShape extends BadgeShape {

    private Canvas canvas;
    private Rect badgeRegion;
    private Rect borderRegion;
    private int badgeColor;
    private int borderColor;

    protected TestBadgeShape(@FloatRange(from = 0, to = 1) float scale, float ratio, int gravity) {
        super(scale, ratio, gravity);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas, @NonNull Rect badgeRegion, @Nullable Rect borderRegion, @NonNull Paint badgePaint, @NonNull Paint borderPaint) {
        this.canvas = canvas;
        this.badgeRegion = badgeRegion;
        this.badgeColor = badgePaint.getColor();
        this.borderRegion = borderRegion;
        this.borderColor = borderPaint.getColor();
    }

    void assertCanvas(Canvas canvas) {
        assertThat(this.canvas, equalTo(canvas));
    }

    void assertBadgeRegion(Rect region) {
        assertThat(this.badgeRegion, equalTo(region));
    }

    void assertBadgeColor(int badgeColor) {
        assertThat(this.badgeColor, is(badgeColor));
    }

    void assertBorderRegion(Rect region) {
        assertThat(this.borderRegion, equalTo(region));
    }

    void assertBorderColor(int badgeColor) {
        assertThat(this.borderColor, is(badgeColor));
    }
}
