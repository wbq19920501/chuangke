package cn.com.easytaxi.receiver;

import java.util.Date;

import cn.com.easytaxi.LocalPreferences;
import cn.com.easytaxi.common.Const;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.service.UploadActionLogFileService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionLogUploadReceiver extends BroadcastReceiver {
	private String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;
	private String ACTION_TIME_CHANGED = Intent.ACTION_TIME_CHANGED;
	/**
	 * �ϴ���ʱ�������� timeMin��ʱ����ϴ�
	 */
	private final int timeMin = 20;

	private final String AUTO_UPLOAD_ACTION_LOG = "auto_upload_action_log";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ACTION_DATE_CHANGED.equals(action)|| ACTION_TIME_CHANGED.equals(action)) {
//			Toast.makeText(context, "ʱ������ڱ仯", Toast.LENGTH_LONG).show();

			if (Const.isActionLogCouldUpload) {
				Date date = new Date();
				String nowDate = date.getDate()+"";
				String saveDate = LocalPreferences.getInstance(context).getCacheString(AUTO_UPLOAD_ACTION_LOG);
				if (!nowDate.equals(saveDate)) {//����û�����
					// ʱ����timeMin�Ժ����ϴ�
					int hour = date.getHours();
					if (hour > timeMin) {
						boolean isAvailableNetwork = NetChecker.getInstance(context).isWifiAvailable();
						if (isAvailableNetwork) {
							SessionAdapter session = new SessionAdapter(context);
							String cityId = session.get("_CITY_ID");
							if (cityId != null && !cityId.equals("")) {

								//�ϴ���־
								Intent intent2 = new Intent(context,UploadActionLogFileService.class);
								UploadActionLogFileService.cityId = cityId;
								context.startService(intent2);
								 
								// �����Զ��ϴ�����
								LocalPreferences.getInstance(context).saveCacheString(AUTO_UPLOAD_ACTION_LOG,"" + date.getDate());
							}
						}
					}
				}
			}
		}
	}
}
