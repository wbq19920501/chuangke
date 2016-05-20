package cn.com.easytaxi;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author xxb
 * @version ����ʱ�䣺2015��9��11�� ����9:57:50
 */
public class PullTool {
	/**
	 * ����ˢ�¿ؼ���ʼ������
	 * 
	 * @author xxb
	 * 
	 * @2015��8��18��
	 */
	public static class PullListViewUtils {
		public static void setPullListViewParams(PullToRefreshListView pull) {
			pull.setScrollingWhileRefreshingEnabled(false);
			pull.getLoadingLayoutProxy(true, true).setPullLabel("�������");
			pull.getLoadingLayoutProxy(true, true).setRefreshingLabel("������...");
			pull.getLoadingLayoutProxy(true, false).setReleaseLabel("ˢ��");
			pull.getLoadingLayoutProxy(false, true).setReleaseLabel("���ظ���");
		}

		public static void setPullDownTime(Context context, PullToRefreshBase<ListView> arg0) {
			String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
			arg0.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		}
	}
}
