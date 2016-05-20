package cn.com.easytaxi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YDUserComments implements Serializable {
	@Expose
	@SerializedName("error")
	public int error;
	
	public int lastId;
	  
	@Expose
	@SerializedName("datas")
	public ArrayList<YDUserComment> comments = new ArrayList<YDUserComments.YDUserComment>(12);
	
	public YDUserComments(){
//		for(int i = 0 ;i < 4; i++){
//			YDUserComment o = new YDUserComment();
//			o.userName = i+"sb";
//			o.commentContent=i+"sb"+i+"sb"+i+"sb";
//			comments.add(o);
//		}
	}
	
	
	public void mockData(int count , int lastId){
		for(int i = 0 ;i < count; i++){
			YDUserComment o = new YDUserComment();
			o.userName = lastId+"___" +i+"我是假的";
			o.commentContent=lastId+"___"+ i+"我是假的"+i+"我是假的"+i+"我是假的";
			comments.add(o);
		}
	}
	
	public static class YDUserComment{
		@Expose
		@SerializedName("_NAME")
		public String userName;
		
		@Expose
		@SerializedName("imgUri")
		public String imgUri;
		
		@Expose
		@SerializedName("_SUGGESTER_ID")
		public long suggesterId;
	 
		@Expose
		@SerializedName("_ID")
		public String id;
	 
		@Expose
		@SerializedName("_CREATE_TIME")
		public long commentTime;
		
		@Expose
		@SerializedName("_CONTENT")
		public String commentContent;
		
		/** 0  好评， 1 差评*/
		@Expose
		@SerializedName("_VALUE")
		public int commentType;
		
		
	}
	
}
