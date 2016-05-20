package cn.com.easytaxi.phone;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.phone.common.DaoAdapter;
import cn.com.easytaxi.platform.common.Util;

public class AddActivity extends Activity {

	private AddActivity self = this;

	private TextView cityTextView;
	private EditText telTextView;
	private EditText numTextView;
	private EditText nameTextView;
	private RadioGroup sexRadioGroup;
	TitleBar bar;

	private Button button;

	private String cityId;
	private String cityName;
	private SessionAdapter session;
	private DaoAdapter dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_add);

		session = new SessionAdapter(this);
		dao = new DaoAdapter(this);

		bar = new TitleBar(this);
		bar.setTitleName("添加司机");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		bar.setBackCallback(new Callback<Object>() {

			@Override
			public void handle(Object param) {
				// TODO Auto-generated method stub
				// doBack();
				finish();
			}
		});

		// 作为接口外部参数
		Intent intent = getIntent();
		cityId = intent.getStringExtra("_CITY_ID");

		cityName = intent.getStringExtra("_CITY_NAME");

		if (StringUtils.isEmpty(cityName)) {
			cityId = session.get("_CITY_ID");
			cityName = session.get("_CITY_NAME");
		}

	 
		cityTextView = (TextView) findViewById(R.id.phone_add_city);
		cityTextView.setText(cityName);

		nameTextView = (EditText) findViewById(R.id.phone_add_name);
		nameTextView.setText(intent.getStringExtra("_NAME"));

		numTextView = (EditText) findViewById(R.id.phone_add_taxinum);
		numTextView.setText(intent.getStringExtra("_TAXI_NUMBER"));

		telTextView = (EditText) findViewById(R.id.phone_add_tel);
		telTextView.setText(intent.getStringExtra("_MOBILE"));

		button = (Button) findViewById(R.id.phone_add_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Window.confirm(self, "确定添加该司机的信息吗?", new Callback<Object>() {
				//
				// @Override
				// public void handle(Object param) {
				button.setEnabled(false);
				String name = nameTextView.getText().toString();
				String num = numTextView.getText().toString();
				String tel = telTextView.getText().toString();

				StringBuffer buf = new StringBuffer();
				boolean error = false;
				if (name.trim().equals("")) {
					buf.append("司机称呼不能为空!\n");
					error = true;
				}
				if (!tel.matches(Util.REGEX_MOBILE)) {
					buf.append("请输入正确的手机号码!");
					error = true;
				}

				if (error) {
					Window.error(self, buf.toString());
					button.setEnabled(true);
					return;
				}
				sexRadioGroup = (RadioGroup) findViewById(R.id.phone_add_sex);
				int sex = sexRadioGroup.getCheckedRadioButtonId() == R.id.phone_add_man ? 1 : 2;

				if (dao.addDriver(name, tel, num, cityId, sex)) {
					Intent intent = new Intent("cn.com.easytaxi.phone.add");
					self.sendBroadcast(intent);
					finish();
				} else {
					Window.error(self, "该司机已经被添加过");
					button.setEnabled(true);
				}

				// }
				// });
			}
		});

	}

	@Override
	protected void onDestroy() {
		if (session != null) {
			session.close();
			session = null;
		}
		if (dao != null) {
			dao.close();
			dao = null;
		}
		bar.close();
		super.onDestroy();
	}

}
