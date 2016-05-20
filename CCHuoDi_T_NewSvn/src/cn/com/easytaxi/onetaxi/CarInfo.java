package cn.com.easytaxi.onetaxi;

import org.json.JSONException;
import org.json.JSONObject;

public class CarInfo {
	
	//��ʼ״̬
	private final int  initState;
	
	// ˾������
	private String driverName;
	// ��˾����
	private String companyName;
	// �Ǽ�
	private float starNumber;
	// ���ƺ�
	private String carNumber;
	// ����
	private int distance;
	// ���۴���
	private int commentCount;
	// Ͷ�ߴ���
	private int complaintCount;
	// ����״̬:0Ϊ�ճ�״̬��>0Ϊ�ǿ���״̬
	private int carState;

	// add at 12-10
	private String taxiId;
	// ���⳵�ľ�γ��
	private double carLng;
	private double carLat;

	public String getTaxiId() {
		return taxiId;
	}

	public void setTaxiId(String taxiId) {
		this.taxiId = taxiId;
	}

	public double getCarLng() {
		return carLng;
	}

	public void setCarLng(double carLng) {
		this.carLng = carLng;
	}

	public double getCarLat() {
		return carLat;
	}

	public void setCarLat(double carLat) {
		this.carLat = carLat;
	}
	

	public CarInfo() {
		driverName = "˾������";
		companyName = "��˾����";
		starNumber = 3;
		carNumber = "��A84546";
		distance = 3;
		commentCount =3;
		complaintCount = 3;
		carState = 1;//�ؿ�
		initState = carState;
		taxiId = "13544444444";
		carLng = 456466;
		carLat = 465646;
	}

	public CarInfo(JSONObject object) {
		driverName = getStringValue(object, "name");
		companyName = getStringValue(object, "company");
		starNumber = getIntValue(object, "star");
		carNumber = getStringValue(object, "carnumber");
		distance = getIntValue(object, "distance");
		commentCount = getIntValue(object, "comment");
		complaintCount = getIntValue(object, "complaint");
		carState = getIntValue(object, "state");
		initState = carState;
		taxiId = getStringValue(object, "taxiid");
		carLng = getDoubleValue(object, "longitude");
		carLat = getDoubleValue(object, "latitude");
	}

	private String getStringValue(JSONObject object, String key) {
		String value = "";
		try {
			value = object.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			value = "δ֪";
		}
		return value;
	}

	private int getIntValue(JSONObject object, String key) {
		int value = 0;
		try {
			value = object.getInt(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	private double getDoubleValue(JSONObject object, String key) {
		double value = 0;
		try {
			value = object.getDouble(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public float getStarNumber() {
		return starNumber;
	}

	public void setStarNumber(float starNumber) {
		this.starNumber = starNumber;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getComplaintCount() {
		return complaintCount;
	}

	public void setComplaintCount(int complaintCount) {
		this.complaintCount = complaintCount;
	}

	public int getCarState() {
		return carState;
	}

	public void setCarState(int carState) {
		this.carState = carState;
	}
	
	public void resetState(){
		carState = initState;
	}
}
