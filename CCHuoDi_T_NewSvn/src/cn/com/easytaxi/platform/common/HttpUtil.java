package cn.com.easytaxi.platform.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.com.easytaxi.common.Callback;

public class HttpUtil {

	public static String post(String server, File file) throws Exception {
		Map<String, File> fileMap = new TreeMap<String, File>();
		fileMap.put(file.getName(), file);
		return post(server, null, fileMap);
	}

	public static String post(String server, List<NameValuePair> paramList, Map<String, File> fileMap) throws Exception {
		System.out.println(server);

		URL url = new URL(server);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

		HttpPost httpPost = new HttpPost(url.toURI());

		MultipartEntity m = new MultipartEntity();
		if (fileMap != null) {
			for (Entry<String, File> entry : fileMap.entrySet()) {
				m.addPart(entry.getKey(), new FileBody(entry.getValue()));
			}
		}
		if (paramList != null) {
			for (NameValuePair nv : paramList) {
				m.addPart(nv.getName(), new StringBody(nv.getValue(), Charset.forName("UTF-8")));
			}
		}

		httpPost.setEntity(m);

		HttpResponse response = httpclient.execute(httpPost);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("HTTP请求异常");
		}
		String rst = EntityUtils.toString(response.getEntity(), "UTF-8");
		return rst;
	}

	public static JSONObject postJsonObject(String server, List<NameValuePair> paramList, Map<String, File> fileMap) throws Exception {
		return new JSONObject(post(server, paramList, fileMap));
	}

	public static JSONObject postJsonObject(String server, List<NameValuePair> paramList) throws Exception {
		return new JSONObject(post(server, paramList));
	}

	public static JSONArray postJsonArray(String server, List<NameValuePair> paramList) throws Exception {
		return new JSONArray(post(server, paramList));
	}

	public static String post(String server, List<NameValuePair> paramList) throws Exception {
		return post(server, paramList, null);
	}

	public static byte[] getBytes(URL url) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url.toURI());
		HttpResponse response = httpclient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("HTTP请求异常");
		}
		return EntityUtils.toByteArray(response.getEntity());
	}

	public static byte[] getBytes(String url) throws Exception {
		return getBytes(new URL(url));
	}

	public static void getBytes(final String url, final Callback<String> callback) {
		final Handler handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				Object result = msg.obj;
				try {
					if (result instanceof Throwable) {
						if (callback != null) {
							callback.error((Throwable) result);
						}
					} else {
						if (callback != null) {
							callback.handle((String) result);
						}
					}
				} finally {
					if (callback != null) {
						callback.complete();
					}
				}
			}
		};

		try {
			String result = new String(getBytes(new URL(url)), "utf-8");
			handler.obtainMessage(0, result).sendToTarget();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.obtainMessage(0, e).sendToTarget();
		}
	}
}
