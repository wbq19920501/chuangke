package cn.com.easytaxi.platform.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class AlphabetBar extends View {
	private static final String TAG = "AlphabetBar";
	private String[] letters;
	private int letterCount = 27;
	private int itemHeight = 0;
	private int currentIndex = 0;
	private Paint textPaint;
	private OnSelectedListener onSelectedListener;
	private SectionIndexer sectionIndexter;

	private ListView list;

	public AlphabetBar(Context context) {
		super(context);
		init();
	}

	public AlphabetBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		letters = new String[] { "»»√≈", "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		setBackgroundColor(0XFFFFFF);

		textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setColor(Color.GRAY);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(17.0F);
		itemHeight = 25;
		letterCount = letters.length;
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}

	public void setList(ListView list) {
		this.list = list;
	}

	public int getLetterCount() {
		return letterCount;
	}

	public void setLetterCount(int letterCount) {
		this.letterCount = letterCount;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setSectionIndexter(SectionIndexer sectionIndexter) {
		this.sectionIndexter = sectionIndexter;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	private OnClickListener mLabelClicked = new OnClickListener() {
		public void onClick(View v) {
			if (mClickCallback != null)
				mClickCallback.onClick(v);
		}
	};

	private OnClickListener mClickCallback = null;

	/**
	 * Set the click listener for alphabet labels.
	 * 
	 * @param listener
	 *            Click listener, or null to unset.
	 */
	public void setLabelClickListener(OnClickListener listener) {
		mClickCallback = listener;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		itemHeight = ((-10 + (bottom - top)) / letters.length);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		 
		final float f = getMeasuredWidth() / 2;
		for (int i = 0; i < letters.length; i++) {
			canvas.drawText(letters[i], f, itemHeight + i * itemHeight,
					textPaint);
		}
		
		
		//super.onDraw(canvas);
	}

	
	
 

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		 
		if(event.getAction() == MotionEvent.ACTION_DOWN ){
			 
			setBackgroundColor(Color.argb(88, 181, 181, 181));
		}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
		 
			setBackgroundColor(Color.argb(88, 239, 239, 239));
		}
		
		
		super.onTouchEvent(event);
		int index = (int) event.getY() / itemHeight;
		if (onSelectedListener == null) {
			return false;
		}
		if (index >= letterCount || index < 0) {
			onSelectedListener.onUnselected();
			//setBackgroundColor(0XFFFFFF);
			return false;
		}
		 
		 
		
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			currentIndex = index;
			onSelectedListener.onSelected(index, letters[currentIndex]);
			//setBackgroundColor(0xCCCCCC);
			
			int select = 0;
			currentIndex = index;
			if (sectionIndexter == null) {
				sectionIndexter = ((SectionIndexer) this.list.getAdapter());
			}

			select = sectionIndexter.getPositionForSection(currentIndex);
			if (select == -1) {
				onSelectedListener.onUnselected();
				//setBackgroundColor(0XFFFFFF);
				return false;
			}
			list.setSelection(select);
			
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			
			int select = 0;
			currentIndex = index;
			if (sectionIndexter == null) {
				sectionIndexter = ((SectionIndexer) this.list.getAdapter());
			}

			select = sectionIndexter.getPositionForSection(currentIndex);
			if (select == -1) {
				onSelectedListener.onUnselected();
				//setBackgroundColor(0XFFFFFF);
				return false;
			}
			list.setSelection(select);
			onSelectedListener.onUnselected();
			//setBackgroundColor(0XFFFFFF);
			return true;
		}
		return false;

	}

	public static abstract interface OnSelectedListener {
		public abstract void onSelected(int index, String name);

		public abstract void onUnselected();
	}

}
