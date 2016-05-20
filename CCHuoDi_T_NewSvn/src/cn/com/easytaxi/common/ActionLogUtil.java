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
 * �û���Ϊ��־�ࣺ������־�ļ�¼���ϴ�
 * @author Administrator
 *
 */
public class ActionLogUtil {
	/**
	 * ʵ�����ã���־��¼���ϴ�
	 */
	private static ActionLogUtil actionLogUtilInstance = null;
	
	private static final String  FILE_NAME = "actionLog";
	private static final String FILE_TYPE = ".txt";
	
	//��־�ļ�
	public static File actionLogFile = null;
	/**
	 * ��ȡʵ������
	 * @return
	 */
	public static ActionLogUtil getActionLogUtilInstance(){
		if(null == actionLogUtilInstance){
			actionLogUtilInstance = new ActionLogUtil();
		}
		return actionLogUtilInstance;
	}
	

	/**
	 * ��ȡ��־�ļ��ϴ�Url
	 * @param cityId ����Id
	 * @param callBack �ص�
	 * @return
	 */
	public void getUploadLogUrl(String cityId,Callback<Object> callBack) {
		NewNetworkRequest.getUploadLogUrl(Integer.parseInt(cityId),callBack);
	}
	
	/**
	 * �ϴ��û���Ϊ��־
	 */
	public static void uploadActionLog(String cityId,final Activity activity){
		File file = ActionLogUtil.getLogFile(activity);
		if(file.exists()){
			ActionLogUtil.uploadActionLog(cityId, activity,new Callback<Object>() {
				@Override
				public void handle(Object param) {
					Message msg = (Message)param;
					switch(msg.what){
					case 1://�ɹ��ϴ�
						FileUtil.getFileUtilInstance().deleteFile(ActionLogUtil.getActionLogUtilInstance().initLogFile(activity));
						break;
					case 0://�ϴ��쳣
						break;
					default:
						break;
					}
				}
			});
		}
	}
	
	/**
	 * �ϴ��û���Ϊ��־
	 */
	public static void uploadActionLog(String cityId,final Context context){
		File file = ActionLogUtil.getLogFile(context);
		if(file.exists()){
			ActionLogUtil.uploadActionLog(cityId, context,new Callback<Object>() {
				@Override
				public void handle(Object param) {
					Message msg = (Message)param;
					switch(msg.what){
					case 1://�ɹ��ϴ�
						FileUtil.getFileUtilInstance().deleteFile(ActionLogUtil.getActionLogUtilInstance().initLogFile(context));
						AppLog.LogD("��־�ϴ��ɹ�");
						break;
					case 0://�ϴ��쳣
						AppLog.LogD("��־�ϴ��쳣");
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
	 * �ϴ��û���Ϊ��־
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
	 * �ϴ��û���Ϊ��־
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
	 * �����־�ļ������ڣ��򴴽�
	 * return File :������־�ļ�
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
	 * ��ȡ��־�ļ�
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
	 * ��ȡ��־�ļ�
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
	 * ��ȡ��־�ļ�
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
	 * �����־�ļ������ڣ��򴴽�
	 * return File :������־�ļ�
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
	 * �ؼ�����¼�  д�뵽��־�ļ�
	 * @param view
	 * @param actionLogfile ��־�ļ�
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
	 * �ؼ���tag����id��valueʱ�ô˷���:�ؼ�����¼�  д�뵽��־�ļ�
	 * @param view
	 * @param value �û�������Ҫ��¼����־�ļ���ֵ
	 * @param actionLogfile ��־�ļ�
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
	 * �ؼ�û����tag����id��valueʱ�ô˷���:�ؼ�����¼�  д�뵽��־�ļ�
	 * @param context ������
	 * @param actionLogfile	��־�ļ�
	 * @param arrayResourceId  �ļ���������Դ���ݵ�id
	 * @param value	�û�������Ҫ��¼����־�ļ���ֵ
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
	 * ����־�ļ��м�¼��Ϊ
	 * @param actionLogfile ��־�ļ�
	 * @param data ʱ��
	 * @param menuId �ؼ��ı�ʶId
	 * @param value �ö�����¼��ֵ
	 */
	public void writeLogToFile(File actionLogfile,String data,int menuId,String value ){
		FileUtil fileUtil = FileUtil.getFileUtilInstance();
		
		JSONObject actionLogJson = new JSONObject() ;
		//��ȡ֮ǰ��־�ļ��еļ�¼
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
		//����Json����д���ļ�
		try{
			actionLogJson.put("userId",ETApp.getInstance().getCurrentUser().getPhoneNumber("_MOBILE"));
			actionLogJson.put("clientType", BookConfig.ClientType.CLIENT_TYPE_PASSENGER);
			actionLogJson.put("clientVersion", ETApp.getInstance().getMobileInfo().getVerisonCode());

			actionLogJson.put("cityId", MainActivityNew.cityId);
			actionLogJson.put("cityName",MainActivityNew.currentCityName);
			//�����¼�
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
			//��ӱ����¼�
			actions.put(actionJSObject);
			
			actionLogJson.put("actions",actions);
			fileUtil.writeFile(actionLogJson.toString(), actionLogfile);
			
			AppLog.LogD("AAAAAAAAAAAA","д��־"+actionJSObject.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ����View��tag����
	 * @param v
	 * @param activity
	 * @param sourceId
	 */
	public static void setViewTag(View v,Activity activity,int sourceId){
		v.setTag(activity.getResources().getStringArray(sourceId));
	}
	
	/**
	 * ����View��tag����
	 * @param v
	 * @param context
	 * @param sourceId
	 */
	public static void setViewTag(View v,Context context,int sourceId){
		v.setTag(context.getResources().getStringArray(sourceId));
	}
	
	/**
	 * д���û�������Arrays��Ĭ�����õĶ���ֵ����־�ļ�
	 * @param v  �������view
	 * @param activity 
	 */
	public static void writeActionLog(View v,Activity activity){
		ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		File file = actionLogUtil.initLogFile(activity);
		actionLogUtil.writeViewClickLog(v, file);
	}
	
	/**
	 * д���û������Ͷ����ϴ�ֵ����־�ļ�
	 * @param v  �������view
	 * @param value �û�������Ҫ��¼����־�ļ���ֵ
	 * @param activity 
	 */
	/*public static void writeActionLog(View v,Activity activity,String value){
		ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		File file = actionLogUtil.initLogFile(activity);
		actionLogUtil.writeViewClickLog(v, file,value);
	}*/
	
	/**
	 * д���û���������־�ļ�
	 * @param v  �������view
	 * @param context  ������
	 */
	public static void writeActionLog(View v,Context context){
		ActionLogUtil actionLogUtil = ActionLogUtil.getActionLogUtilInstance();
		File file = actionLogUtil.initLogFile(context);
		actionLogUtil.writeViewClickLog(v, file);
	}
	
	
	/**
	 * ��¼��־
	 * @param sourceId �ļ���������Դ���ݵ�id
	 * @param value �û�������Ҫ��¼����־�ļ���ֵ
	 */
	public static void writeActionLog(Activity activity ,int sourceId,String value){
		//д����־
		if(cn.com.easytaxi.common.Const.isActionLogCouldUpload){
			File file = ActionLogUtil.getActionLogFile(activity.getBaseContext());
			ActionLogUtil.getActionLogUtilInstance().writeViewClickLog(activity,file, sourceId,value);
		}
	}
	
	/**
	 * ��¼��־
	 * @param sourceId �ļ���������Դ���ݵ�id
	 * @param value �û�������Ҫ��¼����־�ļ���ֵ
	 */
	public static void writeActionLog(Context context ,int sourceId,String value){
		//д����־
		if(cn.com.easytaxi.common.Const.isActionLogCouldUpload){
			File file = ActionLogUtil.getActionLogFile(context);
			ActionLogUtil.getActionLogUtilInstance().writeViewClickLog(context,file, sourceId,value);
		}
	}
	
	/**
	 * �ϴ���־�ļ���������
	 * @param uploadLogUrl
	 * @param file
	 * @param callback
	 */
	public void uploadFile(final String uploadLogUrl,final File file,final Callback<Object>callback) {
		new Thread(new Runnable() {
			public void run() {
				try {
					//����post
					String url = uploadLogUrl;
					HttpPost httpPost = new HttpPost(new URL(url).toURI());
					MultipartEntity m = new MultipartEntity();
					m.addPart("params", new StringBody("{\"op\":\"setObj\"}"));
					m.addPart("file", new FileBody(file));
					httpPost.setEntity(m);
					//ִ��post
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() != 200) {
						throw new RuntimeException("HTTP request Exception");
					}
					String s = EntityUtils.toString(response.getEntity(), "UTF-8");
					System.out.println(s);
					//�����ؽ��
					JSONObject ret = new JSONObject(s);
					int error = Integer.parseInt(ret.get("error").toString());
					Message msg = new Message();
					if (0 == error) {// �ϴ��ɹ�
						msg.what = 1;
					} else {
						msg.what = 0;
					}
					msg.obj = s;
					callback.handle(msg);
					
					AppLog.LogD("AAAAAAAAAAAA", "�ϴ���־�ɹ�");
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 0;//0��ʾʧ��
					msg.obj = null;
					callback.handle(msg);
					e.printStackTrace();
					
					AppLog.LogD("AAAAAAAAAAAA", "�ϴ���־�쳣");
//					throw new RuntimeException(e);
				}
			}
		}).run();
	}
}
