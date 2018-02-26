package com.zman2245.pinpin.view.pagecontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zman2245.pinpin.R;

public class PageControl extends LinearLayout
{
    private static final int INVALID_PAGE_INDEX = -1;
    private static final int DEFAULT_SPACING = 10;

    private int mPageCnt;
    private int mSelectedPageIndex = INVALID_PAGE_INDEX;

    private final int mSelectedPageImageResource;
    private final int mDeselectedPageImageResource;

    private final int mSpacing;

    public PageControl(Context context)
    {
        this(context, null);
    }

    public PageControl(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageControl);
        mSpacing = a.getDimensionPixelSize(R.styleable.PageControl_spacing, DEFAULT_SPACING);

        mSelectedPageImageResource = a.getResourceId(R.styleable.PageControl_selected_item_drawable_src, -1);

        if (mSelectedPageImageResource == -1)
        {
            throw new IllegalStateException("Selected item drawable was not defined in layout");
        }

        mDeselectedPageImageResource = a.getResourceId(R.styleable.PageControl_deselected_item_drawable_src, -1);

        if (mDeselectedPageImageResource == -1)
        {
            throw new IllegalStateException("Deselected item drawable was not defined in layout");
        }

        a.recycle();
    }

    public void setPageNumber(int pageCnt)
    {
        // in case of more complex usage scenarios we need to think about
        // re-using of image views
        removeAllViews();

        mPageCnt = pageCnt;
        mSelectedPageIndex = INVALID_PAGE_INDEX;

        final Context context = getContext();
        final LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < mPageCnt; ++i)
        {
            final ImageView newImageView = new ImageView(context);
            newImageView.setLayoutParams(layoutParams);
            newImageView.setImageResource(mDeselectedPageImageResource);
            newImageView.setPadding(mSpacing, 0, mSpacing, 0);
            addView(newImageView, i, layoutParams);
        }
    }

    public void setSelectedPageIndex(int index)
    {
        if (index == mSelectedPageIndex)
        {
            return;
        }
        if (index < 0 || index >= mPageCnt)
        {
            throw new IllegalArgumentException("Index value is out of range. value = " + index + ", epected in range [0.." + (mPageCnt - 1) + "]");
        }
        if (mSelectedPageIndex != INVALID_PAGE_INDEX)
        {
            final ImageView imageView = (ImageView) getChildAt(mSelectedPageIndex);
            imageView.setImageResource(mDeselectedPageImageResource);
        }
        final ImageView imageView = (ImageView) getChildAt(index);
        imageView.setImageResource(mSelectedPageImageResource);
        mSelectedPageIndex = index;
    }
}
