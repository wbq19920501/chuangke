package cn.com.easytaxi.util;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import com.easytaxi.etpassengersx.R;
import com.xc.lib.layout.LayoutUtils;

public class MyToast {
	public static void show(Context context, String msg) {
		Toast toast = new Toast(context);
		TextView tv = new TextView(context);
		tv.setBackgroundResource(R.drawable.shap_toast_bg);
		tv.setTextColor(Color.WHITE);
		tv.setPadding(LayoutUtils.getRate4px(15), LayoutUtils.getRate4px(15), LayoutUtils.getRate4px(15), LayoutUtils.getRate4px(15));
		tv.setText(msg);
		toast.setView(tv);
		toast.show();
	}
}
