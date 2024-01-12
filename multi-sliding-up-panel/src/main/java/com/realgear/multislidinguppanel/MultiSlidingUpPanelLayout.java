package com.realgear.multislidinguppanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MultiSlidingUpPanelLayout extends ViewGroup {
    public static final int DEFAULT_MIN_FLING_VELOCITY = 240;

    public static final int SLIDE_VERTICAL = 0;
    public static final int SLIDE_UP = 1;
    public static final int SLIDE_DOWN = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SLIDE_VERTICAL, SLIDE_UP, SLIDE_DOWN})
    public @interface SlideDirection { }

    public static final int HIDDEN = 0;
    public static final int COLLAPSED = 1;
    public static final int EXPANDED = 2;
    public static final int DRAGGING = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HIDDEN, COLLAPSED, EXPANDED, DRAGGING})
    public @interface PanelState { }

    private boolean isSlidingUp;
    private boolean isFirstLayout = true;
    private boolean isSlidingEnabled;

    private float mSlidedOffset;
    private float mExpandThreshold;
    private float mCollapseThreshold;
    private float mHideThreshold;

    private int mPanelPrevState = -1;

    private MultiSlidingPanelAdapter mAdapter;
    private IPanel<View> mSlidingPanel;
    private PanelStateListener mPanelStateListener;

    private final ViewDragHelper mDragHelper;

    public MultiSlidingUpPanelLayout(Context context) {
        this(context, null);
    }

    public MultiSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSlidingUpPanelLayout);
        isSlidingEnabled = !a.getBoolean(R.styleable.MultiSlidingUpPanelLayout_mspl_disableSliding, false);
        mExpandThreshold = a.getFloat(R.styleable.MultiSlidingUpPanelLayout_mspl_expandThreshold, 0F);
        mCollapseThreshold = a.getFloat(R.styleable.MultiSlidingUpPanelLayout_mspl_collapseThreshold, 0.95F);
        mHideThreshold = 0.75F;

        a.recycle();

        if (isInEditMode()) {
            mDragHelper = null;
            return;
        }

        mDragHelper = ViewDragHelper.create(this, 1.0F, new DragHelperCallback());
        mDragHelper.setMinVelocity(DEFAULT_MIN_FLING_VELOCITY * getResources().getDisplayMetrics().density);
    }

    ////////////////////////////////// -> Override functions
    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            if (!isSlidingEnabled()) {
                mDragHelper.abort();
                return;
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    ////////////////////////////////// -> Get functions
    public IPanel<View> getSlidingUpPanel() {
        return mSlidingPanel;
    }

    public float getExpandThreshold() {
        return mExpandThreshold;
    }

    public float getCollapseThreshold() {
        return mCollapseThreshold;
    }

    public MultiSlidingPanelAdapter getAdapter() {
        return mAdapter;
    }

    public ViewDragHelper getDragHelper() {
        return mDragHelper;
    }

    ////////////////////////////////// -> Set functions
    public void setExpandThreshold(float threshold) {
        mExpandThreshold = threshold;
    }

    public void setCollapseThreshold(float threshold) {
        mCollapseThreshold = threshold;
    }

    public void setAdapter(@NonNull MultiSlidingPanelAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setSlidingUpPanelLayout(this);

        int itemCount = mAdapter.getItemCount();
        if (itemCount <= 0)
            return;

        if (getChildCount() > 0) {
            View view = getChildAt(0);
            removeAllViews();
            addView(view);
        }

        for (int i = 0; i < itemCount; i++) {
            IPanel<View> panel = mAdapter.onCreateSlidingPanel(i);
            panel.setSlidingUpPanelRoot(this);
            mAdapter.onBindView(panel, i);
            addView(panel.getPanelView());
        }

        isFirstLayout = true;
        mSlidingPanel = null;

        requestLayout();
    }

    public void setHideThreshold(float threshold) {
        mHideThreshold = Math.max(threshold, 1.0F);
    }

    public void setSlidingUpPanel(@NonNull IPanel<View> panel) {
        mSlidingPanel = panel;
    }

    public void setSlidingEnabled(boolean enabled) {
        isSlidingEnabled = enabled;
    }

    public void setPanelStateListener(PanelStateListener listener) {
        mPanelStateListener = listener;
    }

    ////////////////////////////////// -> API methods
    public boolean isSlidingEnabled() {
        return isSlidingEnabled;
    }

    public boolean expandPanel() {
        if (mSlidingPanel == null)
            return false;

        if (isFirstLayout) {
            mSlidingPanel.setPanelState(EXPANDED);
            return true;
        }

        return mSlidingPanel.getPanelState() == EXPANDED || isFirstLayout || smoothSlideTo(1.0F);
    }

    public boolean expandPanel(@NonNull IPanel<View> panel) {
        setSlidingUpPanel(panel);

        return expandPanel();
    }

    public boolean collapsePanel() {
        if (mSlidingPanel == null)
            return false;

        if (isFirstLayout) {
            mSlidingPanel.setPanelState(COLLAPSED);
            return true;
        }

        return mSlidingPanel.getPanelState() == COLLAPSED || isFirstLayout || smoothSlideTo(0.0F);
    }

    public boolean collapsePanel(@NonNull IPanel<View> panel) {
        setSlidingUpPanel(panel);

        return collapsePanel();
    }

    public boolean hidePanel() {
        if (mSlidingPanel == null)
            return false;

        if (isFirstLayout) {
            mSlidingPanel.setPanelState(HIDDEN);
            return true;
        }

        float offset = this.computeSlidedProgress(mSlidingPanel.getPanelTopByPanelState(HIDDEN));

        return mSlidingPanel.getPanelState() == HIDDEN || isFirstLayout || smoothSlideTo(offset);
    }

    public boolean hidePanel(@NonNull IPanel<View> panel) {
        setSlidingUpPanel(panel);

        return hidePanel();
    }

    private boolean smoothSlideTo(float slideOffset) {
        if(!isSlidingEnabled())
            return false;

        int panelTop = computePanelTopPosition(slideOffset);
        if (mDragHelper.smoothSlideViewTo(mSlidingPanel.getPanelView(), mSlidingPanel.getPanelView().getLeft(), panelTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private int computePanelTopPosition(float slideOffset) {
        return (int)(mSlidingPanel.getPanelExpandedHeightOffset() * slideOffset) + (int)((mSlidingPanel.getPanelExpandedHeight() - mSlidingPanel.getPanelCollapsedHeight()) * (1.0F - slideOffset));
    }

    private float computeSlidedProgress(int topPosition) {
        final int collapsedTop = computePanelTopPosition(0);
        return (float) (collapsedTop - topPosition) / (mSlidingPanel.getPanelExpandedHeight() - mSlidingPanel.getPanelCollapsedHeight());
    }

    ////////////////////////////////// -> Motion/Touch events functions
    MotionEvent initialEV;
    float initialX;
    float initialY;

    private void setOnTouchedInternal(final View child) {
        child.setOnTouchListener((view, motionEvent) -> {
            mSlidingPanel = (IPanel<View>) child;

            if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                view.performClick();
                return true;
            }

            return false;
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!isEnabled() || !isSlidingEnabled()) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            this.initialX = ev.getRawX();
            this.initialY = ev.getRawY();
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            this.mPanelPrevState = mSlidingPanel.getPanelState();
        }

        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSlidingEnabled())
            return super.onTouchEvent(ev);

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            this.mPanelPrevState = mSlidingPanel.getPanelState();
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            performClick();
        }

        else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = this.initialX - ev.getRawX();
            float dy = this.initialY - ev.getRawY();

            switch (mSlidingPanel.getPanelSlideDirection()) {
                case SLIDE_DOWN:
                    if (dy > 0) mDragHelper.cancel();
                    break;
                case SLIDE_UP:
                    if (dy < 0) mDragHelper.cancel();
                    break;
            }
        }

        mDragHelper.processTouchEvent(ev);
        return true;
    }

    ////////////////////////////////// -> View layout functions


    @SuppressWarnings("unchecked Cast")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        boolean hasExpandedPanel = false;

        for(int i = 0; i < mAdapter.getItemCount(); i++) {
            if(mAdapter.getItem(i).getPanelState() == EXPANDED) {
                hasExpandedPanel = true;
                break;
            }
        }

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (i == 0 && child instanceof IPanel) {
                throw new IllegalArgumentException("The child view at position 0 can't be an instance of ISlidingUpPanel! ");
            }
            if (i > 0 && !(child instanceof IPanel)) {
                throw new IllegalArgumentException("The child view after position 0 must be an instance of ISlidingUpPanel! ");
            }

            if (isFirstLayout && child instanceof IPanel) {
                if (mSlidingPanel == null) {
                    mSlidingPanel = (IPanel<View>) child;
                }
                setOnTouchedInternal(child);
            }

            // Always layout the sliding view on the first layout
            if (child.getVisibility() == GONE && (i == 0 || isFirstLayout)) {
                continue;
            }

            int childTop = paddingTop;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            childTop += lp.topMargin;
            int childBottom = childTop + child.getMeasuredHeight();
            int childLeft = paddingLeft + lp.leftMargin;
            int childRight = childLeft + child.getMeasuredWidth();

            if (child instanceof IPanel) {
                IPanel<View> panel = (IPanel<View>) child;
                if(hasExpandedPanel) {
                    if (panel.getPanelState() == EXPANDED) {
                        childTop = panel.getPanelTopByPanelState(EXPANDED) + getPaddingTop();
                    } else {
                        childTop = panel.getPanelTopByPanelState(HIDDEN) + getPaddingTop();
                    }
                }
                else {
                    childTop = panel.getPanelTopByPanelState(panel.getPanelState()) + getPaddingTop();
                }
                childBottom = childTop + (((IPanel<View>) child).getPanelExpandedHeight() - ((IPanel) child).getPanelExpandedHeightOffset());

                child.getLayoutParams().height = childBottom - childTop;
            }

            child.layout(childLeft, childTop, childRight, childBottom);
        }

        isFirstLayout = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (h != oldh) {
            isFirstLayout = true;
            mSlidingPanel = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        isFirstLayout = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        isFirstLayout = true;
        mSlidingPanel = null;
        if (mAdapter != null) {
            mAdapter.setSlidingUpPanelLayout(null);
            mAdapter = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
        } else if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Height must have an exact value or MATCH_PARENT");
        }

        final int childCount = getChildCount();

        int layoutHeight = heightSize - getPaddingTop() - getPaddingBottom();

        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();


            // We always measure the sliding panel in order to know it's height (needed for show panel)
            if (child.getVisibility() == GONE && i == 0) {
                continue;
            }

            int childWidth = widthSize - lp.leftMargin - lp.rightMargin;
            int childWidthSpec;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);
            } else if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            }

            int childHeight = layoutHeight - lp.topMargin - lp.bottomMargin;
            int childHeightSpec;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);
            } else if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            }

            child.measure(childWidthSpec, childHeightSpec);
            //Log.i("Measure", MessageFormat.format("Index : {0}, Width : {1}, Height : {2}", i, childWidthSpec, childHeightSpec));
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

    ////////////////////////////////// -> Layout state functions

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.isSlidingEnabled = isSlidingEnabled;
        ss.expandThreshold = mExpandThreshold;
        ss.collapseThreshold = mCollapseThreshold;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        isSlidingEnabled = ss.isSlidingEnabled;
        mExpandThreshold = ss.expandThreshold;
        mCollapseThreshold = ss.collapseThreshold;
    }

    private static class SavedState extends BaseSavedState {

        private boolean isSlidingEnabled;
        float expandThreshold;
        float collapseThreshold;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            isSlidingEnabled = in.readByte() == 1;
            expandThreshold = in.readFloat();
            collapseThreshold = in.readFloat();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeByte(isSlidingEnabled ? (byte) 1 : (byte) 0);
            dest.writeFloat(expandThreshold);
            dest.writeFloat(collapseThreshold);
        }

        public static final Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    ////////////////////////////////// Sub classes
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == mSlidingPanel;
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (mDragHelper == null)
                return;

            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                mSlidedOffset = computeSlidedProgress(mSlidingPanel.getPanelView().getTop());

                if (mSlidedOffset == 1) {
                    if (mSlidingPanel.getPanelState() != EXPANDED) {
                        mSlidingPanel.setPanelState(EXPANDED);
                        if (mPanelStateListener != null) {
                            mPanelStateListener.onPanelExpanded(mSlidingPanel);
                        }
                    }
                }
                else if (mSlidedOffset == 0) {
                    if (mSlidingPanel.getPanelState() != COLLAPSED) {
                        mSlidingPanel.setPanelState(COLLAPSED);
                        if (mPanelStateListener != null) {
                            mPanelStateListener.onPanelCollapsed(mSlidingPanel);
                        }
                    }
                }
                else if (mSlidedOffset < 0) {
                    mSlidingPanel.setPanelState(HIDDEN);
                    if (mPanelStateListener != null) {
                        mPanelStateListener.onPanelHidden(mSlidingPanel);
                    }
                }
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            isSlidingUp = dy < 0;

            mSlidingPanel.setPanelState(DRAGGING);
            mSlidedOffset = computeSlidedProgress(top);
            if (mPanelStateListener != null) {
                mPanelStateListener.onPanelSliding(mSlidingPanel, mSlidedOffset);
            }

            if (mSlidingPanel.isUserHidden() && mSlidingPanel.getPrevPanelState() == HIDDEN) {
                mSlidingPanel.disableUserHiddenMode();
                for (int i = 1; i < getChildCount(); i++) {
                    IPanel<View> view = (IPanel<View>) getChildAt(i);
                    if (view != (IPanel<View>) mSlidingPanel) {
                        view.resetPanelRealHeight();
                    }
                }
            }

            for (int i = 1; i < getChildCount(); i++) {
                IPanel<View> view = (IPanel<View>) getChildAt(i);
                view.onSliding(mSlidingPanel, top, dy, mSlidedOffset);
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int target;

            if (isSlidingUp) { // intent to expend
                target = computePanelTopPosition(mSlidedOffset >= mExpandThreshold ? 1.0f : 0.0f);
            } else { // intent to collapse
                target = computePanelTopPosition(mSlidedOffset >= mCollapseThreshold ? 1.0f : 0.0f);

                if (mSlidedOffset < 0.0F) {
                    float new_offset = mSlidedOffset * -1;
                    int top = computePanelTopPosition(new_offset);

                    int new_top = mSlidingPanel.getPanelExpandedHeight() - mSlidingPanel.getPanelCollapsedHeight();

                    if (top < (new_top - ((BasePanelView)mSlidingPanel).getPeakHeight() * mHideThreshold)) {
                        target = mSlidingPanel.getPanelTopByPanelState(HIDDEN);
                        ((BasePanelView)mSlidingPanel).isHidden = true;
                    }
                }
            }

            if (mDragHelper != null) {
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft() + getPaddingLeft(), target);
                invalidate();
            }
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            if (mSlidingPanel != null) {
                return mSlidingPanel.getPanelExpandedHeight() - ((mPanelPrevState == COLLAPSED) ? 0 : mSlidingPanel.getPanelCollapsedHeight());
            }

            return 0;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            final int collapsedTop = computePanelTopPosition(0.0f);
            final int expandedTop = computePanelTopPosition(1.0f);

            int new_top = (mPanelPrevState == COLLAPSED) ? mSlidingPanel.getPanelExpandedHeight() : collapsedTop;

            return Math.min(Math.max(top, expandedTop), new_top);
        }
    }
}
