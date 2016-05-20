package cn.com.easytaxi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YDMenus implements Serializable{
	
	@Expose
	@SerializedName("datas")
	public ArrayList<YDMenu> menus = new ArrayList<YDMenus.YDMenu>(2);
	
	@Expose
	@SerializedName("error")
	public int error;
	@Expose
	@SerializedName("errormsg")
	public String errormsg;

	public static class YDMenu {
		
		@Expose
		@SerializedName("_INDEX")
		public long index;
		
		
		@Expose
		@SerializedName("_SEQ")
		public int sequence;
 
		
		@Expose
		@SerializedName("_NAME")
		public String title;
		
		@Expose
		@SerializedName("_ACTION")
		public String action;
		
		@Expose
		@SerializedName("_ACTION_TYPE")
		public int actionType;
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[_ID =").append(index).append(",")
			.append("_NAME = ").append(title)
			.append(",_ACTION = ").append(action)
			.append(",_SEQ = ").append(sequence)
			.append(",_ACTION_TYPE = ").append(actionType)
			.append("]");
			
			return sb.toString();
		}
	}

}
