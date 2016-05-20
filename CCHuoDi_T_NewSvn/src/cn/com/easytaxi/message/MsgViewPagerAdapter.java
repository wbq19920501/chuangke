package cn.com.easytaxi.message;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MsgViewPagerAdapter extends PagerAdapter {
	Context ctx;
	private View[] views;

	public MsgViewPagerAdapter(Context ctx, View[] views) {
		super();
		this.ctx = ctx;
		this.views = views;
	}

	@Override
	public int getCount() {
		return views.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views[position]);
		return views[position];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}