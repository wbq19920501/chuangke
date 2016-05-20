package cn.com.easytaxi.mine;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.MyScoreDetailActivity;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.book.BookHistoryFragementActivity;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.common.SessionAdapter;
import cn.com.easytaxi.common.User;
import cn.com.easytaxi.common.Window;
import cn.com.easytaxi.custom.PageIndicator;
import cn.com.easytaxi.message.MyMessage;
import cn.com.easytaxi.mine.bean.MyInfo;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.util.FileUtil;
import cn.com.easytaxi.workpool.BaseActivity;

import com.easytaxi.etpassengersx.R;

/**
 * 我的 主界面
 * 
 * @ClassName: MyMainActivity
 * @Description: TODO
 * @author Brook xu
 * @date 2013-8-6 下午4:22:43
 * @version 1.0
 */
public class MyMainActivity extends BaseActivity {
	private SessionAdapter dao;
	private String phone;
	private ProgressDialog progressDialog;
	private String headPath;
	private Button btnSubmitName;
	private TitleBar titleBar;
	private EditText etEditName;
	private ImageButton ibChangeName;
	private TextView tvName;
	private TextView tvPhone;
	private TextView tvMoney;
//	private TextView tvScore;
	private TextView tvMsg;
	private TextView tvBook;
	private ImageView p_mine_imageView;
	// 违约数
	private TextView tvBreach;

	private SystemMultiMediaIntent mSystemMultiMediaIntent;

	private ViewPager viewPager;
	private PageIndicator mIndicator;
	List<MenuBean> datas;

	Callback<MyInfo> getInfoCallBack = new Callback<MyInfo>() {
		@Override
		public void handle(MyInfo myInfo) {
			try {
				tvMoney.setVisibility(View.GONE);
				tvMoney.setText(myInfo.get_RMB() + "");
//				tvScore.setText(myInfo.get_SCORE() + "");
				tvBook.setText(myInfo.get_CALL_NUMBER() + "");
				tvMsg.setText(myInfo.get_MSG_NUMBER() + "");
				tvBreach.setText(myInfo.get_WEIYUE_NUMBER() + "");
			} catch (Exception e) {

			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mSystemMultiMediaIntent.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dao = new SessionAdapter(this);
		setContentView(R.layout.p_mine);
//		findViewById(R.id.score_layout).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(self, MyScoreDetailActivity.class));
//			}
//		});
		initViews();
		initDatas();

		mSystemMultiMediaIntent = new SystemMultiMediaIntent(this) {
			@Override
			public void onGetPicFromSys(Uri uri) {
				// TODO Auto-generated method stub
				p_mine_imageView.setImageURI(uri);
				if (uri != null) {
					InputStream in = null;
					try {
						in = MyMainActivity.this.getContentResolver().openInputStream(uri);
						FileUtil.copyFileToFile(in, new File(headPath));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onGetVCRFromSys(Uri uri) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onGetAudioFromSys(Uri uri) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTakeCreamerFromSys(Intent data) {
				// TODO Auto-generated method stub
				try {
					File file = mSystemMultiMediaIntent.getPicFileFromIntent(data);
					p_mine_imageView.setImageURI(Uri.fromFile(file));
					FileUtil.copyFileToFile(MyMainActivity.this, file, new File(headPath));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onTakeVideoFromSys(Uri uri) {
				// TODO Auto-generated method stub
			}
		};
	}

	private void initViews() {
		btnSubmitName = (Button) findViewById(R.id.p_mine_submitbtn);
		etEditName = (EditText) findViewById(R.id.p_mine_name_edit);
		ibChangeName = (ImageButton) findViewById(R.id.p_mine_changeIv);
		tvName = (TextView) findViewById(R.id.p_mine_name);
		tvPhone = (TextView) findViewById(R.id.p_mine_phone);
		tvMoney = (TextView) findViewById(R.id.p_mine_money);
//		tvScore = (TextView) findViewById(R.id.p_mine_score);
		tvMsg = (TextView) findViewById(R.id.p_mine_msg);
		tvBreach = (TextView) findViewById(R.id.p_mine_breach);
		tvBook = (TextView) findViewById(R.id.p_mine_book);
		viewPager = (ViewPager) findViewById(R.id.p_mine_pager);
		p_mine_imageView = (ImageView) findViewById(R.id.p_mine_imageView);
		p_mine_imageView.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Window.showDlgList(MyMainActivity.this, "选择头像", new String[] { "拍照", "相册" }, new Callback<String>() {
					@Override
					public void handle(String param) {
						// TODO Auto-generated method stub
						if ("拍照".equals(param)) {
							try {
								mSystemMultiMediaIntent.takeCreamerFromSys();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Toast.makeText(MyMainActivity.this, "请检查SD卡是否正常!", Toast.LENGTH_LONG).show();
							}
						} else if ("相册".equals(param)) {
							mSystemMultiMediaIntent.getPicFromSys();
						}

					}
				});
			}
		});

		ibChangeName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchEditName(true);
			}
		});

		btnSubmitName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String nickName = etEditName.getText().toString();
				switchEditName(false);

				progressDialog = ProgressDialog.show(MyMainActivity.this, "提示", "请稍后...", true, true);
				NewNetworkRequest.regNewUser(nickName, phone, Long.valueOf(phone), 0, new Callback<Object>() {
					@Override
					public void handle(Object param) {
						if (param != null) {
							try {
								JSONObject jsonObject = (JSONObject) param;
								if (jsonObject.getInt("error") == 0) {
									Toast.makeText(MyMainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
									tvName.setText(nickName);
									dao.set("_NAME", nickName);
									dao.set(User._NICKNAME, nickName);
									ETApp.getInstance().getCurrentUser().setUserNickName(nickName);
								} else {
									Toast.makeText(MyMainActivity.this, "修改失败，请重试", Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(MyMainActivity.this, "修改失败，请重试", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(MyMainActivity.this, "修改失败，请重试", Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void error(Throwable e) {
						Toast.makeText(MyMainActivity.this, "修改成功，请重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void complete() {
						progressDialog.cancel();
					}
				});

			}
		});
	}

	private void switchEditName(boolean isEdit) {
		if (isEdit) {
			etEditName.setVisibility(View.VISIBLE);
			btnSubmitName.setVisibility(View.VISIBLE);
			tvName.setVisibility(View.GONE);
			ibChangeName.setVisibility(View.GONE);
			etEditName.setText(tvName.getText());
		} else {
			btnSubmitName.setVisibility(View.GONE);
			etEditName.setVisibility(View.GONE);
			tvName.setVisibility(View.VISIBLE);
			ibChangeName.setVisibility(View.VISIBLE);
			etEditName.setText("");
		}
	}

	private void initDatas() {
		titleBar = new TitleBar(this);
		titleBar.setTitleName("我的");
		User user = ETApp.getInstance().getCurrentUser();
		phone = user.getPhoneNumber("_MOBILE");
		headPath = ETApp.getmSdcardAppDir() + File.separator + phone + "/head_img.jpg";
		File file = new File(headPath);
		if (file.exists()) {
			try {
				p_mine_imageView.setImageURI(Uri.fromFile(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			file.getParentFile().mkdirs();
		}
		tvName.setText(user.getUserNickName());
		tvPhone.setText(phone);
		tvMoney.setText("0");
		tvMsg.setText("0");
		tvBreach.setText("0");
		tvBook.setText("0");
//		tvScore.setText("0");

		datas = getMenuDatas();
		viewPager.setAdapter(new MeFunctionAdapter(this, datas));
		// mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		// mIndicator.setViewPager(viewPager);
		// 从网络获取数据
//		NewNetworkRequest.getMyInfo(getPassengerId(), getInfoCallBack);
	}

	@Override
	protected void onResume() {
		super.onResume();
		NewNetworkRequest.getMyInfo(getPassengerId(), getInfoCallBack);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (titleBar != null) {
			titleBar.close();
		}
		super.onDestroy();
	}

	private List<MenuBean> getMenuDatas() {
		List<MenuBean> list = new ArrayList<MenuBean>();
		MenuBean m = new MenuBean();

		// m.setInternal(true);
		// m.setCacheId(2L);
		// m.setSeq(2);
		// m.setName("我的账户");
		// m.setImgRes(R.drawable.me_account);
		// m.setActionType(MenuBean.ACTION_TYPE_ACTIVITY);
		// m.setAction(MyAccountActivity.class.getName());
		// list.add(m);

		m = new MenuBean();
		m.setInternal(true);
		m.setCacheId(3L);
		m.setSeq(3);
		m.setName("我的订单");
		m.setImgRes(R.drawable.me_order);
		m.setActionType(MenuBean.ACTION_TYPE_ACTIVITY);
		m.setAction(BookHistoryFragementActivity.class.getName());
		list.add(m);

		m = new MenuBean();
		m.setInternal(true);
		m.setCacheId(4L);
		m.setSeq(4);
		m.setName("公告");
		m.setImgRes(R.drawable.me_msg);
		m.setActionType(MenuBean.ACTION_TYPE_ACTIVITY);
		m.setAction(MyMessage.class.getName());
		list.add(m);

		m = new MenuBean();
		m.setInternal(true);
		m.setCacheId(5L);
		m.setSeq(5);
		m.setName("积分");
		m.setImgRes(R.drawable.me_jifen);
		m.setActionType(MenuBean.ACTION_TYPE_ACTIVITY);
		m.setAction(MyScoreDetailActivity.class.getName());
		list.add(m);
		// m = new MenuBean();
		// m.setInternal(true);
		// m.setCacheId(5L);
		// m.setSeq(5);
		// m.setName("账户充值");
		// m.setImgRes(R.drawable.me_recharge);
		// m.setActionType(MenuBean.ACTION_TYPE_ACTIVITY);
		// m.setAction(PayActivity.class.getName());
		// list.add(m);

		// m = new MenuBean();
		// m.setInternal(true);
		// m.setCacheId(6L);
		// m.setSeq(6);
		// m.setName("电话召车");
		// m.setImgRes(R.drawable.me_call_taxi);
		// m.setActionType(MenuBean.ACTION_TYPE_ACTIVITY);
		// m.setAction(cn.com.easytaxi.phone.MainActivity.class.getName());
		// list.add(m);

		return list;
	}

}
