package cn.com.easytaxi.ui;

import cn.com.easytaxi.AppLog;

import com.xc.lib.xutils.HttpUtils;
import com.xc.lib.xutils.http.HttpHandler;
import com.xc.lib.xutils.http.RequestParams;
import com.xc.lib.xutils.http.callback.RequestCallBack;
import com.xc.lib.xutils.http.client.HttpRequest.HttpMethod;

public class MyHttpUtils extends HttpUtils {
	private static MyHttpUtils instance;

	public static MyHttpUtils getInstance() {
		if (instance == null)
			instance = new MyHttpUtils();
		return instance;
	}

	public <T> HttpHandler<T> send(String url, String params, RequestCallBack<T> callBack) {
		AppLog.LogD("xxb", "request - >" + params);
		RequestParams _params = new RequestParams();
		_params.addBodyParameter("params", params);
		return send(HttpMethod.POST, url, _params, callBack);
	}

}
