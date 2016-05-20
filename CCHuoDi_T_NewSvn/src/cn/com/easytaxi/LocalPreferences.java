package cn.com.easytaxi;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalPreferences {
	private static LocalPreferences instance = null;
	private SharedPreferences preferences;

	private LocalPreferences(Context context) {
		preferences = context.getSharedPreferences("cache", 0);
	}

	public static LocalPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new LocalPreferences(context);
		}
		return instance;
	}

	public void saveCacheString(String key, String value) {
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getCacheString(String key) {

		String value = preferences.getString(key, "");
		if (key.equalsIgnoreCase("pricelist") && StringUtils.isEmpty(value)) {
			value = preferences.getString(key, "{\"defaultKey\":1,\"index\":3 , \"error\":0,\"prices\":[{\"text\":\"0\",\"val\":0},{\"text\":\"3\",\"val\":3},{\"text\":\"5\",\"val\":5},{\"text\":\"10\",\"val\":10},{\"text\":\"15\",\"val\":15}]}");
		} else if (key.equalsIgnoreCase("book_pricelist") && StringUtils.isEmpty(value)) {
			value = preferences.getString(key, "{\"defaultKey\":1,\"index\":3 , \"error\":0,\"prices\":[{\"text\":\"0\",\"val\":0},{\"text\":\"3\",\"val\":3},{\"text\":\"5\",\"val\":5},{\"text\":\"10\",\"val\":10},{\"text\":\"15\",\"val\":15}]}");
		}

		return value;
	}

	public void saveCahceInt(String key, int value) {

		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();

	}

	public int getCacheInt(String key) {
		int value = preferences.getInt(key, 0);
		return value;
	}

	public void saveCahceLong(String key, long value) {

		Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getCacheLong(String key) {
		long value = preferences.getLong(key, 0);
		return value;
	}

	public void saveCahceBoolean(String key, boolean value) {
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getCacheBoolean(String key) {
		return preferences.getBoolean(key, false);
	}

}
