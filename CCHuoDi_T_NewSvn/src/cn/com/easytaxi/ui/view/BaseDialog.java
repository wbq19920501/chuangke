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
 * @date 2013-8-20 上午11:00:44
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
		// 获取屏幕宽、高用
		Display d = winManager.getDefaultDisplay();
		// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
		wmLayoutParams.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
		// wmLayoutParams.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
		window.setAttributes(wmLayoutParams);
		// win.setWindowAnimations(R.style.dialogWindowAnim);
		// 去背景遮盖
		// wmLayoutParams.dimAmount = 0;
	}

	/**
	 * 保持系统对话框在点击确定或取消按钮时仍然显示
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
	 * 让系统对话框在点击确定或取消按钮时消失
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
	 * 设置点击对话框之外是否可以取消对话框
	 * @param isCanceledOnTouchOutSide
	 */
	public void setCanceledOnTouchOutSide(boolean isCanceledOnTouchOutSide){
		setCancelable(isCanceledOnTouchOutSide);
		setCanceledOnTouchOutside(isCanceledOnTouchOutSide);
	}
}
