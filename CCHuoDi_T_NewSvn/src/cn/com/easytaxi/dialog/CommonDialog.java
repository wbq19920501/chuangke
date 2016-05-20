package cn.com.easytaxi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public abstract class CommonDialog extends Dialog {
	protected Context context;
	private Dialog self;
	
	public CommonDialog(Context context, int layout, int style) {
		super(context, style);
		setContentView(layout);
		this.context = context;
		self = this;
		Window window = getWindow();
		
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		initListener();
	}
	public void setLeftTxt(CharSequence sequence){
	}
	public void setRightTxt(CharSequence sequence){
	}
	public abstract void initListener();
	
	public  void closeListener(){
	}

	public void closeDialog(){
		self.dismiss();
		closeListener();
	}
	
}
