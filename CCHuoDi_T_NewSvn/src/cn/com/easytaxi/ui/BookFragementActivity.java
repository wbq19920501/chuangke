package cn.com.easytaxi.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Build.VERSION;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.ETApp;

import com.easytaxi.etpassengersx.R;

import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.view.CommonDialog;

public class BookFragementActivity extends BaseFragementActivity implements ViewPager.OnPageChangeListener {

	private ViewPager viewPager;
	private BookPagerAdapter bookPagerAdapter;
	private TitleBar titleBar;
//	private RadioGroup radioGroup_choice;
	
	private BookPublishFragement bookPublishFragment;
//	private BookListFragement bookListFragment;
	private ArrayList<Fragment> fragments;
//	private RadioButton tab_new;
//	private RadioButton tab_history;
	 

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		

	}
	

	@Override
	public void onPageSelected(int arg0) {
		
		AppLog.LogD("onPageSelected =  " + arg0);
		if(arg0 == 0){
			bookPublishFragment.loadData();
//			tab_new.setChecked(true);
//			tab_history.setChecked(false);
		}
		
		if(arg0 == 1){
//			bookListFragment.loadData();
//			tab_new.setChecked(false);
//			tab_history.setChecked(true);
			 
		}
		
		
		
		
	}

 
	
	public void setCurrentPage(int index){
		if(viewPager != null){			
			viewPager.setCurrentItem(index, true);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_main_activity);
		if (VERSION.SDK_INT >= 14) {
			setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
		}
		initViews(savedInstanceState);
		initConfig();
		initListeners();
		initUserData();
		if (!ETApp.getInstance().isLogin()) {
			showRegistDialog(getBaseContext(), "提示", "请先注册以后再使用此功能", "立刻注册", "稍后再说", "cn.com.easytaxi.platform.RegisterActivity");
		}
	}

	private void initUserData() {
		// TODO Auto-generated method stub	
	}

	private void initListeners() {
//		tab_new.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				int currentItem = viewPager.getCurrentItem();
//				if(currentItem == 0){
//					
//				}else if(currentItem==1){
//					viewPager.setCurrentItem(0,true);
//				}
//				//写入日志
//				ActionLogUtil.writeActionLog(BookFragementActivity.this,R.array.book_tab_new, "");
//			}
//		});
		
//		tab_history.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				int currentItem = viewPager.getCurrentItem();
//				if(currentItem == 1){
//					
//				}else if(currentItem==0){
//					viewPager.setCurrentItem(1,true);
//				}
//				//写入日志
//				ActionLogUtil.writeActionLog(BookFragementActivity.this,R.array.book_tab_history, "");
//			}
//		});
//		
//	radioGroup_choice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup view, int id) {
//				refreshData(id);
//			}
//		});
		
		 
	}

	protected void refreshData(int id) {
		// TODO Auto-generated method stub
		AppLog.LogD(id+"");
	}

	private void initConfig() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		AppLog.LogD("--------onStart-------------");
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
 
	}
	
	@Override
	protected void onDestroy() {

		if(titleBar != null){
			titleBar.close();
			titleBar = null;
		}
		super.onDestroy();
	}
	
	private void initViews(Bundle savedInstanceState) {
	
		titleBar = new TitleBar(this);
		titleBar.setTitleName("预约叫车");
 
//		tab_new = (RadioButton)this.findViewById(R.id.tab_new);
//		tab_history = (RadioButton)this.findViewById(R.id.tab_history);
//		radioGroup_choice = (RadioGroup)findViewById(R.id.tab);
		
		bookPublishFragment = new BookPublishFragement();
//		bookListFragment = new BookListFragement();
		
		fragments = new ArrayList<Fragment>(1);
		fragments.add(0,bookPublishFragment);
//		fragments.add(1, bookListFragment);
		bookPagerAdapter = new BookPagerAdapter(this.getSupportFragmentManager(), fragments);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(bookPagerAdapter);
		 
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
	 
		AppLog.LogD( " setCurrentItem " +  viewPager.getCurrentItem());
	}
 
	public class BookPagerAdapter extends FragmentPagerAdapter {
		List<Fragment> list = null;

		public BookPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			this(fm);
			this.list = fragments;
		}

		private BookPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}

	}
	/**
	 * 显示注册dialog
	 */
	public  void showRegistDialog(final Context context,String titile,String content,String btn1,String btn2,final String gotoClassName){
			 Callback<Object> okBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						try {
							Intent intent;
							intent = new Intent(context,Class.forName(gotoClassName));
							startActivity(intent);
							
							CommonDialog dialog= (CommonDialog)param;
							dialog.dismiss();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				};
				
				Callback<Object> cancelBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						CommonDialog dialog= (CommonDialog)param;
						dialog.dismiss();
					}
				};
				
				Dialog dialog = new CommonDialog(BookFragementActivity.this, titile, content, btn1, btn2, R.layout.dlg_close,okBtnCallback,cancelBtnCallback);
				dialog.show();
		}
}
