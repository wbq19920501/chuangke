package cn.com.easytaxi.onetaxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.easytaxi.etpassengersx.R;

public class SearchAddressNewActivity extends Activity{

	private ImageButton map_title_leftbtn;
	private EditText editText;
	private Button okBtn;
	private Button cancelBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ontexi_address_seartch_start);
		initView();
		initListener();
	}

	private void initListener() {
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				String str = editText.getText().toString();
				intent.putExtra("extraAddress", str);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchAddressNewActivity.this.finish();
			}
		});
		
		map_title_leftbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchAddressNewActivity.this.finish();
			}
		});
		
	}

	private void initView() {
		map_title_leftbtn = (ImageButton)findViewById(R.id.map_title_leftbtn);
		editText = (EditText)findViewById(R.id.editText_ontexi_address_seartch);
		okBtn = (Button)findViewById(R.id.ok_btn_ontexi_address_seartch);
		cancelBtn = (Button)findViewById(R.id.cancel_btn_ontexi_address_seartch);
		editText.setText(MainActivityNew.extraAddress);
		
		if (editText.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


}
