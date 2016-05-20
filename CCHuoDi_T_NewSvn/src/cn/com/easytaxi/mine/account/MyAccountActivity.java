package cn.com.easytaxi.mine.account;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.BaseFragementActivity;

/**
 * @ClassName: MyAccountActivity
 * @Description: TODO 我的账户
 * @author Brook xu
 * @date 2013-8-1 上午11:22:49
 * @version 1.0
 */
public class MyAccountActivity extends BaseFragementActivity implements OnCheckedChangeListener, OnPageChangeListener {

	private int currentSelectType;
	private TitleBar titleBar;

	private long startTimeMillis;
	private long endTimeMillis;
	private ViewPager viewPager;

	private RadioButton radioButtonMoney;
	private RadioButton radioButtonCoin;
	private RadioButton radioButtonScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_mine_account);

		initViews();
		initDatas();
	}

	private void initDatas() {
		// TODO Auto-generated method stub
		currentSelectType = Account.TYPE_ET_MONEY;
	}

	private void initViews() {
		// TODO Auto-generated method stub
		titleBar = new TitleBar(this);
		titleBar.setTitleName("我的账户");
		viewPager = (ViewPager) this.findViewById(R.id.p_mine_account_pager);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new SampleFragmentPagerAdapter(this.getSupportFragmentManager(), getPagers()));
		viewPager.setOnPageChangeListener(this);

		radioButtonMoney = (RadioButton) this.findViewById(R.id.p_mine_account_radio0);
		radioButtonCoin = (RadioButton) this.findViewById(R.id.p_mine_account_radio1);
		radioButtonScore = (RadioButton) this.findViewById(R.id.p_mine_account_radio2);
		radioButtonMoney.setOnCheckedChangeListener(this);
		radioButtonCoin.setOnCheckedChangeListener(this);
		radioButtonScore.setOnCheckedChangeListener(this);
	}

	private List<Fragment> getPagers() {
		// TODO Auto-generated method stub
		List<Fragment> list = new ArrayList<Fragment>();
		Fragment fragmentMoney = new AccountFragment(Account.TYPE_ET_MONEY);
		Fragment fragmentCoin = new AccountFragment(Account.TYPE_ET_COIN);
		Fragment fragmentScore = new AccountFragment(Account.TYPE_ET_SCORE);
		list.add(fragmentMoney);
		list.add(fragmentCoin);
		list.add(fragmentScore);
		return list;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			int id = buttonView.getId();
			switch (id) {
			case R.id.p_mine_account_radio0:
				currentSelectType = Account.TYPE_ET_MONEY;
				break;
			case R.id.p_mine_account_radio1:
				currentSelectType = Account.TYPE_ET_COIN;
				break;
			case R.id.p_mine_account_radio2:
				currentSelectType = Account.TYPE_ET_SCORE;
				break;
			default:
				break;
			}
			switchDatas(currentSelectType);
		}
	}

	private void switchDatas(int dataType) {
		switch (dataType) {
		case Account.TYPE_ET_MONEY:
			viewPager.setCurrentItem(0, true);
			break;
		case Account.TYPE_ET_COIN:
			viewPager.setCurrentItem(1, true);
			break;
		case Account.TYPE_ET_SCORE:
			viewPager.setCurrentItem(2, true);
			break;
		default:
			break;
		}
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
		switch (arg0) {
		case 0:
			radioButtonMoney.setChecked(true);
			break;
		case 1:
			radioButtonCoin.setChecked(true);
			break;
		case 2:
			radioButtonScore.setChecked(true);
			break;
		default:
			radioButtonMoney.setChecked(true);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (titleBar != null) {
			titleBar.close();
		}
		super.onDestroy();
	}
}
