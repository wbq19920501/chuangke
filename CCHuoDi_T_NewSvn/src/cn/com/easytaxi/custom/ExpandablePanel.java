package cn.com.easytaxi.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import com.easytaxi.etpassengersx.R;

public class ExpandablePanel extends LinearLayout implements OnTouchListener, OnClickListener {

	private static final String TAG = "ExpandablePanel";
	private final int mHandleId;
	private final int mContentId;
	// 上下拖动时，起始Y坐标
	private float y = 0;
	// 上下拖动时状态，滑动位置：0中间，1上限，2下限
	private int state = 0;
	// 上下拖动时，移动Y坐标
	private float yOffset = 0;
	private View mHandle;
	private View mContent;

	private boolean mExpanded = false;
	private int mCollapsedHeight = 0;
	private int mContentHeight = 0;
	private int mAnimationDuration = 0;

	private OnExpandListener mListener;

	public ExpandablePanel(Context context) {
		this(context, null);
	}

	public ExpandablePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		mListener = new DefaultOnExpandListener();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandablePanel, 0, 0);

		// How high the content should be in "collapsed" state
		mCollapsedHeight = (int) a.getDimension(R.styleable.ExpandablePanel_collapsedHeight, 0.0f);

		// How long the animation should take
		mAnimationDuration = a.getInteger(R.styleable.ExpandablePanel_animationDuration, 300);

		int handleId = a.getResourceId(R.styleable.ExpandablePanel_handle, 0);
		if (handleId == 0) {
			throw new IllegalArgumentException("The handle attribute is required and must refer " + "to a valid child.");
		}

		int contentId = a.getResourceId(R.styleable.ExpandablePanel_content, 0);
		if (contentId == 0) {
			throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
		}

		mHandleId = handleId;
		mContentId = contentId;

		a.recycle();
	}

	public void setOnExpandListener(OnExpandListener listener) {
		mListener = listener;
	}

	public void setCollapsedHeight(int collapsedHeight) {
		mCollapsedHeight = collapsedHeight;
	}

	public void setAnimationDuration(int animationDuration) {
		mAnimationDuration = animationDuration;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mHandle = findViewById(mHandleId);
		if (mHandle == null) {
			throw new IllegalArgumentException("The handle attribute is must refer to an" + " existing child.");
		}

		mHandle.setOnClickListener(this);
		mHandle.setOnTouchListener(this);

		mContent = findViewById(mContentId);
		if (mContent == null) {
			throw new IllegalArgumentException("The content attribute must refer to an" + " existing child.");
		}
		LayoutParams contentLayoutParam = (LayoutParams) mContent.getLayoutParams();
		contentLayoutParam.height = mCollapsedHeight;
		mContent.setLayoutParams(contentLayoutParam);
		mContent.setOnClickListener(new PanelToggler());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// First, measure how high content wants to be
		mContent.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
		mContentHeight = mContent.getMeasuredHeight();

		if (mContentHeight < mCollapsedHeight) {
			mHandle.setVisibility(View.GONE);
			Log.d(TAG, "Hidden because content small");
		} else {
			mHandle.setVisibility(View.VISIBLE);
			Log.d(TAG, "Not Hidden");
		}

		// Then let the usual thing happen
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void expand(boolean anim) {
		if (anim) {
			Animation a = new ExpandAnimation(mContent.getLayoutParams().height, mContentHeight);
			mListener.onExpand(mHandle, mContent);
			a.setDuration(mAnimationDuration);
			if (mContent.getLayoutParams().height == 0) // Need to do this or
														// else the animation
														// will not play if the
														// height is 0
			{
				Log.d(TAG, "mContent height is 0? WTFFFF?");
				android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
				lp.height = 1;
				mContent.setLayoutParams(lp);
				mContent.requestLayout();
			}
			Log.d(TAG, "Now starting animation");
			mContent.startAnimation(a);
			mExpanded = !mExpanded;
		} else {
			mListener.onExpand(mHandle, mContent);
			if (mContent.getLayoutParams().height == 0) // Need to do this or
														// else the animation
														// will not play if the
														// height is 0
			{
				Log.d(TAG, "mContent height is 0? WTFFFF?");
				android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
				lp.height = 1;
				mContent.setLayoutParams(lp);
				mContent.requestLayout();
			}
			android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
			lp.height = mContentHeight;
			mContent.setLayoutParams(lp);
			mExpanded = true;
		}
	}

	public void toggle() {
		if (mExpanded) {
			collapse(true);
		} else {
			expand(true);
		}
	}

	public void collapse(boolean anim) {
		if (anim) {
			Animation a = new ExpandAnimation(mContent.getLayoutParams().height, mCollapsedHeight);
			mListener.onCollapse(mHandle, mContent);
			a.setDuration(mAnimationDuration);
			if (mContent.getLayoutParams().height == 0) // Need to do this or
														// else the animation
														// will not play if the
														// height is 0
			{
				Log.d(TAG, "mContent height is 0? WTFFFF?");
				android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
				lp.height = 1;
				mContent.setLayoutParams(lp);
				mContent.requestLayout();
			}
			Log.d(TAG, "Now starting animation");
			mContent.startAnimation(a);
			mExpanded = !mExpanded;
		} else {
			mListener.onCollapse(mHandle, mContent);
			if (mContent.getLayoutParams().height == 0) // Need to do this or
														// else the animation
														// will not play if the
														// height is 0
			{
				Log.d(TAG, "mContent height is 0? WTFFFF?");
				android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
				lp.height = 1;
				mContent.setLayoutParams(lp);
				mContent.requestLayout();
			}
			android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
			lp.height = mCollapsedHeight;
			mContent.setLayoutParams(lp);
			mExpanded = false;

		}
	}

	private class PanelToggler implements OnClickListener {
		public void onClick(View v) {
			if (mExpanded) {
				collapse(true);
			} else {
				expand(true);
			}
		}
	}

	private class ExpandAnimation extends Animation {
		private final int mStartHeight;
		private final int mDeltaHeight;

		public ExpandAnimation(int startHeight, int endHeight) {
			mStartHeight = startHeight;
			mDeltaHeight = endHeight - startHeight;
			Log.d(TAG, "mStartHeight:" + mStartHeight);
			Log.d(TAG, "endHeight: " + endHeight);
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			android.view.ViewGroup.LayoutParams lp = mContent.getLayoutParams();
			lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
			Log.d(TAG, "Calculated Height is : " + lp.height);
			mContent.setLayoutParams(lp);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}

	public interface OnExpandListener {
		public void onExpand(View handle, View content);

		public void onCollapse(View handle, View content);
	}

	private class DefaultOnExpandListener implements OnExpandListener {
		public void onCollapse(View handle, View content) {
		}

		public void onExpand(View handle, View content) {
		}
	}

	public boolean ismExpanded() {
		return mExpanded;
	}

	public void setmExpanded(boolean mExpanded) {
		this.mExpanded = mExpanded;
	}

	public View getContent() {
		return mContent;
	}

	public int getCollapsedHeight() {
		return mCollapsedHeight;
	}

	public int getContentHeight() {
		return mContentHeight;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			state = 0;
			y = event.getRawY();
			Log.d("xyw", "ACTION_DOWN" + ",Y:" + y);
			break;
		case MotionEvent.ACTION_MOVE:
			yOffset = event.getRawY() - y;
			Log.d("xyw", "ACTION_MOVE" + ",Y:" + y);
			Log.d("xyw", "ACTION_MOVE" + ",Offset:" + yOffset);
			View content = this.getContent();
			android.view.ViewGroup.LayoutParams lp = content.getLayoutParams();
			lp.height += yOffset;
			if (lp.height < this.getCollapsedHeight()) {
				lp.height = this.getCollapsedHeight();
				state = 1;
				Log.d("xyw", "111--------------------->lp.height" + lp.height);
			} else if (lp.height > this.getContentHeight()) {
				lp.height = this.getContentHeight();
				state = 2;
				Log.d("xyw", "222--------------------->lp.height" + lp.height);
			} else {
				state = 0;
			}
			content.setLayoutParams(lp);
			y = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			Log.d("xyw", "ACTION_UP" + ",Y:" + y);
		case MotionEvent.ACTION_CANCEL:
			Log.d("xyw", "ACTION_CANCEL" + ",Y:" + y);
			y = 0;
			if (state == 0) {
				this.toggle();
			} else if (state == 1) {
				if (mExpanded) {
					this.toggle();
				}
			} else {
				if (!mExpanded) {
					this.toggle();
				}
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// if (v == mHandle) {
		// this.toggle();
		// }
	}
}