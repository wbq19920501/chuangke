package cn.com.easytaxi.mine.account;

import java.util.Date;

import cn.com.easytaxi.util.TimeTool;

import com.google.gson.annotations.SerializedName;

public class Account {
	public static final int TYPE_ET_MONEY = 1;
	public static final int TYPE_ET_COIN = 2;
	public static final int TYPE_ET_SCORE = 3;
	//类型：1-金钱，2-易达币，3-积分
	private int type;

	private long uId;
	
	@SerializedName("createTime")
	private long date;

	@SerializedName("jyDesc")
	private String remark;

	public Account(int type) {
		setType(type);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getDate() {
		return date;
	}
	
	public String getDateString(){
		return TimeTool.NO_YEAY_DATE_FORMATTER.format(new Date(date));
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getuId() {
		return uId;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	@Override
	public String toString() {
		return "Account [type=" + type + ", uId=" + uId + ", date=" + date + ", remark=" + remark + "]";
	}
}
