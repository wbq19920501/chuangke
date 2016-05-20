package cn.com.easytaxi.util;

import android.content.Context;

public class ToastUtil {

	public static void show(Context context, String msg) {
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		try {
			MyToast.show(context, msg);
		} catch (Exception e) {
		}
		
	}
	
	public static void show(Context context,int msgId){
		show(context, context.getResources().getString(msgId));
	}

}
