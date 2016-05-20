package com.easytaxi.etpassengersx.wxapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.simcpux.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easypay.alipay.config.PartnerConfig;
import cn.com.easypay.alipay.util.AlixId;
import cn.com.easypay.alipay.util.BaseHelper;
import cn.com.easypay.alipay.util.MobileSecurePayHelper;
import cn.com.easypay.alipay.util.ResultChecker;
import cn.com.easypay.alipay.util.Rsa;
import cn.com.easypay.upomppay.pay.Star_Upomp_Pay;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Config;
import cn.com.easytaxi.common.SocketUtil;
import cn.com.easytaxi.platform.RegisterActivity;
import cn.com.easytaxi.platform.YdLocaionBaseActivity;
import cn.com.easytaxi.util.ToastUtil;
import cn.com.easytaxi.workpool.BaseActivity;

import com.alipay.android.msp.demo.Result;
import com.alipay.sdk.pay.demo.PayDemo;
import com.allinpay.appayassistex.APPayAssistEx;
import com.easytaxi.etpassengersx.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @ClassName: PayActivity
 * @Description: TODO
 * @author Brook xu
 * @date 2013-7-30 下午2:34:40
 * @version 1.0
 */
public class WXPayEntryActivity extends YdLocaionBaseActivity implements OnClickListener, IWXAPIEventHandler {
	
	public static final String PAY_RECEIVER = "com.eteasun.etpassengersx.book.pay";
	
	
	// 1-支付宝、2-银联、3-神州付、4-通联支付 、5-微信支付
	public static final int PAY_CHANNEL_ALIPAY = 1;
	public static final int PAY_CHANNEL_UNION = 2;
	public static final int PAY_CHANNEL_SHENZHOU = 3;
	public static final int PAY_CHANNEL_TONGLIAN = 4;
	public static final int PAY_CHANNEL_WEIXIN = 5;

	// public static final String url =
	// "http://easytaxi.f3322.org:8088/YdRecharge/do";// 测试服务器用
	// public static final String url =
	// "http://10.143.132.88:8082/YdRecharge/do";// 测试服务器用
	public static final String url = "http://121.42.180.59:80/YdRecharge/do";// 正式服务器用
	public final static String TAG = "payBtnClick";
	private ImageButton backBtn;
	private TextView title;
	private ViewGroup alipay;
	private ViewGroup alipay2;
	private ViewGroup union;
	private ViewGroup tonglian;
	private ViewGroup offlinePay;
	private ViewGroup xianjinPay;// 现金支付

	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;

	private Star_Upomp_Pay star;
	private MobileSecurePayHelper mspHelper;
	private boolean isMobileSpExist;
	private ProgressDialog mProgress;
	// 打车充值金额
	private EditText mEtMoney;

	private String mRechargeId;
	private int mMoney;
	private int channel = PAY_CHANNEL_UNION;

	private IWXAPI api;
	private String mBookId;
	
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easypay);
		mBookId = this.getIntent().getStringExtra("bookId");
		mspHelper = new MobileSecurePayHelper(WXPayEntryActivity.this);
		initView();
		String fee = getIntent().getStringExtra("fee");
		if (!TextUtils.isEmpty(fee)) {
			try {
				int feei = Integer.parseInt(fee);
				if (feei != 0)
					mEtMoney.setText("" + feei);
			} catch (Exception e) {
			}
		}
		initListener();
	}

	/**
	 * 给控件创建监听器
	 */
	private void initListener() {
		backBtn.setOnClickListener(this);
		alipay.setOnClickListener(this);
		alipay2.setOnClickListener(this);
		union.setOnClickListener(this);
		tonglian.setOnClickListener(this);
		offlinePay.setOnClickListener(this);
		xianjinPay.setOnClickListener(this);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		title = (TextView) findViewById(R.id.map_title_mid_tv);
		title.setText("支付");
		backBtn = (ImageButton) findViewById(R.id.map_title_leftbtn);
		alipay = (ViewGroup) findViewById(R.id.pay_alipay);
		alipay2 = (ViewGroup) findViewById(R.id.pay_alipay2);
		// alipay.setVisibility(View.GONE);
		union = (ViewGroup) findViewById(R.id.pay_union);
		tonglian = (ViewGroup) findViewById(R.id.pay_tonglian);
		offlinePay = (ViewGroup) findViewById(R.id.pay_offline);
		mEtMoney = (EditText) findViewById(R.id.etPayMoney);
		xianjinPay = (ViewGroup) findViewById(R.id.pay_xianjin);
	}

	@Override
	public void onBackPressed() {
		mMoney = 0;
		mRechargeId = "";
		backPayResult(false);
	}

	Handler mNewHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			result.parseResult();
			String strResult = result.getResultCode();
			if ("9000".equals(strResult)) {
				backPayResult(true);
			} else {
				backPayResult(false);
			}

		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_title_leftbtn:// 返回
			onBackPressed();
			break;
		case R.id.pay_xianjin: // 现金支付
			if (ETApp.getInstance().isLogin()) {
				int money = getRechargeFee();
				if (money == 0) {
					Toast.makeText(WXPayEntryActivity.this, "请输入正确的金额!", Toast.LENGTH_LONG).show();
				} else {
					doCompleteOrder(money);
				}
			} else {
				switchToRegister();
			}
			break;
		case R.id.pay_alipay:// 微信
			if (ETApp.getInstance().isLogin()) {
				int money = getRechargeFee();
				if (money == 0) {
					Toast.makeText(WXPayEntryActivity.this, "请输入正确的金额!", Toast.LENGTH_LONG).show();
				} else {
					channel = PAY_CHANNEL_WEIXIN;
					doPay(money, channel);
				}
			} else {
				switchToRegister();
			}
			break;
		case R.id.pay_alipay2:// 支付宝
			if (ETApp.getInstance().isLogin()) {
				int money = getRechargeFee();
				if (money == 0) {
					Toast.makeText(WXPayEntryActivity.this, "请输入正确的金额!", Toast.LENGTH_LONG).show();
				} else {
					channel = PAY_CHANNEL_ALIPAY;
					doPay(money, channel);
				}
			} else {
				switchToRegister();
			}
			break;
		case R.id.pay_union:// 银联
			if (ETApp.getInstance().isLogin()) {
				int money = getRechargeFee();
				if (money == 0) {
					Toast.makeText(WXPayEntryActivity.this, "请输入正确的金额!", Toast.LENGTH_LONG).show();
				} else {
					channel = PAY_CHANNEL_UNION;
					doPay(money, channel);
				}
			} else {
				switchToRegister();
			}
			break;
		case R.id.pay_tonglian:// 通联
			if (ETApp.getInstance().isLogin()) {
				int money = getRechargeFee();
				if (money == 0) {
					Toast.makeText(WXPayEntryActivity.this, "请输入正确的金额!", Toast.LENGTH_LONG).show();
				} else {
					channel = PAY_CHANNEL_TONGLIAN;
					doPay(money, channel);
				}
			} else {
				switchToRegister();
			}
			break;

		case R.id.pay_offline:// 线下支付
			if (ETApp.getInstance().isLogin()) {
				Intent i = new Intent();
				i.putExtra("isOffline", true);
				setResult(RESULT_OK, i);
				finish();
			} else {
				switchToRegister();
			}
			break;
		default:
			break;
		}
	}

	private void switchToRegister() {
		Intent intent = new Intent(WXPayEntryActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	private int getRechargeFee() {

		try {
			return Integer.parseInt(mEtMoney.getText().toString().trim());
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 现金支付，
	 */
	private void doCompleteOrder(int money) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "scheduleAction");
			jsonObject.put("method", "linePayment");
			jsonObject.put("bookId", mBookId);
			jsonObject.put("price", money);
			jsonObject.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			String value = jsonObject.toString();
			AppLog.LogD("现金支付：params-->" + value);
			mProgress = BaseHelper.showProgress(WXPayEntryActivity.this, null, "正在请求...", false, true);
			SocketUtil.getJSONObject(Long.valueOf(getPassengerId()), jsonObject, new Callback<JSONObject>() {
				@Override
				public void handle(JSONObject param) {
					try {
						String errormsg = param.getString("errormsg");
						if (param.getInt("error") == 0) {
							backPayResult(true);
						} else {
							Toast.makeText(WXPayEntryActivity.this, "" + errormsg, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(WXPayEntryActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
					}
				}
				@Override
				public void error(Throwable e, int errorcode) {
					super.error(e, errorcode);
					Toast.makeText(WXPayEntryActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				}
				@Override
				public void complete() {
					super.complete();
					closeProgress();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Http post方式
	 * 
	 * @param type
	 *            1-支付宝、2-银联、3-神州付、4-通联支付 、5-微信支付
	 * @return void
	 */
	private void doPay(int fee, final int type) {
		mRechargeId = "";
		mMoney = fee;
		AsyncHttpClient client = new AsyncHttpClient();
		long uId = ETApp.getInstance().getCurrentUser().getPassengerId();
		JSONObject jsonObject = new JSONObject();
		try {
			// "action":"rechargeAction"
			// ,"method":"rechargeSubmit"
			// ,"platform":"电招平台" //string：平台标志，使各平台账户相互独立
			// ,"userId":"" //string：用户ID
			// ,"money":50 //int：充值金额(元)
			// ,"payModeId":1 //int：支付方式
			// ,"sn":"……" //string：卡号，仅神州付支付时需提交
			// ,"password":"……"

			jsonObject.put("action", "rechargeAction");
			jsonObject.put("method", "rechargeSubmit");
			jsonObject.put("platform", "shanxionecity");

			jsonObject.put("userId", String.valueOf(uId));
			if (type == PAY_CHANNEL_WEIXIN) {
				jsonObject.put("money", fee*100);
			}else {
				jsonObject.put("money", fee);
			}
			jsonObject.put("payModeId", type);
			jsonObject.put("sn", "");
			jsonObject.put("password", "");
			jsonObject.put("bookId", mBookId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String value = jsonObject.toString();
		RequestParams params = new RequestParams("params", value);
		AppLog.LogD("支付：url-->" + url);
		AppLog.LogD("支付：params-->" + value);
		client.setTimeout(30000);
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				mProgress = BaseHelper.showProgress(WXPayEntryActivity.this, null, "正在生成订单", false, true);
			}

			@Override
			public void onFinish() {
				closeProgress();
			}

			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				Log.d("xyw", "http-post" + content);

				try {
					JSONObject result = new JSONObject(content);
					if (result.getInt("error") == 0) {
						if (type == PAY_CHANNEL_WEIXIN) { // 5-微信
							JSONObject aliResult = new JSONObject(result.getString("datas"));
//							mRechargeId = aliResult.getString("rechargeId");

							String appid = aliResult.getString("appid");
							String partnerId = aliResult.getString("partnerid");
							String prepayId = aliResult.getString("prepayid");
							String nonceStr = aliResult.getString("noncestr");
							String timeStamp = aliResult.getString("timestamp");
							String packageValue = aliResult.getString("package");
							String sign = aliResult.getString("sign");
						
							
							sendPayReq(Config.APP_ID, partnerId, prepayId, nonceStr, timeStamp, packageValue, sign);

						} else if (type == PAY_CHANNEL_UNION) {// 2-银联
							JSONObject unionResult = new JSONObject(result.getString("datas"));
							// String requestStr =
							// unionResult.getString("requestStr");
							// doUnionpay(mRechargeId, requestStr);
							mRechargeId = unionResult.getString("rechargeId");
							String sn = unionResult.getString("tn");
							if (star == null)
								star = new Star_Upomp_Pay();
							star.doStartUnionPayPlugin(WXPayEntryActivity.this, sn, "00");

						} else if (type == PAY_CHANNEL_TONGLIAN) {// 4-通联
							JSONObject unionResult = new JSONObject(result.getString("datas"));
							mRechargeId = unionResult.getString("orderNo");
							// AppLog.LogD("xxb", "ordernumber - > " +
							// unionResult.toString());
							APPayAssistEx.startPay(WXPayEntryActivity.this, unionResult.toString(), APPayAssistEx.MODE_PRODUCT);
							// APPayAssistEx.startPay(WXPayEntryActivity.this,
							// unionResult.toString(),
							// APPayAssistEx.MODE_DEBUG);
							// JSONObject payData = PaaCreator.randomPaa();
							// String param = payData.toString();
							// Log.d("xyw", param);
							// APPayAssistEx.startPay(PayActivity.this, param,
							// APPayAssistEx.MODE_DEBUG);
						}else if(type == PAY_CHANNEL_ALIPAY){//支付宝
							//Toast.makeText(WXPayEntryActivity.this, type+result.toString(), Toast.LENGTH_LONG).show();
							JSONObject aliResult = new JSONObject(result.getString("datas"));
							String requestStr = aliResult.getString("requestStr");
							AppLog.LogD("requestStr---------->" + requestStr);
							PayDemo payDemo = new PayDemo(WXPayEntryActivity.this);
							payDemo.pay(requestStr);
						}  else {
							AppLog.LogD("doPay--->error type");
						}
					} else {
						AppLog.LogD("充值-->服务端返回失败");
						Toast.makeText(WXPayEntryActivity.this, "充值-->服务端返回失败-->"+"生成订单失败", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(WXPayEntryActivity.this, "Exception-->"+"生成订单失败" + e.toString(), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				Toast.makeText(WXPayEntryActivity.this, "onFailure-->"+"生成订单失败", Toast.LENGTH_SHORT).show(); 
			}

		});
	}

	/**
	 * get the selected order info for pay. 获取商品订单信息
	 * 
	 * @param position
	 *            商品在列表中的位置
	 * @return
	 */
	String getOrderInfo() {
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "out_trade_no=" + "\"" + getOutTradeNo() + "\"";
		strOrderInfo += "&";
		strOrderInfo += "subject=" + "\"" + PartnerConfig.subject + "\"";
		strOrderInfo += "&";
		strOrderInfo += "body=" + "\"" + PartnerConfig.body + "\"";
		strOrderInfo += "&";
		strOrderInfo += "total_fee=" + "\"" + PartnerConfig.price.replace("一口价:", "") + "\"";
		strOrderInfo += "&";
		strOrderInfo += "notify_url=" + "\"" + "http://notify.java.jpxx.org/index.jsp" + "\"";

		return strOrderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 * @return
	 */
	String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);

		java.util.Random r = new java.util.Random();
		strKey = strKey + r.nextInt();
		strKey = strKey.substring(0, 15);
		return strKey;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param signType
	 *            签名方式
	 * @param content
	 *            待签名订单信息
	 * @return
	 */
	String sign(String signType, String content) {
		return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 * @return
	 */
	String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	/**
	 * get the char set we use. 获取字符集
	 * 
	 * @return
	 */
	String getCharset() {
		String charset = "charset=" + "\"" + "utf-8" + "\"";
		return charset;
	}

	/**
	 * check some info.the partner,seller etc. 检测配置信息
	 * partnerid商户id，seller收款帐号不能为空
	 * 
	 * @return
	 */
	private boolean checkInfo() {
		String partner = PartnerConfig.PARTNER;
		String seller = PartnerConfig.SELLER;
		if (partner == null || partner.length() <= 0 || seller == null || seller.length() <= 0)
			return false;

		return true;
	}

	// the handler use to receive the pay result.
	// 这里接收支付结果，支付宝手机端同步通知
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String strRet = (String) msg.obj;

				Log.e(TAG, strRet); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();

					BaseHelper.log(TAG, strRet);

					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = strRet.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = strRet.indexOf("};memo=");
						tradeStatus = strRet.substring(imemoStart, imemoEnd);

						// 先验签通知
						ResultChecker resultChecker = new ResultChecker(strRet);
						int retVal = resultChecker.checkSign();

						// 验签失败
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(WXPayEntryActivity.this, "提示", WXPayEntryActivity.this.getResources().getString(R.string.check_sign_failed), android.R.drawable.ic_dialog_alert);
							backPayResult(false);
						} else {// 验签成功。验签成功后再判断交易状态码
							if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
								backPayResult(true);
								BaseHelper.showDialog(WXPayEntryActivity.this, "提示", "支付成功。交易状态码：" + tradeStatus, R.drawable.infoicon);
							} else {
								BaseHelper.showDialog(WXPayEntryActivity.this, "提示", "支付失败。交易状态码:" + tradeStatus, R.drawable.infoicon);
								backPayResult(false);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog(WXPayEntryActivity.this, "提示", strRet, R.drawable.infoicon);
						backPayResult(false);
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				backPayResult(false);
			}
		}
	};

	private void backPayResult(boolean isSuccessed) {
		Intent i = new Intent();
		i.putExtra("chargeId", mRechargeId);
		i.putExtra("money", mMoney);
		i.putExtra("payModeId", channel);
		if (isSuccessed) {
			setResult(RESULT_OK, i);
		} else {
			setResult(RESULT_CANCELED, i);
		}

		finish();
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

	//
	// close the progress bar
	// 关闭进度框
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

	/*
	 * 插件返回支付信息的**回调函数** (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1356) {// 通联支付
			if (0 == resultCode) {
				if (null != data) {
					String payRes = null;
					String payAmount = null;
					String payTime = null;
					String payOrderId = null;
					try {
						JSONObject resultJson = new JSONObject(data.getExtras().getString("result"));
						payRes = resultJson.getString(APPayAssistEx.KEY_PAY_RES);
						payAmount = resultJson.getString("payAmount");
						payTime = resultJson.getString("payTime");
						payOrderId = resultJson.getString("payOrderId");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (null != payRes && payRes.equals(APPayAssistEx.RES_SUCCESS)) {
						backPayResult(true);
					} else {
						backPayResult(false);
					}
				} else {
					// backPayResult(false);
				}
			} else {
				backPayResult(false);
			}
		} else {// 银联手机支付

			/*************************************************
			 * 
			 * 步骤3：处理银联手机支付控件返回的支付结果
			 * 
			 ************************************************/
			if (data == null) {
				return;
			}

			String msg = "";
			/*
			 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
			 */
			String str = data.getExtras().getString("pay_result");
			if (str.equalsIgnoreCase("success")) {
				msg = "支付成功！";
				backPayResult(true);
			} else if (str.equalsIgnoreCase("fail")) {
				msg = "支付失败！";
				backPayResult(false);
			} else if (str.equalsIgnoreCase("cancel")) {
				msg = "用户取消了支付";
				backPayResult(false);
			} else {
				msg = "失败";
				backPayResult(false);
			}
			AppLog.LogD("银联支付结果--->" + msg);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 调用微信支付控件
	 * 
	 * @param appid
	 * @param partnerId
	 * @param prepayId
	 * @param nonceStr
	 * @param timeStamp
	 * @param packageValue
	 * @param sign
	 */
	private void sendPayReq(String appid, String partnerId, String prepayId, String nonceStr, String timeStamp, String packageValue, String sign) {
		PayReq req = new PayReq();
		req.appId = appid;
		req.partnerId = partnerId;
		req.prepayId = prepayId;
		req.nonceStr = nonceStr;
		req.timeStamp = timeStamp;
		req.packageValue = packageValue;
		req.sign = sign;

		AppLog.LogD("PayReq-->" + req.toString());
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		api = WXAPIFactory.createWXAPI(this, Config.APP_ID);
		api.registerApp(Config.APP_ID);
		api.handleIntent(getIntent(), this);
		api.sendReq(req);
		
		
		
		
	}

	@Override
	public void onReq(BaseReq arg0) {
		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0) {
				backPayResult(true);
			} else {
				backPayResult(false);
			}
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
