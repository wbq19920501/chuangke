package cn.com.easytaxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.com.easytaxi.platform.service.EasyTaxiCmd;

public class OneBookReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 

		int cmd = intent.getIntExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, 0);
		switch (cmd) {
		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_OK:
			// currentBookbean = intent.getSerializableExtra("bookbean");
			//BookBean bk = ETApp.getInstance().getCacheBookbean();
			//bk.startWait();
			//ActivityManager am = get
			
			
			// 让订单自己去计时

			// dispatchStat(BOOK_STAT_WAITINGRESP);
			break;
		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED:

			break;

		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_WAITTING:

			break;

		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_TIMEOUT:

			break;

		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_JIEDAN_OK:

			break;
		case EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_UDP_RESP:
			int msgId = intent.getIntExtra("msgId", 0);
			byte[] message = intent.getByteArrayExtra("message");

			break;
		default:
			break;
		}

	}

}
