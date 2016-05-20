package cn.com.easytaxi.platform.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 注意：在布局文件引用本view时,paddingLeft,paddingRigh都必须为0dp. android:ellipsize="mark"
 * android:singleLine="true" 这两个属性也要加上
 * 
 * @ClassName: MarqueeText
 * @Description: TODO
 * @author Brook xu
 * @date 2013-7-9 上午11:00:44
 * @version 1.0
 */
public class MarqueeText extends TextView implements Runnable {
	// 移动类型-始终移动
	public static final int SCROLL_TYPE_ALWAYS = 1;
	// 移动类型-自动：当可显示完文字时，就不移动
	public static final int SCROLL_TYPE_AUTO = 2;
	// 向左移动
	public static final int SCROLL_TO_LEFT = 1;
	// 向右移动
	public static final int SCROLL_TO_RIGHT = 2;
	// 左右来回移动
	public static final int SCROLL_TO_LEFT_RIGHT = 3;

	// 滚动速度,每次移动位置
	private static final int SCROLL_OFFSET_X = 1;
	// 滚动速度:刷新时间间隔
	private static final int SCROLL_REFRESH_TIME = 10;
	private int currentScrollX;// 当前滚动的位置
	private boolean isStop = false;
	// MarqueeText文字内容的长度
	private int textWidth;
	// MarqueeText控件的宽度
	private int mViewWidth;
	private boolean isMeasure = false;
	private int scrollDirection = SCROLL_TO_LEFT;
	private int scrollType = SCROLL_TO_LEFT;

	public MarqueeText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (!isMeasure) {// 文字宽度只需获取一次就可以了
			getTextWidth();
			isMeasure = true;
		}
	}

	/**
	 * 获取文字宽度及屏幕宽度
	 */
	private void getTextWidth() {
		Paint paint = this.getPaint();
		String str = this.getText().toString();
		textWidth = (int) paint.measureText(str);
		mViewWidth = this.getWidth();
	}

	@Override
	public void run() {
		if (isStop)
			return;

		if (scrollType == SCROLL_TYPE_AUTO) {
			if (textWidth <= mViewWidth) {
				return;
			}
		}

		if (scrollDirection == SCROLL_TO_RIGHT) {
			currentScrollX -= SCROLL_OFFSET_X;
			scrollTo(currentScrollX, 0);
			if (getScrollX() <= -mViewWidth) {
				scrollTo(textWidth, 0);
				currentScrollX = textWidth;
			}
		} else if (scrollDirection == SCROLL_TO_LEFT) {
			currentScrollX += SCROLL_OFFSET_X;
			scrollTo(currentScrollX, 0);
			if (getScrollX() >= textWidth) {
				scrollTo(-mViewWidth, 0);
				currentScrollX = -mViewWidth;
			}
		} 
		else {
			// 来回移动待实现
		}

		postDelayed(this, SCROLL_REFRESH_TIME);
	}

	// 开始滚动
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}

	// 停止滚动
	public void stopScroll() {
		isStop = true;
	}

	// 从头开始滚动
	public void startFor0() {
		currentScrollX = 0;
		startScroll();
	}

	public int getScrollDirection() {
		return scrollDirection;
	}

	public void setScrollDirection(int scrollDirection) {
		this.scrollDirection = scrollDirection;
	}

	public int getScrollType() {
		return scrollType;
	}

	public void setScrollType(int scrollType) {
		this.scrollType = scrollType;
	}
}
