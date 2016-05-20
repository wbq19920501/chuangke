package cn.com.easytaxi.ui.view;

import java.lang.reflect.Field;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import com.easytaxi.etpassengersx.R;

/**
 * @ClassName: BaseDialog
 * @Description: TODO
 * @author Brook xu
 * @date 2013-8-20 ����11:00:44
 * @version 1.0
 */
public class BaseDialog extends Dialog {

	private WindowManager winManager;

	public BaseDialog(Context context) {
		super(context, R.style.CustomDialog);
		// super(context, android.R.style.Theme_Dialog);
		winManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);  
	}

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(layoutResID);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams wmLayoutParams = getWindow().getAttributes();
		Window window = getWindow();
		// ��ȡ��Ļ������
		Display d = winManager.getDefaultDisplay();
		// p.height = (int) (d.getHeight() * 0.6); // �߶�����Ϊ��Ļ��0.6
		wmLayoutParams.width = (int) (d.getWidth() * 0.95); // �������Ϊ��Ļ��0.95
		// wmLayoutParams.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
		window.setAttributes(wmLayoutParams);
		// win.setWindowAnimations(R.style.dialogWindowAnim);
		// ȥ�����ڸ�
		// wmLayoutParams.dimAmount = 0;
	}

	/**
	 * ����ϵͳ�Ի����ڵ��ȷ����ȡ����ťʱ��Ȼ��ʾ
	 * 
	 * @param dialog
	 * @return void
	 */
	public static void keepDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ϵͳ�Ի����ڵ��ȷ����ȡ����ťʱ��ʧ
	 * 
	 * @param dialog
	 * @return void
	 */
	public static void dismissDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���õ���Ի���֮���Ƿ����ȡ���Ի���
	 * @param isCanceledOnTouchOutSide
	 */
	public void setCanceledOnTouchOutSide(boolean isCanceledOnTouchOutSide){
		setCancelable(isCanceledOnTouchOutSide);
		setCanceledOnTouchOutside(isCanceledOnTouchOutSide);
	}
}
