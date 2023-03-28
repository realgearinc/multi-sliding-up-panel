package com.realgear.multislidinguppanel;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

public abstract class BasePanelView extends FrameLayout implements IPanel<View> {
    private MultiSlidingUpPanelLayout mParentSlidingPanel;

    @MultiSlidingUpPanelLayout.PanelState
    protected int mPanelState = MultiSlidingUpPanelLayout.COLLAPSED;

    @MultiSlidingUpPanelLayout.SlideDirection
    protected int mSlideDirection = MultiSlidingUpPanelLayout.SLIDE_VERTICAL;

    protected int mExpandedHeightOffset;
    protected int mRealPanelHeight;
    protected int mExpandedHeight;
    protected int mPeakHeight;
    protected int mIndex;

    protected float mSlope;

    public BasePanelView(@NonNull Context context, MultiSlidingUpPanelLayout panelLayout) {
        super(context);
        setClickable(true);
        this.mParentSlidingPanel = panelLayout;
    }

    public Lifecycle getLifecycle() {
        return this.mParentSlidingPanel.getAdapter().getAppCompatActivity().getLifecycle();
    }

    public FragmentManager getSupportFragmentManager() {
        return this.mParentSlidingPanel.getAdapter().getAppCompatActivity().getSupportFragmentManager();
    }


    ////////////////////////////////// -> Abstract functions
    public abstract void onCreateView();

    public abstract void onBindView();

    public abstract void onPanelStateChanged(@MultiSlidingUpPanelLayout.PanelState int panelSate);

    ////////////////////////////////// -> Override functions

    @NonNull
    @Override
    public BasePanelView getPanelView() {
        return this;
    }

    @Override
    public int getPanelExpandedHeight() {
        if (this.mExpandedHeight == 0) {
            int status_bar_height = 0;
            int navigation_bar_height = 0;

            int status_r_id = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if(status_r_id > 0)
                status_bar_height = getResources().getDimensionPixelSize(status_r_id);

            int nav_r_id = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if(nav_r_id > 0)
                navigation_bar_height = getResources().getDimensionPixelSize(nav_r_id);

            DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getRealMetrics(dm);
            }

            this.mExpandedHeight = (dm.heightPixels - (status_bar_height + navigation_bar_height));
            //Log.i("BaseSlideView", MessageFormat.format("HeightPixels {0} - (SB {1} + NB {2}) = {3}", dm.heightPixels, statusbarheight, navigationbarheight, this.mExpandedHeight));
        }
        return this.mExpandedHeight;
    }

    @Override
    public int getPanelCollapsedHeight() {
        return getPanelRealHeight();
    }

    @Override
    public int getPanelSlideDirection() {
        return this.mSlideDirection;
    }

    @Override
    public int getPanelState() {
        return this.mPanelState;
    }

    @Override
    public int getPanelTopByPanelState(int panelState) {
        switch (panelState) {
            case MultiSlidingUpPanelLayout.COLLAPSED:
                return (this.getPanelExpandedHeight() - this.getPanelCollapsedHeight());

            case MultiSlidingUpPanelLayout.EXPANDED:
                return this.getPanelExpandedHeight();

            default:
                return this.getPanelExpandedHeightOffset();
        }
    }

    @Override
    public MultiSlidingUpPanelLayout getMultiSlidingUpPanel() {
        return this.mParentSlidingPanel;
    }

    @Override
    public void onSliding(@NonNull IPanel<View> panel, int top, int dy, float slidingOffset) {
        if (panel != this) {
            int myTop = (int) (this.getPanelExpandedHeight() + this.getSlope(((BasePanelView) panel).getPanelRealHeight()) * top);
            this.setTop(myTop);
        }
    }

    @Override
    public void setPanelState(int panelState) {
        this.mPanelState = panelState;

        if (this.mPanelState != MultiSlidingUpPanelLayout.EXPANDED) {
            this.mSlope = 0;
        }

        this.onPanelStateChanged(panelState);
    }

    @Override
    public void setSlideDirection(int slideDirection) {
        this.mSlideDirection = slideDirection;
    }

    @Override
    public void setSlidingUpPanelRoot(MultiSlidingUpPanelLayout rootSlidingUpPanel) {
        this.mParentSlidingPanel = rootSlidingUpPanel;
    }

    ////////////////////////////////// -> Get functions
    public int getPeakHeight() {
        return this.mPeakHeight;
    }

    public int getPanelExpandedHeightOffset() {
        return this.mExpandedHeightOffset;
    }

    public int getFloor() {
        return this.mIndex;
    }

    public int getPanelRealHeight() {
        if (this.mRealPanelHeight == 0) {
            this.mRealPanelHeight = getPrevPanelsHeight(this.mIndex) + this.mPeakHeight;
        }

        return this.mRealPanelHeight;
    }

    private int getPrevPanelsHeight(int currentPosition) {
        int maxHeight = 0;

        int count = this.mParentSlidingPanel.getAdapter().getItemCount();
        int i = currentPosition + 1;

        for (; i < count; i++) {
            maxHeight += ((BasePanelView)this.mParentSlidingPanel.getAdapter().getItem(i)).getPeakHeight();
        }

        return maxHeight;
    }

    public float getSlope(int viewHeight) {
        if (this.mSlope == 0)
            this.mSlope = -1.0F * this.getPanelRealHeight() / (this.getPanelExpandedHeight() - viewHeight);

        return this.mSlope;
    }

    int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    ////////////////////////////////// -> Set functions
    public void setFloor(int floor) {
        this.mIndex = floor;
    }

    public void setPeakHeight(int peakHeight) {
        this.mPeakHeight = peakHeight;
    }

    public void setPanelExpandedHeightOffset(int offset) {
        this.mExpandedHeightOffset = offset;
    }

    ////////////////////////////////// -> API functions
    @SuppressWarnings("all")
    public void expandPanel() {
        this.mParentSlidingPanel.expandPanel(this, true);
    }
    @SuppressWarnings("all")
    public void collapsePanel() {
        this.mParentSlidingPanel.collapsePanel(this, true);
    }

    ////////////////////////////////// -> Instance functions

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.mSavedPanelState = mPanelState;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mPanelState = ss.mSavedPanelState;
    }

    private static class SavedState extends View.BaseSavedState {
        int mSavedPanelState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            mSavedPanelState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mSavedPanelState);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
    }
}
