package cn.com.easytaxi.mine;

import java.io.Serializable;

import android.content.Context;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.platform.service.CacheBean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 业务菜单
 * 
 * @author magi
 * 
 */
public class MenuBean extends CacheBean implements Serializable {
	public final static int ACTION_TYPE_URL = 1;
	public final static int ACTION_TYPE_ACTIVITY = 2;
	public final static int ACTION_TYPE_CALLBACK = 3;

	// _STATE:’状态(0:无,1:热门,2,新,3.推荐)’
	public final static int STATE_NONE = 0;
	public final static int STATE_HOT = 1;
	public final static int STATE_NEW = 2;
	public final static int STATE_RECOMMEND = 3;

	private boolean internal;

	private int imgRes;

	public int getImgRes() {
		return imgRes;
	}

	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}
	// _STATE:’状态(0:无,1:热门,2,新,3.推荐)’
	@SerializedName("_STATE")
	private int state;

	@SerializedName("_NAME")
	private String name;

	@SerializedName("_SEQ")
	private int seq;

	@SerializedName("_ACTION_TYPE")
	private int actionType;

	// actionType=URL|ACTIVITY
	@SerializedName("_ACTION")
	private String action;

	// actionType=CALLBACK
	@Expose
	private Callback<Context> callback;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public Callback<Context> getCallback() {
		return callback;
	}

	public void setCallback(Callback<Context> callback) {
		this.callback = callback;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

}
