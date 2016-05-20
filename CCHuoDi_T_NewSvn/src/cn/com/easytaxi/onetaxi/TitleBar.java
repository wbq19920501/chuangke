package cn.com.easytaxi.onetaxi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.phone.common.DaoAdapter;
import cn.com.easytaxi.platform.CitySelectActivity;

/**
 * @ClassName: TitleBar
 * @Description: TODO
 * @author Yuanwei Xu
 * @date 2013-1-9 ÉÏÎç11:13:06
 * @version V1.0
 */
public class TitleBar implements OnClickListener {
	public static final int TYPE_HOME = 1;
	public static final int TYPE_OTHER = 2;

	private static final int HOME = 1;
	private static final int CITY_SELECT = 2;
	private ImageButton leftButton;
	private Button rightButton;
	private Button rightCityButton;
	private Button rightHomeButton;
	private TextView mMidTextView;
	private Activity mActivity;
	private int type;

	private SessionAdapter session;
	private DaoAdapter dao;
	private CitySwitchReceiver citySwitchReceiver;

	private Callback<Object> backCallback;
	private boolean cityClickAble = true;

	public void setBackCallback(Callback<Object> backCallback) {
		this.backCallback = backCallback;
	}

	public TitleBar(Activity activity) {
		this.mActivity = activity;

		session = new SessionAdapter(activity);
		dao = new DaoAdapter(activity);

		leftButton = (ImageButton) activity.findViewById(R.id.map_title_leftbtn);
		//rightButton = (Button) activity.findViewById(R.id.map_title_right_spinner);
		rightCityButton = (Button) activity.findViewById(R.id.map_title_right_spinner_city);
		rightHomeButton = (Button) activity.findViewById(R.id.map_title_right_spinner);
		rightButton = rightHomeButton;
		mMidTextView = (TextView) activity.findViewById(R.id.map_title_mid_tv);
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		type = 1;
		setRightButtonVisible(View.GONE);
		{
			citySwitchReceiver = new CitySwitchReceiver();
			IntentFilter intentFilter = new IntentFilter("cn.com.easytaxi.cityswitch");
			activity.registerReceiver(citySwitchReceiver, intentFilter);
		}

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.map_title_leftbtn:
			if (backCallback == null) {
				back();
			} else {
				backCallback.handle(null);
			}
			break;
		case R.id.map_title_right_spinner:
			goToHome();
			break;
		default:
			break;
		}
	}

	public void setTitleName(String name) {
		mMidTextView.setText(name);
	}

	private void back() {
		mActivity.finish();
	}

	private void goToHome() {
		Intent i = new Intent(mActivity, cn.com.easytaxi.platform.MainActivityNew.class);
		mActivity.startActivity(i);
		mActivity.finish();
	}

/*	public void switchToHomeButton() {
		type = HOME;
		rightButton.setVisibility(View.GONE);
		rightButton = rightHomeButton; 
		rightButton.setVisibility(View.VISIBLE);
		rightButton.setBackgroundResource(R.drawable.bg_home);
		rightButton.setOnClickListener(this);
	}*/

	
	public Button getRightCityButton() {
		return rightCityButton;
	}

	public Button getRightHomeButton() {
		return rightHomeButton;
	}

	public void switchToCityButton() {
		
		
		type = CITY_SELECT;
		rightButton.setVisibility(View.GONE);
		rightButton = rightCityButton; 
		rightButton.setVisibility(View.VISIBLE);
		rightButton.setBackgroundResource(R.drawable.btn001);
		
	 
		rightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, CitySelectActivity.class);
				mActivity.startActivity(intent);
				// TODO Auto-generated method stub
		/*		Window.selectCity(mActivity, session, new Callback<JSONObject>() {

					@Override
					public void handle(JSONObject json) {
						try {
							session.set("_CITY_NAME", json.getString("_NAME"));
							session.set("_CITY_ID", json.getString("_ID"));
							Intent intent = new Intent("cn.com.easytaxi.cityswitch");
							mActivity.sendBroadcast(intent);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				});*/
			}
		});
	}

	public void setRightButtonText(String txt) {
		if (type == CITY_SELECT) {
			rightButton = rightCityButton; 
			rightButton.setText(txt);
		}
	}

	private class CitySwitchReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context action, Intent intent) {
			setRightButtonText(session.get("_CITY_NAME"));
		}
	}

	public void close() {
		if (session != null) {
			session.close();
			session = null;
		}
		if (dao != null) {
			dao.close();
			dao = null;
		}
		if (citySwitchReceiver != null) {
			mActivity.unregisterReceiver(citySwitchReceiver);
			citySwitchReceiver = null;
		}
	}

	public void setRightButtonVisible(int type) {
		
		if(type == View.VISIBLE){
			rightCityButton.setVisibility(View.GONE);
			rightHomeButton.setVisibility(View.VISIBLE);
		}else{
			rightCityButton.setVisibility(View.GONE);
			rightHomeButton.setVisibility(View.GONE);
		}
		rightButton.setVisibility(type);
 
		
	}

	/**
	 * @deprecated
	 * @param type
	 */
	public void setRightButtonTextTypeface(int type) {
		if (type == CITY_SELECT) {
			ColorStateList colorState;
			Resources res = mActivity.getResources();
			if (type == TYPE_HOME) {
				rightButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

				int statePressed = android.R.attr.state_pressed;
				int stateFocesed = android.R.attr.state_focused;
				int stateDefault = android.R.attr.defaultValue;
				int[][] state = { { statePressed }, { stateFocesed }, { stateDefault } };
				int color1 = res.getColor(R.color.white);
				int color2 = res.getColor(R.color.white);
				int color3 = res.getColor(R.color.main_black);
				int[] color = { color1, color2, color3 };
				colorState = new ColorStateList(state, color);
			} else {
				rightButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

				int statePressed = android.R.attr.state_pressed;
				int stateFocesed = android.R.attr.state_focused;
				int stateDefault = android.R.attr.defaultValue;
				int[][] state = { { statePressed }, { stateFocesed }, { stateDefault } };
				int color1 = res.getColor(R.color.white);
				int color2 = res.getColor(R.color.yellow);
				int color3 = res.getColor(R.color.yellow);
				int[] color = { color1, color2, color3 };
				colorState = new ColorStateList(state, color);
			}
			rightButton.setTextColor(colorState);
		}
	}

	 

}
