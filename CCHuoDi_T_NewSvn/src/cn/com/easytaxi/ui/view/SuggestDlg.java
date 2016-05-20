package cn.com.easytaxi.ui.view;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.NewNetworkRequest.DiaoDuPrice;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.book.view.scrollwheel.ArrayWheelAdapter;
import cn.com.easytaxi.book.view.scrollwheel.WheelView;

public class SuggestDlg extends Dialog implements OnClickListener {

	Context context;
	private Button button_ok;
	private Button button_cancel;
	private cn.com.easytaxi.book.view.scrollwheel.WheelView wheelMoney;

	private MoneyDialogListener listener;
	private String[] moneys;
	private int defalutPriceIndex;
	private DiaoDuPrice disDiaoDuPrice;
	private List<DiaoDuPrice> priceList;

	public interface MoneyDialogListener {
		public void onClick(View view);
	}

	public SuggestDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public SuggestDlg(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setWheelMoney(cn.com.easytaxi.book.view.scrollwheel.WheelView wheelMoney) {
		this.wheelMoney = wheelMoney;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// View view =
		// LayoutInflater.from(context).inflate(R.layout.wheel_scroll_layout,
		// null, true);

		setContentView(R.layout.money_dialog);

		button_ok = (Button) findViewById(R.id.button_ok);
		button_cancel = (Button) findViewById(R.id.button_cancel);
		wheelMoney = (WheelView) findViewById(R.id.money_items);

		button_ok.setOnClickListener(this);
		button_cancel.setOnClickListener(this);

		AppLog.LogD("onCreate - -11 -");

	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.button_ok) {

		}

		listener.onClick(arg0);

	}

	public void setListener(MoneyDialogListener listener) {
		this.listener = listener;
	}

	public void getSelectItemKey() {

	}

}
