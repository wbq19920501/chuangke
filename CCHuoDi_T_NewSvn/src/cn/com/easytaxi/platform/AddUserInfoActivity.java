package cn.com.easytaxi.platform;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.MoreWebviewActivity;

import com.easytaxi.etpassengersx.R;

public class AddUserInfoActivity extends Activity {
	private SessionAdapter dao;
	private ProgressDialog progressDialog;
	public static final String ACTION_REGISTER = "cn.com.easytaxi.ACTION_REGISTER";

	protected static final int CLOSE_DLG = 250;

	private EditText recommendEditText;
	private EditText nickNameEditText;
	private String nickName;
	private TitleBar bar;
	private CheckBox mCbProtocol;
	private Spinner sexSpinner;
	private EditText recommendNameEditText;
	private Button submitButton;

	private String mobile;
	private int sex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_register_more);
		mobile = this.getIntent().getStringExtra("mobile");
		dao = new SessionAdapter(this);
		bar = new TitleBar(this);
		bar.setTitleName("完善信息");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		bar.setBackCallback(new Callback<Object>() {

			@Override
			public void handle(Object param) {
				// TODO Auto-generated method stub
				back(false);
			}
		});

		findViewById(R.id.register_tv_protocol).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doShowProtocol(v);
			}
		});

		findViewById(R.id.register_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back(false);
			}
		});

		findViewById(R.id.layout).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					InputMethodManager imm = (InputMethodManager) AddUserInfoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});

		recommendEditText = (EditText) findViewById(R.id.register_recommend);
		nickNameEditText = (EditText) findViewById(R.id.register_nickname);
		mCbProtocol = (CheckBox) findViewById(R.id.register_cb_protocol);
		mCbProtocol.setChecked(false);
		recommendNameEditText = (EditText) findViewById(R.id.register_recommend_name);

		sexSpinner = (Spinner) findViewById(R.id.spinner_register_sex);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexSpinner.setAdapter(adapter);

		submitButton = (Button) findViewById(R.id.register_submit);
		submitButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
				sex = sexSpinner.getSelectedItemPosition() - 1;
				nickName = nickNameEditText.getText().toString();
				String recommend = recommendEditText.getText().toString();
				String recName = recommendNameEditText.getText().toString();

				if (!mCbProtocol.isChecked()) {
					Window.info(AddUserInfoActivity.this, "请先阅读并同意服务条款!");
					return;
				}

				if (StringUtils.isEmpty(nickName)) {
					Toast.makeText(AddUserInfoActivity.this, "昵称不能为空", Toast.LENGTH_LONG).show();
					return;
				}

				submitButton.setEnabled(false);
				progressDialog = ProgressDialog.show(AddUserInfoActivity.this, "请稍后", "注册中...", true, true);
				NewNetworkRequest.regNewUser(nickName, mobile, Long.valueOf(mobile), sex, new Callback<Object>() {
					@Override
					public void handle(Object param) {
						if (param != null) {
							try {
								JSONObject jsonObject = (JSONObject) param;
								System.out.println("params - > " + param.toString());
								if (jsonObject.getInt("error") == 0) {
									Toast.makeText(AddUserInfoActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
									submitButton.setEnabled(true);
									back(true);
								} else {
									Toast.makeText(AddUserInfoActivity.this, jsonObject.getString("errormsg"), Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(AddUserInfoActivity.this, "注册失败，请重新提交", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(AddUserInfoActivity.this, "注册失败，请重新提交", Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void error(Throwable e) {
						Toast.makeText(AddUserInfoActivity.this, "注册失败，请重新提交", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void complete() {
						submitButton.setEnabled(true);
						progressDialog.cancel();
					}
				});

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void back(boolean isSuccess) {
		Intent i = new Intent();
		i.putExtra("nickName", nickName);
		i.putExtra("sex", sex);
		if (isSuccess) {
			setResult(Activity.RESULT_OK, i);
		} else {
			setResult(Activity.RESULT_CANCELED, i);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		bar.close();
		super.onDestroy();
	}

	public void doShowProtocol(View v) {
		Intent intent = new Intent(AddUserInfoActivity.this, MoreWebviewActivity.class);
		intent.putExtra("title", "服务条款");
		String uri = "file:///android_asset/tiaokuan_shanxi.html";
		intent.putExtra("uri", uri);
		intent.putExtra("type", 0);
		startActivity(intent);
	}

	private static final String[] sexValues = new String[] { "保密", "女", "男" };
}
