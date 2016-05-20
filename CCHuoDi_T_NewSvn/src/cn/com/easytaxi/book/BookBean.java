package cn.com.easytaxi.book;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.platform.service.CacheBean;
import cn.com.easytaxi.util.TimeTool;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

/**
 * ҵ�񶩵�
 * 
 * @author magi
 * 
 */
public class BookBean extends CacheBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private long dyTime = 0;

	/**
	 * �������
	 */
	@Expose
	private Long id;

	/**
	 * ��������������ʾ
	 */
	private int bookFlags;

	/**
	 * ��������
	 */
	@Expose
	private Integer cityId;

	/**
	 * ��������
	 */
	@Expose
	private String cityName;

	/**
	 * �������� 1:�°漴ʱ���� 2:�ɰ汾��ʱ���� 3:�°汾ԤԼ���� 4:�ɰ汾ԤԼ���� 5:ԤԼ����
	 */
	@Expose
	private Integer bookType = 5;
	/**
	 * ��Ҷ����ļ۸�
	 */
	private String fee;
	/**
	 * 0 ���ﶩ����1��Ҷ���
	 */
	private int order_type;

	/**
	 * �˿͵绰
	 */
	@Expose
	private String passengerPhone = "";

	/**
	 * �˿�����
	 */
	@Expose
	private String passengerName = "";

	/**
	 * �˿�ID
	 */
	@Expose
	private Long passengerId;

	/**
	 * �ö����Ļ���
	 */
	private Integer score;

	/**
	 * �ö��������ѵ���,�״��
	 */
	private Integer points;

	/**
	 * �Ƿ���ʾ����ʱ
	 */
	private Integer count = 0;

	/**
	 * ������Դ����(IOSΪ2,AndroidΪ1)
	 */
	@Expose
	private Integer source;

	/**
	 * ������Դ�������� IOS:iso.�汾�� ANDROID:android.�汾��
	 */
	@Expose
	private String sourceName;

	/**
	 * �ϳ��ص�
	 */
	@Expose
	@SerializedName("startAddr")
	private String startAddress = "";

	/**
	 * �ϳ�����
	 */
	@Expose
	private Integer startLongitude = 0;

	/**
	 * �ϳ�γ��
	 */
	@Expose
	private Integer startLatitude = 0;

	/**
	 * �³��ص�
	 */
	@Expose
	private String endAddress = "";

	/**
	 * �³�����
	 */
	@Expose
	@SerializedName("endLng")
	private Integer endLongitude = 0;

	/**
	 * �³�γ��
	 */
	@Expose
	@SerializedName("endLat")
	private Integer endLatitude = 0;

	@Expose
	@SerializedName("bookState")
	private Integer state = 0;

	/**
	 * �۸�ģʽ 0:�Ӽ�ģʽ 1:һ�ڼ�ģʽ
	 */
	@Expose
	private Integer priceMode = 0;

	/**
	 * �۸�
	 */
	@Expose
	private Integer price = 0;

	/**
	 * Ӧ������(˾������)
	 */
	@Expose
	private String replyerName;

	/**
	 * Ӧ��ID
	 */
	@Expose
	private Long replyerId;

	/**
	 * Ӧ�𷽾���
	 */
	@Expose
	private Integer replyerLongitude;

	/**
	 * Ӧ��γ��
	 */
	@Expose
	private Integer replyerLatitude;

	/**
	 * Ӧ�𷽵绰
	 */
	@Expose
	private String replyerPhone;

	/**
	 * ��Ӧ�����ͱ�ʶ
	 */
	@Expose
	private Integer replyerType;

	/**
	 * Ӧ�𷽱�ţ����ƣ�
	 */
	@Expose
	private String replyerNumber;

	/**
	 * Ӧ�𷽹�˾
	 */
	@Expose
	private String replyerCompany;

	/**
	 * ��Ӧʱ��
	 */
	@Expose
	private String replyTime;

	/**
	 * �µ�ʱ��
	 */
	@Expose
	private String submitTime = Calendar.getInstance().getTime().toString();

	/**
	 * �ó�ʱ��
	 */
	@Expose
	private String useTime;

	/**
	 * �����ȵ�����
	 */
	@Expose
	private Integer scheduleCount = 0;

	/**
	 * ����������ID
	 */
	@Expose
	private Long blockId;

	@Expose
	private int forecastDistance;

	@Expose
	private int forecastPrice;

	/**
	 * �ϴ�����ʱ��
	 */
	@Expose
	private String preTime = Calendar.getInstance().getTime().toString();

	/**
	 * �ж�UPD���Ϳͻ����Ƿ��յ� 0:�ط��ر� 1:��˿Ͷ��ط�˾���ӵ���� 2:��˾�����س˿�ȡ����Ϣ 3.��˿Ͷ��ط�˾��ȡ����Ϣ
	 */
	@Expose
	private Integer udpReply;

	/**
	 * IOS�豸Ψһ��ʶ��������Ϣ����
	 */
	@Expose
	private String deviceToken;

	@Expose
	private String audioName;

	/**
	 * ¼���ļ����ص�ַ
	 */
	private String audioIp;

	/**
	 * ¼���ļ����ض˿�
	 */
	private Integer audioPort;

	/**
	 * ���� 150:�ǳ����� 100:���� 10:���� 500:�˿�ȡ��
	 */
	@Expose
	private Integer evaluate;

	public BookBean() {

	}

	public BookBean(Long id, Integer count, Integer points, Integer evaluate, String passengerPhone, String passengerName, Long passengerId, Integer source, String sourceName, String startAddress, Integer startLongitude, Integer startLatitude, String endAddress, Integer endLongitude, Integer endLatitude, Integer state, Integer priceMode, Integer price, String replyerName, Long replyerId, String replyerPhone, String replyTime, Integer replyerType, Integer bookType, String useTime, Integer forecastDistance, String audioName, String audioIp, Integer audioPort, Integer forecastPrice) {
		setCacheId(id);
		setId(id);
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
		this.useTime = useTime;
		this.forecastDistance = forecastDistance;
		this.audioName = audioName;
		this.audioIp = audioIp;
		this.audioPort = audioPort;
		this.forecastPrice = forecastPrice;

	}

	public String getAudioIp() {
		return audioIp;
	}

	public void setAudioIp(String audioIp) {
		this.audioIp = audioIp;
	}

	public int getAudioPort() {
		return audioPort;
	}

	public void setAudioPort(int audioPort) {
		this.audioPort = audioPort;
	}

	public Long getCacheId() {
		return id;
	}

	public void setCacheId(Long id) {
		this.id = id;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getBookType() {
		return bookType;
	}

	private boolean read = false;// �Ѷ�

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setBookType(Integer bookType) {
		this.bookType = bookType;
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

	public Integer getReplyerLongitude() {
		return replyerLongitude;
	}

	public void setReplyerLongitude(Integer replyerLongitude) {
		this.replyerLongitude = replyerLongitude;
	}

	public Integer getReplyerLatitude() {
		return replyerLatitude;
	}

	public void setReplyerLatitude(Integer replyerLatitude) {
		this.replyerLatitude = replyerLatitude;
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

	public String getReplyerNumber() {
		return replyerNumber;
	}

	public void setReplyerNumber(String replyerNumber) {
		this.replyerNumber = replyerNumber;
	}

	public String getReplyerCompany() {
		return replyerCompany;
	}

	public void setReplyerCompany(String replyerCompany) {
		this.replyerCompany = replyerCompany;
	}

	public String getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		try {
			this.useTime = TimeTool.DEFAULT_DATE_FORMATTER.format(TimeTool.DEFAULT_DATE_FORMATTER.parse(useTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUseTime(long useTime) {
		this.useTime = TimeTool.getDateString(useTime);
	}

	public Integer getScheduleCount() {
		return scheduleCount;
	}

	public void setScheduleCount(Integer scheduleCount) {
		this.scheduleCount = scheduleCount;
	}

	public Long getBlockId() {
		return blockId;
	}

	public void setBlockId(Long blockId) {
		this.blockId = blockId;
	}

	public int getForecastDistance() {
		return forecastDistance;
	}

	public void setForecastDistance(int forecastDistance) {
		this.forecastDistance = forecastDistance;
	}

	public int getForecastPrice() {
		return forecastPrice;
	}

	public void setForecastPrice(int forecastPrice) {
		this.forecastPrice = forecastPrice;
	}

	public String getPreTime() {
		return preTime;
	}

	public void setPreTime(String preTime) {
		this.preTime = preTime;
	}

	public Integer getUdpReply() {
		return udpReply;
	}

	public void setUdpReply(Integer udpReply) {
		this.udpReply = udpReply;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getAudioName() {
		return audioName;
	}

	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}

	public Integer getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(Integer evaluate) {
		this.evaluate = evaluate;
	}

	public long getDyTime() {
		return dyTime;
	}

	public void setDyTime(long dyTime) {
		this.dyTime = dyTime;
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
	
	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "BookBean [dyTime=" + dyTime + ", id=" + id + ", cityId=" + cityId + ", cityName=" + cityName + ", bookType=" + bookType + ", passengerPhone=" + passengerPhone + ", passengerName=" + passengerName + ", passengerId=" + passengerId + ", source=" + source + ", sourceName=" + sourceName + ", startAddress=" + startAddress + ", startLongitude=" + startLongitude + ", startLatitude=" + startLatitude + ", endAddress=" + endAddress + ", endLongitude=" + endLongitude + ", endLatitude=" + endLatitude + ", state=" + state + ", priceMode=" + priceMode + ", price=" + price + ", replyerName=" + replyerName + ", replyerId=" + replyerId + ", replyerLongitude=" + replyerLongitude + ", replyerLatitude=" + replyerLatitude + ", replyerPhone=" + replyerPhone + ", replyerType=" + replyerType + ", replyerNumber=" + replyerNumber + ", replyerCompany=" + replyerCompany + ", replyTime=" + replyTime + ", submitTime=" + submitTime + ", useTime=" + useTime + ", scheduleCount=" + scheduleCount
				+ ", blockId=" + blockId + ", forecastDistance=" + forecastDistance + ", forecastPrice=" + forecastPrice + ", preTime=" + preTime + ", udpReply=" + udpReply + ", deviceToken=" + deviceToken + ", audioName=" + audioName + ", evaluate=" + evaluate + "]";
	}

	public int getBookFlags() {
		return bookFlags;
	}

	public void setBookFlags(int bookFlags) {
		this.bookFlags = bookFlags;
	}

}
