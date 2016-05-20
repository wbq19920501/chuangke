package cn.com.easytaxi.message;

import android.os.Bundle;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.bean.MsgBean;
import cn.com.easytaxi.util.TimeTool;
import cn.com.easytaxi.workpool.BaseActivity;

public class MsgDetailActivity extends BaseActivity {

	TitleBar headView;

	TextView dateView;
	TextView msgSimpleView;
	TextView msgDetailView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.msg_detail);
		headView = new TitleBar(this);
		initView();

	}

	private void initView() {
		MsgBean msg = (MsgBean) getIntent().getSerializableExtra("msg");
		if (msg == null) {
			return;
		}

		headView = new TitleBar(this);
		headView.setTitleName(getString(R.string.msg_detail_title));
		
		dateView = (TextView) findViewById(R.id.message_date);
		msgDetailView = (TextView) findViewById(R.id.message_detail);
		
		dateView.setText(TimeTool.getListTime(msg.getDate().getTime()));
//		msgSimpleView.setText(msg.getBody());
		msgDetailView.setText(msg.getBody());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		headView.close();
		super.onDestroy();
	}
}
