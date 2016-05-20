package cn.com.easytaxi.ui.view;

import org.apache.commons.lang3.StringUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;

public class CommonDialog extends BaseDialog {

	Callback<Object> okBtnCallback;
	Callback<Object> cancleBtnCallback;

	private String title;
	private String contentInfo;
	private String okBtnStr;
	private String cancelBtnStr;

	private TextView dlg_title;
	private TextView dlg_info;
	private Button button_ok;
	private Button button_cancel;
	/**
	 * dialog的资源文件id
	 */
	private int resourceId;
	Context context;

	public CommonDialog(Context context, String title, String contentInfo, String okBtn, String cancelBtn, int resourceId, Callback<Object> okBtnCallback, Callback<Object> cancleBtnCallback) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.title = title;
		this.contentInfo = contentInfo;
		this.okBtnStr = okBtn;
		this.cancelBtnStr = cancelBtn;
		this.resourceId = resourceId;

		this.okBtnCallback = okBtnCallback;
		this.cancleBtnCallback = cancleBtnCallback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub R.layout.dlg_close
		super.onCreate(savedInstanceState);
		this.setContentView(resourceId);
		initView();
		initListener();
	}

	public void initView() {
		dlg_title = (TextView) findViewById(R.id.dlg_title);
		dlg_info = (TextView) findViewById(R.id.dlg_info);
		button_ok = (Button) findViewById(R.id.button_ok);
		button_cancel = (Button) findViewById(R.id.button_cancel);

		dlg_title.setText(title);
		dlg_info.setText(contentInfo);
		button_ok.setText(okBtnStr);
		button_cancel.setText(cancelBtnStr);

		if (StringUtils.isEmpty(cancelBtnStr)) {
			button_cancel.setVisibility(View.GONE);
		}

		if (StringUtils.isEmpty(okBtnStr)) {
			button_ok.setVisibility(View.GONE);
		}
	}

	public void initListener() {
		button_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okBtnCallback.handle(CommonDialog.this);
			}
		});

		button_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancleBtnCallback.handle(CommonDialog.this);
				// MyDialog.this.dismiss();
			}
		});
	}

	/**
	 * 设置dialog在点击返回键不消失
	 */
	public OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
	};
}