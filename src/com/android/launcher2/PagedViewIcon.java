package com.android.launcher2;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.TextView;

public class PagedViewIcon extends TextView {
	
    public static interface PressedCallback {
        void iconPressed(PagedViewIcon icon);
    }

    @SuppressWarnings("unused")
    private static final String TAG = "PagedViewIcon";
    private static final float PRESS_ALPHA = 0.4f;
    private PagedViewIcon.PressedCallback mPressedCallback;
    private boolean mLockDrawableState = false;
    private Bitmap mIcon;

    public PagedViewIcon(Context context) {
        this(context, null);
    }

    public PagedViewIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void applyFromApplicationInfo(ApplicationInfo info, boolean scaleUp, PagedViewIcon.PressedCallback cb) {
        mIcon = info.iconBitmap;
        mPressedCallback = cb;
        setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(mIcon), null, null);
        setText(info.title);
        setTag(info);
    }

    public void lockDrawableState() {
        mLockDrawableState = true;
    }

    public void resetDrawableState() {
        mLockDrawableState = false;
        post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        // We keep in the pressed state until resetDrawableState() is called to reset the press feedback
        if (isPressed()) {
            setAlpha(PRESS_ALPHA);
            if (mPressedCallback != null) {
                mPressedCallback.iconPressed(this);
            }
        } else if (!mLockDrawableState) {
            setAlpha(1f);
        }
    }
}
