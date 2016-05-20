package cn.com.easytaxi.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;

public class PingJiaDlg extends Dialog implements OnClickListener {

	Context context;
	private Button button_ok;
	private Button button_cancel;
	private RadioGroup radioGroup;

	String[] choiceInfo = new String[] { "非常满意", "满意", "不满意" };

	private String title;

	private int currenPingji = 100;

	private MyDialogListener listener;
	private EditText editText_info;
	private TextView dlg_title;

	public interface MyDialogListener {
		public void onClick(View view);
	}

	public PingJiaDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		title = "评价司机";
	}

	public PingJiaDlg(Context context, int theme) {
		super(context, theme);
		this.context = context;
		title = "评价司机";
	}

	public PingJiaDlg(Context context, int theme, String title, String[] choiceInfo) {
		this(context, theme);
		this.title = title;
		this.choiceInfo = choiceInfo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// View view =
		// LayoutInflater.from(context).inflate(R.layout.wheel_scroll_layout,
		// null, true);

		setContentView(R.layout.pingjia_dialog);

		button_ok = (Button) findViewById(R.id.button_ok);
		button_cancel = (Button) findViewById(R.id.button_cancel);
		editText_info = (EditText) findViewById(R.id.editText_info);
		dlg_title = (TextView) findViewById(R.id.dlg_title);

		button_ok.setOnClickListener(this);
		button_cancel.setOnClickListener(this);

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		dlg_title.setText(title);

		AppLog.LogD("onCreate - -dsdsdsdsd11 -");
		AppLog.LogD("onCreate - - ds-");

		compulteCurrentSelectId(radioGroup.getCheckedRadioButtonId());

		RadioButton rb = (RadioButton) findViewById(R.id.good);
		rb.setText(choiceInfo[0]);
		rb = (RadioButton) findViewById(R.id.not_good);
		rb.setText(choiceInfo[1]);
		rb = (RadioButton) findViewById(R.id.bad);
		rb.setText(choiceInfo[2]);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				Log.d("jjj", checkedId + " --------------------");
				compulteCurrentSelectId(checkedId);
			}
		});

	}

	private void compulteCurrentSelectId(int checkedRadioButtonId) {
		// TODO Auto-generated method stub
		switch (checkedRadioButtonId) {
		case R.id.good:
			currenPingji = 100;
			break;
		case R.id.not_good:
			currenPingji = 60;
			break;
		case R.id.bad:
			currenPingji = 10;
			break;

		default:
			break;
		}
	}
 
	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.button_ok) {

		}

		listener.onClick(arg0);

	}

	public void setListener(MyDialogListener listener) {
		this.listener = listener;
	}

	public void getSelectItemKey() {

	}

	public String getPingjiaInfo() {
		if (editText_info != null) {
			return editText_info.getText().toString();
		}
		return null;
	}

	public int getCurrenPingji() {
		return currenPingji;
	}

}
