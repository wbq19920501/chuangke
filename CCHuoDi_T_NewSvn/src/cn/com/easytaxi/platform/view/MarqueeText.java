package cn.com.easytaxi.platform.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * ע�⣺�ڲ����ļ����ñ�viewʱ,paddingLeft,paddingRigh������Ϊ0dp. android:ellipsize="mark"
 * android:singleLine="true" ����������ҲҪ����
 * 
 * @ClassName: MarqueeText
 * @Description: TODO
 * @author Brook xu
 * @date 2013-7-9 ����11:00:44
 * @version 1.0
 */
public class MarqueeText extends TextView implements Runnable {
	// �ƶ�����-ʼ���ƶ�
	public static final int SCROLL_TYPE_ALWAYS = 1;
	// �ƶ�����-�Զ���������ʾ������ʱ���Ͳ��ƶ�
	public static final int SCROLL_TYPE_AUTO = 2;
	// �����ƶ�
	public static final int SCROLL_TO_LEFT = 1;
	// �����ƶ�
	public static final int SCROLL_TO_RIGHT = 2;
	// ���������ƶ�
	public static final int SCROLL_TO_LEFT_RIGHT = 3;

	// �����ٶ�,ÿ���ƶ�λ��
	private static final int SCROLL_OFFSET_X = 1;
	// �����ٶ�:ˢ��ʱ����
	private static final int SCROLL_REFRESH_TIME = 10;
	private int currentScrollX;// ��ǰ������λ��
	private boolean isStop = false;
	// MarqueeText�������ݵĳ���
	private int textWidth;
	// MarqueeText�ؼ��Ŀ��
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
		if (!isMeasure) {// ���ֿ��ֻ���ȡһ�ξͿ�����
			getTextWidth();
			isMeasure = true;
		}
	}

	/**
	 * ��ȡ���ֿ�ȼ���Ļ���
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
			// �����ƶ���ʵ��
		}

		postDelayed(this, SCROLL_REFRESH_TIME);
	}

	// ��ʼ����
	public void startScroll() {
		isStop = false;
		this.removeCallbacks(this);
		post(this);
	}

	// ֹͣ����
	public void stopScroll() {
		isStop = true;
	}

	// ��ͷ��ʼ����
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
