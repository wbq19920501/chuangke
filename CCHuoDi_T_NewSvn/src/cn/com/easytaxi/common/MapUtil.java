package cn.com.easytaxi.common;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;

import com.baidu.mapapi.model.inner.GeoPoint;

public class MapUtil {
	protected static final String tag = "MapUtil";

	public static void reverseGeocode(final int sessionId, final Long id, final GeoPoint point, final Callback<Object> callback) {
		new Thread() {
			public void run() {
				JSONObject json = new JSONObject();
				try {
					json.put("action", "geoCodingAction");
					json.put("method", "getAddressByLocation");
					json.put("lat", point.getLatitudeE6());
					json.put("lng", point.getLongitudeE6());
					byte[] buf = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes());
					final String result = new String(buf);
					
					AppLog.LogD(result);
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						public void run() {
							if (callback != null) {
								callback.handle(new Object[] { sessionId, result });
							}
						}
					});
				} catch (final Exception e) {
					e.printStackTrace();
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						public void run() {
							if (callback != null) {
								callback.error(e);
							}
						}
					});

				} finally {
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						public void run() {
							if (callback != null) {
								callback.complete();
							}
						}
					});

				}
			};
		}.start();
	}

	// public void get(MapView map){
	// Projection projection = map.getProjection();
	// projection.toPixels(arg0, arg1);
	// }

	public static void poiSearchInCity(final Long id, final String city, final String key, final Callback<Object> callback) {

		new Thread() {
			public void run() {
				JSONObject json = new JSONObject();
				try {
					json.put("action", "geoCodingAction");
					json.put("method", "getHotAddress");
					json.put("query", key);
					json.put("region", city);
					AppLog.LogD(tag, "=====get poi " + json.toString());
					byte[] buf = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes());
					final String result = new String(buf);
					AppLog.LogD(tag, result);
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						public void run() {
							if (callback != null) {
								callback.handle(result);
							}
						}
					});
				} catch (final Exception e) {
					e.printStackTrace();
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						public void run() {
							if (callback != null) {
								callback.error(e);
							}
						}
					});

				} finally {
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						public void run() {
							if (callback != null) {
								callback.complete();
							}
						}
					});

				}
			};
		}.start();

	}

	public static String getRoutePlan(long id, int sLng, int sLat, int eLng, int eLat) throws Exception {
		JSONObject json = new JSONObject();
		json.put("action", "geoCodingAction");
		json.put("method", "getRoutePlan");
		json.put("slng", sLng);
		json.put("slat", sLat);
		json.put("elng", eLng);
		json.put("elat", eLat);

		byte[] buf = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, json.toString().getBytes());
		return new String(buf);
	}

	public static void getRoutePlanAsync(final long id, int sLng, int sLat, int eLng, int eLat, final Callback<JSONObject> callBack)
			throws Exception {
		JSONObject json = new JSONObject();
		json.put("action", "geoCodingAction");
		json.put("method", "getRoutePlan");
		json.put("slng", sLng);
		json.put("slat", sLat);
		json.put("elng", eLng);
		json.put("elat", eLat);

		new AsyncTask<JSONObject, Object, String>() {

			@Override
			protected String doInBackground(JSONObject... params) {
				// TODO Auto-generated method stub
				byte[] buf = null;
				try {
					buf = TcpClient.send(id, MsgConst.MSG_TCP_ACTION, params[0].toString().getBytes());
					return new String(buf);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				try {
					callBack.handle(new JSONObject(result));
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					callBack.error(e);
				}

				callBack.complete();
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,json);

	}

	

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @return lat - result[0]; lng - result[1]
	 */
	public static double[] fromBaiduToWgs84(double lat, double lng) {
		// AppLog.LogD("xyw", "fromBaiduToWgs84:baidu--->lat=" + lat + ",lng=" +
		// lng);
		// double[] res = fromWgs84ToBaidu(lat, lng);
		// AppLog.LogD("xyw", "fromBaiduToWgs84:gps--->lat=" + res[0] + ",lng="
		// +
		// res[1]);
		// double[] gps = new double[] { 2 * lat - res[0], 2 * lng - res[1] };
		// AppLog.LogD("xyw", "fromBaiduToWgs84:gps--->lat=" + gps[0] + ",lng="
		// +
		// gps[1]);
		// return gps;
		return new double[] { lat, lng };

		// double logdeviation = 1.0000568461567492425578691530827;//经度偏差
		// double latdeviation = 1.0002012762190961772159526495686;//纬度偏差
		// google地图坐标=百度坐标*经验值
		// double[] res = new double[2];
		// res[0] = lat * 1.0002012762190961772159526495686;
		// res[1] = lng * 1.0000568461567492425578691530827;
		// return res;
	}

	/**
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static double[] fromWgs84ToBaidu(double lat, double lng) {
		// GeoPoint geoPoint =
		// CoordinateConvert.bundleDecode(CoordinateConvert.fromWgs84ToBaidu(new
		// GeoPoint((int) (lat * 1E6), (int) (lng * 1E6))));
		// return new double[] { geoPoint.getLatitudeE6() / 1E6,
		// geoPoint.getLongitudeE6() / 1E6 };
		return new double[] { lat, lng };

		// double[] res = new double[2];
		// res[0] = lat / 1.0002012762190961772159526495686;
		// res[1] = lng / 1.0000568461567492425578691530827;
		// return res;
	}

	/**
	 * @param lat
	 * @param lng
	 * @return
	 */
/*	public static double[] newWgs84ToBaidu(double lat, double lng) {
		GeoPoint geoPoint = CoordinateConvert.bundleDecode(CoordinateConvert.fromWgs84ToBaidu(new GeoPoint((int) (lat * 1E6),
				(int) (lng * 1E6))));
		return new double[] { geoPoint.getLatitudeE6() / 1E6, geoPoint.getLongitudeE6() / 1E6 };
	}*/

	/**
	 * 
	 * @param lat
	 * @param lng
	 * @return lat - result[0]; lng - result[1]
	 */
	public static double[] newBaiduToWgs84(double lat, double lng) {
		AppLog.LogD("xyw", "fromBaiduToWgs84:baidu--->lat=" + lat + ",lng=" + lng);
		double[] res = new double[2];
		res = fromWgs84ToBaidu(lat, lng);
		res = new double[] { 2 * lat - res[0], 2 * lng - res[1] };
		AppLog.LogD("xyw", "fromBaiduToWgs84:gps--->lat=" + res[0] + ",lng=" + res[1]);
		return res;
	}
}
