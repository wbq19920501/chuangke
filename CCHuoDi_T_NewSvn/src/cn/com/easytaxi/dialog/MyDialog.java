package cn.com.easytaxi.dialog;

import com.easytaxi.etpassengersx.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class MyDialog {

	public static CommonDialog comfirm(final Context context, final String title, final String text, final SureCallback callback) {
		return comfirm(context, title, text, callback, true, false, false);
	}
	public static CommonDialog comfirm(final Context context, final String title, final String text, final SureCallback callback,boolean isShow) {
		return comfirm(context, title, text, callback, isShow, false, false);
	}

	public static CommonDialog comfirm(final Context context, final String title, final String text, final SureCallback callback, boolean isShow, final boolean isCheck, final boolean isSign) {
		CommonDialog dialog = new CommonDialog(context, R.layout.confirm, R.style.MyDialog) {
			CheckBox checkbox;
			Button cancelButton,okButton;
			public boolean isGpsCheck() {
				return checkbox.isChecked();
			}
			
			public void setLeftTxt(CharSequence sequence){
				cancelButton.setText(sequence);
			}
			public void setRightTxt(CharSequence sequence){
				okButton.setText(sequence);
			}
			public void initListener() {
				final TextView titleTextView = (TextView) findViewById(R.id.title);
				final TextView textTextView = (TextView) findViewById(R.id.text);

				checkbox = (CheckBox) findViewById(R.id.checkBox_gps);
				final View signLine = findViewById(R.id.sign_line);
				titleTextView.setText(title);
				textTextView.setText(text);

				cancelButton = (Button) findViewById(R.id.cancel_btn);
				okButton = (Button) findViewById(R.id.ok_btn);

				if (isSign) {
					signLine.setVisibility(View.GONE);
					cancelButton.setVisibility(View.GONE);
				}

				if (isCheck) {
					checkbox.setVisibility(View.VISIBLE);
				}
				cancelButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						callback.onClick(0);
						callback.onClick(0, isGpsCheck());
						closeDialog();
					}
				});

				okButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						callback.onClick(1);
						callback.onClick(1, isGpsCheck());
						closeDialog();
					}
				});
			}

		};
		if (isShow)
			dialog.show();
		return dialog;
	}

	public static class SureCallback {
		public int LEFT = 0;
		public int RIGHT = 1;

		public void onClick(int tag) {
		}

		public void onClick(int tag, boolean isGpsCheck) {
		}

	}

}
