package cn.com.easytaxi.airport.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * ������Ϣ
 * 
 * @author kaka
 * 
 */
public class AirportBean implements Serializable {

	private static final long serialVersionUID = 20130701L;
    
	/**
	 * �Ƿ�Ϊͷ����Ϣ���˲�����list�б��л��õ�
	 */
	public boolean isHeader;
	
	/**
	 * ����id
	 */
	@Expose
	public int id;
	

	/**
	 * ��������
	 */
	@Expose
	public String name;
	
	/**
	 * ����γ��
	 */
	@Expose
	public int latitude;
	
	/**
	 * ��������
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
