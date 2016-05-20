package com.easytaxi.etpassengersx.wxapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.simcpux.Constants;
import net.sourceforge.simcpux.MD5;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.easypay.alipay.util.BaseHelper;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.client.common.ConfigUtil;
import cn.com.easytaxi.dialog.MyDialog;
import cn.com.easytaxi.dialog.MyDialog.SureCallback;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdLocaionBaseActivity;
import cn.com.easytaxi.receiver.LocationBroadcastReceiver;
import cn.com.easytaxi.ui.MyHttpUtils;
import cn.com.easytaxi.ui.SimpleCallBack;

import com.alipay.sdk.pay.demo.PayDemo;
import com.easytaxi.etpassengersx.R;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class PayActivity extends YdLocaionBaseActivity implements OnClickListener {
	public static final String PAY_RECEIVER = "com.eteasun.etpassengersx.book.pay";
	//http://120.24.231.9:80/EasytaxiMonitor/rule_Rule!getRule?cityName= 
	/**
	 * 计费规则连接
	 */
	public static final String PRICE_RULE = "http://" + ConfigUtil.getString("PAY_IPPORT") + "/EasytaxiMonitor/rule_Rule!getRule?cityName=";
	public static final int PAY_TYPE_WEIXIN = 5;
	public static final int PAY_TYPE_ZHIFUBAO = 1;

	public static final String url = "https://" + ConfigUtil.getString("PAY_IPPORT") + ":8444/YdRecharge/do";
	private ProgressDialog mProgress;
	private long mBookId;
	private int price;
	PayReq req;
	private TextView jine, jifei_guize;// 金额
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	Map<String, String> resultunifiedorder;
	StringBuffer sb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meidipay);
		TitleBar bar = new TitleBar(this);
		bar.setTitleName("选择支付方式");
		jine = (TextView) findViewById(R.id.money_number);
		jifei_guize = (TextView) findViewById(R.id.jifei_guize);
		jifei_guize.setText(Html.fromHtml("<u>计费规则查看</u>"));
		mBookId = this.getIntent().getLongExtra("bookId", 0l);
		price = getIntent().getIntExtra("price", 0);

		jine.setText(getPriceInYuan(price) + "");
		findViewById(R.id.pay_wx).setOnClickListener(this);
		findViewById(R.id.pay_zfb).setOnClickListener(this);
		jifei_guize.setOnClickListener(this);
		req = new PayReq();
		sb = new StringBuffer();

		msgApi.registerApp(Constants.APP_ID);
		registerReceiver();
		// 生成签名参数
	}

	private float getPriceInYuan(int price) {
		return price / 100f;
	}

	PayResultReceiver receiver = null;

	private void registerReceiver() {
		if (receiver == null)
			receiver = new PayResultReceiver();
		registerReceiver(receiver, new IntentFilter(PAY_RECEIVER));
	}

	@Override
	protected void onDestroy() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private void genPayReq(String prepayId) {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = prepayId;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		Log.e("orion", signParams.toString());

	}

	private boolean isWXAppInstalledAndSupported(Context context, IWXAPI api) {
		// LogOutput.d(TAG, "isWXAppInstalledAndSupported");
		boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
		if (!sIsWXAppInstalledAndSupported) {
			// Log.w(TAG, "~~~~~~~~~~~~~~微信客户端未安装，请确认");
			MyDialog.comfirm(context, "温馨提示", "您还没有安装微信哦", new SureCallback(), true, false, true);
		}
		return sIsWXAppInstalledAndSupported;
	}

	// private void sendPayReq() {
	//
	// msgApi.registerApp(Constants.APP_ID);
	// msgApi.sendReq(req);
	// }

	private void sendPayReq(String appid, String partnerId, String prepayId, String nonceStr, String timeStamp, String packageValue, String sign) {
		req.appId = appid;
		req.partnerId = partnerId;
		req.prepayId = prepayId;
		req.nonceStr = nonceStr;
		req.timeStamp = timeStamp;
		req.packageValue = packageValue;
		req.sign = sign;

		AppLog.LogD("xxb", "PayReq-->" + req.toString());
		Log.d("xxb", "PayReq-->" + req.toString());
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 调用预支付=微信支付
	 */
	private void requestPre(final int type) {
		if (type == PAY_TYPE_WEIXIN && !isWXAppInstalledAndSupported(this, msgApi)) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("action", "rechargeAction");
			jsonObject.put("method", "rechargeSubmit");
			jsonObject.put("platform", "meidichuxing");

			jsonObject.put("userId", String.valueOf(getPassengerId()));
			 jsonObject.put("money", price);
			//jsonObject.put("money", 1);
			jsonObject.put("payModeId", type);
			jsonObject.put("sn", "");
			jsonObject.put("password", "");
			jsonObject.put("bookId", mBookId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mProgress = BaseHelper.showProgress(PayActivity.this, null, "正在生成订单", false, true);
		MyHttpUtils.getInstance().send(url, jsonObject.toString(), new SimpleCallBack(PayActivity.this) {
			@Override
			public void onSuccess(com.xc.lib.xutils.http.ResponseInfo<String> responseInfo) {
				System.out.println("content" + responseInfo.result);
				try {
					JSONObject result = new JSONObject(responseInfo.result);
					if (type == PAY_TYPE_WEIXIN) {// 微信支付
						JSONObject aliResult = new JSONObject(result.getString("datas"));
						String appid = aliResult.getString("appid");
						String partnerId = aliResult.getString("partnerid");
						String prepayId = aliResult.getString("prepayid");
						String nonceStr = aliResult.getString("noncestr");
						String timeStamp = aliResult.getString("timestamp");
						String packageValue = aliResult.getString("package");
						String sign = aliResult.getString("sign");
						sendPayReq(Constants.APP_ID, partnerId, prepayId, nonceStr, timeStamp, packageValue, sign);
					} else if (type == PAY_TYPE_ZHIFUBAO) {
						JSONObject aliResult = new JSONObject(result.getString("datas"));
						String requestStr = aliResult.getString("requestStr");
//						PayDemo payDemo = new PayDemo(PayActivity.this);
//						payDemo.pay(requestStr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};

			@Override
			public void onFinish() {
				super.onFinish();
				closeProgress();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pay_wx:
			// 微信支付
			requestPre(PAY_TYPE_WEIXIN);
			break;
		case R.id.pay_zfb:
			// 支付宝支付
			requestPre(PAY_TYPE_ZHIFUBAO);
			// Toast.makeText(this, "该功能尚在开发中，请使用微信支付",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.jifei_guize:
//			Intent intent = new Intent(this, MyWebViewActivity.class);
//			intent.putExtra("title", "计费规则");
//			intent.putExtra("url", PRICE_RULE + LocationBroadcastReceiver.getcity());
//			startActivity(intent);
			break;
		}
	}

	// 关闭进度�?
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
	 */
	public static class AlixOnCancelListener implements DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	public class PayResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Intent intents = new Intent();
			intents.putExtra("error", intent.getIntExtra("error", -1));
			intents.putExtra("type", intent.getIntExtra("type", -1));
			PayActivity.this.setResult(RESULT_OK, intents);
			finish();
		}
	}

	@Override
	protected void initViews() {

	}

	@Override
	protected void initListeners() {

	}

	@Override
	protected void initUserData() {

	}

	@Override
	protected void regReceiver() {

	}

	@Override
	protected void unRegReceiver() {

	}

}
