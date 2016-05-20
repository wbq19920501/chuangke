package cn.com.easytaxi.ui.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.easytaxi.platform.service.CacheBean;

import com.google.gson.annotations.SerializedName;

/**
 * 业务订单
 * 
 * @author magi
 * 
 */
public class NewBookBean extends CacheBean implements Serializable {
	// ui相关
	private boolean newOrder = true;// 新单
	private int speakCount = 0;// 播报次数
	private boolean read = false;// 已读
	private boolean highLight = false;// 高亮
	// ui相关end

	public static final int INCREASES_PRICE = 0;

	public static final int QUOTED_PRICE = 1;

	@SerializedName("_ID")
	public long id;
	
	/**
	 * 是否显示倒计时
	 */
	@SerializedName("_COUNT")
	private int count = 0;

	/**
	 * 该订单的积分
	 */
	@SerializedName("_SCORE")
	private Integer score;

	/**
	 * 该订单的消费点数
	 */
	@SerializedName("_POINTS")
	private Integer points;

	/**
	 * 好：100-200 差：<100 乘客取消：>200
	 */
	@SerializedName("_EVALUATE")
	private Integer evaluate;

	/**
	 * 乘客电话
	 */
	@SerializedName("_PASSENGER_PHONE")
	private String passengerPhone = "";

	/**
	 * 乘客姓名
	 */
	@SerializedName("_PASSENGER_NAME")
	private String passengerName = "";

	/**
	 * 乘客ID
	 */
	@SerializedName("_PASSENGER_ID")
	private Long passengerId;

	/**
	 * 订单来源类型(IOS为2,Android为1)
	 */
	@SerializedName("_SOURCE")
	private Integer source;

	/**
	 * 订单来源类型描述 IOS:iso.版本号 ANDROID:android.版本号
	 */
	@SerializedName("_SOURCE_NAME")
	private String sourceName;

	/**
	 * 上车地点
	 */
	@SerializedName("_START_ADDRESS")
	private String startAddress = "";

	/**
	 * 上车经度
	 */
	@SerializedName("_START_LONGITUDE")
	private Integer startLongitude = 0;

	/**
	 * 上车纬度
	 */
	@SerializedName("_START_LATITUDE")
	private Integer startLatitude = 0;

	/**
	 * 下车地点
	 */
	@SerializedName("_END_ADDRESS")
	private String endAddress = "";

	/**
	 * 下车经度
	 */
	@SerializedName("_END_LONGITUDE")
	private Integer endLongitude = 0;

	/**
	 * 下车纬度
	 */
	@SerializedName("_END_LATITUDE")
	private Integer endLatitude = 0;

	/**
	 * 订单状态 0:未处理 1:有车抢答 2:乘客取消 3:司机取消 4:无车应答 5:处理中
	 */
	@SerializedName("_STATE")
	private Integer state = 0;

	/**
	 * 价格模式 0:加价模式 1:一口价模式
	 */
	@SerializedName("_PRICE_MODE")
	private Integer priceMode = 0;

	/**
	 * 价格
	 */
	@SerializedName("_PRICE")
	private Integer price = 0;

	/**
	 * 应答方名称(出租车车牌等...)
	 */
	@SerializedName("_REPLYER_NAME")
	private String replyerName;

	/**
	 * 应答方ID
	 */
	@SerializedName("_REPLYER_ID")
	private Long replyerId;

	/**
	 * 应答方电话
	 */
	@SerializedName("_REPLYER_PHONE")
	private String replyerPhone;

	/**
	 * 应答时间
	 */
	@SerializedName("_REPLY_TIME")
	private String replyTime;

	/**
	 * 回应者类型标识
	 */
	@SerializedName("_REPLYER_TYPE")
	private Integer replyerType;

	/**
	 * 订单类型 1:即时订单 2:预约订单
	 */
	@SerializedName("_BOOK_TYPE")
	private Integer bookType;

	/**
	 * 用车时间
	 */
	@SerializedName("_USE_TIME")
	private long useTime;

	/**
	 * 预计距离
	 */
	@SerializedName("_FORECAST_DISTANCE")
	private Integer forecastDistance;
	/**
	 * 录音文件id
	 */
	@SerializedName("_AUDIO_NAME")
	private String audioName;

	/**
	 * 录音文件下载地址
	 */
	@SerializedName("_AUDIO_IP")
	private String audioIp;

	/**
	 * 录音文件下载端口
	 */
	@SerializedName("_AUDIO_PORT")
	private int audioPort;

	/**
	 * 预计价格
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
			return "id:" + getCacheId() + ",state:" + getState() + ",evluate:" + getEvaluate() + ",usetime:" + new SimpleDateFormat("MM月dd日 hh点mm分").format(getUseTime()) + ",replyerId:" + getReplyerId();
		} else {
			return "id:" + getCacheId() + ",state:" + getState() + ",evluate:" + getEvaluate() + ",replyerId:" + getReplyerId();
		}
	}
}
