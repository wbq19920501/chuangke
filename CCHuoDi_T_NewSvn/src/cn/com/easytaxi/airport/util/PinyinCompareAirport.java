package cn.com.easytaxi.airport.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import cn.com.easytaxi.airport.bean.AirportBean;

public class PinyinCompareAirport implements Comparator<AirportBean> {
	@Override
	public int compare(AirportBean first, AirportBean second) {
		try{
			
			String s1 = new String(first.name.toString().getBytes("GB2312"), "ISO-8859-1");
			String s2 = new String(second.name.toString().getBytes("GB2312"), "ISO-8859-1");
			// 运用String类的 compareTo（）方法对两对象进行比较
			return s1.compareTo(s2);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		return Collator.getInstance(Locale.CHINA).compare(
				first.name, second.name);
		
	}
}
