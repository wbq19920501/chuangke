package cn.com.easytaxi.ui.bean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.content.ContentValues;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.client.common.MsgConst;
import cn.com.easytaxi.common.Config;
import cn.com.easytaxi.onetaxi.TaxiState;
import cn.com.easytaxi.taxi.bean.MsgBeanDao;
import cn.com.easytaxi.ui.adapter.DateAdapter;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.util.AsyncUtil;
import cn.com.easytaxi.util.DBManager;
import cn.com.easytaxi.util.ETException;
import cn.com.easytaxi.util.GlobalConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author shiner
 */
/**
 * @author shiner
 * 
 */
public class MsgData extends BaseData<MsgBean> {
	private static final String TAG = "MsgData";

	public static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new DateAdapter()).create();

	static int all = 32;
	static int newAll = all;

	/**
	 * �ӷ�������ҳ��ȡ��Ϣ���Զ����뻺��(���ݿ�)
	 * 
	 * @param start
	 * @param cityId
	 * @param passengerId
	 * @return
	 * @return List<MsgBean>
	 */
	private List<MsgBean> getMsgListRemote(int start, String cityId, String passengerId) {
		List<MsgBean> result = null;
		try {
			JSONObject param = new JSONObject();
			param.put("action", GlobalConfig.NEW_TCP_ACTION_PROXY);
			param.put("method", GlobalConfig.NEW_TCP_METHOD_QUERY);
			param.put("op", "getMessageList");
			param.put("cityId", Integer.valueOf(cityId));
			param.put("objectId", Long.valueOf(passengerId));
			param.put("objectType", 2);
			param.put("startId", start);
			param.put("order", "asc");
			param.put("orderType", "_ID");
			AppLog.LogD(TAG, "getMsgListRemote:req:" + param.toString());

			byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, param.toString().getBytes("UTF-8"));

			JSONObject jObj = new JSONObject(new String(response, "UTF-8"));

			AppLog.LogD(TAG, "getMsgListRemote:res:  ---------- " + jObj);
			if (jObj.getInt("error") == 0) {
				Type type = new TypeToken<List<MsgBean>>() {
				}.getType();
				synchronized (MsgData.this) {
					result = gson.fromJson(jObj.getString("datas"), type);
				}
			} else {
				throw new ETException(jObj.getString("errormsg"));
			}

			if (result != null && !result.isEmpty()) {
				Collections.sort(result, new ComparatorId());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (result == null) {
				result = new ArrayList<MsgBean>();
			}
		}

		return result;
	}

	/**
	 * �ӷ�������ҳ��ȡ��Ϣ���Զ����뻺��(���ݿ�)
	 * 
	 * @param id
	 *            ˾��id
	 * @return
	 */
	public void getMsgList(final int start, final int size, final String cityId, final String passengerId, final boolean loadNew, LoadCallback<List<MsgBean>> callback) {
		if (loadNew) {// ��������Ϣ���б�����ˢ��ʱ��
			AsyncUtil.goAsync(new Callable<List<MsgBean>>() {

				@Override
				public List<MsgBean> call() {
					List<MsgBean> msgs = getMsgListRemote(getCount(), cityId, passengerId);

					// ���浽���ݿ�
					insert(msgs);

					return msgs;
				}
			}, callback);

		} else {// �������ظ���
			final List<MsgBean> result = getMsg(start, Integer.MAX_VALUE);//�ӱ��ؿ��м�������

			if (result != null && !result.isEmpty()) { // �ȷ��ظ������ߣ��ٴ��������
				AsyncUtil.handle(callback, result);
			}
			AsyncUtil.goAsync(new Callable<List<MsgBean>>() {

				@Override
				public List<MsgBean> call() throws Exception {
					List<MsgBean> msgs = getMsgListRemote(start + result.size(), cityId, passengerId);
					// ���浽���ݿ�
					insert(msgs);

					return msgs;
				}
			}, callback);
		}
	}

	/**
	 * ��ѯ
	 * 
	 * @param start
	 * @param size
	 * @return
	 */
	private List<MsgBean> getMsg(int start, int size) {
		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		return dao.queryBuilder().orderDesc(MsgBeanDao.Properties.Id).offset(start).limit(size).list();
	}

	/**
	 * ���ر��Ϊ�Ѷ�
	 * 
	 * @param msgId
	 * @return
	 */
	public int markLocalRead(Long msgId) {
		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		ContentValues values = new ContentValues();
		values.put(MsgBeanDao.Properties.Read.columnName, true);
		int result = dao.getDatabase().update(MsgBeanDao.TABLENAME, values, MsgBeanDao.Properties.Id.columnName + " = ?", new String[] { msgId + "" });
		return result;
	}

	/**
	 * ���Ϊ�Ѷ�
	 * 
	 * @param msgId
	 * @return
	 */
	public void markRead(final Long msgId, LoadCallback<Boolean> callback) {
		// �ȸ��»���
		markLocalRead(msgId);

		// �ٱ�ǵ�������
		AsyncUtil.goAsync(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return makeReadRemote(msgId);
			}
		}, callback);
	}

	public boolean makeReadRemote(final Long msgId) throws Exception {
		boolean result = false;
		JSONObject param = new JSONObject();
		param.put("action", Config.NEW_TCP_ACTION);
		param.put("method", Config.NEW_TCP_ACTION_QUERY);
		param.put("op", "setMessageMark");
		param.put("mark", true);
		param.put("Id", msgId);

		AppLog.LogD(TAG, "makeReadRemote:req:" + param.toString());
		byte[] response = TcpClient.send(1L, MsgConst.MSG_TCP_ACTION, param.toString().getBytes("UTF-8"));
		JSONObject jObj = new JSONObject(new String(response, "UTF-8"));
		AppLog.LogD(TAG, "makeReadRemote:res:" + jObj.toString());
		if (jObj.getInt("error") == 0) {
			result = true;
		} else {
			throw new ETException(jObj.getString("errormsg"));
		}
		return result;

	}

	public static void saveMsg(MsgBean msg) {

		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		// ContentValues values = new ContentValues();
		dao.insert(msg);
	}

	/**
	 * ���ȫ��Ϊ�Ѷ�
	 * 
	 * @return
	 */
	public int makeAllRead() {
		MsgBeanDao dao = DBManager.getMsgDao(ETApp.getInstance());
		ContentValues values = new ContentValues();
		values.put(MsgBeanDao.Properties.Read.columnName, true);
		int result = dao.getDatabase().update(MsgBeanDao.TABLENAME, values, null, null);
		return result;
	}

	/**
	 * id�Ƚ���
	 * 
	 * @author shiner
	 * 
	 */
	public static class ComparatorId implements Comparator<MsgBean> {

		@Override
		public int compare(MsgBean object1, MsgBean object2) {
			try {
				if (object1 == null) {
					return -1;
				} else if (object2 == null) {
					return 1;
				}
				return (int) (object2.getCacheId() - object1.getCacheId());
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
	};
}
