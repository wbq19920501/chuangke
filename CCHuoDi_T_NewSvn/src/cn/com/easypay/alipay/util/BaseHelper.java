package cn.com.easypay.alipay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.easytaxi.etpassengersx.R;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;

/**
 * ������
 * 
 */
public class BaseHelper {

	/**
	 * ��ת�ַ�������
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * ��ʾdialog
	 * 
	 * @param context
	 *            ����
	 * @param strTitle
	 *            ����
	 * @param strText
	 *            ����
	 * @param icon
	 *            ͼ��
	 */
	public static void showDialog(Activity context, String strTitle,
			String strText, int icon) {
		AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
		tDialog.setIcon(icon);
		tDialog.setTitle(strTitle);
		tDialog.setMessage(strText);
		tDialog.setPositiveButton(R.string.Ensure, null);
		tDialog.show();
	}

	/**
	 * ��ӡ��Ϣ
	 * 
	 * @param tag
	 *            ��ǩ
	 * @param info
	 *            ��Ϣ
	 */
	public static void log(String tag, String info) {
		// Log.d(tag, info);
	}

	/**
	 * ��ȡȨ��
	 * 
	 * @param permission
	 *            Ȩ��
	 * @param path
	 *            ·��
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// show the progress bar.
	/**
	 * ��ʾ������
	 * 
	 * @param context
	 *            ����
	 * @param title
	 *            ����
	 * @param message
	 *            ��Ϣ
	 * @param indeterminate
	 *            ȷ����
	 * @param cancelable
	 *            �ɳ���
	 * @return
	 */
	public static ProgressDialog showProgress(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(false);
		// dialog.setDefaultButton(false);
		dialog.setOnCancelListener(new WXPayEntryActivity.AlixOnCancelListener((Activity) context));
		dialog.show();
		return dialog;
	}

	/**
	 * �ַ���תjson����
	 * @param str
	 * @param split
	 * @return
	 */
	public static JSONObject string2JSON(String str, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arrStr = str.split(split);
			for (int i = 0; i < arrStr.length; i++) {
				String[] arrKeyValue = arrStr[i].split("=");
				json.put(arrKeyValue[0],
						arrStr[i].substring(arrKeyValue[0].length() + 1));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}
}