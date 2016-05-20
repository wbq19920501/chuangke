package cn.com.easytaxi.common;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.util.Log;

import com.easytaxi.etpassengersx.R;

import cn.com.easytaxi.airport.AirportBookBaseFragement;
import cn.com.easytaxi.airport.AirportBookPublishFragement;

public class ToolUtil {

	public static DecimalFormat df1 = new DecimalFormat("#0");

	public static String getDistanceDescp(int meter) {
		if (meter < 1000) {
			return meter + "m";
		} else {
			return df1.format(meter / 1000D) + "km";
		}
	}

	private static HashMap<Integer, MediaPlayer> soundMap = new HashMap<Integer, MediaPlayer>();

	public static void playSound(final Context context, final int resId) {
		playSound(context, resId, null, null);
	}

	public static void playSound(final Context context, final int resId, final OnPreparedListener startlistener,
			final OnCompletionListener endListener) {
		new Thread() {
			@Override
			public void run() {
				try {
					MediaPlayer mediaPlayer = soundMap.get(resId);
					if (mediaPlayer == null) {
						mediaPlayer = MediaPlayer.create(context, resId);
						soundMap.put(resId, mediaPlayer);
					}
					// this.join();
					if (startlistener != null) {

						mediaPlayer.setOnPreparedListener(startlistener);
					}

					if (endListener != null) {
						mediaPlayer.setOnCompletionListener(endListener);
					}

					mediaPlayer.start();

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static int getDistance(int lng1, int lat1, int lng2, int lat2) {
		float[] results = new float[1];
		Location.distanceBetween(lat1 / 1E6, lng1 / 1E6, lat2 / 1E6, lng2 / 1E6, results);
		return (int) results[0];
	}

	public static void createShortCut(Context context, SessionAdapter session) {
		if (session.get("SHORT_CUT") == null) {
			Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.et_name));
			shortcutIntent.putExtra("duplicate", false);
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.easytaxi.etpassengersx", "cn.com.easytaxi.platform.IndexActivity"));

			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
			context.sendBroadcast(shortcutIntent);

			session.set("SHORT_CUT", "YES");
		}
	}

	public static void delShortcut(Context cx) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.et_name);
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("cn.com.easytaxi", "cn.com.easytaxi.platform.IndexActivity"));
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		cx.sendBroadcast(shortcut);
	}

	public static SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static boolean compareTime(long date, long date2) {
		return getTimeDiff(date, date2) > 0;
	}

	public static boolean compareTime(String date, String date2) {
		return getTimeDiff(date, date2) > 0;
	}

	public static boolean compareTime(Date date, Date date2) {
		return getTimeDiff(date, date2) > 0;
	}

	public static long getTimeDiff(String date, String date2) {
		try {
			long c = f.parse(date).getTime();
			long u = f.parse(date2).getTime();
			return c - u;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getTimeDiff(long date, long date2) {
		try {
			return date - date2;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getTimeDiff(Date date, Date date2) {
		try {
			long c = date.getTime();
			long u = date2.getTime();
			return c - u;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String bookNumber(String order) {
		if (order == null) {
			return "";
		}
		int length = order.length();
		if (length >= 10) {
			return "B" + order;
		}
		StringBuilder result = new StringBuilder(order);
		int add = 10 - length;
		for (int i = 0; i < add; i++) {
			result.insert(0, "0");
		}
		result.insert(0, "B");
		return result.toString();
	}

	public static SimpleDateFormat fNoSeconds = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * 旧的时间显示
	 * @param d
	 * @return
	 */
	public static String showTime(Date d) {
		String ttsTime = "";
		try {
			Calendar c = Calendar.getInstance();
			int today = c.get(Calendar.DATE);
			c.setTime(d);
			int date = c.get(Calendar.DATE) - today;
			if (date == 0) {
				ttsTime += "今天";
			} else if (date == 1) {
				ttsTime += "明天";
			}else if (date == 2) {
				ttsTime += "后天";
			}  
			else {
				return fNoSeconds.format(d);
			}
			int ampm = c.get(Calendar.AM_PM);
			int minutes = c.get(Calendar.MINUTE);
			if (ampm == Calendar.AM) {
				ttsTime += "上午" + c.get(Calendar.HOUR_OF_DAY) + "时" + minutes + "分";
			} else {
				ttsTime += "下午" + c.get(Calendar.HOUR_OF_DAY) + "时" + minutes + "分";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return fNoSeconds.format(d);
		}
		return ttsTime;
	}
	
	/**
	 * 新的时间显示
	 * @param d
	 * @return
	 */
	public static String showTime(String dayName,int hour,int minutes) {
		String ttsTime = dayName;
		try {
			if (hour < 12) {
				ttsTime += "上午" + hour + "时" + minutes + "分";
			} else {
				ttsTime += "下午" + hour + "时" + minutes + "分";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return ttsTime;
	}
	
	public static long getLongTime(String[] param) {
		long time = 0;

		Date date = new Date();
		// 设置day
		if (param[0].equals("明天")) {
			date.setDate(date.getDate() + 1);
		} else if(param[0].equals("后天")){
			date.setDate(date.getDate() + 2);
		}

		date.setHours(Integer.parseInt(param[1]));
		date.setMinutes(Integer.parseInt(param[2]));
		date.setSeconds(0);
		time = date.getTime();
		return time;
	}
	
	/**
	 * 新的获取时间方法
	 * @param param
	 * @return
	 */
	public static long getLongTimeNew(int dayChoosedIndex,int hour,int minites,int removeDays) {
		long time = 0;

		Date date = new Date();
		date.setDate(date.getDate()+dayChoosedIndex+removeDays);
		date.setHours(hour);
		date.setMinutes(minites);
		date.setSeconds(0);
		time = date.getTime();
		return time;
	}

	public static String getTimeStr(long time) {
		// return f_d.format(new Date(time - 8 * 3600 * 1000));// 减去时差
		int HH = (int) (time / 3600000);
		int mm = (int) ((time - HH * 3600000) / 60000);
		int ss = (int) (time - HH * 3600000 - mm * 60000) / 1000;
		return HH + "时" + mm + "分" + ss + "秒";
	}
	
	public static boolean isTopActivity(Context context,String packageName) {
		if(TextUtils.isEmpty(packageName)){
			packageName = context.getPackageName();
		}
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			Log.d("xyw", "---------------包名-----------"
					+ tasksInfo.get(0).topActivity.getPackageName());
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}

		return false;
	}

}
