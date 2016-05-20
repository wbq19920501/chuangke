package cn.com.easytaxi.book;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.util.TimeTool;

public class BookUtil {

	/**
	 * 司机已接单，且还未超时
	 * 
	 * @param b
	 * @return
	 */
	public static boolean isActive(BookBean b) {
		try {
			return b.getState() > 0x04 && ToolUtil.compareTime(BaseBookLoader.f.parse(b.getUseTime()).getTime(), System.currentTimeMillis());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 司机还未接单，订单还未超时
	 * 
	 * @param b
	 * @return
	 */
	public static boolean isNew(BookBean b) {
		try {
			return b.getState() == 0x01 && ToolUtil.compareTime(TimeTool.DEFAULT_DATE_FORMATTER.parse(b.getUseTime()).getTime(), System.currentTimeMillis());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 订单超时：包括 已接单且订单超时，未接单且订单超时
	 * 
	 * @param b
	 * @return
	 */
	public static boolean isHistory(BookBean b) {
		return (!isNew(b)) && (!isActive(b));
	}

	/**
	 * 归类排序
	 * 
	 * @param list
	 */
	public static void categarySort(ArrayList<BookBean> list) {
		ArrayList<BookBean> tmp = new ArrayList<BookBean>();
		for (BookBean bookBean : list) {
			if (BookUtil.isNew(bookBean)) {
				tmp.add(bookBean);
			}
		}
		for (BookBean bookBean : list) {
			if (BookUtil.isActive(bookBean)) {
				tmp.add(bookBean);
			}
		}
		for (BookBean bookBean : list) {
			if (BookUtil.isHistory(bookBean)) {
				tmp.add(bookBean);
			}
		}
		list.clear();
		list.addAll(tmp);
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static String getDecimalNumber(long number) {
		return getDecimalNumber(number, 1000.0f);
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static String getDecimalNumber(long number, float divider) {
		DecimalFormat format = new DecimalFormat("#0.0");
		return format.format(number / divider);
	}

	public static SpannableStringBuilder getSpecialText(String txt, String subTxt, int color) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		SpannableString sb = new SpannableString(txt);
		int start = txt.indexOf(subTxt);
		int end = start + subTxt.length();
		sb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		// sb.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, count.length() -
		// 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return builder.append(sb);
	}

	public static SpannableStringBuilder getSpecialText(SpannableStringBuilder ssb, String txt, String subTxt, int color) {
		int start = txt.indexOf(subTxt);
		int end = start + subTxt.length();
		ssb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return ssb;
	}

	public static boolean isEffective(Long id) {
		if (id == null) {
			return false;
		} else {
			if (id == 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	// 蛋疼
	public static void validate(List<BookBean> result) {
		Iterator<BookBean> itr = result.iterator();
		while (itr.hasNext()) {
			BookBean b = itr.next();
			b = validate(b);
			if (b == null) {
				itr.remove();
			}
		}
	}

	public static BookBean validate(BookBean result) {
		if (result == null) {
			Log.w("BookUtil", "validate,remove null bookbean");
			return null;
		} else if (result.getUseTime() == null) {
			Log.w("BookUtil", "validate,remove null_usetime bookbean");
			return null;
		} else {
			return result;
		}
	}

	public static boolean isEffective(Integer id) {
		if (id == null) {
			return false;
		} else {
			if (id == 0) {
				return false;
			} else {
				return true;
			}
		}
	}
}
