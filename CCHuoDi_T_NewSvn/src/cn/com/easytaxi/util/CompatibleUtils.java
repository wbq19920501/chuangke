package cn.com.easytaxi.util;

import java.lang.reflect.Method;

import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.ui.bean.NewBookBean;

/**
 * @author shiner
 */
public class CompatibleUtils {
	/**
	 * 变量加上@SerializedName后取得的FiledName是SerializedName的value，不能拼接成方法名，
	 * 所以通过get方法名直接找set方法
	 * 
	 * @param dest
	 * @param source
	 */
	public static void copy(BookBean dest, NewBookBean source) {
		dest.setCacheId(source.getCacheId());
		dest.setAudioIp(source.getAudioIp());
		dest.setAudioName(source.getAudioName());
		dest.setAudioPort(source.getAudioPort());
		dest.setBookType(source.getBookType());
		dest.setCacheRef(source.getCacheRef());
		dest.setCount(source.getCount());
		dest.setEndAddress(source.getEndAddress());// setCount(source.getCount());
		dest.setEndLatitude(source.getEndLatitude());// Count(source.getCount());
		dest.setEndLongitude(source.getEndLongitude());// Count(source.getCount());
		dest.setEvaluate(source.getEvaluate());// getCount());
		dest.setForecastDistance(source.getForecastDistance());// Count(source.getCount());
		dest.setForecastPrice(source.getForecastPrice());// (source.getCount());
		// dest.setHighLight(source.isHighLight());//Count(source.getCount());
		dest.setId(source.getId());
		// dest.setNewOrder(source.getNewOrder());
		dest.setPassengerId(source.getPassengerId());
		dest.setPassengerName(source.getPassengerName());
		dest.setPassengerPhone(source.getPassengerPhone());
		dest.setPoints(source.getPoints());
		dest.setPrice(source.getPrice());
		dest.setPriceMode(source.getPriceMode());
		dest.setRead(source.isRead());
		dest.setReplyerId(source.getReplyerId());
		dest.setReplyerName(source.getReplyerName());
		dest.setReplyerPhone(source.getReplyerPhone());
		dest.setReplyerType(source.getReplyerType());
		dest.setReplyTime(source.getReplyTime());
		dest.setScore(source.getScore());
		dest.setSource(source.getSource());
		dest.setSourceName(source.getSourceName());
		dest.setStartAddress(source.getStartAddress());
		dest.setStartLatitude(source.getStartLatitude());
		dest.setStartLongitude(source.getStartLongitude());
		dest.setState(source.getState());
		dest.setUseTime(source.getUseTime());// useTime);//
	}
}
