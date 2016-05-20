package cn.com.easytaxi.workpool.bean;

import java.io.Serializable;

public class GeoPointLable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2257227318326959484L;
	private int lat;
	private int log;
	private String name;
	private String cityName;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLog() {
		return log;
	}

	public void setLog(int log) {
		this.log = log;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GeoPointLable(int lat, int log, String name,String cityName) {
		super();
		this.lat = lat;
		this.log = log;
		this.name = name;
		this.cityName=cityName;
	}

	@Override
	public String toString() {
		return name;
	}
}
