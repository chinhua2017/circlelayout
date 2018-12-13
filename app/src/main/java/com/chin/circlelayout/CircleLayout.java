package com.chin.circlelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author nameless
 */
public class CircleLayout extends ViewGroup {
    private static final float DEFAULT_OFFSET = 270;
    private static final float DEFAULT_RANGE = 360;
    private static final int DEFAULT_RADIUS = 0;

    private float mAngleOffset;
    private float mAngleRange;
    private int mInnerRadius;

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleLayout, 0, 0);
        try {
            mAngleOffset = a.getFloat(R.styleable.CircleLayout_angleOffset, DEFAULT_OFFSET);
            mAngleRange = a.getFloat(R.styleable.CircleLayout_angleRange, DEFAULT_RANGE);
            mInnerRadius = a.getDimensionPixelSize(R.styleable.CircleLayout_innerRadius, DEFAULT_RADIUS);
        } finally {
            a.recycle();
        }
    }

    public void setAngleOffset(float offset) {
        mAngleOffset = offset;
    }

    public void setAngleRange(float range) {
        mAngleRange = range;
    }

    public void setInnerRadius(int radius) {
        mInnerRadius = radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        if(count <= 0) {
            return;
        }

        int maxHeight = 0;
        int maxWidth = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        int width = resolveSize(maxWidth, widthMeasureSpec);
        int height = resolveSize(maxHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private LayoutParams layoutParams(View child) {
        return (LayoutParams) child.getLayoutParams();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if(count <= 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        float startAngle = mAngleOffset;

        float totalWeight = 0f;
        float lastWeight = 0f;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams lp = layoutParams(child);
            totalWeight += lp.weight;
            lastWeight = lp.weight;
        }
        if (mAngleRange < DEFAULT_RANGE) {
            totalWeight -= lastWeight;
            if (totalWeight == 0) {
                totalWeight = 1;
            }
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams lp = layoutParams(child);
            float angle = mAngleRange / totalWeight * lp.weight;
            int x = (int) (mInnerRadius * Math.cos(Math.toRadians(startAngle))) + width / 2;
            int y = (int) (mInnerRadius * Math.sin(Math.toRadians(startAngle))) + height / 2;
            int halfChildWidth = child.getMeasuredWidth() / 2;
            int halfChildHeight = child.getMeasuredHeight() / 2;
            int left = lp.width != LayoutParams.MATCH_PARENT ? x - halfChildWidth : 0;
            int top = lp.height != LayoutParams.MATCH_PARENT ? y - halfChildHeight : 0;
            int right = lp.width != LayoutParams.MATCH_PARENT ? x + halfChildWidth : width;
            int bottom = lp.height != LayoutParams.MATCH_PARENT ? y + halfChildHeight : height;
            child.layout(left, top, right, bottom);
            startAngle += angle;
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        LayoutParams lp = new LayoutParams(p.width, p.height);
        if (p instanceof LinearLayout.LayoutParams) {
            lp.weight = ((LinearLayout.LayoutParams) p).weight;
        }
        return lp;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    static class LayoutParams extends ViewGroup.LayoutParams {
        public float weight = 1f;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }
}
