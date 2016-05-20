package cn.com.easytaxi.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.com.easytaxi.AppLog;

public final class HttpUtil {
	private static Map<String, String> cacheMap = new HashMap<String, String>();

	public static void sendMsg(final String url,
			final List<NameValuePair> paramList, final Callback<Object> callback) {
		sendMsg(url, paramList, false, callback);
	}

	public static void sendMsg(final String url,
			final List<NameValuePair> paramList, final boolean cache,
			final Callback<Object> callback) {
		String cacheKey = url;
		AppLog.LogD("MY", "url--->" + url);
		try {
			if (!cache) {// 清理陈旧缓存，建立新缓存
				cacheMap = new HashMap<String, String>();
			} else { // 从缓存中读取数据
				if (paramList != null) {
					StringBuffer buf = new StringBuffer();
					for (NameValuePair nameValuePair : paramList) {
						buf.append("/");
						buf.append(nameValuePair.getName());
						buf.append("/");
						buf.append(nameValuePair.getValue());
					}
					cacheKey += buf.toString();
				}

				String data = cacheMap.get(cacheKey);
				if (data != null) {
					if (callback != null) {
						JSONObject json = new JSONObject(data);
						callback.isCache = true;
						callback.handle(json.get("data"));
						callback.complete();
					}
					// AppLog.LogD("MY", "读取缓存数据 catchKey:" + cacheKey);
					return;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		final Handler handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				Object o = msg.obj;
				try {
					if (o instanceof Throwable) {
						if (callback != null) {
							callback.isCache = false;
							callback.error((Throwable) o);
						}
					} else {
						if (callback != null) {
							callback.isCache = false;
							callback.handle(o);
						}
					}
				} finally {
					if (callback != null) {
						callback.complete();
					}
				}

			}
		};

		final String cacheKeyTmp = cacheKey;
		new Thread() {
			public void run() {
				try {
					HttpPost post = new HttpPost(url);
					if (paramList != null) {
						post.setEntity(new UrlEncodedFormEntity(paramList,
								HTTP.UTF_8));
						for (NameValuePair nameValuePair : paramList) {
//							AppLog.LogD("MY", nameValuePair.getName() + "--->"
//									+ nameValuePair.getValue());
						}
					}
					HttpResponse response = new DefaultHttpClient()
							.execute(post);
					if (response.getStatusLine().getStatusCode() == 200) {
						String data = EntityUtils.toString(
								response.getEntity(), HTTP.UTF_8);
						//Log.d("MY", "data--->" + data);
						JSONObject json = new JSONObject(data);
						if (json.getInt("status") == 1) {
							if (cache) { // 缓存数据(只有在正确数据的时候才缓存)
								cacheMap.put(cacheKeyTmp, data);
							}
							handler.obtainMessage(0, json.get("data"))
									.sendToTarget();
						} else {
							throw new Throwable(json.getString("info"));
						}
					} else {
						throw new Throwable("请求失败："
								+ response.getStatusLine().getStatusCode());
					}
				} catch (Throwable e) {
					handler.obtainMessage(0, e).sendToTarget();
				}
			};
		}.start();
	}
}
