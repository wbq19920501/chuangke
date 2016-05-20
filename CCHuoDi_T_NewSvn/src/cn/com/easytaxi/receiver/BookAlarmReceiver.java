package cn.com.easytaxi.receiver;

import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.service.AlarmClockBookService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BookAlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//ÅĞ¶ÏÍøÂç×´Ì¬
		boolean isNetAvailable = NetChecker.getInstance(context).isAvailableNetwork();
		if(isNetAvailable){
			// ¿ªÆô¶©µ¥ÄÖÖÓÌáĞÑ·şÎñ
			Intent alarmIntent = new Intent(context, AlarmClockBookService.class);
			context.startService(alarmIntent);
		}
	}
	
}
