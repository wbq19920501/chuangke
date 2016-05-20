package cn.com.easytaxi.airport.view;

import cn.com.easytaxi.AppLog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 循环切换改变文字内容
 * 
 * @author Administrator
 * 
 */
public class ChangingTextView extends TextView implements Runnable {
	String[] strs = new String[] { "正在录音15秒", "正在录音14秒", "正在录音13秒", "正在录音12秒", "正在录音11秒", "正在录音10秒", "正在录音9秒", "正在录音8秒", "正在录音7秒", "正在录音6秒", "正在录音5秒", "正在录音4秒", "正在录音3秒", "正在录音2秒", "正在录音1秒", "正在录音0秒" };
	private int index;
	private TimeCallback mTimeCallback;

	/**
	 * 线程开关
	 */
	private boolean isRun = true;

	/**
	 * UI线程的handler
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				AppLog.LogD("index--->" + index);
				setText(strs[index]);
				break;
			}
		}

	};

	public ChangingTextView(Context context) {
		super(context);
	}

	public ChangingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void run() {
		AppLog.LogD("start----------->");
		index = 0;
		handler.sendEmptyMessage(1);
		while (isRun) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			index++;
			if (index == strs.length) {// 从0开始
				stopChangingText();
				if (mTimeCallback != null) {
					mTimeCallback.timeOver();
				}
			}else{
				handler.sendEmptyMessage(1);
			}

		}
	}

	/**
	 * 开始变化
	 */
	public void startChangingText() {
		isRun = true;
		Thread thread = new Thread(ChangingTextView.this);
		thread.start();
	}

	/**
	 * 结束变化
	 */
	public void stopChangingText() {
		isRun = false;
		index = 0;
	}

	public static interface TimeCallback {
		public void timeOver();
	}

	public void setmTimeCallback(TimeCallback mTimeCallback) {
		this.mTimeCallback = mTimeCallback;
	}

}
