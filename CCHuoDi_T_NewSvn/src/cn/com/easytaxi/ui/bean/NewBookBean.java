package cn.com.easytaxi.ui.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.easytaxi.platform.service.CacheBean;

import com.google.gson.annotations.SerializedName;

/**
 * ҵ�񶩵�
 * 
 * @author magi
 * 
 */
public class NewBookBean extends CacheBean implements Serializable {
	// ui���
	private boolean newOrder = true;// �µ�
	private int speakCount = 0;// ��������
	private boolean read = false;// �Ѷ�
	private boolean highLight = false;// ����
	// ui���end

	public static final int INCREASES_PRICE = 0;

	public static final int QUOTED_PRICE = 1;

	@SerializedName("_ID")
	public long id;
	
	/**
	 * �Ƿ���ʾ����ʱ
	 */
	@SerializedName("_COUNT")
	private int count = 0;

	/**
	 * �ö����Ļ���
	 */
	@SerializedName("_SCORE")
	private Integer score;

	/**
	 * �ö��������ѵ���
	 */
	@SerializedName("_POINTS")
	private Integer points;

	/**
	 * �ã�100-200 �<100 �˿�ȡ����>200
	 */
	@SerializedName("_EVALUATE")
	private Integer evaluate;

	/**
	 * �˿͵绰
	 */
	@SerializedName("_PASSENGER_PHONE")
	private String passengerPhone = "";

	/**
	 * �˿�����
	 */
	@SerializedName("_PASSENGER_NAME")
	private String passengerName = "";

	/**
	 * �˿�ID
	 */
	@SerializedName("_PASSENGER_ID")
	private Long passengerId;

	/**
	 * ������Դ����(IOSΪ2,AndroidΪ1)
	 */
	@SerializedName("_SOURCE")
	private Integer source;

	/**
	 * ������Դ�������� IOS:iso.�汾�� ANDROID:android.�汾��
	 */
	@SerializedName("_SOURCE_NAME")
	private String sourceName;

	/**
	 * �ϳ��ص�
	 */
	@SerializedName("_START_ADDRESS")
	private String startAddress = "";

	/**
	 * �ϳ�����
	 */
	@SerializedName("_START_LONGITUDE")
	private Integer startLongitude = 0;

	/**
	 * �ϳ�γ��
	 */
	@SerializedName("_START_LATITUDE")
	private Integer startLatitude = 0;

	/**
	 * �³��ص�
	 */
	@SerializedName("_END_ADDRESS")
	private String endAddress = "";

	/**
	 * �³�����
	 */
	@SerializedName("_END_LONGITUDE")
	private Integer endLongitude = 0;

	/**
	 * �³�γ��
	 */
	@SerializedName("_END_LATITUDE")
	private Integer endLatitude = 0;

	/**
	 * ����״̬ 0:δ���� 1:�г����� 2:�˿�ȡ�� 3:˾��ȡ�� 4:�޳�Ӧ�� 5:������
	 */
	@SerializedName("_STATE")
	private Integer state = 0;

	/**
	 * �۸�ģʽ 0:�Ӽ�ģʽ 1:һ�ڼ�ģʽ
	 */
	@SerializedName("_PRICE_MODE")
	private Integer priceMode = 0;

	/**
	 * �۸�
	 */
	@SerializedName("_PRICE")
	private Integer price = 0;

	/**
	 * Ӧ������(���⳵���Ƶ�...)
	 */
	@SerializedName("_REPLYER_NAME")
	private String replyerName;

	/**
	 * Ӧ��ID
	 */
	@SerializedName("_REPLYER_ID")
	private Long replyerId;

	/**
	 * Ӧ�𷽵绰
	 */
	@SerializedName("_REPLYER_PHONE")
	private String replyerPhone;

	/**
	 * Ӧ��ʱ��
	 */
	@SerializedName("_REPLY_TIME")
	private String replyTime;

	/**
	 * ��Ӧ�����ͱ�ʶ
	 */
	@SerializedName("_REPLYER_TYPE")
	private Integer replyerType;

	/**
	 * �������� 1:��ʱ���� 2:ԤԼ����
	 */
	@SerializedName("_BOOK_TYPE")
	private Integer bookType;

	/**
	 * �ó�ʱ��
	 */
	@SerializedName("_USE_TIME")
	private long useTime;

	/**
	 * Ԥ�ƾ���
	 */
	@SerializedName("_FORECAST_DISTANCE")
	private Integer forecastDistance;
	/**
	 * ¼���ļ�id
	 */
	@SerializedName("_AUDIO_NAME")
	private String audioName;

	/**
	 * ¼���ļ����ص�ַ
	 */
	@SerializedName("_AUDIO_IP")
	private String audioIp;

	/**
	 * ¼���ļ����ض˿�
	 */
	@SerializedName("_AUDIO_PORT")
	private int audioPort;

	/**
	 * Ԥ�Ƽ۸�
	 */
	@SerializedName("_FORECAST_PRICE")
	private Integer forecastPrice;

	public String getAudioName() {
		return audioName;
	}

	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}

	public Integer getForecastDistance() {
		return forecastDistance;
	}

	public void setForecastDistance(Integer forecastDistance) {
		this.forecastDistance = forecastDistance;
	}

	public Integer getForecastPrice() {
		return forecastPrice;
	}

	public void setForecastPrice(Integer forecastPrice) {
		this.forecastPrice = forecastPrice;
	}

	public String getPassengerPhone() {
		return passengerPhone;
	}

	public void setPassengerPhone(String passengerPhone) {
		this.passengerPhone = passengerPhone;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public Integer getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(Integer startLongitude) {
		this.startLongitude = startLongitude;
	}

	public Integer getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(Integer startLatitude) {
		this.startLatitude = startLatitude;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public Integer getEndLongitude() {
		return endLongitude;
	}

	public void setEndLongitude(Integer endLongitude) {
		this.endLongitude = endLongitude;
	}

	public Integer getEndLatitude() {
		return endLatitude;
	}

	public void setEndLatitude(Integer endLatitude) {
		this.endLatitude = endLatitude;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getPriceMode() {
		return priceMode;
	}

	public void setPriceMode(Integer priceMode) {
		this.priceMode = priceMode;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getReplyerName() {
		return replyerName;
	}

	public void setReplyerName(String replyerName) {
		this.replyerName = replyerName;
	}

	public Long getReplyerId() {
		return replyerId;
	}

	public void setReplyerId(Long replyerId) {
		this.replyerId = replyerId;
	}

	public String getReplyerPhone() {
		return replyerPhone;
	}

	public void setReplyerPhone(String replyerPhone) {
		this.replyerPhone = replyerPhone;
	}

	public Integer getReplyerType() {
		return replyerType;
	}

	public void setReplyerType(Integer replyerType) {
		this.replyerType = replyerType;
	}

	public Integer getBookType() {
		return bookType;
	}

	public void setBookType(Integer bookType) {
		this.bookType = bookType;
	}

	public 	String getUseTime() {
		return String.valueOf(useTime);
	}

	public void setUseTime(String useTime) {
		this.useTime = Long.valueOf(useTime);
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Integer getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(Integer evaluate) {
		this.evaluate = evaluate;
	}

	public String getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}

	public boolean isHighLight() {
		return highLight;
	}

	public void setHighLight(boolean highLight) {
		this.highLight = highLight;
	}

	public String getAudioIp() {
		return audioIp;
	}

	public int getAudioPort() {
		return audioPort;
	}

	public void setAudioPort(int audioPort) {
		this.audioPort = audioPort;
	}

	public void setAudioIp(String audioIp) {
		this.audioIp = audioIp;
	}

	public boolean isNewOrder() {
		return newOrder;
	}

	public void setNewOrder(boolean newOrder) {
		this.newOrder = newOrder;
	}

	public int getSpeakCount() {
		return speakCount;
	}

	public void setSpeakCount(int speakCount) {
		this.speakCount = speakCount;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public NewBookBean() {
		super();
	}

	public NewBookBean(Long id, Integer count, Integer points, Integer evaluate, String passengerPhone, String passengerName, Long passengerId, Integer source, String sourceName, String startAddress, Integer startLongitude, Integer startLatitude, String endAddress, Integer endLongitude, Integer endLatitude, Integer state, Integer priceMode, Integer price, String replyerName, Long replyerId, String replyerPhone, String replyTime, Integer replyerType, Integer bookType, String useTime, Integer forecastDistance, String audioName, String audioIp, Integer audioPort, Integer forecastPrice) {
		setCacheId(id);
		this.count = count;
		this.points = points;
		this.evaluate = evaluate;
		this.passengerPhone = passengerPhone;
		this.passengerName = passengerName;
		this.passengerId = passengerId;
		this.source = source;
		this.sourceName = sourceName;
		this.startAddress = startAddress;
		this.startLongitude = startLongitude;
		this.startLatitude = startLatitude;
		this.endAddress = endAddress;
		this.endLongitude = endLongitude;
		this.endLatitude = endLatitude;
		this.state = state;
		this.priceMode = priceMode;
		this.price = price;
		this.replyerName = replyerName;
		this.replyerId = replyerId;
		this.replyerPhone = replyerPhone;
		this.replyTime = replyTime;
		this.replyerType = replyerType;
		this.bookType = bookType;
		this.useTime = Long.valueOf(useTime);
		this.forecastDistance = forecastDistance;
		this.audioName = audioName;
		this.audioIp = audioIp;
		this.audioPort = audioPort;
		this.forecastPrice = forecastPrice;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		if (getUseTime() != null) {
			return "id:" + getCacheId() + ",state:" + getState() + ",evluate:" + getEvaluate() + ",usetime:" + new SimpleDateFormat("MM��dd�� hh��mm��").format(getUseTime()) + ",replyerId:" + getReplyerId();
		} else {
			return "id:" + getCacheId() + ",state:" + getState() + ",evluate:" + getEvaluate() + ",replyerId:" + getReplyerId();
		}
	}
}
