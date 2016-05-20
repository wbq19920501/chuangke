package cn.com.easytaxi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.Primitives;

public class XGsonUtil {

	private static JsonParser jsonParser = new JsonParser();

	/**
	 * 将Json String解析为JsonObject
	 * 
	 * @param json
	 *            要解析的json字符�?
	 * @throws Exception
	 * @return JsonObject 解析后的JsonObject对象
	 */
	public static JsonObject getJsonObject(String json) throws Exception {
		if (json == null || json.length() == 0) {
			return new JsonObject();
		} else {
			JsonObject object = jsonParser.parse(json).getAsJsonObject();
			return object;
		}
	}

	/**
	 * 将Json String解析为JsonArray
	 * 
	 * @param json
	 *            要解析的json字符�?
	 * @throws Exception
	 * @return JsonArray 解析后的JsonArray对象
	 */
	public static JsonArray getJsonArray(String json) throws Exception {
		if (json == null || json.length() == 0) {
			return new JsonArray();
		} else {
			return jsonParser.parse(json).getAsJsonArray();
		}
	}

	/**
	 * 根据json字符串，获取pojo对象
	 * 
	 * @param useExpose
	 *            为true时， 不解析实体中没有用@Expose注解的属性；为false时，解析�?��属�?
	 * @param json
	 * @param cls
	 * @return
	 * @return T
	 */
	public static <T> T getObjectFromJson(boolean useExpose, String json, Class<T> cls) throws Exception {
		Gson gson = null;
		if (useExpose) {
			// 不解析实体中没有用@Expose注解的属�?
			gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		} else {
			// 全部解析
			gson = new Gson();
		}
		return gson.fromJson(json, cls);
	}

	/**
	 * 
	 * @param useExpose
	 *            为true时， 不解析实体中没有用@Expose注解的属性；为false时，解析�?��非transient属�?
	 * @param json
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getListFromJson(boolean useExpose, String json, Class<T> cls) throws Exception {

		List<T> list = new ArrayList<T>();

		JsonArray array = getJsonArray(json);
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			list.add(getObjectFromJson(useExpose, object.toString(), cls));
		}

		return list;
	}

	/**
	 * 为true时， 不解析实体中没有用@Expose注解的属性；为false时，解析�?��非transient属�?
	 * 
	 * @param useExpose
	 * @param array
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getListFromJson(boolean useExpose, JsonArray array, Class<T> cls) throws Exception {

		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.size(); i++) {
			JsonObject object = array.get(i).getAsJsonObject();
			list.add(getObjectFromJson(useExpose, object.toString(), cls));
		}

		return list;
	}

	/**
	 * 根据pojo对象，获取json字符�?
	 * 
	 * @param useExpose
	 *            为true时， 不解析实体中没有用@Expose注解的属性；为false时，解析�?��非transient属�?
	 * @param t
	 * @return String
	 */
	public static <T> String getJsonFromObject(boolean useExpose, T t) {
		Gson gson = null;
		if (useExpose) {
			gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		} else {
			gson = new Gson();
		}
		return gson.toJson(t);
	}

	/**
	 * 根据pojo对象，获取json字符�?
	 * 
	 * @param useExpose
	 *            为true时， 不解析实体中没有用@Expose注解的属性；为false时，解析�?��非transient属�?
	 * @param t
	 * @return String
	 */
	public static <T> String getJsonFromList(boolean useExpose, Collection<T> list) {
		Gson gson = null;
		if (useExpose) {
			gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		} else {
			gson = new Gson();
		}
		return gson.toJson(list);
	}

	public static <K, T> String getJsonFromObject(boolean useExpose, JsonSerializer<K> serializer, T t) {
		GsonBuilder builder = new GsonBuilder();
		if (serializer != null) {
			builder.registerTypeAdapter(serializer.getClass(), serializer);
		}

		if (useExpose) {
			builder.excludeFieldsWithoutExposeAnnotation();
		}

		return builder.create().toJson(t);
	}

	@Deprecated
	public static <T> T getParamFromJsonObject(JsonObject obj, String param, Class<T> cls) {
		Object resObject = null;
		try {
			if (cls.getClass().getSimpleName().equals(String.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsString();

			} else if (cls.getClass().getSimpleName().equals(Long.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsLong();

			} else if (cls.getClass().getSimpleName().equals(Integer.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsInt();

			} else if (cls.getClass().getSimpleName().equals(Short.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsShort();

			} else if (cls.getClass().getSimpleName().equals(Byte.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsByte();

			} else if (cls.getClass().getSimpleName().equals(Double.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsDouble();

			} else if (cls.getClass().getSimpleName().equals(Float.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsFloat();

			} else if (cls.getClass().getSimpleName().equals(Boolean.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsBoolean();

			} else if (cls.getClass().getSimpleName().equals(Character.class.getClass().getSimpleName())) {
				resObject = obj.get(param).getAsCharacter();
			}

			return Primitives.wrap(cls).cast(resObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * JSONArray转list
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public List<JSONObject> getListByJSONArray(JSONArray array) throws JSONException {
		List<JSONObject> list = new ArrayList<JSONObject>();
		if (array != null) {
			for (int i = 0; i < array.length(); i++) {
				list.add(array.getJSONObject(i));
			}
		}

		return list;
	}
	
	public List<JsonObject> getListByJSONArray(JsonArray array) throws Exception{
		List<JsonObject> list = new ArrayList<JsonObject>();
		if (array != null) {
			for (int i = 0; i < array.size(); i++) {
				list.add(array.get(i).getAsJsonObject());
			}
		}

		return list;
	}
}
