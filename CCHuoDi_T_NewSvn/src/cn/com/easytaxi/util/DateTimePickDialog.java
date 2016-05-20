package cn.com.easytaxi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import cn.com.easytaxi.common.Callback;

import com.easytaxi.etpassengersx.R;

public class DateTimePickDialog implements OnDateChangedListener, OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog mAlertDialog;
	private String dateTime;
	private Activity activity;

	/**
	 * 设置后的时间
	 */
	private Calendar mCalendar;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialog(Activity activity, String initDateTime) {
		this.activity = activity;
		this.dateTime = initDateTime;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		mCalendar = Calendar.getInstance();
		if (!(null == dateTime || "".equals(dateTime))) {
			mCalendar = this.getCalendarByInintData(dateTime);
		} else {
			dateTime = mCalendar.get(Calendar.YEAR) + "年" + mCalendar.get(Calendar.MONTH) + "月" + mCalendar.get(Calendar.DAY_OF_MONTH) + "日 " + mCalendar.get(Calendar.HOUR_OF_DAY) + ":" + mCalendar.get(Calendar.MINUTE);
		}

		datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final Callback<Calendar> callback) {
		LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		mAlertDialog = new AlertDialog.Builder(activity).setTitle(dateTime).setView(dateTimeLayout).setPositiveButton("设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				callback.handle(mCalendar);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// inputDate.setText("");
			}
		}).show();

		onDateChanged(null, 0, 0, 0);
		return mAlertDialog;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		mCalendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		dateTime = sdf.format(mCalendar.getTime());
		mAlertDialog.setTitle(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "日", "index", "front"); // 日期
		String time = spliteString(initDateTime, "日", "index", "back"); // 时间

		String yearStr = spliteString(date, "年", "index", "front"); // 年份
		String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

		String hourStr = spliteString(time, ":", "index", "front"); // 时
		String minuteStr = spliteString(time, ":", "index", "back"); // 分

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay, currentHour, currentMinute);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern, String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

	/**
	 * 判断当前日期是星期几
	 * 
	 * @param calendar
	 * @return
	 */
	public static String getWeekByCalendar(Calendar calendar) {

		String week = "星期";

		if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
			week += "天";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
			week += "一";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 3) {
			week += "二";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
			week += "三";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 5) {
			week += "四";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
			week += "五";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
			week += "六";
		}

		return week;
	}
	
	public static String getDateByCalendar(Calendar calendar){
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
		return format.format(calendar.getTime());
	}
	
	public static String getDateTimeByCalendar(Calendar calendar){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		return format.format(calendar.getTime()) + getTimeByCalendar(calendar);
	}
	
	public static String getTimeByCalendar(Calendar calendar){
		StringBuilder time = new StringBuilder();
		int hour =  calendar.get(Calendar.HOUR_OF_DAY);
		int minute =  calendar.get(Calendar.MINUTE);
		if(hour <10	){
			time.append("0");
		}
		time.append(hour);
		time.append(":");
		if(minute <10	){
			time.append("0");
		}
		time.append(minute);
		return time.toString();
	}
}
