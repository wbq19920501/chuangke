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

	private String gzstr = "1�����ֻ��:���޴��ͻ���APP�µ�������ɹ����ܻ�û���;\n2�����ֻ���:������С��λ��1Ԫ,ÿʵ��֧��1Ԫ��1��;\n3���һ��ɹ��󽫴ӻ�Ա�ʻ��пۼ���Ӧ���ַ�ֵ;\n4�����ֶһ��ݲ��ṩ�������˻�����,��������ȷ���˽Ȿ˵�����ٽ��жһ�;\n5�����ֶһ���׼:100����=5Ԫ��Ʒ,��1000���ַ��ɶһ�,�һ��ɹ�����˻��ۼ���Ӧ�Ļ���;\n6����Ʒ�һ����������ѯ4000453828;\n7����˵����������,���ս���Ȩ��ɽ��ͬ���������޹�˾����.";

	private void initTitlebar() {
		bar = new TitleBar(this);
		bar.setTitleName("���ֹ���");
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
