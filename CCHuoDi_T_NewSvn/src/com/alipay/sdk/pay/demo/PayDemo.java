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

	// �̻�PID
	public static final String PARTNER = "2088221727003495";
	// �̻��տ��˺�
	public static final String SELLER = "mdjcycp@163.com";
	//	// �̻�˽Կ��pkcs8��ʽ
	//	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPcUxSZoSwTizaQP7UUZqeVa+Syi4/FUNdq37sGd8MOAr9lQMpL75mXRoPUASDJ4FoioXnvr4ngOYxHZI4H+kXHLdlGjdGRnCxCr6C3Cym0B+tp0XNk8JaQfsrpOAFkL5jAWwIgZybuJijrn9WNTlrjCEDAQZK3/iXOyTObAIdgvAgMBAAECgYEAgMZ9fmgK+W/v99spMcOPbe99QMtg/A2EyArnUFgt/OccP6nxwfOyAE1Ck47jEhLOw8jk/bdSYHo3Hj1KqVzxlhRiBxvwMSBNl9AQn/leoKEgmhHTJFSfc7UY59TXMXQCzDMAwG1uStf4I63AIQtksF165MK3xUQjuwQJqlwKqHkCQQD/Cg1WSy4rf/qPnURmvxSWCxtEQDkLK6dV0vwYR+w3VVlKYr/LR7CO4I75bwRFru8eFPHDm/bMCMquC8gffdo7AkEA+AMLJ16zp4fRWSeWmC+D7F2gbRKt0ZG9YN/sZQzG5NPe+gR+XntD+aTD4OLC44B3Uuh/Ps1gpPV88QCrOYbmnQJARVvXerNhcAUTAEGV25d1osSMesg4ezg4/tctJfFRZU75xIgMft0VQBYD7APsFoNXlke95COjP6PYcvLTTvUsBwJBAPBWWrezQRvHNStVfs1JtmkWrgElCcY9mizHspiWEiPr4XedZSx//XZtodhLRxsaAggZAD5G3jHdJ0+RG1yrCs0CQDN9dj8NZK1rq5ZY4RwgMFjV58UpreW6bmg/ZrhgN1bmzDRdGsustMd7IXTlxXKMkcA4mgKRgPZ5AF9+twHwfpI=";
	//	// ֧������Կ
	//	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	// �̻�˽Կ��pkcs8��ʽ
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKiA9RL0l6zIeWK8tnxINIcbz1xCxgmrDY3dWxMPUfbAli0EeWmMr0h3pxcMJXFXup+i2ID24iKp3FFAKhooGcDIhW1OTJQAlZ2AAejrzA5i9ko5yZQesbuXPHzJPYkaI8H+ewh0hSU5DPMOPxKJNsXMg+WK7orExQEpkkYTZ559AgMBAAECgYB9KYzgz536Uc8YcGR8XIXn9dhJB7BibhRbVBjaR37ycosaVUB6Dm4Evhv3GqaVUjzlZui6oqfGZ7WnBKpBU5gMTkY2HmoKnufh7SpkULb4gCVnmT2/+wkrfMNZD+7fekVlqTbABgVYduK+0noLJ2BbdcBLTWHESet/MSTrd4JDAQJBANRPEOhxkFiLBILWvsToNs8/YArpHT3P3TJR2DprKNNhbgSwo/U6CrgTZXj/onKMH1bAmNa3F/voLJYePn4JhiUCQQDLLiJQ50shJlFbrb6E0Qe14wxaA2BomKGnsFgj66nk4Ij9+XaQAl8OhpYDAXAqS448vMXr+cOVPjfWnlQWMyt5AkAa9ML/0FN0eoojFqL4G0fCPpiyKfD1hSvflLawjCSN2iP+4nKe0zTDNGtA1qxIgPQFrsR5Fpwr9smacdKbbBglAkBYfwqhVGzLzoXHOcDu1qDWH0Ok/S2DwV8/y7ZIRwAj2YyxmnOCvBWtHP+/5WN2eJxGZi6K3qnKmmP1zdZwKO95AkEAhlPu+jOV/MddPRs4odQQrSFoNuRsH3QrGpH6e8EfD3kzPHWsG+qAoN+Pe0g32SQQStymtjtuiyasEXVYXP6bOA==";
	// ֧������Կ
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
				ToastUtil.show(activity, "�����Ϊ��" + msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};


	private void paySuc(Message msg){
		PayResult payResult = new PayResult((String) msg.obj);



		// ֧�������ش˴�֧���������ǩ�������֧����ǩ����Ϣ��ǩԼʱ֧�����ṩ�Ĺ�Կ����ǩ
		String resultInfo = payResult.getResult();
		//ToastUtil.show(activity, "resultInfo----->"+resultInfo+"---->");

		String resultStatus = payResult.getResultStatus();

		//ToastUtil.show(activity, payResult.getResultStatus());

		// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬�������ɲο��ӿ��ĵ�
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
			ToastUtil.show(activity, "֧���ɹ�");
		} else {
			// �ж�resultStatus Ϊ�ǡ�9000����������֧��ʧ��
			// ��8000������֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
			if (TextUtils.equals(resultStatus, "8000")) {
				ToastUtil.show(activity, "֧�����ȷ����");

			} else {
				errCode = 0;
				// ����ֵ�Ϳ����ж�Ϊ֧��ʧ�ܣ������û�����ȡ��֧��������ϵͳ���صĴ���
				ToastUtil.show(activity, "֧��ʧ��");
				sendPayResult();
			}
		}
	}

	/**
	 * call alipay sdk pay. ����SDK֧��
	 * 
	 */
	public void pay(final String payInfo) {
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// ����PayTask ����
				PayTask alipay = new PayTask(activity);
				// ����֧���ӿڣ���ȡ֧�����
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// �����첽����
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * ��ѯ�ն��豸�Ƿ����֧������֤�˻�
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// ����PayTask ����
				PayTask payTask = new PayTask(activity);
				// ���ò�ѯ�ӿڣ���ȡ��ѯ���
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
	 * get the sdk version. ��ȡSDK�汾��
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(activity);
		String version = payTask.getVersion();
		ToastUtil.show(activity, version);
	}
	/**
	 * ���߷�����֧���ɹ�
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
