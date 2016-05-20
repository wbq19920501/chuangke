package cn.com.easytaxi.ui.bean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.client.channel.TcpClient;
import cn.com.easytaxi.platform.service.CacheBean;
import cn.com.easytaxi.taxi.bean.BookBeanDao;
import cn.com.easytaxi.ui.adapter.DateAdapter;
import cn.com.easytaxi.ui.adapter.LoadCallback;
import cn.com.easytaxi.util.AsyncUtil;
import cn.com.easytaxi.util.CompatibleUtils;
import cn.com.easytaxi.util.ETException;
import cn.com.easytaxi.util.GlobalConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author shiner
 * 
 */
public class BookDataSource  extends BaseData<BookBean>{
	private final static String TAG = "BookDataSource";

	public final static String CACHE_PENDING = "books_pending";
	public final static String CACHE_HANDLED = "books_handled";
	public final static String CACHE_EVALUATE = "books_evaluate";

	private static final int ORDER_NONE = -1;
	private static final int ORDER_NEER = 0;
	private static final int ORDER_USETIME = 1;
	private static final int ORDER_DISTANCE = 2;
	private static final int ORDER_EARNST = 3;
	private static final int ORDER_REPLAYTIME = 4;

	private static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new DateAdapter()).create();

	/**
	 * 从服务器获取所有已接订单，自动放入缓存
	 * 
	 * @param id
	 *            司机id
	 * @return
	 */
	/*public List<BookBean> getHandledListRemote(Long id) {
		List<BookBean> result = null;
		try {
			JSONObject param = new JSONObject();
			param.put("action", Config.TCP_ACTION);
			param.put("method", "getBookListByReplyer");
			param.put("replyerId", id);
			AppLog.LogD(TAG, "getHandledListRemote:req:" + param.toString());
			JSONObject jObj = TcpClient.sendAction(id, param);
			AppLog.LogD(TAG, "getHandledListRemote:res:" + jObj);
			if (jObj.getInt("error") == 0) {
				Type type = new TypeToken<List<BookBean>>() {
				}.getType();
				synchronized (BookDataSource.this) {
					result = gson.fromJson(jObj.getString("books"), type);
				}
			} else {
				throw new ETException(jObj.getString("errormsg"));
			}

			if (result != null) {
				// 已接订单是获取所有已接，所以可以清除已缓存的
				TaxiApp.cacheService.clear(CACHE_HANDLED);
				removeDuplicate(result);
				TaxiApp.cacheService.puts(CACHE_HANDLED, result);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (result == null) {
				result = new ArrayList<BookBean>();
			}
		}

		BookUtil.validate(result);

		synchronized (BookDataSource.this) {
			Collections.sort(result, new ComparatorReplyTime());
		}
		return result;
	}
*/
	/**
	 * 从服务器获取所有已接订单，自动放入缓存(异步)
	 * 
	 * @param id
	 *            司机id
	 * @return
	 */
/*	public void getHandledListRemote(final Long id, LoadCallback<List<BookBean>> callback) {
		AsyncUtil.goAsync(new Callable<List<BookBean>>() {

			@Override
			public List<BookBean> call() throws Exception {
				return getHandledListRemote(id);
			}
		}, callback);

	}
*/
	final static int rawAll = 32;
	static int all = rawAll;

	/**
	 * 从服务器分页获取历史订单，自动放入缓存
	 * 
	 * @param id
	 *            司机id
	 * @return
	 */
	private List<BookBean> getHistoryListRemote(int start , String CityId, String passengerId) {
		List<BookBean> result = null;
		List<NewBookBean> tmpResult = null;
		try {
			JSONObject param = new JSONObject();
			param.put("action", GlobalConfig.NEW_TCP_ACTION_PROXY);
			param.put("method", GlobalConfig.NEW_TCP_METHOD_QUERY);
			param.put("cityId", Integer.valueOf(CityId));
			param.put("op", "getHistoryBookListByPassengerId");
			param.put("passengerId", Long.valueOf(passengerId));
			param.put("startId", start);
			param.put("order", "desc");
			param.put("orderName", "_ID");
			AppLog.LogD(TAG, "getHistoryListRemote:req:" + param.toString());
			byte[] buf= TcpClient.send(Long.valueOf(passengerId),0xF00001 ,param.toString().getBytes("UTF-8"));
			JSONObject jObj = new JSONObject(new String(buf, "UTF-8"));
 
			AppLog.LogD(TAG, "getHistoryListRemote:res:" + jObj);
			if (jObj.getInt("error") == 0) {
				Type type = new TypeToken<List<NewBookBean>>() {
				}.getType();
				synchronized (BookDataSource.this) {
					tmpResult = gson.fromJson(jObj.getString("datas"), type);
				}
			} else {
				throw new ETException(jObj.getString("errormsg"));
			}

			if (tmpResult != null) {
//				removeDuplicate(tmpResult);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (tmpResult == null) {
				tmpResult = new ArrayList<NewBookBean>();
			}
		}

		result = new ArrayList<BookBean>();

		for (NewBookBean newBookBean : tmpResult) {
			BookBean book = new BookBean();
			try {
				CompatibleUtils.copy(book, newBookBean);
				result.add(book);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		BookUtil.validate(result);

		return result;
	}

	/**
	 * 从服务器分页获取历史订单，自动放入缓存(异步)
	 * 
	 * @param id
	 *            司机id
	 * @return
	 */
	public void getHistoryList(final int start, final boolean loadNew,final String CityId, final String passengerId ,LoadCallback<List<BookBean>> callback) {

		if (loadNew) {// 列表下拉刷新
			AsyncUtil.goAsync(new Callable<List<BookBean>>() {

				@Override
				public List<BookBean> call() throws Exception {
					// 从服务器加载
					List<BookBean> books = getHistoryListRemote(getCount(), CityId,  passengerId);

					// 缓存到数据库
					insert(books);

					return books;
				}
			}, callback);

		} else {// 上拉加载更多
			final List<BookBean> result = getList(start, Integer.MAX_VALUE, BookBeanDao.Properties.Id);

			if (result != null && !result.isEmpty()) { // 先返回给调用者，再从网络加载
				AsyncUtil.handle(callback, result);
			}
			AsyncUtil.goAsync(new Callable<List<BookBean>>() {

				@Override
				public List<BookBean> call() throws Exception {
					// 从服务器加载
					List<BookBean> books = getHistoryListRemote(start + result.size() , CityId ,passengerId);

					// 缓存到数据库
					insert(books);

					return books;
				}
			}, callback);
		}
	}
 

	
	
	/**
	 * 投诉,评价
	 * 
	 * @throws Exception
	 */
/*	public boolean evaluate(Long passenderId, Long bookId, String content) throws Exception {
		boolean result = false;
		JSONObject param = new JSONObject();
		param.put("action", Config.NEW_TCP_ACTION);
		param.put("method", Config.NEW_TCP_ACTION_EXECUTE);
		param.put("cityId", TaxiState.Driver.cityId);
		param.put("op", "suggest");
		param.put("bookId", bookId);
		param.put("passenderId", passenderId);
		param.put("content", content);
		param.put("objectId", TaxiState.Driver.id);
		param.put("objectType", 2);
		AppLog.LogD(TAG, "suggest:req:" + param.toString());
		JSONObject jObj = TcpClient.sendAction(TaxiState.Driver.id, param);
		AppLog.LogD(TAG, "suggest:res:" + jObj.toString());
		if (jObj.getInt("error") == 0) {
			result = true;
		} else {
			throw new ETException(jObj.getString("errormsg"));
		}
		return result;
	}*/

 
	private void removeDuplicate(List<? extends CacheBean> list) {
		try {
			List<Long> tmp = new ArrayList<Long>();
			Iterator<? extends CacheBean> itr = list.iterator();
			while (itr.hasNext()) {
				CacheBean b = itr.next();
				if (tmp.contains(b.getCacheId())) {
					itr.remove();
				} else {
					tmp.add(b.getCacheId());
				}
			}
			tmp.clear();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用车时间比较器
	 * 
	 * @author shiner
	 * 
	 */
	private static class ComparatorUseTime implements Comparator<BookBean> {

		@Override
		public int compare(BookBean object1, BookBean object2) {
			return (int) (new Date(object1.getUseTime()).getTime() - new Date(object2.getUseTime()).getTime());
		}

	};

	/**
	 * 接单时间比较器
	 * 
	 * @author shiner
	 * 
	 */
	private static class ComparatorReplyTime implements Comparator<BookBean> {

		@Override
		public int compare(BookBean object1, BookBean object2) {
			if (object1.getReplyTime() != null && object2.getReplyTime() != null) {
				return (int) (new Date(object2.getReplyTime()).getTime() - new Date(object1.getReplyTime()).getTime());
			} else {
				return 0;
			}
		}

	};

	/**
	 * 小费比较器
	 * 
	 * @author shiner
	 * 
	 */
	private static class ComparatorEarnst implements Comparator<BookBean> {

		@Override
		public int compare(BookBean object1, BookBean object2) {
			if (object1.getPrice() != null && object2.getPrice() != null) {
				return object2.getPrice() - object1.getPrice();
			} else {
				return 0;
			}
		}

	};

	/**
	 * 距离比较器
	 * 
	 * @author shiner
	 * 
	 */
	private static class ComparatorDistance implements Comparator<BookBean> {

		@Override
		public int compare(BookBean object1, BookBean object2) {
			if (object1.getForecastDistance() != 0 && object2.getForecastDistance() != 0) {
				return object2.getForecastDistance() - object1.getForecastDistance();
			} else {
				return 0;
			}
		}

	}

}
