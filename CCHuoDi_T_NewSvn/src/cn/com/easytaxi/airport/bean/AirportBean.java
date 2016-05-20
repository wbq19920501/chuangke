package cn.com.easytaxi.airport.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * 机场信息
 * 
 * @author kaka
 * 
 */
public class AirportBean implements Serializable {

	private static final long serialVersionUID = 20130701L;
    
	/**
	 * 是否为头部信息：此参数在list列表中会用到
	 */
	public boolean isHeader;
	
	/**
	 * 机场id
	 */
	@Expose
	public int id;
	

	/**
	 * 机场名称
	 */
	@Expose
	public String name;
	
	/**
	 * 机场纬度
	 */
	@Expose
	public int latitude;
	
	/**
	 * 机场经度
	 */
	@Expose
	public int longitude;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	
	public boolean getIsHeader() {
		return isHeader;
	}
	
	public void setIsHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
}
