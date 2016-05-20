package cn.com.easytaxi.onetaxi.common;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.onetaxi.MainActivityNew;
import cn.com.easytaxi.platform.service.EasyTaxiCmd;
import cn.com.easytaxi.platform.service.OneBookService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * ҵ�񶩵�
 * 
 * @author magi
 * 
 */
public class BookBean implements Serializable {

	/**
	 * �������
	 */
	@Expose
	@SerializedName("id")
	private long id;

	private long timeOut = 30000;

	private int progress = 0;
	
	private boolean onlinePayment;

	/**
	 * �˿͵绰
	 */
	@Expose
	@SerializedName("passengerPhone")
	private String passengerPhone = "";

	/**
	 * �˿�����
	 */
	@Expose
	@SerializedName("passengerName")
	private String passengerName = "";

	@Expose
	@SerializedName("audioName")
	private String audioName="";
	/**
	 * �˿�ID
	 */
	@Expose
	@SerializedName("passengerId")
	private long passengerId;

	/**
	 * ��������
	 */
	@Expose
	@SerializedName("type")
	private int type = 1;

	/**
	 * ������Դ����(IOSΪ2,AndroidΪ1)
	 */
	@Expose
	@SerializedName("source")
	private int source;

	/**
	 * ������Դ�������� IOS:iso.�汾�� ANDROID:android.�汾��
	 */
	@Expose
	@SerializedName("sourceName")
	private String sourceName="";

	/**
	 * �ϳ��ص�
	 */
	@Expose
	@SerializedName("startAddress")
	private String startAddress = "";

	/**
	 * �ϳ�����
	 */
	@Expose
	@SerializedName("startLongitude")
	private int startLongitude = 0;

	/**
	 * �ϳ�γ��
	 */
	@Expose
	@SerializedName("startLatitude")
	private int startLatitude = 0;

	/**
	 * �³��ص�
	 */
	@Expose
	@SerializedName("endAddress")
	private String endAddress = "";

	/**
	 * �³�����
	 */
	@Expose
	@SerializedName("endLongitude")
	private int endLongitude = 0;

	/**
	 * �³�γ��
	 */
	@Expose
	@SerializedName("endLatitude")
	private int endLatitude = 0;

	/**
	 * ����״̬ 0:δ���� 1:�г����� 2:�˿�ȡ�� 3:˾��ȡ�� 4:�޳�Ӧ�� 5:������
	 */
	@Expose
	@SerializedName("state")
	private int state = 0;

	/**
	 * �۸�ģʽ 0:�Ӽ�ģʽ 1:һ�ڼ�ģʽ
	 */
	@Expose
	@SerializedName("priceMode")
	private int priceMode = 0;

	/**
	 * �۸�
	 */
	@Expose
	@SerializedName("price")
	private int price = 0;

	/**
	 * ��Ӧ�߾���
	 */
	@Expose
	@SerializedName("replyerLongitude")
	private int replyerLongitude;

	/**
	 * ��Ӧ��ά��
	 */
	@Expose
	@SerializedName("replyerLatitude")
	private int replyerLatitude;

	/**
	 * Ӧ������(���⳵���Ƶ�...)
	 */
	@Expose
	@SerializedName("replyerName")
	private String replyerName="";

	/**
	 * Ӧ��ID
	 */
	@Expose
	@SerializedName("replyerId")
	private long replyerId;

	/**
	 * Ӧ�𷽵绰
	 */
	@Expose
	@SerializedName("replyerPhone")
	private String replyerPhone="";

	/**
	 * ��Ӧ�����ͱ�ʶ
	 */
	@Expose
	@SerializedName("replyerType")
	private int replyerType;

	/**
	 * Ӧ�𷽱�ţ����ƣ�
	 */
	@Expose
	@SerializedName("replyerNumber")
	private String replyerNumber="";

	/**
	 * Ӧ�𷽹�˾
	 */
	@Expose
	@SerializedName("replyerCompany")
	private String replyerCompany="";

	/**
	 * �������� 1:��ʱ���� 2:ԤԼ����
	 */
	@Expose
	@SerializedName("bookType")
	private int bookType = 1;

	/**
	 * �ó�ʱ��
	 */
	@Expose
	@SerializedName("useTime")
	private String useTime="";

	/**
	 * Ԥ�ƾ���
	 */
	@Expose
	@SerializedName("forecastDistance")
	private int forecastDistance;

	/**
	 * Ԥ�Ƽ۸�
	 */
	@Expose
	@SerializedName("forecastPrice")
	private int forecastPrice;

	@Expose
	@SerializedName("cityId")
	private int cityId;

	@Expose
	@SerializedName("cityName")
	private String cityName="";

	private int dispStat;
	
	private String  sendMode = "wordInput";
	
	private String udp003Info="����Ŭ����Ϊ�����ȳ��������Ժ�...";

	private Timer timer;

	private ProgressTimeTask task;

	public int getCityId() {
		return cityId;
	}

	public int getDispStat() {
//		AppLog.LogD("getDispStat : " + dispStat);
		return dispStat;
	}

 

	public String getUdp003Info() {
		 
		return udp003Info;
	}

	public void setUdp003Info(String udp003Info) {
		 
		this.udp003Info = udp003Info;
	}

	public String getSendMode() {
		return sendMode;
	}

	public void setSendMode(String sendMode) {
		this.sendMode = sendMode;
	}

	public void setDispStat(int dispStat) {
		
		AppLog.LogD(" setDispStat dispStat : "+dispStat);
		this.dispStat = dispStat;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getReplyerLongitude() {
		return replyerLongitude;
	}

	public void setReplyerLongitude(int replyerLongitude) {
		this.replyerLongitude = replyerLongitude;
	}

	public int getReplyerLatitude() {
		return replyerLatitude;
	}

	public String getAudioName() {
		return audioName;
	}

	public void setAudioName(String audioName) {
		this.audioName = audioName;
	}

	public void setReplyerLatitude(int replyerLatitude) {
		this.replyerLatitude = replyerLatitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(long passengerId) {
		this.passengerId = passengerId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
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

	public int getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(int startLongitude) {
		this.startLongitude = startLongitude;
	}

	public int getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(int startLatitude) {
		this.startLatitude = startLatitude;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public int getEndLongitude() {
		return endLongitude;
	}

	public void setEndLongitude(int endLongitude) {
		this.endLongitude = endLongitude;
	}

	public int getEndLatitude() {
		return endLatitude;
	}

	public void setEndLatitude(int endLatitude) {
		this.endLatitude = endLatitude;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getPriceMode() {
		return priceMode;
	}

	public void setPriceMode(int priceMode) {
		this.priceMode = priceMode;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getReplyerName() {
		return replyerName;
	}

	public void setReplyerName(String replyerName) {
		this.replyerName = replyerName;
	}

	public long getReplyerId() {
		return replyerId;
	}

	public void setReplyerId(long replyerId) {
		this.replyerId = replyerId;
	}

	public String getReplyerPhone() {
		return replyerPhone;
	}

	public void setReplyerPhone(String replyerPhone) {
		this.replyerPhone = replyerPhone;
	}

	public int getReplyerType() {
		return replyerType;
	}

	public void setReplyerType(int replyerType) {
		this.replyerType = replyerType;
	}

	public int getBookType() {
		return bookType;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		this.useTime = useTime;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[");
		sb.append("id:").append(id).append(", ");
		sb.append("passengerPhone:").append(passengerPhone).append(", ");
		sb.append("passengerId:").append(passengerId).append(", ");
		sb.append("passengerName:").append(passengerName).append(", ");
		sb.append("type").append(type).append(", ");
		sb.append("source:").append(source).append(", ");
		sb.append("sourceName:").append(sourceName).append(", ");
		sb.append("startAddress:").append(startAddress).append(", ");
		sb.append("startLongitude:").append(startLongitude).append(", ");
		sb.append("startLatitude:").append(startLatitude).append(", ");
		sb.append("endAddress:").append(endAddress).append(", ");
		sb.append("endLatitude:").append(endLatitude).append(", ");
		sb.append("endLongitude:").append(endLongitude).append(", ");
		sb.append("state:").append(state).append(", ");
		sb.append("priceMode:").append(priceMode).append(", ");
		sb.append("price:").append(price).append(", ");
		sb.append("replyerLongitude:").append(replyerLongitude).append(", ");
		sb.append("replyerLatitude:").append(replyerLatitude).append(", ");
		sb.append("replyerName:").append(replyerName).append(", ");
		sb.append("replyerId:").append(replyerId).append(", ");
		sb.append("replyerPhone:").append(replyerPhone).append(", ");

		sb.append("replyerType:").append(replyerType).append(", ");
		sb.append("replyerNumber:").append(replyerNumber).append(", ");
		sb.append("replyerCompany:").append(replyerCompany).append(", ");
		sb.append("bookType:").append(bookType).append(", ");
		sb.append("useTime:").append(useTime).append(", ");
		sb.append("forecastDistance:").append(forecastDistance).append(", ");
		sb.append("forecastPrice:").append(forecastPrice).append(", ");
		sb.append("cityId:").append(cityId).append(", ");
		sb.append("cityName:").append(cityName).append(" ");

		sb.append("]");
		return sb.toString();

	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void startWait() {
		
		OneBookService.maybeAquireWakelock();
		timer = new Timer();
		int period = 250;
		task = new ProgressTimeTask(period);
		timer.schedule(task, 100, period);
	}

	public void stopWait() {
		if (task != null) {
			task.cancel();
		}
		task = null;
		if(timer != null){			
			timer.cancel();
			timer = null;
		}
	}

	class ProgressTimeTask extends TimerTask {

		private int delay = 1000;

		public ProgressTimeTask(int delay) {
			this.delay = delay;
		}

		@Override
		public void run() {
			progress += delay;
			// AppLog.LogD(">>== " + progress + " -- timeOut "+ timeOut);
			if (progress >= timeOut) {
				progress = -200; // ��ʱ
				AppLog.LogD("bookId "+ id + " , time out " );
				dispStat =MainActivityNew.BOOK_STAT_REBOOK;//BOOK_STAT_REBOOK;
				Intent intent = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
				intent.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_TIMEOUT);
				intent.putExtra("progress", progress);
				intent.putExtra("bookId", id);
				ETApp.getInstance().sendBroadcast(intent);
				while(!this.cancel());
				
			} else {
				
				if(dispStat == 3){
					while(!this.cancel());
					return;
				}else{
//					System.gc();
					Intent intent = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
					intent.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_WAITTING);
					intent.putExtra("progress", (int) (timeOut) - progress);
					intent.putExtra("bookId", id);
					
					
					ETApp.getInstance().sendBroadcast(intent);
				}
			}
		}

	}

	public boolean isOnlinePayment() {
		return onlinePayment;
	}

	public void setOnlinePayment(boolean onlinePayment) {
		this.onlinePayment = onlinePayment;
	}

}
