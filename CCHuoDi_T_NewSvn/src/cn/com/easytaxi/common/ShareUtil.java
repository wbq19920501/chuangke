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
				final CharSequence[] items = { "����", "����΢��", "��Ѷ΢��","�Ƽ���˾��","�Ƽ����˿�" };
				new AlertDialog.Builder(context).setTitle("��ѡ�����;��").setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						
					}

				}).create().show();
			}
		});
	}
}