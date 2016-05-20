package cn.com.easytaxi.dialog;

import com.easytaxi.etpassengersx.R;

import android.content.Context;
import android.widget.TextView;


public class MyLoadingDialog extends CommonLogdingDialog {
	private TextView tv;

	public MyLoadingDialog(Context context) {
		super(context);
		tv = (TextView) findViewById(R.id.tipTextView);
		setCanceledOnTouchOutside(false);
	}

	public void showWithText(String text) {
		if (tv != null) {
			tv.setText(text);
		}
		showWithAnimation();

	}

}
