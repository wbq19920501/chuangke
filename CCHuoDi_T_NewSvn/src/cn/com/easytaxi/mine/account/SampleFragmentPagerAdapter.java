package cn.com.easytaxi.mine.account;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> mFragments = new ArrayList<Fragment>();

	public SampleFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public SampleFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragment) {
		super(fm);
		mFragments = fragment;
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}