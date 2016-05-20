package cn.com.easytaxi.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;

/**
 * @ClassName: CancelBookDialog
 * @Description: TODO
 * @author Brook xu
 * @date 2013-8-20 下午1:17:41
 * @version 1.0
 */
public class CancelBookDialog extends BaseDialog implements OnClickListener, OnCheckedChangeListener {
	/**
	 *  个人原因
	 */
	public static final int CANCEL_REASON_PERSONAL = 0x400;
	/** 
	 * 司机没来
	 */
	public static final int CANCEL_REASON_TAXI = 0x200;

	private Callback<Object> okBtnCallback;
	private Callback<Object> cancleBtnCallback;

	private RadioGroup radioGroup;
	private Button btnComfirm;
	private Button btnCancel;
	private int reason;

	private long useTime;

	public CancelBookDialog(Context context, Callback<Object> okBtnCallback, Callback<Object> cancleBtnCallback) {
		super(context);
		// TODO Auto-generated constructor stub
		this.okBtnCallback = okBtnCallback;
		this.cancleBtnCallback = cancleBtnCallback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub R.layout.dlg_close
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_detail_cancel_book);
		initView();
		initListener();
	}

	public void initView() {
		radioGroup = (RadioGroup) findViewById(R.id.new_detail_radioGroup);
		btnComfirm = (Button) findViewById(R.id.stopservice_comfirm);
		btnCancel = (Button) findViewById(R.id.stopservice_cancel);

		reason = CANCEL_REASON_PERSONAL;
	}

	public void initListener() {
		btnComfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(this);
	}

	public void setOkBtnCallback(Callback<Object> okBtnCallback) {
		this.okBtnCallback = okBtnCallback;
	}

	public void setCancleBtnCallback(Callback<Object> cancleBtnCallback) {
		this.cancleBtnCallback = cancleBtnCallback;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.stopservice_comfirm:
			CancelBookDialog.this.dismiss();
			if (okBtnCallback != null)
				okBtnCallback.handle(reason);
			break;

		case R.id.stopservice_cancel:
			CancelBookDialog.this.dismiss();
			if (cancleBtnCallback != null)
				cancleBtnCallback.handle(CancelBookDialog.this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.book_detail_cancel_radio1:
			reason = CANCEL_REASON_PERSONAL;
			break;
		case R.id.book_detail_cancel_radio2:
			reason = CANCEL_REASON_TAXI;
			break;
		default:
			break;
		}
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	private String getFalseReason(int reason) {
		String msg = "";
		if (reason == CANCEL_REASON_PERSONAL) {

		} else if (reason == CANCEL_REASON_TAXI) {

		}

		return msg;
	}
}