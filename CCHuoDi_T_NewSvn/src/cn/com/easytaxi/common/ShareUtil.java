package cn.com.easytaxi.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.sax.StartElementListener;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.easytaxi.platform.RemPassengerActiviy;
import cn.com.easytaxi.platform.RemTaxiActiviy;

import com.umeng.api.sns.UMSnsService;

public class ShareUtil {

	public static void share(final Context context, View view) {
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final CharSequence[] items = { "短信", "新浪微博", "腾讯微博","推荐给司机","推荐给乘客" };
				new AlertDialog.Builder(context).setTitle("请选择分享途径").setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						
					}

				}).create().show();
			}
		});
	}
}