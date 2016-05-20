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
 * 业务订单
 * 
 * @author magi
 * 
 */
public class BookBean extends CacheBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private long dyTime = 0;

	/**
	 * 订单编号
	 */
	@Expose
	private Long id;

	/**
	 * 订单操作动作标示
	 */
	private int bookFlags;

	/**
	 * 订单城市
	 */
	@Expose
	private Integer cityId;

	/**
	 * 订单城市
	 */
	@Expose
	private String cityName;

	/**
	 * 订单类型 1:新版即时订单 2:旧版本即时订单 3:新版本预约订单 4:旧版本预约订单 5:预约订单
	 */
	@Expose
	private Integer bookType = 5;
	/**
	 * 搬家定单的价格
	 */
	private String fee;
	/**
	 * 0 货物订单，1搬家订单
	 */
	private int order_type;

	/**
	 * 乘客电话
	 */
	@Expose
	private String passengerPhone = "";

	/**
	 * 乘客姓名
	 */
	@Expose
	private String passengerName = "";

	/**
	 * 乘客ID
	 */
	@Expose
	private Long passengerId;

	/**
	 * 该订单的积分
	 */
	private Integer score;

	/**
	 * 该订单的消费点数,易达币
	 */
	private Integer points;

	/**
	 * 是否显示倒计时
	 */
	private Integer count = 0;

	/**
	 * 订单来源类型(IOS为2,Android为1)
	 */
	@Expose
	private Integer source;

	/**
	 * 订单来源类型描述 IOS:iso.版本号 ANDROID:android.版本号
	 */
	@Expose
	private String sourceName;

	/**
	 * 上车地点
	 */
	@Expose
	@SerializedName("startAddr")
	private String startAddress = "";

	/**
	 * 上车经度
	 */
	@Expose
	private Integer startLongitude = 0;

	/**
	 * 上车纬度
	 */
	@Expose
	private Integer startLatitude = 0;

	/**
	 * 下车地点
	 */
	@Expose
	private String endAddress = "";

	/**
	 * 下车经度
	 */
	@Expose
	@SerializedName("endLng")
	private Integer endLongitude = 0;

	/**
	 * 下车纬度
	 */
	@Expose
	@SerializedName("endLat")
	private Integer endLatitude = 0;

	@Expose
	@SerializedName("bookState")
	private Integer state = 0;

	/**
	 * 价格模式 0:加价模式 1:一口价模式
	 */
	@Expose
	private Integer priceMode = 0;

	/**
	 * 价格
	 */
	@Expose
	private Integer price = 0;

	/**
	 * 应答方名称(司机姓名)
	 */
	@Expose
	private String replyerName;

	/**
	 * 应答方ID
	 */
	@Expose
	private Long replyerId;

	/**
	 * 应答方经度
	 */
	@Expose
	private Integer replyerLongitude;

	/**
	 * 应答方纬度
	 */
	@Expose
	private Integer replyerLatitude;

	/**
	 * 应答方电话
	 */
	@Expose
	private String replyerPhone;

	/**
	 * 回应者类型标识
	 */
	@Expose
	private Integer replyerType;

	/**
	 * 应答方编号（车牌）
	 */
	@Expose
	private String replyerNumber;

	/**
	 * 应答方公司
	 */
	@Expose
	private String replyerCompany;

	/**
	 * 响应时间
	 */
	@Expose
	private String replyTime;

	/**
	 * 下单时间
	 */
	@Expose
	private String submitTime = Calendar.getInstance().getTime().toString();

	/**
	 * 用车时间
	 */
	@Expose
	private String useTime;

	/**
	 * 被调度的轮数
	 */
	@Expose
	private Integer scheduleCount = 0;

	/**
	 * 所属于区块ID
	 */
	@Expose
	private Long blockId;

	@Expose
	private int forecastDistance;

	@Expose
	private int forecastPrice;

	/**
	 * 上次推送时间
	 */
	@Expose
	private String preTime = Calendar.getInstance().getTime().toString();

	/**
	 * 判断UPD发送客户端是否收到 0:重发关闭 1:向乘客端重发司机接单结果 2:向司机端重乘客取消消息 3.向乘客端重发司机取消消息
	 */
	@Expose
	private Integer udpReply;

	/**
	 * IOS设备唯一标识，用于消息推送
	 */
	@Expose
	private String deviceToken;

	@Expose
	private String audioName;

	/**
	 * 录音文件下载地址
	 */
	private String audioIp;

	/**
	 * 录音文件下载端口
	 */
	private Integer audioPort;

	/**
	 * 评价 150:非常满意 100:好评 10:差评 500:乘客取消
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

	private boolean read = false;// 已读

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
