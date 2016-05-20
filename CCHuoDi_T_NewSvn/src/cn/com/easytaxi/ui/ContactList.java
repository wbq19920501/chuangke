package cn.com.easytaxi.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdLocaionBaseActivity;
import cn.com.easytaxi.ui.view.MyLetterListView;
import cn.com.easytaxi.ui.view.MyLetterListView.OnTouchingLetterChangedListener;

public class ContactList extends YdLocaionBaseActivity {

	/** Called when the activity is first created. */

	private TitleBar bar;
	private ListAdapter adapter;
	private ListView personList;
	private TextView overlay;
	private MyLetterListView letterListView;
	private AsyncQueryHandler asyncQuery;
	private static final String NAME = "name", NUMBER = "number", SORT_KEY = "sort_key";
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private List<ContactEntity> selectContactList = new ArrayList<ContactList.ContactEntity>(12);
	private Button select_ok;
	private Button select_clear;
	private Button select_cancel;
	private Button select_all;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list);

		initViews();
		initListeners();
		initUserData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void initViews() {

		select_ok = (Button) findViewById(R.id.select_ok);
		select_cancel = (Button) findViewById(R.id.select_cancel);
		select_clear = (Button) findViewById(R.id.select_clear);
		select_all = (Button) findViewById(R.id.select_all);

		bar = new TitleBar(this);
		bar.setTitleName("联系人选择");
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		bar.setBackCallback(new Callback<Object>() {
			@Override
			public void handle(Object param) {
				doBack();
			}
		});

		personList = (ListView) findViewById(R.id.list_view);
		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
	}

	@Override
	protected void initListeners() {

		select_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
//				intent.putParcelableArrayListExtra("selcontact", selectContactList);
				Bundle extras = new Bundle();
				extras.putParcelableArrayList("selcontact", (ArrayList<? extends Parcelable>) selectContactList);
				intent.putExtras(extras);
				setResult(RESULT_OK, intent);
				doBack();
			}
		});
		select_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				selectContactList.clear();
				doBack();
			}
		});
		select_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				selectContactList.clear();
				List<ContactEntity> list = adapter.getList();
				
				for(ContactEntity c:list){
					c.isSelected = false;
					//selectContactList.add(c);
				}
				 
				adapter.notifyDataSetChanged();
				
			}
		});
		select_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				selectContactList.clear();
				List<ContactEntity> list = adapter.getList();
				
				for(ContactEntity c:list){
					c.isSelected = true;
					selectContactList.add(c);
				}
				 
				adapter.notifyDataSetChanged();
			}
		});

		personList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (adapter != null) {
					List<ContactEntity> list = adapter.getList();
					if (list != null && list.size() > 0) {
						ContactEntity cv = list.get(position);
						if (cv != null) {
							cv.name.replaceAll(" ", "");
							cv.isSelected = !cv.isSelected;
							adapter.notifyDataSetChanged();
						}

						if (cv != null && !cv.isSelected) {
							selectContactList.remove(cv);
						}
						if (cv != null && cv.isSelected) {
							selectContactList.add(cv);
						}
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (bar != null) {
			bar.close();
			bar = null;
		}
		super.onDestroy();
	}

	@Override
	protected void initUserData() {
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc");
	}

	@Override
	protected void regReceiver() {

	}

	@Override
	protected void unRegReceiver() {

	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {
		List<ContactEntity> list = new ArrayList<ContactEntity>();

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list.clear();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					ContactEntity cv = new ContactEntity();
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					// System.out.println(sortKey);
					if (number.startsWith("+86")) {
						cv.name = name;
						cv.phone = number.substring(3); // 去掉+86
						cv.sortKey = sortKey;

					} else {
						cv.name = name;
						cv.phone = number;
						cv.sortKey = sortKey;
					}
					cv.phone.replaceAll(" ", "");
					cv.isSelected = false;
					list.add(cv);
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}

			if (cursor != null) {
				cursor.close();
			}
			//
		}

	}

	private void setAdapter(List<ContactEntity> list) {
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);

	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ContactEntity> list;

		public ListAdapter(Context context, List<ContactEntity> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				String currentStr = getAlpha(list.get(i).sortKey);
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1).sortKey) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).sortKey);
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.contact_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number = (TextView) convertView.findViewById(R.id.number);
				holder.check = (CheckBox) convertView.findViewById(R.id.image_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContactEntity cv = list.get(position);
			holder.name.setText(cv.name);
			holder.number.setText(cv.phone);
			holder.check.setChecked(cv.isSelected);
			String currentStr = getAlpha(list.get(position).sortKey);// 当前字母
			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).sortKey) : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
			TextView number;
			CheckBox check;
		}

		public List<ContactEntity> getList() {
			return list;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		// LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) findViewById(R.id.rmd_overly);
		// overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		// WindowManager.LayoutParams lp = new
		// WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT,
		// WindowManager.LayoutParams.TYPE_APPLICATION,
		// WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
		// PixelFormat.TRANSLUCENT);
		// WindowManager windowManager = (WindowManager)
		// this.getSystemService(Context.WINDOW_SERVICE);
		// windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1000);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {

		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("[a-zA-Z]");// ");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	public static class ContactEntity implements Parcelable {
		public String phone;
		public boolean isSelected = false;
		public String name;
		public String sortKey;

		public ContactEntity(){
			
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return  "num "+ phone + ", name "+name;
		}
		
		private ContactEntity(Parcel in) {
			phone = in.readString();
			name = in.readString();
			sortKey = in.readString();
			isSelected = (in.readInt() == 0) ? false : true;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel parcel, int arg1) {
			parcel.writeString(phone);
			parcel.writeString(name);
			parcel.writeString(sortKey);
			parcel.writeInt(isSelected ? 1 : 0);
		}

		public static final Parcelable.Creator<ContactEntity> CREATOR = new Parcelable.Creator<ContactEntity>() {
			public ContactEntity createFromParcel(Parcel in) {
				return new ContactEntity(in);
			}

			public ContactEntity[] newArray(int size) {
				return new ContactEntity[size];
			}
		};

	}

}
