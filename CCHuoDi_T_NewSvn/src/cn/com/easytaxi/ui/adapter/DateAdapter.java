package cn.com.easytaxi.ui.adapter;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author shiner
 */
public class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
	public final static SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public Date deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		if (json == null || json.getAsString().equals("")) {
			return null;
		} else {
			try {
				String dateStr = json.getAsString();
				return  DEFAULT_DATE_FORMATTER.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public JsonElement serialize(Date arg0, Type arg1, JsonSerializationContext arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
