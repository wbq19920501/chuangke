package cn.com.easytaxi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdBaseActivity;

import com.easytaxi.etpassengersx.R;

public class JfGuizeHistory extends YdBaseActivity {
	private TextView lable;
	private TitleBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jfgzactivity);
		initTitlebar();
		lable = (TextView) findViewById(R.id.lable);
		lable.setText(gzstr);

	}

	private String gzstr = "1、积分获得:仅限创客货的APP下单并付款成功才能获得积分;\n2、积分换算:积分最小单位是1元,每实际支付1元积1分;\n3、兑换成功后将从会员帐户中扣减相应积分分值;\n4、积分兑换暂不提供更换或退货服务,所以请您确认了解本说明后再进行兑换;\n5、积分兑换标准:100积分=5元礼品,满1000积分方可兑换,兑换成功后从账户扣减相应的积分;\n6、礼品兑换活动详情请咨询4000453828;\n7、本说明以上条款,最终解释权归山西同城商务有限公司所有.";

	private void initTitlebar() {
		bar = new TitleBar(this);
		bar.setTitleName("积分规则");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUserData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void regReceiver() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unRegReceiver() {
		// TODO Auto-generated method stub

	}
}
