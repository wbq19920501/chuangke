package cn.com.easytaxi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;

/**
 * User: qii Date: 12-8-28
 */
public class TimeTool {
	public final static SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public final static SimpleDateFormat NO_YEAY_DATE_FORMATTER = new SimpleDateFormat("MM-dd HH:mm");
//	public final static SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static int MILL_MIN = 1000 * 60;
	private static int MILL_HOUR = MILL_MIN * 60;
	private static int MILL_DAY = MILL_HOUR * 24;

	private static String JUST_NOW = ETApp.getInstance().getString(R.string.justnow);
	private static String MIN = ETApp.getInstance().getString(R.string.min);
	private static String HOUR = ETApp.getInstance().getString(R.string.hour);
	private static String DAY = ETApp.getInstance().getString(R.string.day);
	private static String MONTH = ETApp.getInstance().getString(R.string.month);
	private static String YEAR = ETApp.getInstance().getString(R.string.year);

	private static String YESTER_DAY = ETApp.getInstance().getString(R.string.yesterday);
	private static String THE_DAY_BEFORE_YESTER_DAY = ETApp.getInstance().getString(R.string.the_day_before_yesterday);
	private static String TODAY = ETApp.getInstance().getString(R.string.today);

	private static String DATE_FORMAT = ETApp.getInstance().getString(R.string.date_format);
	private static String YEAR_FORMAT = ETApp.getInstance().getString(R.string.year_format);

	private static Calendar msgCalendar = null;
	private static java.text.SimpleDateFormat dayFormat = null;
	private static java.text.SimpleDateFormat dateFormat = null;
	private static java.text.SimpleDateFormat yearFormat = null;

	public static String getListTime(long time) {
		long now = System.currentTimeMillis();
		long msg = time;

		Calendar nowCalendar = Calendar.getInstance();

		if (msgCalendar == null)
			msgCalendar = Calendar.getInstance();

		msgCalendar.setTimeInMillis(time);

		long calcMills = now - msg;

		long calSeconds = calcMills / 1000;

		if (calSeconds < 60) {
			return JUST_NOW;
		}

		long calMins = calSeconds / 60;

		if (calMins < 60) {

			return new StringBuilder().append(calMins).append(MIN).toString();
		}

		long calHours = calMins / 60;

		if (calHours < 24 && isSameDay(nowCalendar, msgCalendar)) {
			if (dayFormat == null)
				dayFormat = new java.text.SimpleDateFormat("HH:mm");

			String result = dayFormat.format(msgCalendar.getTime());
			return new StringBuilder().append(TODAY).append(" ").append(result).toString();

		}

		long calDay = calHours / 24;

		if (calDay < 31) {
			if (isYesterDay(nowCalendar, msgCalendar)) {
				if (dayFormat == null)
					dayFormat = new java.text.SimpleDateFormat("HH:mm");

				String result = dayFormat.format(msgCalendar.getTime());
				return new StringBuilder(YESTER_DAY).append(" ").append(result).toString();

			} else if (isTheDayBeforeYesterDay(nowCalendar, msgCalendar)) {
				if (dayFormat == null)
					dayFormat = new java.text.SimpleDateFormat("HH:mm");

				String result = dayFormat.format(msgCalendar.getTime());
				return new StringBuilder(THE_DAY_BEFORE_YESTER_DAY).append(" ").append(result).toString();

			} else {
				if (dateFormat == null)
					dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);

				String result = dateFormat.format(msgCalendar.getTime());
				return new StringBuilder(result).toString();
			}
		}

		long calMonth = calDay / 31;

		if (calMonth < 12) {
			if (dateFormat == null)
				dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);

			String result = dateFormat.format(msgCalendar.getTime());
			return new StringBuilder().append(result).toString();

		}
		if (yearFormat == null)
			yearFormat = new java.text.SimpleDateFormat(YEAR_FORMAT);
		String result = yearFormat.format(msgCalendar.getTime());
		return new StringBuilder().append(result).toString();

	}

	private static boolean isSameHalfDay(Calendar now, Calendar msg) {
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		int msgHOur = msg.get(Calendar.HOUR_OF_DAY);

		if (nowHour <= 12 & msgHOur <= 12) {
			return true;
		} else if (nowHour >= 12 & msgHOur >= 12) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isSameDay(Calendar now, Calendar msg) {
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int msgDay = msg.get(Calendar.DAY_OF_YEAR);

		return nowDay == msgDay;
	}

	private static boolean isYesterDay(Calendar now, Calendar msg) {
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int msgDay = msg.get(Calendar.DAY_OF_YEAR);

		return (nowDay - msgDay) == 1;
	}

	private static boolean isTheDayBeforeYesterDay(Calendar now, Calendar msg) {
		int nowDay = now.get(Calendar.DAY_OF_YEAR);
		int msgDay = msg.get(Calendar.DAY_OF_YEAR);

		return (nowDay - msgDay) == 2;
	}

	
	/**
	 * 
	 * @param 要转换的毫秒数
	 * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
	 * @author fy.zhang
	 */
	public static HashMap<String, String> formatDuring(long mss) {
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("days",""+days);
		map.put("hours",""+hours);
		map.put("minutes",""+minutes);
		map.put("seconds",""+seconds);
		return map;
		
	}
	
	
	/**
	 * 
	 * @param begin 时间段的开始
	 * @param end	时间段的结束
	 * @return	输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
	 * @author fy.zhang
	 */
	public static HashMap<String, String> formatDuring(Date begin, Date end) {
		return formatDuring(end.getTime() - begin.getTime());
	}
	
	
	public static String getDateString(long time){
		return TimeTool.DEFAULT_DATE_FORMATTER.format(new Date(time));
	}
}
