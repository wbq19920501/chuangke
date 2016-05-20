package cn.com.easytaxi;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author xxb
 * @version 创建时间：2015年9月11日 上午9:57:50
 */
public class PullTool {
	/**
	 * 下拉刷新控件初始化设置
	 * 
	 * @author xxb
	 * 
	 * @2015年8月18日
	 */
	public static class PullListViewUtils {
		public static void setPullListViewParams(PullToRefreshListView pull) {
			pull.setScrollingWhileRefreshingEnabled(false);
			pull.getLoadingLayoutProxy(true, true).setPullLabel("加载完成");
			pull.getLoadingLayoutProxy(true, true).setRefreshingLabel("加载中...");
			pull.getLoadingLayoutProxy(true, false).setReleaseLabel("刷新");
			pull.getLoadingLayoutProxy(false, true).setReleaseLabel("加载更多");
		}

		public static void setPullDownTime(Context context, PullToRefreshBase<ListView> arg0) {
			String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
			arg0.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		}
	}
}
