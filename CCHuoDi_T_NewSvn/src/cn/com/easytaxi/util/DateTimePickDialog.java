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
	 * ���ú��ʱ��
	 */
	private Calendar mCalendar;

	/**
	 * ����ʱ�䵯��ѡ����캯��
	 * 
	 * @param activity
	 *            �����õĸ�activity
	 * @param initDateTime
	 *            ��ʼ����ʱ��ֵ����Ϊ�������ڵı��������ʱ���ʼֵ
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
			dateTime = mCalendar.get(Calendar.YEAR) + "��" + mCalendar.get(Calendar.MONTH) + "��" + mCalendar.get(Calendar.DAY_OF_MONTH) + "�� " + mCalendar.get(Calendar.HOUR_OF_DAY) + ":" + mCalendar.get(Calendar.MINUTE);
		}

		datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
	}

	/**
	 * ��������ʱ��ѡ��򷽷�
	 * 
	 * @param inputDate
	 *            :Ϊ��Ҫ���õ�����ʱ���ı��༭��
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final Callback<Calendar> callback) {
		LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		mAlertDialog = new AlertDialog.Builder(activity).setTitle(dateTime).setView(dateTimeLayout).setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				callback.handle(mCalendar);
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��MM��dd�� HH:mm");
		dateTime = sdf.format(mCalendar.getTime());
		mAlertDialog.setTitle(dateTime);
	}

	/**
	 * ʵ�ֽ���ʼ����ʱ��2012��07��02�� 16:45 ��ֳ��� �� �� ʱ �� ��,����ֵ��calendar
	 * 
	 * @param initDateTime
	 *            ��ʼ����ʱ��ֵ �ַ�����
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// ����ʼ����ʱ��2012��07��02�� 16:45 ��ֳ��� �� �� ʱ �� ��
		String date = spliteString(initDateTime, "��", "index", "front"); // ����
		String time = spliteString(initDateTime, "��", "index", "back"); // ʱ��

		String yearStr = spliteString(date, "��", "index", "front"); // ���
		String monthAndDay = spliteString(date, "��", "index", "back"); // ����

		String monthStr = spliteString(monthAndDay, "��", "index", "front"); // ��
		String dayStr = spliteString(monthAndDay, "��", "index", "back"); // ��

		String hourStr = spliteString(time, ":", "index", "front"); // ʱ
		String minuteStr = spliteString(time, ":", "index", "back"); // ��

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay, currentHour, currentMinute);
		return calendar;
	}

	/**
	 * ��ȡ�Ӵ�
	 * 
	 * @param srcStr
	 *            Դ��
	 * @param pattern
	 *            ƥ��ģʽ
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern, String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // ȡ���ַ�����һ�γ��ֵ�λ��
		} else {
			loc = srcStr.lastIndexOf(pattern); // ���һ��ƥ�䴮��λ��
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // ��ȡ�Ӵ�
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // ��ȡ�Ӵ�
		}
		return result;
	}

	/**
	 * �жϵ�ǰ���������ڼ�
	 * 
	 * @param calendar
	 * @return
	 */
	public static String getWeekByCalendar(Calendar calendar) {

		String week = "����";

		if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
			week += "��";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
			week += "һ";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 3) {
			week += "��";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
			week += "��";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 5) {
			week += "��";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
			week += "��";
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
			week += "��";
		}

		return week;
	}
	
	public static String getDateByCalendar(Calendar calendar){
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
		return format.format(calendar.getTime());
	}
	
	public static String getDateTimeByCalendar(Calendar calendar){
		SimpleDateFormat format = new SimpleDateFormat("yyyy��MM��dd��", Locale.CHINA);
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
