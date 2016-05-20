package cn.com.easytaxi.common;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.platform.MainActivityNew;
import cn.com.easytaxi.service.UploadActionLogFileService;

/**
 * 用户行为日志类：包含日志的记录，上传
 * @author Administrator
 *
 */
public class ActionLogUtil {
	/**
	 * 实例引用：日志记录与上传
	 */
	private static ActionLogUtil actionLogUtilInstance = null;
	
	private static final String  FILE_NAME = "actionLog";
	private static final String FILE_TYPE = ".txt";
	
	//日志文件
	public static File actionLogFile = null;
	/**
	 * 获取实例引用
	 * @return
	 */
	public static ActionLogUtil getActionLogUtilInstance(){
		if(null == actionLogUtilInstance){
			actionLogUtilInstance = new ActionLogUtil();
		}
		return actionLogUtilInstance;
	}
	

	/**
	 * 获取日志文件上传Url
	 * @param cityId 城市Id
	 * @param callBack 回调
	 * @return
	 */
	public void getUploadLogUrl(String cityId,Callback<Object> callBack) {
		NewNetworkRequest.getUploadLogUrl(Integer.parseInt(cityId),callBack);
	}
	
	/**
	 * 上传用户行为日志
	 */
	public static void uploadActionLog(String cityId,final Activity activity){
		File file = ActionLogUtil.getLogFile(activity);
		if(file.exists()){
			ActionLogUtil.uploadActionLog(cityId, activity,new Callback<Object>() {
				@Override
				public void handle(Object param) {
					Message msg = (Message)param;
					switch(msg.what){
					case 1://成功上传
						FileUtil.getFileUtilInstance().deleteFile(ActionLogUtil.getActionLogUtilInstance().initLogFile(activity));
						break;
					case 0://上传异常
						break;
					default:
						break;
					}
				}
			});
		}
	}
	
	/**
	 * 上传用户行为日志
	 */
	public static void uploadActionLog(String cityId,final Context context){
		File file = ActionLogUtil.getLogFile(context);
		if(file.exists()){
			ActionLogUtil.uploadActionLog(cityId, context,new Callback<Object>() {
				@Override
				public void handle(Object param) {
					Message msg = (Message)param;
					switch(msg.what){
					case 1://成功上传
						FileUtil.getFileUtilInstance().deleteFile(ActionLogUtil.getActionLogUtilInstance().initLogFile(context));
						AppLog.LogD("日志上传成功");
						break;
					case 0://上传异常
						AppLog.LogD("日志上传异常");
						break;
					default:
						break;
					}
					Intent intent = new Intent(context, UploadActionLogFileService.class);
					context.stopService(intent);
				}
			});
		}else{
			Intent intent = new Intent(context, UploadActionLogFileService.class);
			context.stopService(intent);
		}
	}
	
	/**
	 * 上传用户行为日志
	 */
	public static void uploadActionLog(String cityId,final Activity activity,final Callback<Object> callback){
		final ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		actionLogUtil.getUploadLogUrl(cityId, new Callback<Object>() {
			@Override
			public void handle(Object param) {
				String uploadLogUrl = param.toString();
				actionLogUtil.uploadFile(uploadLogUrl,ActionLogUtil.getLogFile(activity),callback); 
			}

		});
	}
	
	
	/**
	 * 上传用户行为日志
	 */
	public static void uploadActionLog(String cityId,final Context context,final Callback<Object>callBack){
		final ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		actionLogUtil.getUploadLogUrl(cityId, new Callback<Object>() {
			@Override
			public void handle(Object param) {
				String uploadLogUrl = param.toString();
				actionLogUtil.uploadFile(uploadLogUrl,actionLogUtil.initLogFile(context),callBack); 
			}

		});
	}

	
	/**
	 * 如果日志文件不存在，则创建
	 * return File :返回日志文件
	 */
	public File initLogFile(Activity activity){
        FileUtil fileUtil = FileUtil.getFileUtilInstance();
		File dir = fileUtil.getAppDataDir(activity.getBaseContext(),FILE_NAME);
		fileUtil.createFileOrDir(dir);
		File actionLogfile = new File(dir.getPath() + "/"+FILE_NAME+FILE_TYPE);
		fileUtil.createFileOrDir(actionLogfile);
		return actionLogfile;
	}
	
	/**
	 * 获取日志文件
	 * @param activity
	 * @return
	 */
	public static File getLogFile(Activity activity){
        FileUtil fileUtil = FileUtil.getFileUtilInstance();
		File dir = fileUtil.getAppDataDir(activity.getBaseContext(),FILE_NAME);
		fileUtil.createFileOrDir(dir);
		File actionLogfile = new File(dir.getPath() + "/"+FILE_NAME+FILE_TYPE);
		return actionLogfile;
	}
	
	/**
	 * 获取日志文件
	 * @param activity
	 * @return
	 */
	public static File getActionLogFile(Context context){
		if(actionLogFile == null){
			FileUtil fileUtil = FileUtil.getFileUtilInstance();
			File dir = fileUtil.getAppDataDir(context, FILE_NAME);
			fileUtil.createFileOrDir(dir);
			actionLogFile = new File(dir.getPath() + "/" + FILE_NAME+ FILE_TYPE);
		}
		return actionLogFile;
	}
	
	/**
	 * 获取日志文件
	 * @param activity
	 * @return
	 */
	public static File getLogFile(Context context){
        FileUtil fileUtil = FileUtil.getFileUtilInstance();
		File dir = fileUtil.getAppDataDir(context,FILE_NAME);
		fileUtil.createFileOrDir(dir);
		File actionLogfile = new File(dir.getPath() + "/"+FILE_NAME+FILE_TYPE);
		return actionLogfile;
	}
	
	
	/**
	 * 如果日志文件不存在，则创建
	 * return File :返回日志文件
	 */
	public File initLogFile(Context context){
        FileUtil fileUtil = FileUtil.getFileUtilInstance();
		File dir = fileUtil.getAppDataDir(context,FILE_NAME);
		fileUtil.createFileOrDir(dir);
		File actionLogfile = new File(dir.getPath() + "/"+FILE_NAME+FILE_TYPE);
		fileUtil.createFileOrDir(actionLogfile);
		return actionLogfile;
	}
	
	
	/**
	 * 控件点击事件  写入到日志文件
	 * @param view
	 * @param actionLogfile 日志文件
	 */
	public void writeViewClickLog(View v,File actionLogfile){
		String[] map = (String[])v.getTag();
        int menuId = Integer.parseInt(map[0]);
        String value = map[1];
        
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data = f.format(new Date());
		
		writeLogToFile(actionLogfile, data, menuId, value);
	}
	
	/**
	 * 控件用tag保存id和value时用此方法:控件点击事件  写入到日志文件
	 * @param view
	 * @param value 用户动作需要记录到日志文件的值
	 * @param actionLogfile 日志文件
	 */
	public void writeViewClickLog(View v,File actionLogfile,String value){
		String[] map = (String[])v.getTag();
        int menuId = Integer.parseInt(map[0]);
//        String value = map[1];
        if(null == value){
        	value = map[1];
        }
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data = f.format(new Date());
		writeLogToFile(actionLogfile, data, menuId, value);
	}
	
	/**
	 * 控件没有用tag保存id和value时用此方法:控件点击事件  写入到日志文件
	 * @param context 上下文
	 * @param actionLogfile	日志文件
	 * @param arrayResourceId  文件中配置资源数据的id
	 * @param value	用户动作需要记录到日志文件的值
	 */
	public void writeViewClickLog(Context context,File actionLogfile,int sourceId,String value){
        String[] str = context.getResources().getStringArray(sourceId);
        int menuId = Integer.parseInt(str[0]);
        if(null == value ){
        	value = str[1];
        }
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String data = f.format(new Date());
		writeLogToFile(actionLogfile, data, menuId, value);
	}
	
	/**
	 * 向日志文件中记录行为
	 * @param actionLogfile 日志文件
	 * @param data 时间
	 * @param menuId 控件的标识Id
	 * @param value 该动作记录的值
	 */
	public void writeLogToFile(File actionLogfile,String data,int menuId,String value ){
		FileUtil fileUtil = FileUtil.getFileUtilInstance();
		
		JSONObject actionLogJson = new JSONObject() ;
		//获取之前日志文件中的记录
		try {
			byte[] bt = fileUtil.readFileData(actionLogfile);
			String str = new String(bt);
			if(str.length() > 0){
				actionLogJson = new JSONObject(str);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		//生成Json数据写入文件
		try{
			actionLogJson.put("userId",ETApp.getInstance().getCurrentUser().getPhoneNumber("_MOBILE"));
			actionLogJson.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			actionLogJson.put("clientVersion", ETApp.getInstance().getMobileInfo().getVerisonCode());

			actionLogJson.put("cityId", MainActivityNew.cityId);
			actionLogJson.put("cityName",MainActivityNew.currentCityName);
			//本次事件
			JSONObject actionJSObject = new JSONObject();
			actionJSObject.put("data", data);
			actionJSObject.put("menuId", menuId);
			actionJSObject.put("value", value);
			//
			JSONArray actions;
			if(!actionLogJson.isNull("actions") ){
				try{
					actions = actionLogJson.getJSONArray("actions");
				}catch(Exception e){
					e.printStackTrace();
					actions = new JSONArray();
				}
			}else{
				actions = new JSONArray();
			}
			//添加本次事件
			actions.put(actionJSObject);
			
			actionLogJson.put("actions",actions);
			fileUtil.writeFile(actionLogJson.toString(), actionLogfile);
			
			AppLog.LogD("AAAAAAAAAAAA","写日志"+actionJSObject.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置View的tag数据
	 * @param v
	 * @param activity
	 * @param sourceId
	 */
	public static void setViewTag(View v,Activity activity,int sourceId){
		v.setTag(activity.getResources().getStringArray(sourceId));
	}
	
	/**
	 * 设置View的tag数据
	 * @param v
	 * @param context
	 * @param sourceId
	 */
	public static void setViewTag(View v,Context context,int sourceId){
		v.setTag(context.getResources().getStringArray(sourceId));
	}
	
	/**
	 * 写入用户动作和Arrays中默认配置的动作值到日志文件
	 * @param v  被点击的view
	 * @param activity 
	 */
	public static void writeActionLog(View v,Activity activity){
		ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		File file = actionLogUtil.initLogFile(activity);
		actionLogUtil.writeViewClickLog(v, file);
	}
	
	/**
	 * 写入用户动作和动作上传值到日志文件
	 * @param v  被点击的view
	 * @param value 用户动作需要记录到日志文件的值
	 * @param activity 
	 */
	/*public static void writeActionLog(View v,Activity activity,String value){
		ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		File file = actionLogUtil.initLogFile(activity);
		actionLogUtil.writeViewClickLog(v, file,value);
	}*/
	
	/**
	 * 写入用户动作到日志文件
	 * @param v  被点击的view
	 * @param context  上下文
	 */
	public static void writeActionLog(View v,Context context){
		ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		File file = actionLogUtil.initLogFile(context);
		actionLogUtil.writeViewClickLog(v, file);
	}
	
	
	/**
	 * 记录日志
	 * @param sourceId 文件中配置资源数据的id
	 * @param value 用户动作需要记录到日志文件的值
	 */
	public static void writeActionLog(Activity activity ,int sourceId,String value){
		//写入日志
		if(cn.com.easytaxi.common.Const.isActionLogCouldUpload){
			File file = ActionLogUtil.getActionLogFile(activity.getBaseContext());
			ActionLogUtil.getActionLogUtilInstance().writeViewClickLog(activity,file, sourceId,value);
		}
	}
	
	/**
	 * 记录日志
	 * @param sourceId 文件中配置资源数据的id
	 * @param value 用户动作需要记录到日志文件的值
	 */
	public static void writeActionLog(Context context ,int sourceId,String value){
		//写入日志
		if(cn.com.easytaxi.common.Const.isActionLogCouldUpload){
			File file = ActionLogUtil.getActionLogFile(context);
			ActionLogUtil.getActionLogUtilInstance().writeViewClickLog(context,file, sourceId,value);
		}
	}
	
	/**
	 * 上传日志文件到服务器
	 * @param uploadLogUrl
	 * @param file
	 * @param callback
	 */
	public void uploadFile(final String uploadLogUrl,final File file,final Callback<Object>callback) {
		new Thread(new Runnable() {
			public void run() {
				try {
					//构建post
					String url = uploadLogUrl;
					HttpPost httpPost = new HttpPost(new URL(url).toURI());
					MultipartEntity m = new MultipartEntity();
					m.addPart("params", new StringBody("{\"op\":\"setObj\"}"));
					m.addPart("file", new FileBody(file));
					httpPost.setEntity(m);
					//执行post
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() != 200) {
						throw new RuntimeException("HTTP request Exception");
					}
					String s = EntityUtils.toString(response.getEntity(), "UTF-8");
					System.out.println(s);
					//处理返回结果
					JSONObject ret = new JSONObject(s);
					int error = Integer.parseInt(ret.get("error").toString());
					Message msg = new Message();
					if (0 == error) {// 上传成功
						msg.what = 1;
					} else {
						msg.what = 0;
					}
					msg.obj = s;
					callback.handle(msg);
					
					AppLog.LogD("AAAAAAAAAAAA", "上传日志成功");
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;//0表示失败
					msg.obj = null;
					callback.handle(msg);
					e.printStackTrace();
					
					AppLog.LogD("AAAAAAAAAAAA", "上传日志异常");
//					throw new RuntimeException(e);
				}
			}
		}).run();
	}
}
