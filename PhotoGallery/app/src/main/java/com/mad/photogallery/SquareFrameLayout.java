// SquareFrameLayout.java
// Place in: app/src/main/java/com/mad/photogallery/SquareFrameLayout.java
// This makes each grid cell's height always equal its width — perfect squares.
package com.mad.photogallery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(Context context) {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Force height to always equal width → perfect square
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
