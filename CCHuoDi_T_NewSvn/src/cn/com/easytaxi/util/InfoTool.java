package cn.com.easytaxi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.res.Resources;
import android.text.TextUtils;
import cn.com.easytaxi.AppLog;
import com.easytaxi.etpassengersx.R;

public class InfoTool {

	/** yyyy-MM-dd */
	public final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
	/** yyyy-MM-dd HH:mm:ss */
	private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static boolean isTheSameDay(String date1, String date2) {
		boolean isSame = false;
		Date date_1 = toDate(date1);
		Date date_2 = toDate(date2);

		long lt = date_1.getTime() / 86400000;
		long ct = date_2.getTime() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			isSame = true;
		}
		return isSame;
	}

	public static boolean isToday(String date) {
		if (TextUtils.isEmpty(date)) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		String date2 = cal.getTime().toString();
		return isTheSameDay(date, date2);
	}

	/**
	 * 将传入的数字转换成以逗号分隔的形式，如传入123456789，转换成123,456,789
	 * 
	 * @author
	 * @param number
	 * @return
	 */
	public static String getShowNumber(long number) {
		// 如果是负数，取正
		boolean bellowzero = false;
		if (number < 0) {
			bellowzero = true;
			number = -number;
		}
		StringBuilder sb = new StringBuilder(number + "");
		// 根据数字的大小决定循环几次
		int count = (sb.length() - 1) / 3;
		int mod = sb.length() % 3 == 0 ? 3 : sb.length() % 3;
		for (int i = 1; i <= count; i++) {
			sb.insert(mod + 3 * (count - i), ",");
		}
		if (bellowzero) {
			sb.insert(0, "-");
		}
		return sb.toString();
	}

	public static String friendTime(long timout) {

		if (timout <= 0) {
			return "0秒";
		}

		if (timout <= 1000 * 60) {
			return (int) (timout / 1000) + "秒";
		}

		int miao = (int) (timout / 1000);

		int fen = (int) (miao / 60);
		int miaoDip = (int) (miao % 60);

		return fen + "分" + miaoDip + "秒";
	}

	public static String friendlyDistance(int distance) {
		if (distance < 1000) {
			return distance + "米";
		} else {
			String i = distance / 1000 + "公里";
			return i;
		}
	}

	public static String spliteWiht4hour(String sdate) {
		String priceValue = "5";

		Date time = toDate(sdate);

		String ftime = "";
		Calendar cal = Calendar.getInstance();

		String curDate = dateFormater2.format(cal.getTime()); // 当前
		String paramDate = dateFormater2.format(time);

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;

		long dTime = Math.abs(cal.getTimeInMillis() - time.getTime());

		if (dTime > 3600000) {
			priceValue = "6";
		} else {
			priceValue = "5";
		}
		return priceValue;
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String friendlyDate(Date date) {
		try {
			return dateFormater.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	public static String friendlyDate(Resources resource, long sdate) {
		Date d = new Date(sdate);
		String format = dateFormater.format(d);
		AppLog.LogD(format.toString());
		return friendlyDate(resource, format);
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendlyDate(Resources resource, String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}

		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + resource.getString(R.string.time_minutes_ago);
			else
				ftime = hour + resource.getString(R.string.time_hours_ago);
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + resource.getString(R.string.time_minutes_ago);
			else
				ftime = hour + resource.getString(R.string.time_hours_ago);
		} else if (days == 1) {
			ftime = resource.getString(R.string.time_yesterday);
		} else if (days == 2) {
			ftime = resource.getString(R.string.time_before_yesterday);
		} else if (days > 2 && days <= 10) {
			ftime = days + resource.getString(R.string.time_days_ago);
		} else if (days > 10) {
			ftime = dateFormater2.format(time);
		}
		return ftime;
	}

}
