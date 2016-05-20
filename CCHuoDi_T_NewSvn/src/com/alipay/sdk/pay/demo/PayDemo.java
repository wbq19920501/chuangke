package com.alipay.sdk.pay.demo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.client.common.ConfigUtil;
import cn.com.easytaxi.ui.MyHttpUtils;
import cn.com.easytaxi.ui.SimpleCallBack;
import cn.com.easytaxi.util.HttpUtil;
import cn.com.easytaxi.util.ToastUtil;

import com.alipay.sdk.app.PayTask;
import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.xc.lib.xutils.exception.HttpException;
import com.xc.lib.xutils.http.ResponseInfo;
import com.xc.lib.xutils.http.callback.RequestCallBack;
import com.xc.lib.xutils.http.client.HttpRequest.HttpMethod;

public class PayDemo {
	private WXPayEntryActivity activity;
	//public static final String url = "http://" + ConfigUtil.getString("PAY_IPPORT") + "/YdRecharge/Sync/alipay/recharge/";
	public static final String url = "http://" + "121.42.180.59" + "/YdRecharge/Sync/alipay/recharge/";
	private ProgressDialog mProgress;
	public PayDemo(WXPayEntryActivity activity) {
		this.activity = activity;
	}

	// 商户PID
	public static final String PARTNER = "2088221727003495";
	// 商户收款账号
	public static final String SELLER = "mdjcycp@163.com";
	//	// 商户私钥，pkcs8格式
	//	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPcUxSZoSwTizaQP7UUZqeVa+Syi4/FUNdq37sGd8MOAr9lQMpL75mXRoPUASDJ4FoioXnvr4ngOYxHZI4H+kXHLdlGjdGRnCxCr6C3Cym0B+tp0XNk8JaQfsrpOAFkL5jAWwIgZybuJijrn9WNTlrjCEDAQZK3/iXOyTObAIdgvAgMBAAECgYEAgMZ9fmgK+W/v99spMcOPbe99QMtg/A2EyArnUFgt/OccP6nxwfOyAE1Ck47jEhLOw8jk/bdSYHo3Hj1KqVzxlhRiBxvwMSBNl9AQn/leoKEgmhHTJFSfc7UY59TXMXQCzDMAwG1uStf4I63AIQtksF165MK3xUQjuwQJqlwKqHkCQQD/Cg1WSy4rf/qPnURmvxSWCxtEQDkLK6dV0vwYR+w3VVlKYr/LR7CO4I75bwRFru8eFPHDm/bMCMquC8gffdo7AkEA+AMLJ16zp4fRWSeWmC+D7F2gbRKt0ZG9YN/sZQzG5NPe+gR+XntD+aTD4OLC44B3Uuh/Ps1gpPV88QCrOYbmnQJARVvXerNhcAUTAEGV25d1osSMesg4ezg4/tctJfFRZU75xIgMft0VQBYD7APsFoNXlke95COjP6PYcvLTTvUsBwJBAPBWWrezQRvHNStVfs1JtmkWrgElCcY9mizHspiWEiPr4XedZSx//XZtodhLRxsaAggZAD5G3jHdJ0+RG1yrCs0CQDN9dj8NZK1rq5ZY4RwgMFjV58UpreW6bmg/ZrhgN1bmzDRdGsustMd7IXTlxXKMkcA4mgKRgPZ5AF9+twHwfpI=";
	//	// 支付宝公钥
	//	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKiA9RL0l6zIeWK8tnxINIcbz1xCxgmrDY3dWxMPUfbAli0EeWmMr0h3pxcMJXFXup+i2ID24iKp3FFAKhooGcDIhW1OTJQAlZ2AAejrzA5i9ko5yZQesbuXPHzJPYkaI8H+ewh0hSU5DPMOPxKJNsXMg+WK7orExQEpkkYTZ559AgMBAAECgYB9KYzgz536Uc8YcGR8XIXn9dhJB7BibhRbVBjaR37ycosaVUB6Dm4Evhv3GqaVUjzlZui6oqfGZ7WnBKpBU5gMTkY2HmoKnufh7SpkULb4gCVnmT2/+wkrfMNZD+7fekVlqTbABgVYduK+0noLJ2BbdcBLTWHESet/MSTrd4JDAQJBANRPEOhxkFiLBILWvsToNs8/YArpHT3P3TJR2DprKNNhbgSwo/U6CrgTZXj/onKMH1bAmNa3F/voLJYePn4JhiUCQQDLLiJQ50shJlFbrb6E0Qe14wxaA2BomKGnsFgj66nk4Ij9+XaQAl8OhpYDAXAqS448vMXr+cOVPjfWnlQWMyt5AkAa9ML/0FN0eoojFqL4G0fCPpiyKfD1hSvflLawjCSN2iP+4nKe0zTDNGtA1qxIgPQFrsR5Fpwr9smacdKbbBglAkBYfwqhVGzLzoXHOcDu1qDWH0Ok/S2DwV8/y7ZIRwAj2YyxmnOCvBWtHP+/5WN2eJxGZi6K3qnKmmP1zdZwKO95AkEAhlPu+jOV/MddPRs4odQQrSFoNuRsH3QrGpH6e8EfD3kzPHWsG+qAoN+Pe0g32SQQStymtjtuiyasEXVYXP6bOA==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	private int errCode;

	private void sendPayResult() {
		Intent intent = new Intent(WXPayEntryActivity.PAY_RECEIVER);
		intent.putExtra("error", errCode);
		intent.putExtra("type", WXPayEntryActivity.PAY_CHANNEL_ALIPAY);
		activity.sendBroadcast(intent);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				try {
					paySuc(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
			case SDK_CHECK_FLAG: {
				ToastUtil.show(activity, "检查结果为：" + msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};


	private void paySuc(Message msg){
		PayResult payResult = new PayResult((String) msg.obj);



		// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
		String resultInfo = payResult.getResult();
		//ToastUtil.show(activity, "resultInfo----->"+resultInfo+"---->");

		String resultStatus = payResult.getResultStatus();

		//ToastUtil.show(activity, payResult.getResultStatus());

		// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
		if (TextUtils.equals(resultStatus, "9000")) {
			errCode = 1;
			send(resultInfo);
			
//			String [] rstArr = resultInfo.split("&");
//			String rstStr = "";
//			try {
//				for(int i=0;i<rstArr.length;i++){
//					if(i<rstArr.length-1){
//						rstStr += rstArr[i].split("=")[0]+"="+URLEncoder.encode(rstArr[i].split("=")[1],"utf-8")+"&" ;
//					}else{
//						rstStr +="sign="+URLEncoder.encode(rstArr[i].split("sign=")[1],"utf-8");
//					}
//				}
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			HttpUtil.get(url+"?"+rstStr);
//			send(resultInfo.replace("/\\+/g", "%2B"));
			ToastUtil.show(activity, "支付成功");
		} else {
			// 判断resultStatus 为非“9000”则代表可能支付失败
			// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
			if (TextUtils.equals(resultStatus, "8000")) {
				ToastUtil.show(activity, "支付结果确认中");

			} else {
				errCode = 0;
				// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
				ToastUtil.show(activity, "支付失败");
				sendPayResult();
			}
		}
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(final String payInfo) {
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(activity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(activity);
		String version = payTask.getVersion();
		ToastUtil.show(activity, version);
	}
	/**
	 * 告诉服务器支付成功
	 * @param str
	 */
	private void send(String str) {
		activity.showLoadingDialog("");
	//	ToastUtil.show(activity, "url----->" + url +"?" + str);
		AppLog.LogD("PayDemo -- url --->", url +"?" + str);
		
		String [] rstArr = str.split("&");
		String rstStr = "";
		try {
			for(int i=0;i<rstArr.length;i++){
				if(i<rstArr.length-1){
					rstStr += rstArr[i].split("=")[0]+"="+URLEncoder.encode(rstArr[i].split("=")[1],"utf-8")+"&" ;
				}else{
					rstStr +="sign="+URLEncoder.encode(rstArr[i].split("sign=")[1],"utf-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new MyHttpUtils().send(HttpMethod.POST, url + "?" + rstStr, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				sendPayResult();
				activity.cancelLoadingDialog(); 
				//ToastUtil.show(activity, "onFailure--->"+arg0.toString());

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				sendPayResult();
				activity.cancelLoadingDialog();
				//ToastUtil.show(activity, "onSuccess--->"+arg0.toString());
			}

			@Override
			public void onSuccessPre(ResponseInfo<String> arg0) {

			}
		});


	}
}
