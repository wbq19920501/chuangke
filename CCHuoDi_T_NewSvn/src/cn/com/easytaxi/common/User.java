package cn.com.easytaxi.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.com.easytaxi.ui.bean.YDUserProfile;

public class User implements Serializable {

	public static String _NICKNAME = "_NICKNAME";
	public static String _PUID = "_PUID";
	public static String _SEX = "_SEX";
	public static String _MOBILE = "_MOBILE";
	public static String _MOBILE_NEW = "_MOBILE_NEW";
	
	public static String _ISLOGIN = "_ISLOGIN";
	public static String _ISNEW_LONGIN_MODE = "_ISNEW_LONGIN_MODE";
	public static String _LOGIN_CANCLED = "_LOGIN_CANCLED";
	public static String _LOGIN_LOGIN = "_LOGIN_LOGIN";
 

	private YDUserProfile ydUserProfile;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7154687028347913234L;

	private String userRealName;
	private String userNickName="";
	private long passengerId;

	private Map<String, String> userPhone = new HashMap<String, String>();
	private int sex;
	private boolean isLogin = false;

	public boolean isLogin() {
	/*	String mobile = userPhone.get("_MOBILE");		
		
		if (StringUtils.isEmpty(mobile)) {
			isLogin = false;
		} else {
			isLogin = true;
		}*/
		return isLogin;
	}

	public YDUserProfile getYdUserProfile() {
		return ydUserProfile;
	}

	public void setYdUserProfile(YDUserProfile ydUserProfile) {
		this.ydUserProfile = ydUserProfile;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public String getPhoneNumber(String key) {
		return userPhone.get(key);
	}

	public void setPhoneNumber(String key, String value) {
		userPhone.put(key, value);
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(long passengerId) {
		this.passengerId = passengerId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[userRealName : ").append(userRealName ).append(",")
		.append("userNickName : ").append(userNickName).append(",")
		.append("passengerId : ").append(passengerId).append(",")
		.append("_MOBILE : ").append(userPhone.get(_MOBILE));
		 
		return sb.toString();
	}
	
	
}
