package cn.com.easytaxi.book;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.BaseFragementActivity;

public class BookHistoryFragementActivity extends BaseFragementActivity implements ViewPager.OnPageChangeListener {

	private TitleBar titleBar;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_history_list);
		initViews(savedInstanceState);
		
	}

	private void initViews(Bundle savedInstanceState) {
		titleBar = new TitleBar(this);
		titleBar.setTitleName("ÎÒµÄ¶©µ¥");
	}
	

	
	@Override
	protected void onDestroy() {
		if(titleBar != null){
			titleBar.close();
			titleBar = null;
		}
		super.onDestroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
	}

}
