package cn.com.easytaxi.platform.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.BookConfig;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.ToolUtil;
import cn.com.easytaxi.message.MyMessageDialogActivity;
import cn.com.easytaxi.onetaxi.MainActivityNew;
import cn.com.easytaxi.onetaxi.common.BookBean;
import cn.com.easytaxi.platform.common.common.Const;
import cn.com.easytaxi.platform.common.common.ReceiveMsgBean;
import cn.com.easytaxi.platform.common.common.SendMsgBean;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.ui.bean.MsgData;

import com.easytaxi.etpassengersx.wxapi.WXPayEntryActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OneBookCore {
	public static OneBookCore instance = null;
	private BookBean oneBookbean;
	private Service mService;

	private final Object mutex = new Object();

	private OneBookCore(Service service) {
		this.mService = service;
	}

	public static OneBookCore getInstance(Service service) {
		if (instance == null) {
			instance = new OneBookCore(service);
		}

		return instance;
	}

	class SouondBack {
		public int error;
		public int endLatitude;
		public String filename;
		public String endAddress;
		public int endLongitude;
	}

	public void submitOneBook(Context context, Intent intent) {

		boolean isAppendSound = intent.getBooleanExtra("appsound", false);
		AppLog.LogD("xyw", "即时打车-->发送订单请求");
		if (isAppendSound) {

			byte[] soundData = ETApp.getInstance().getSoundData();
			oneBookbean = (BookBean) intent.getSerializableExtra("bookbean");
			oneBookbean.setDispStat(MainActivityNew.BOOK_STAT_SENDING);
			ETApp.getInstance().setCacheBookbean(oneBookbean);
			AppLog.LogD("send book  with sound ");
			if (soundData != null && soundData.length > 0) {

				NewNetworkRequest.sendSound(soundData, new Callback<Object>() {

					@Override
					public void handle(Object param) {
						if (param != null) {
							try {
								JSONObject F = (JSONObject) param;
								int error = F.getInt("error");
								if (error == 0) {// 成功
									// AppLog.LogD(F.toString());
									Gson gson = new Gson();
									SouondBack sb = gson.fromJson(F.toString(), SouondBack.class);

//									oneBookbean.setEndAddress(sb.endAddress);
//									oneBookbean.setEndLatitude(sb.endLatitude);
//									oneBookbean.setEndLongitude(sb.endLongitude);
									oneBookbean.setAudioName(sb.filename);
									sendBookReal(oneBookbean);
								} else {// 服务器返回失败原因
									Toast.makeText(ETApp.getInstance().getApplicationContext(), F.getString("errormsg"), Toast.LENGTH_LONG).show();
									Intent intentResp = null;
									intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
									intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
									ETApp.getInstance().sendBroadcast(intentResp);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(ETApp.getInstance().getApplicationContext(), "网络不给力", Toast.LENGTH_LONG).show();
								Intent intentResp = null;
								intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
								intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
								ETApp.getInstance().sendBroadcast(intentResp);
							}

						} else {
							Toast.makeText(ETApp.getInstance().getApplicationContext(), "网络不给力", Toast.LENGTH_LONG).show();
							Intent intentResp = null;
							intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
							intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
							ETApp.getInstance().sendBroadcast(intentResp);
						}
					}
				});

			} else {
				Intent intentResp = null;
				intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
				intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
				context.sendBroadcast(intentResp);

			}

		} else {

			oneBookbean = (BookBean) intent.getSerializableExtra("bookbean");
			oneBookbean.setDispStat(MainActivityNew.BOOK_STAT_SENDING);
			// AppLog.LogD(oneBookbean.toString());
			sendBookReal(oneBookbean);
		}

	}

	// 发送即时订单
	private void sendBookReal(BookBean oneBookbean) {
		Intent intentResp = null;
		if (oneBookbean.getStartLatitude() == 0 || oneBookbean.getStartLongitude() == 0) {
			intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
			intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
			oneBookbean.setDispStat(MainActivityNew.BOOK_STAT_REBOOK);
			ETApp.getInstance().sendBroadcast(intentResp);

			return;
		}

		AppLog.LogD("service 开始 发送 订单 ");

		// 没有终点地址名 直接发送

		if (StringUtils.isEmpty(oneBookbean.getEndAddress())) {
			// 直接发送

			NewNetworkRequest.submitBook(oneBookbean, oneBookCallback);
			// return;
		} else {
			// 有终点地址 有经纬度
			if (oneBookbean.getEndLatitude() != 0 && oneBookbean.getEndLongitude() != 0) {
				// 直接发送
				NewNetworkRequest.submitBook(oneBookbean, oneBookCallback);
			} else {
				// 有终点地址没有经纬度
				// 查询经纬度后再发送 (可以直接让服务器端去查询 )
				NewNetworkRequest.getAddressLngLat(oneBookbean.getCityName(), oneBookbean.getEndAddress(), callback);
			}
		}
	}

	private Callback<Object> callback = new Callback<Object>() {

		@Override
		public void handle(Object param) {

			if (param != null) {
				try {

					JSONObject json = (JSONObject) (param);
					oneBookbean.setEndLatitude(json.getInt("lat"));
					oneBookbean.setEndLongitude(json.getInt("lng"));

					NewNetworkRequest.submitBook(oneBookbean, oneBookCallback);
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else {
				sendFailedBroadcast();
			}

		}
	};
	protected long currentBookId;
	private long timeout;

	final Callback<Object> oneBookCallback = new Callback<Object>() {

		@Override
		public void handle(Object param) {
			Intent intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);

			if (param != null) {
				isReceive002 = false;

				try {
					JSONObject booJson = (JSONObject) param;
					int error = booJson.getInt("error");
					if (error == 0) {// 成功
						currentBookId = booJson.getLong("bookId");
						timeout = booJson.getLong("timeout");
						intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_OK);
						oneBookbean.setId(currentBookId);
						oneBookbean.setTimeOut(timeout);

						ETApp.getInstance().setCacheBookbean(oneBookbean);

						intentResp.putExtra("bookbean", oneBookbean);

						ETApp.getInstance().sendBroadcast(intentResp);
						oneBookbean.setDispStat(MainActivityNew.BOOK_STAT_WAITINGRESP);
						oneBookbean.startWait();
						System.gc();
					} else {// 服务器返回了失败原因
						Toast.makeText(ETApp.getInstance().getApplicationContext(), booJson.getString("errormsg"), Toast.LENGTH_LONG).show();
						intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
						ETApp.getInstance().sendBroadcast(intentResp);
						if(error == 3){//余额不足
							Intent intent = new Intent(ETApp.getInstance().getApplicationContext(), WXPayEntryActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("payMoney",oneBookbean.getPrice());
							mService.getBaseContext().startActivity(intent);
						}
					}
				} catch (JSONException e) {
					Toast.makeText(ETApp.getInstance().getApplicationContext(), "网络不给力！", Toast.LENGTH_SHORT).show();
					intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
					ETApp.getInstance().sendBroadcast(intentResp);
					e.printStackTrace();
				}
			} else {// 网络问题
				Toast.makeText(ETApp.getInstance().getApplicationContext(), "网络不给力！", Toast.LENGTH_SHORT).show();
				intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);
				ETApp.getInstance().sendBroadcast(intentResp);
			}
		}
	};

	boolean isReceive002 = false;

	// 回应给服务器
	private void replyServerUdp(ReceiveMsgBean msg) {
		try {
			byte[] buf = msg.getBody();
			String json = new String(buf, "UTF-8");
			JSONObject jobj = new JSONObject(json);
			JSONObject replyData = new JSONObject();
			replyData.put("id", jobj.optLong("id", -1));
			AppLog.LogD("reply udp：" + replyData.toString());
			MainService.udpChannelService.sendMessage(new SendMsgBean(Const.UDP_NOTIFY_ECHO, replyData.toString().getBytes("utf-8")));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void dispatchUdp(ReceiveMsgBean msg, int msgId) {
		replyServerUdp(msg);

		/*
		 * Intent udpIntent = new
		 * Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
		 * udpIntent.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP,
		 * EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_UDP_RESP);
		 */
		Intent intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
		intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_UDP_RESP);
		intentResp.putExtra("message", msg.getBody());
		intentResp.putExtra("msgId", msg.getMsgId());

		try {

			AppLog.LogD("--- ====udp===msg.getMsgId()= " + msg.getMsgId() + " , " + new String(msg.getBody(), "utf-8"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 当前应用在前端
		if (ToolUtil.isTopActivity(ETApp.getInstance().getApplicationContext(), null)) {
			if (msgId == 0xFF0001) {
				try {
					JSONObject json = new JSONObject(new String(msg.getBody(), "utf-8"));
					int bookType = json.getInt("bookType");
					if (true) {// 预约订单消息

						ETApp.getInstance().sendBroadcast(new Intent("cn.com.easytaxi.book.refresh_list"));

						if (MainService.udpChannelService != null) {
							MainService.udpChannelService.sendMessage(new SendMsgBean(msg.getMsgId(), msg.getBody()));

						} else {
							AppLog.LogD(" udpChannelService- nulll- 0xFF0001 ");
						}
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (msgId == 0xFF0002) {
				try {

					final byte[] data = msg.getBody();
					if (data == null || data.length == 0) {
						return;
					}

					JSONObject json = new JSONObject(new String(data, "utf-8"));
					AppLog.LogD("---0xFF0002-------------------------------" + json.toString());
					int bookType = json.getInt("bookType");
					if (bookType == 5) {// 预约订单消息

						dealBookReceived0025(msg);
						return;
					} else {

						Thread.sleep(200);
						if (oneBookbean.getId() != json.getLong("bookId")) {
							return;
						} else {
							dealOneBookReceived002(msg);
						}
					}

				} catch (Exception e) {
					// TODO: handle exception'
					e.printStackTrace();
				}
			} else if (msgId == 0xFF0003) {

				try {

					JSONObject Json = new JSONObject(new String(msg.getBody(), "utf-8"));

					Thread.sleep(200);

					if (oneBookbean.getId() != Json.getLong("bookId")) {

						return;
					} else {
						BookBean cacheBookbean = ETApp.getInstance().getCacheBookbean();
						if (cacheBookbean != null) {

							cacheBookbean.setUdp003Info(Json.getString("msg"));
						} else {
							AppLog.LogD("cacheBookbean == null");
						}
						ETApp.getInstance().sendBroadcast(intentResp);
					}

				} catch (Exception e) {

				}

			} else if (msgId == 0xFF0006) {

				try {
					JSONObject Json = new JSONObject(new String(msg.getBody(), "utf-8"));
					if (oneBookbean.getId() != Json.getLong("bookId")) {
						return;
					} else {
						dealOneBookReceived006(msg, intentResp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msgId == 0xFF0007) {// 订单状态变化
				AppLog.LogD("xyw", "订单状态变化");
				Intent i = new Intent("cn.com.easytaxi.book.state.changed");
				try {
					JSONObject jsonObject = new JSONObject(new String(msg.getBody(), "utf-8"));
					//即时打车状态改变
					changeOneTaxiState(jsonObject.getLong("bookId"), jsonObject.getInt("bookState"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//详情状态变化
				ETApp.getInstance().sendBroadcast(i);
				//列表状态变化
				ETApp.getInstance().sendBroadcast(new Intent(cn.com.easytaxi.common.Const.BOOK_STATE_CHANGED_LIST));
			} else if (msgId == 0xFF0010) {
				// 在应用中显示消息
				try {
					// 需要入库的消息
					MsgBean msgBean = null;
					try {
						Type type = new TypeToken<MsgBean>() {
						}.getType();
						msgBean = MsgData.gson.fromJson(new String(msg.getBody()), type);
					} catch (Exception e) {
						e.printStackTrace();
						msgBean = null;
					}
					// 消息提示
					JSONObject jsonObject = new JSONObject(new String(msg.getBody()));
					final String msgTxt = jsonObject.getString("msg");
					final String msgUrl = jsonObject.getString("url");
					AppLog.LogD("====---2222--udp--new message ----on top---  " + Integer.toHexString(msg.getMsgId()));
					if (!StringUtils.isEmpty(msgUrl)) {
						Intent intent = new Intent(mService.getBaseContext(), MyMessageDialogActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						intent.putExtra("title", "提示");
						intent.putExtra("content", "您有新消息，通过查看，阅读消息详情！");
						intent.putExtra("btnOkText", "查看");
						intent.putExtra("btnCancleText", "取消");
						intent.putExtra("gotoClassName", "cn.com.easytaxi.ui.MoreWebviewActivity");
						intent.putExtra("url", msgUrl);

						intent.putExtra("msgBean", msgBean);
						mService.getBaseContext().startActivity(intent);
						// Window.showMessageDialog(mService.getBaseContext(),"新消息",
						// "您可以通过查看，阅读消息详情！", "查看", "取消", new Callback<Object>()
						// {
						//
						// @Override
						// public void handle(Object param) {
						// // TODO Auto-generated method stub
						// Intent intent = new Intent(mService.getBaseContext(),
						// MoreWebviewActivity.class );
						// intent.putExtra("title", "消息详情");
						// intent.putExtra("uri", msgUrl);
						// mService.getBaseContext().startActivity(intent);
						// }
						// }, null);
					} else {
						Intent intent = new Intent(mService.getBaseContext(), MyMessageDialogActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						intent.putExtra("title", "新消息");
						intent.putExtra("content", msgTxt);
						intent.putExtra("btnOkText", "确定");
						intent.putExtra("btnCancleText", "");
						intent.putExtra("gotoClassName", "");
						intent.putExtra("url", msgUrl);

						intent.putExtra("msgBean", msgBean);
						mService.getBaseContext().startActivity(intent);
						// Window.showMessageDialog(mService.getBaseContext(),"新消息",
						// msgTxt, "查看", "取消", null, null);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {// 应用在后台，只再标题栏提示
			if(msgId == 0xFF0007){
				ETApp.getInstance().sendBroadcast(new Intent(cn.com.easytaxi.common.Const.BOOK_STATE_CHANGED_LIST));
			}else if (msgId == 0xFF0010) {
				AppLog.LogD("====---3333--udp--new message ----on background---  " + Integer.toHexString(msg.getMsgId()));
				try {
					JSONObject jsonObject = new JSONObject(new String(msg.getBody()));
					AppLog.LogD("====---！！！！！！！！！！！！---  " + new String(msg.getBody()));
					String msgTxt = jsonObject.getString("msg");
					if (mService instanceof MainService) {
						((MainService) mService).showNotification("您有新的消息", msgTxt);
					} else if (mService instanceof OneBookService) {
						((OneBookService) mService).showNotification("您有新的消息", msgTxt);
					}
					ETApp.getInstance().saveCacheBoolean("new_message", true);

					// 消息入库
					try {
						Type type = new TypeToken<MsgBean>() {
						}.getType();
						MsgBean msgBean = MsgData.gson.fromJson(new String(msg.getBody()), type);
						// 消息入库：设置为未读
						if (msgBean != null) {
							msgBean.setRead(false);
							MsgData msgData = new MsgData();
							msgData.insert(msgBean);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 取消即时打车状态
	 * 
	 * @param bookId
	 * @param state
	 * @return void
	 */
	private void changeOneTaxiState(long bookId, int state) {
		cn.com.easytaxi.onetaxi.common.BookBean bk = ETApp.getInstance().getCacheBookbean();
		if (bk != null) {
			long currentBookId = bk.getId();
			if (bookId == currentBookId) {
				if (!(state == 1 || state == 5)) {
					AppLog.LogD("xyw", "change onetaxi book state");
					ETApp.getInstance().setCacheBookbean(null);
				}
			}
		}
	}

	private void dealOneBookReceived006(ReceiveMsgBean msg, Intent intentResp) {
		ETApp.getInstance().sendBroadcast(intentResp);
		if (MainService.udpChannelService != null) {

			MainService.udpChannelService.sendMessage(new SendMsgBean(msg.getMsgId(), msg.getBody()));
			BookBean bkk = ETApp.getInstance().getCacheBookbean();
			if (bkk != null) {
				bkk.setDispStat(MainActivityNew.BOOK_STAT_DRIVER_CANCELBOOK);
			}
		} else {
			AppLog.LogD(" udpChannelService- null- ");
		}
	}

	/**
	 * 处理预约订车的002
	 * 
	 * @param msg
	 */
	private void dealBookReceived0025(ReceiveMsgBean msg) {
		ETApp.getInstance().sendBroadcast(new Intent("cn.com.easytaxi.book.refresh_list"));

		if (MainService.udpChannelService != null) {
			MainService.udpChannelService.sendMessage(new SendMsgBean(msg.getMsgId(), msg.getBody()));

		} else {
			AppLog.LogD(" udpChannelService- nulll-0xFF0002 -- bookType = 5");
		}
	}

	/**
	 * 处理一键召车的002
	 * 
	 * @param msg
	 */
	private void dealOneBookReceived002(ReceiveMsgBean msg) {
		if (MainService.udpChannelService != null && !isReceive002) {
			BookBean bkk = ETApp.getInstance().getCacheBookbean();

			if (bkk != null) {
				bkk.setDispStat(MainActivityNew.BOOK_STAT_RECEIVED);
			} else {
				AppLog.LogD(" -------bkk == NULL");
			}

			synchronized (mutex) {
				if (!isReceive002) {
					AppLog.LogD(" -------bkk == isReceive002" + isReceive002);
					NewNetworkRequest.getReplyBook(oneBookbean.getId(), new ReplayBookbeakCallback(new SendMsgBean(msg.getMsgId(), msg.getBody())));
				} else {
					MainService.udpChannelService.sendMessage(new SendMsgBean(msg.getMsgId(), msg.getBody()));
					AppLog.LogD(" -------bkk == isReceive002" + isReceive002);
				}

			}

		} else if (MainService.udpChannelService != null && isReceive002) {
			MainService.udpChannelService.sendMessage(new SendMsgBean(msg.getMsgId(), msg.getBody()));
			// MainService.udpChannelService.sendMessage(new
			// SendMsgBean(msg.getMsgId(), msg.getBody()));
			AppLog.LogD(" udpChannelService- isReceive002-  0xFF0002 --- " + isReceive002);
		}
	}

	private void sendFailedBroadcast() {
		Intent intentResp = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
		intentResp.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_FAILED);

		ETApp.getInstance().sendBroadcast(intentResp);
	}

	public class ReplayBookbeakCallback extends Callback<BookBean> {

		SendMsgBean sendMsgBean;

		public ReplayBookbeakCallback(SendMsgBean sendMsgBean) {
			this.sendMsgBean = sendMsgBean;
		}

		@Override
		public void handle(BookBean param) {
			if (param != null) {
				AppLog.LogD("获取----huang-------到订单信息");
				//
				MainService.udpChannelService.sendMessage(sendMsgBean);
				setReceived002(true);

				BookBean bookBean = param;
				ETApp.getInstance().getCacheBookbean().stopWait();
				ETApp.getInstance().setCacheBookbean(bookBean);
				bookBean.setDispStat(MainActivityNew.BOOK_STAT_RECEIVED);
				Intent intent = new Intent(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_CMD_RESP);
				intent.putExtra(EasyTaxiCmd.ONE_TAXI_BOOK_MAIN_SUB_CMD_RESP, EasyTaxiCmd.ONE_TAXI_BOOK_SUB_CMD_SUBMIT_RESP_JIEDAN_OK);
				ETApp.getInstance().sendBroadcast(intent);
			} else {
				AppLog.LogD("没有 ----huang------ 获取到订单信息");
				setReceived002(false);
			}

		}

	}

	private void setReceived002(boolean isReceived) {
		this.isReceive002 = isReceived;
	}

	public boolean isReceived002() {
		AppLog.LogD(" udp isReceived002-  - ");
		return isReceive002;
	}

	Callback<BookBean> replayBookbeakCallback = null;
}
