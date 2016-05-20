package cn.com.easytaxi.ui.bean;

import java.io.Serializable;

import android.text.TextUtils;
import cn.com.easytaxi.common.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YDUserProfile implements Serializable {

	private String userName;
	private String userPhone;
	

	@Expose
	@SerializedName("_CALLED_NUMBER")
	private int jiedanCounts;	//接单数
	
	
	@Expose
	@SerializedName("_BAD_NUMBER")
	private int badCommentCounts;//坏评
	
	@Expose
	@SerializedName("_GOOD_NUMBER")
	private int goodCommentCounts;//好评
	
	@Expose
	@SerializedName("_CALL_NUMBER")
	private int sendBookCounts;//本月发单数
	
	@Expose
	@SerializedName("_WEIYUE_NUBMER")
	private int weiyueCounts;
	
	@Expose
	@SerializedName("_RECOMMEND_DRIVER")
	private int rmdTaxiCounts;//本月推荐司机数
	
	@Expose
	@SerializedName("_RECOMMEND_PASSENGER")
	private int rmdPassengerCounts;//本月推荐乘客数
	
	@Expose
	@SerializedName("_RANK")
	private int rank;
	
	@Expose
	@SerializedName("_YD_MONEY")
	private long money;
	
	
	
	
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getBadCommentCounts() {
		return badCommentCounts;
	}
	public void setBadCommentCounts(int badCommentCounts) {
		this.badCommentCounts = badCommentCounts;
	}
	public int getGoodCommentCounts() {
		return goodCommentCounts;
	}
	public void setGoodCommentCounts(int goodCommentCounts) {
		this.goodCommentCounts = goodCommentCounts;
	}
	public int getSendBookCounts() {
		return sendBookCounts;
	}
	public void setSendBookCounts(int sendBookCounts) {
		this.sendBookCounts = sendBookCounts;
	}
	public int getWeiyueCounts() {
		return weiyueCounts;
	}
	public void setWeiyueCounts(int weiyueCounts) {
		this.weiyueCounts = weiyueCounts;
	}
	public int getRmdTaxiCounts() {
		return rmdTaxiCounts;
	}
	public void setRmdTaxiCounts(int rmdTaxiCounts) {
		this.rmdTaxiCounts = rmdTaxiCounts;
	}
	public int getRmdPassengerCounts() {
		return rmdPassengerCounts;
	}
	public void setRmdPassengerCounts(int rmdPassengerCounts) {
		this.rmdPassengerCounts = rmdPassengerCounts;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public long getMoney() {
		return money;
	}
	public void setMoney(long money) {
		this.money = money;
	}
	
	public void setUserProfile(YDUserProfile profile){
		this.badCommentCounts = profile.badCommentCounts;
		this.goodCommentCounts = profile.goodCommentCounts;
		this.sendBookCounts = profile.sendBookCounts;
		this.weiyueCounts = profile.weiyueCounts;
		this.rmdTaxiCounts = profile.rmdTaxiCounts;
		
		this.rmdPassengerCounts = profile.rmdPassengerCounts;
		this.rank = profile.rank;
		this.money = profile.money;
	}
	public String getUserName() {
		return userName;
	}
	public void importUserInfo(User user) {
		
		 String name = user.getUserNickName();
		 this.userPhone = user.getPhoneNumber(User._MOBILE);
		 
		 if(TextUtils.isEmpty(name)){
			 this.userName = user.getPhoneNumber(User._MOBILE);
		 }else{
			 this.userName = name;
		 }
	}
 
	
}
