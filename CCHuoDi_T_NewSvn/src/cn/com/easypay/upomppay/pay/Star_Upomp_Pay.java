package cn.com.easypay.upomppay.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.com.easypay.upomppay.config.UpompConfig;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.pay.Callback;

import com.unionpay.UPPayAssistEx;

public class Star_Upomp_Pay {

	public static final int PLUGIN_VALID = 0;
	public static final int PLUGIN_NOT_INSTALLED = -1;
	public static final int PLUGIN_NEED_UPGRADE = 2;

	/*
	 * ����������÷�����PluginHelper������com_unionpay_upomp_lthj_lib.jar
	 */


	/**
	 * 
	 * @param activity
	 * @param tn
	 * @param mode
	 *            �������ͣ� 00 - ����������ʽ����; 01 - �����������Ի���
	 */
	public void doStartUnionPayPlugin(final Activity activity, String tn, String mode) {
		int ret = UPPayAssistEx.startPay(activity, null, null, tn, mode);
		if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
			// ��Ҫ���°�װ�ؼ�
			AppLog.LogE(" plugin not found or need upgrade!!!");

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("��ʾ");
			builder.setMessage("��ɹ�����Ҫ��װ����֧���ؼ����Ƿ�װ��");

			builder.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					UPPayAssistEx.installUPPayPlugin(activity);
					dialog.dismiss();
				}
			});

			builder.setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();

		}
		AppLog.LogE(" plugin ret"  + ret);
	}

}
