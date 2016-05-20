package cn.com.easytaxi.platform.common;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SDInfo;
import cn.com.easytaxi.common.Window;

public final class Util {
	public static String REGEX_MOBILE = "^1[3|4|5|7|8][0-9]{9}$";
	public static String REGEX_ZH_NAME = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]{2,4}$";
	public static String REGEX_CARD_ID = "^\\d{14}\\d{3}?\\w$";
	public static String REGEX_TAXI_NUM = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D][A-Za-z][a-zA-Z0-9]{5}$";
	public static String REGEX_COMPANY = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D0-9a-zA-Z]{2,20}$";

	public static boolean isUpdating = false;

	public static Bitmap scale(Bitmap bitmap, int newWidth, int newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	public static void asynUploadImage(final String url, final List<NameValuePair> paramList, final List<Bitmap> bitmaps,
			final Callback<Object> callback) {
		new AsyncTask<Object, Throwable, Object>() {

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				if (result != null && callback != null) {
					callback.handle(result);
				}
			}

			@Override
			protected void onProgressUpdate(Throwable... values) {
				super.onProgressUpdate(values);

				if (callback != null) {
					callback.error(values[0]);
				}
			}

			@Override
			protected Object doInBackground(Object... params) {
				try {
					AppLog.LogD("url--->" + url);
					for (NameValuePair nameValuePair : paramList) {
						AppLog.LogD(nameValuePair.getName() + "--->" + nameValuePair.getValue());
					}
					return uploadImage(url, paramList, bitmaps);
				} catch (Throwable e) {
					publishProgress(e);
				}
				return null;
			}
		}.execute();
	}

	public static Object uploadImage(String actionUrl, List<NameValuePair> paramList, List<Bitmap> bitmaps) throws Throwable {

		DataOutputStream outStream = null;
		InputStream in = null;
		HttpURLConnection conn = null;

		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";

			URL uri = new URL(actionUrl);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(5 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (NameValuePair param : paramList) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\"" + param.getName() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(param.getValue());
				sb.append(LINEND);
			}

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());

			// 发送文件数据
			for (Bitmap bitmap : bitmaps) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				// name是post中传参的键 filename是文件的名称
				sb1.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + UUID.randomUUID().toString() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				bitmap.compress(CompressFormat.PNG, 100, outStream);
				outStream.write(LINEND.getBytes());
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			if (res == 200) {
				in = conn.getInputStream();
				int ch;
				StringBuilder sb2 = new StringBuilder();
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}

				JSONObject json = new JSONObject(sb2.toString());
				if (json.getInt("status") == 1) {
					return json.get("data");
				} else {
					throw new Throwable(json.getString("info"));
				}
			} else {
				throw new Throwable("与服务器通讯异常");
			}
		} finally {
			if (in != null)
				in.close();
			if (outStream != null)
				outStream.close();
			if (conn != null)
				conn.disconnect();
		}
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 * @throws Throwable
	 */
	public static String uploadImage(Bitmap bitmap, String RequestURL) throws Throwable {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		URL url = new URL(RequestURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(10000);
		conn.setDoInput(true); // 允许输入流
		conn.setDoOutput(true); // 允许输出流
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST"); // 请求方式
		conn.setRequestProperty("Charset", "utf-8"); // 设置编码
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(BOUNDARY);
		sb.append(LINE_END);
		/**
		 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
		 * 比如:abc.png
		 */
		sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + BOUNDARY + "\"" + LINE_END);
		sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
		sb.append(LINE_END);

		dos.write(sb.toString().getBytes());
		bitmap.compress(CompressFormat.JPEG, 100, dos);
		dos.write(LINE_END.getBytes());
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
		dos.write(end_data);
		dos.flush();
		/**
		 * 获取响应码 200=成功 当响应成功，获取响应的流
		 */
		int res = conn.getResponseCode();
		if (res == 200) {
			InputStream input = conn.getInputStream();
			StringBuffer sb1 = new StringBuffer();
			int ss;
			while ((ss = input.read()) != -1) {
				sb1.append((char) ss);
			}
			result = sb1.toString();
		} else {
			throw new Throwable("服务端响应错误：错误代码" + res);
		}
		return result;
	}

	// public static void download(final Context context, final String fileName,
	// final int size, final Callback<String> callback1,
	// final Callback<Integer> callback2) {
	//
	// new AsyncTask<Object, Integer, String>() {
	// @Override
	// protected void onProgressUpdate(Integer... values) {
	// int progress = values[0];
	// if (callback2 != null) {
	// callback2.handle(progress);
	// }
	// }
	//
	// @Override
	// protected void onPostExecute(String file) {
	// if (file == null || file.equals("")) {
	// if (callback1 != null) {
	// callback1.error(new Throwable("下载升级包失败"));
	// }
	// } else {
	// if (callback1 != null) {
	// callback1.handle(file);
	// }
	// }
	// }
	//
	// @Override
	// protected String doInBackground(Object... params) {
	// File file = null;
	// InputStream in = null;
	// FileOutputStream out = null;
	//
	// // 检查内存卡或手机空间等
	// if (!checkSpace(context, true)) {
	// return "";
	// }
	//
	// if (SDInfo.checkSdcard()) {// SD卡
	// file = new File(Config.storePath + fileName);
	// File dir = file.getParentFile();
	// if (dir != null && !dir.exists()) {
	// if (!dir.mkdirs()) {// 创建文件夹失败
	// return "";
	// }
	// }
	//
	// if (file.exists()) {
	// if (!file.delete()) {
	// return "";
	// } else {
	// try {
	// file.createNewFile();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return "";
	// }
	// }
	// } else {
	// try {
	// file.createNewFile();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return "";
	// }
	// }
	//
	// try {
	// out = new FileOutputStream(file);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return "";
	// }
	// } else {// 内存
	// file = new File(Config.memoryPath + fileName);
	// try {
	// out = SDInfo.saveInMemory(context, fileName);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return "";
	// }
	// }
	//
	// HttpURLConnection httpConn = null;
	// try {
	// URL url = new URL(Const.REQUEST_DOWNLOAD + fileName);
	// httpConn = (HttpURLConnection) url.openConnection();
	// httpConn.setRequestMethod("GET");
	// httpConn.connect();
	// in = httpConn.getInputStream();
	// byte[] buf = new byte[size];
	// int count = 0;
	// while (count < size) {
	// count += in.read(buf, count, size - count);
	// publishProgress(count);
	// }
	// out.write(buf);
	// out.flush();
	//
	// } catch (Throwable e) {
	// e.printStackTrace();
	// return "";
	// } finally {
	// try {
	// if (in != null) {
	// in.close();
	// }
	// if (out != null) {
	// out.close();
	// }
	// if (httpConn != null) {
	// httpConn.disconnect();
	// }
	// } catch (Throwable e) {
	// e.printStackTrace();
	// return "";
	// }
	// }
	// return file.getAbsolutePath();
	// }
	// }.execute();
	// }

	public static boolean checkSpace(final Context ctx, boolean notifiy) {
		int state = -1;
		if (SDInfo.hasSDCard()) {// 有sd卡，检查sd卡空间
			if (SDInfo.hasEnoughAvailableSize()) {// sd卡空间足够
				return true;
			} else {// sd卡空间不足
				state = 0;
			}
		} else {// 无卡
			state = 1;
		}

		// 无卡或卡空间不足，检查手机空间
		if (SDInfo.hasEnoughAvailableSizeInternal()) {// 手机空间足够
			return true;
		}
		if (notifiy) {
			String msg0 = null;
			if (state == 0) {
				msg0 = "内存卡空间不足，不能下载";
			} else if (state == 1) {
				msg0 = "手机空间不足，不能下载";
			}
			final String msg = msg0;
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					Window.alert(ctx, msg);
				}
			});
		}
		return false;
	}



	public static boolean hasNull(Object[] objects) {
		for (Object object : objects) {
			if (object == null)
				return true;
		}
		return false;
	}

	public static boolean validate(TextView[] objects, String[] tips) {
		boolean suc = true;
		for (int i = objects.length - 1; i >= 0; i--) {// 逆序遍历，让第一个聚焦
			TextView object = objects[i];
			if (object == null || object.getText() == null || object.getText().toString().trim().equals("")) {
				if (tips != null) {
					Toast.makeText(object.getContext(), tips[i], Toast.LENGTH_SHORT).show();
				}
				object.requestFocus();
				suc = false;
				break;
			} else {
				object.setError(null);
			}
		}
		return suc;
	}

	public static boolean checkCityUpdate(String older, String newer) {
		int old = 0;
		int ne = 0;

		try {
			old = Integer.valueOf(older);
		} catch (Exception e) {
			return true;
		}

		try {
			ne = Integer.valueOf(newer);
		} catch (Exception e) {
			return false;
		}

		return old < ne ? true : false;
	}
	
	
	public static int dip2px(int dipValue) {
		float reSize = ETApp.getInstance().getResources().getDisplayMetrics().density;
		return (int) ((dipValue * reSize) + 0.5);
	}

	public static int px2dip(int pxValue) {
		float reSize = ETApp.getInstance().getResources().getDisplayMetrics().density;
		return (int) ((pxValue / reSize) + 0.5);
	}

	public static float sp2px(int spValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, ETApp.getInstance().getResources().getDisplayMetrics());
	}
}
