package cn.com.easytaxi.airport.view;

import cn.com.easytaxi.AppLog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ѭ���л��ı���������
 * 
 * @author Administrator
 * 
 */
public class ChangingTextView extends TextView implements Runnable {
	String[] strs = new String[] { "����¼��15��", "����¼��14��", "����¼��13��", "����¼��12��", "����¼��11��", "����¼��10��", "����¼��9��", "����¼��8��", "����¼��7��", "����¼��6��", "����¼��5��", "����¼��4��", "����¼��3��", "����¼��2��", "����¼��1��", "����¼��0��" };
	private int index;
	private TimeCallback mTimeCallback;

	/**
	 * �߳̿���
	 */
	private boolean isRun = true;

	/**
	 * UI�̵߳�handler
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
			if (index == strs.length) {// ��0��ʼ
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
	 * ��ʼ�仯
	 */
	public void startChangingText() {
		isRun = true;
		Thread thread = new Thread(ChangingTextView.this);
		thread.start();
	}

	/**
	 * �����仯
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
