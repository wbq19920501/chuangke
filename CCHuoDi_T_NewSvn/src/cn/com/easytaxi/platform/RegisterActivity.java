package cn.com.easytaxi.platform;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.mine.MyMainActivity;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.common.Util;
import cn.com.easytaxi.platform.service.EasyTaxiCmd;
import cn.com.easytaxi.platform.service.MainService;
import cn.com.easytaxi.service.AlarmClockBookService;
import cn.com.easytaxi.ui.view.CommonDialog;
import cn.com.easytaxi.util.RandomUtils;
import com.easytaxi.etpassengersx.R;
import com.google.gson.JsonObject;

public class RegisterActivity extends Activity {
	private ProgressDialog progressDialog;
	public static final String ACTION_REGISTER = "cn.com.easytaxi.ACTION_REGISTER";

	protected static final int CLOSE_DLG = 250;
	public static final int SEND_SMS_OK = 900;

	private RegisterActivity self = this;

	private EditText mobileEditText;
	private EditText codeEditText;
	private EditText recommendEditText;
	private TitleBar bar;

	private Spinner sexSpinner;

	private Button sendSmsButton; // 发送短信按钮
	private Button submitButton;
	private String mobile;

	// private Long code;

	// 注册成功后执行的代码类型：0-只广播，1-广播并按pkg.className跳转，并关闭设置界面
	private int type;

	private String pkg;
	private String className;

	private SessionAdapter dao;

	private String code = "1358";

	private Handler handler;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_register);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == SEND_SMS_OK) {
					String str = (String) msg.obj;
					if (!TextUtils.isEmpty(str)) {
						try {
							JSONObject json = new JSONObject(str);
							int error = json.getInt("error");
							if (error == 0) {
								Toast.makeText(RegisterActivity.this, "正在为您发送注册码，请稍等！", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(RegisterActivity.this, "注册码获取失败，请稍后重试！", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						Toast.makeText(RegisterActivity.this, "注册码获取失败，请稍后重试！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		};

		bar = new TitleBar(this);
		bar.setTitleName("用户注册");
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

		findViewById(R.id.layout).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					InputMethodManager imm = (InputMethodManager) RegisterActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});

		Intent intent = getIntent();
		type = intent.getIntExtra("type", 0);
		pkg = intent.getStringExtra("pkg");
		className = intent.getStringExtra("className");

		dao = new SessionAdapter(self);

		recommendEditText = (EditText) findViewById(R.id.register_recommend);
		mobileEditText = (EditText) findViewById(R.id.register_mobile);

		sexSpinner = (Spinner) findViewById(R.id.spinner_register_sex);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexSpinner.setAdapter(adapter);

		String tmpMobile = dao.get("TMP_MOBILE");

		if (TextUtils.isEmpty(tmpMobile)) {

		} else {
			mobileEditText.setText(tmpMobile);
		}

		sendSmsButton = (Button) findViewById(R.id.register_sendmsg);
		sendSmsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				mobile = mobileEditText.getText().toString();
				if (mobile == null || mobile.equals("") || !mobile.matches(Util.REGEX_MOBILE)) {
					Window.info(self, "请输入正确的本机号码");
					return;
				} else {
					dao.set("TMP_MOBILE", mobile);

					generageCode(mobile);
       
					System.out.println("code--->" + code);
					sendSms(mobile);

					sendSmsButton.setEnabled(false);
					sendSmsButton.setBackgroundResource(R.drawable.icon013);
					new AsyncTask<Object, Integer, Object>() {
						@Override
						protected void onProgressUpdate(Integer... values) {
							sendSmsButton.setText("重发 (" + values[0] + "s)");
						}

						@Override
						protected void onPostExecute(Object result) {
							sendSmsButton.setEnabled(true);
							sendSmsButton.setText("重新获取");
							sendSmsButton.setBackgroundResource(R.drawable.icon011);
						}

						@Override
						protected Object doInBackground(Object... params) {
							try {
								for (int i = 60; i >= 0; i--) {
									publishProgress(i);
									Thread.sleep(1000);
								}
							} catch (Throwable e) {
								e.printStackTrace();
							}
							return null;
						}
					}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});

		codeEditText = (EditText) findViewById(R.id.register_code);

		submitButton = (Button) findViewById(R.id.register_submit);
		submitButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				mobile = mobileEditText.getText().toString();
				String _code = codeEditText.getText().toString();
				String recommend = recommendEditText.getText().toString();

				if (recommend.length() > 0 && !recommend.matches(Util.REGEX_MOBILE)) {
					Window.info(self, "请输入正确的推荐人号码");
					return;
				}

				if (mobile == null || mobile.equals("")) {
					Window.info(self, "请输入正确的本机号码");
					return;
				} else {
					String number = dao.get("TMP_MOBILE");
					if (number != null) {
						if (!mobile.equals(number)) {
							Window.info(self, "请重新获取验证码");
							submitButton.setEnabled(true);
							return;
						}
					}
				}

				submitButton.setEnabled(false);

				if (code.equals(_code)) {
					new LoadNickName().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mobile);
				} else {
					submitButton.setEnabled(true);
					codeEditText.setText("");
					Window.info(self, "验证码输入不正确");
				}
			}
		});
	}

	private class LoadNickName extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			AppLog.LogD("LoadNickName onPreExecute");
			progressDialog = ProgressDialog.show(RegisterActivity.this, "提示", "请稍后...", true, true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			AppLog.LogD("LoadNickName doInBackground");
			JsonObject json = new JsonObject();
			json.addProperty("action", "userAction");
			json.addProperty("method", "getPassengerName");
			json.addProperty("id", params[0]);
			try {
				byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, json.toString().getBytes("UTF-8"));
				if (response != null && response.length > 0) {
					String rs = new String(response, "UTF-8");
					AppLog.LogD("doInBackground response-->" + rs);
					JSONObject retJson = new JSONObject(rs);
					int error = retJson.getInt("error");
					if (error == 0) {
						String nickName=null;
						if(retJson.has("name")){
							   nickName =  retJson.getString("name");	
						}
						if (TextUtils.isEmpty(nickName)) {
							return false;
						} else {
							int sex = 0;
							dao.set(User._NICKNAME, nickName);
							saveLocal(mobile, nickName, sex);
							return true;
						}
					}
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.cancel();
			if (result) {
				registerSuccess();
			} else {
				Intent i = new Intent(RegisterActivity.this, AddUserInfoActivity.class);
				i.putExtra("mobile", mobile);
				RegisterActivity.this.startActivityForResult(i, 100);

			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		submitButton.setEnabled(true);
		if (requestCode == 100) {
			if (resultCode == Activity.RESULT_OK) {
				String nickName = data.getStringExtra("nickName");
				int sex = data.getIntExtra("sex", 0);
				dao.set(User._NICKNAME, nickName);
				saveLocal(mobile, nickName, sex);
				registerSuccess();
			} else {
				Toast.makeText(RegisterActivity.this, "取消注册", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void saveLocal(String mobile, String nickName, int sex) {
		dao.delete("TMP_MOBILE");
		dao.set("_MOBILE", mobile);
		dao.set("TMP_MOBILE", mobile);
		dao.set("_NAME", nickName);
		dao.set(User._NICKNAME, nickName);
		dao.set(User._SEX, String.valueOf(sex));
		dao.set(User._ISLOGIN, User._LOGIN_LOGIN);
		dao.set(User._MOBILE_NEW, mobile);

		User user = ETApp.getInstance().getCurrentUser();
		try {
			user.setPassengerId(StringUtils.isEmpty(mobile) ? 0 : Long.valueOf(mobile));
			dao.set(User._PUID, StringUtils.isEmpty(mobile) ? "0" : mobile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		user.setUserNickName(nickName);
		user.setPhoneNumber("_MOBILE", mobile);
		user.setLogin(true);
	}

	private void registerSuccess() {

		RegisterActivity.this.sendBroadcast(new Intent(ACTION_REGISTER));

		Intent serviceIntent = new Intent(self, MainService.class);
		serviceIntent.setAction(EasyTaxiCmd.START_MAINSERVICE);
		startService(serviceIntent);

		if (type == 1) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName(pkg, pkg + "." + className);
			intent.setComponent(cn);
			self.startActivity(intent);
		}

		if (type == 2) {
			Intent intent = new Intent(self, MyMainActivity.class);
			self.startActivity(intent);
		}
		// ETApp.getInstance().setLogin(true);
		self.finish();
		submitButton.setEnabled(true);
		// finish();
		// 开启订单闹钟提醒服务
		Intent alarmIntent = new Intent(self, AlarmClockBookService.class);
		startService(alarmIntent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(CLOSE_DLG);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case CLOSE_DLG:
			return createLogoutDlg();
			// break;

		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	private Dialog createLogoutDlg() {
		Callback<Object> okBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
				RegisterActivity.this.finish();
			}
		};

		Callback<Object> cancelBtnCallback = new Callback<Object>() {
			@Override
			public void handle(Object param) {
				CommonDialog dialog = (CommonDialog) param;
				dialog.dismiss();
				// dismissDialog(CLOSE_DLG);
			}
		};

		Dialog dialog = new CommonDialog(RegisterActivity.this, "注意", "确定要退出注册吗？", "确定", "取消", R.layout.dlg_close, okBtnCallback, cancelBtnCallback);
		// dialog.show();
		return dialog;
	}

	@Override
	protected void onDestroy() {
		if (dao != null) {
			dao.close();
			dao = null;
		}
		bar.close();
		super.onDestroy();
	}

	private void sendSms(String mobile) {
		
		NewNetworkRequest.sendMms(handler, mobile, getAuthCode());
		AppLog.LogD("MY", code + "...........");
	}

	private String getAuthCode() {
		return "创客货的注册码为：" + code + "（此验证码单次有效）";
	}

	private void generageCode(String mobile) {
		code = RandomUtils.getRandomNumbers(4);
		if (code.length() > 4) {
			code = code.substring(0, 4);
		}
	}

	private static final String[] sexValues = new String[] { "保密", "女", "男" };
}
