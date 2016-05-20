package cn.com.easytaxi.platform.common;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import cn.com.easytaxi.platform.common.Cities.City;

public class Cities {

 
	public static class PinyinCompare implements Comparator<City> {
		@Override
		public int compare(City first, City second) {
		/*	if(first == null || second == null || first.cityNameSimple==null || second.cityNameSimple==null){
				return 0;
			}*/
			try{
				
				String s1 = new String(first.cityNameSimple.toString().getBytes("GB2312"), "ISO-8859-1");
				String s2 = new String(second.cityNameSimple.toString().getBytes("GB2312"), "ISO-8859-1");
				// 运用String类的 compareTo（）方法对两对象进行比较
				return s1.compareTo(s2);
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				
			}
			return Collator.getInstance(Locale.CHINA).compare(
					first.cityNameSimple, second.cityNameSimple);
			
		}
	}

	 

	public static class City {
		public City() {

		}
 
		public City(int id, String province, String name ,String simpleName, int lat,
				int lng, String pinyin ,int type) {
			this.id = id;
			this.provice = province;
			this.name = name;
			this.cityNameSimple = simpleName;
			this.lat = lat;
			this.lng = lng;
			this.pinyin = pinyin;
			this.type = type;
			 
		}
		public City(int id, String province,  String simpleName, int lat,
				int lng, String pinyin) {
			this.id = id;
			this.provice = province;
			 
			this.cityNameSimple = simpleName;
			this.lat = lat;
			this.lng = lng;
			this.pinyin = pinyin;
			 
			
		}

		public int id;
		public String cityNameSimple;
		public int lat;
		public int lng;
		public boolean isHeader = false;
		public String name;
		public String provice;
		public String pinyin;
		public int type;
	 
	}

	public static ArrayList<City> getCities(int i) {
		 
		return null;
	}
 

}
