package cn.com.easytaxi.book.adapter;

import java.util.ArrayList;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.book.BookUtil;
import cn.com.easytaxi.book.NewBookDetail;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.ui.BookListFragement;

public class BookListAdapter extends BaseAdapter implements OnClickListener {
	Activity act;
	ArrayList<BookBean> datas = new ArrayList<BookBean>();
	LayoutInflater inflater;
	Timer timer;
	Resources res;

	public BookListAdapter(Activity atc, ArrayList<BookBean> datas) {
		super();
		this.act = atc;
		this.res = act.getResources();
		this.datas = datas;
		this.inflater = LayoutInflater.from(atc);
	}

	@Override
	public void notifyDataSetChanged() {
//		BookUtil.categarySort(datas);
		super.notifyDataSetChanged();
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Holder h = null;
		if (v == null) {
			v = inflater.inflate(R.layout.book_list_item, null);
			h = new Holder();
			h.head = (ImageView) v.findViewById(R.id.book_head_icon);
			h.number = (TextView) v.findViewById(R.id.book_number);
			h.startAddr = (TextView) v.findViewById(R.id.book_start_addr);
			h.endAddr = (TextView) v.findViewById(R.id.book_end_addr);
			h.time = (TextView) v.findViewById(R.id.book_time_val);
			h.distance = (TextView) v.findViewById(R.id.book_distance);
			h.cancel = (Button) v.findViewById(R.id.book_cancel);
			h.dyTime = (TextView) v.findViewById(R.id.book_dytime);
			h.content = v.findViewById(R.id.center);

			// h.status = (TextView) v.findViewById(R.id.book_status);

			h.iconStartAddr = (ImageView) v.findViewById(R.id.iv_start_addr);
			h.iconEndAddr = (ImageView) v.findViewById(R.id.iv_end_addr);
			h.iconBookTime = (ImageView) v.findViewById(R.id.iv_book_time);
			h.iconBookState = (ImageView) v.findViewById(R.id.iv_book_state);

			v.setTag(h);
		} else {
			h = (Holder) v.getTag();
		}

		BookBean b = (BookBean) getItem(position);

		if (position % 2 == 0) {
			v.setBackgroundResource(R.drawable.bg_booklist_white_d);
		} else {
			v.setBackgroundResource(R.drawable.bg_booklist_dark_d);
		}

		h.startAddr.setText(b.getStartAddress());
		h.endAddr.setText(b.getEndAddress());
		h.time.setText(b.getUseTime());


		// h.distance.setText(act.getResources().getString(R.string.book_distance_item,
		// new Object[] { b.getDistance(), b.getMoney() }));
		// h.distance.setVisibility(View.GONE);

		h.head.setTag(position);
		// h.head.setOnClickListener(this);
		h.cancel.setTag(R.id.tag_position, position);
		h.cancel.setOnClickListener(this);
//		h.content.setTag(position);
//		h.content.setOnClickListener(this);

//		if (BookUtil.isEffective(b.getEvaluate()) && BookUtil.isEffective(b.getReplyerId())) {
//			h.cancel.setTag(3);
//			h.cancel.setText("我已评价");
//			h.cancel.setVisibility(View.INVISIBLE);
//		} else if (BookUtil.isNew(b)) {
//			h.cancel.setVisibility(View.VISIBLE);
//			h.cancel.setTag(1);
//			h.cancel.setText("取消订单");
//		} else if (BookUtil.isActive(b)) {
//			h.cancel.setVisibility(View.VISIBLE);
//			h.cancel.setTag(2);
//			h.cancel.setText("结束服务");
//			// h.cancel.setEnabled(true);
//		} else {// 历史订单
//			if (b.getReplyerId() != null) {// 有司机接单
//				h.cancel.setTag(3);
//				h.cancel.setText("我要评价");
//				h.cancel.setVisibility(View.VISIBLE);
//			} else {
//				h.cancel.setTag(3);
//				h.cancel.setText("我要评价");
//				h.cancel.setVisibility(View.INVISIBLE);
//			}
//
//			// h.cancel.setEnabled(false);
//		}
		
		h.cancel.setVisibility(View.GONE);

		// 设置状态
		setState(v, b, h);
		return v;
	}

	// 订单状态,0.未处理,1.表示已处理,2表示乘客取消，3表示出租车取消
	private void setState(View v, BookBean b, Holder h) {
		switch (b.getState()) {
		// 司机接单前
		case 0x01:
			h.head.setBackgroundResource(R.drawable.q13);
			h.number.setVisibility(View.INVISIBLE);
			h.dyTime.setTextColor(res.getColor(R.color.yellow_state));
//			h.dyTime.setText(ToolUtil.getTimeStr(b.getDyTime()));
			h.dyTime.setText("调度中");
			break;
		// 调度失败
		case 0x03:
			h.head.setBackgroundResource(R.drawable.q15);
			h.number.setVisibility(View.INVISIBLE);
			h.dyTime.setTextColor(res.getColor(R.color.text_gray));
			h.dyTime.setText("调度失败");
			break;
		// 未接单乘客取消
		case 0x04:
			h.head.setBackgroundResource(R.drawable.q15);
			h.number.setVisibility(View.INVISIBLE);
			h.dyTime.setTextColor(res.getColor(R.color.text_gray));
			h.dyTime.setText("我已取消");
			break;

		// 执行订单-有司机接单
		case 0x05:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("已接单");
			break;
		// 结束订单中-乘客确认上车/司机确认上车，乘客终止订单/司机终止订单；
		case 0x06:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("结束订单中");
			break;
		// 订单完成-司机确认上车/乘客确认上车
		case 0x07:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("订单完成");
			break;
		// 订单执行失败-乘客同意终止订单/司机同意终止订单，争议仲裁
		case 0x08:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("订单执行失败");
			break;
		// 争议-乘客不同意终止订单/司机不同意终止订单，司机未确认上车/乘客未确认上车，订单执行超时
		case 0x09:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("争议");
			break;
		default:
			try{
				h.head.setBackgroundResource(R.drawable.q12);
				h.number.setVisibility(View.GONE);
				h.dyTime.setTextColor(res.getColor(R.color.green_state));
				h.dyTime.setText("未知状态");
			}catch (Exception e) {
			}
			break;
		}
	}

	static class Holder {
		ImageView head;
		TextView number;
		TextView startAddr;
		TextView endAddr;
		TextView time;
		TextView distance;
		Button cancel;
		// TextView status;
		TextView dyTime;
		View content;

		ImageView iconStartAddr;
		ImageView iconEndAddr;
		ImageView iconBookTime;
		ImageView iconBookState;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.book_cancel) {

			int position = (Integer) v.getTag(R.id.tag_position);
			BookBean book = (BookBean) getItem(position);
			String detailTaxiNumber = book.getReplyerPhone();

			long taxiId = 0;
			if (book.getReplyerId() == null) {
				taxiId = 0;
			} else {
				taxiId = book.getReplyerId();
			}

			int type = (Integer) v.getTag();
			BookListFragement.stopService(v.getContext(), book.getId(), taxiId, type);
			// 打乘客电话
			// BookListActivity.callDriver(v.getContext(), detailTaxiNumber);

			// 写入日志
			switch (type) {
			case 1:// 取消订单
				ActionLogUtil.writeActionLog(act, R.array.airport_and_book_history_book_cancel, "");
				break;
			case 2:// 结束订单
				ActionLogUtil.writeActionLog(act, R.array.airport_and_book_history_book_endServer, "");
				break;
			case 3:// 评价
				ActionLogUtil.writeActionLog(act, R.array.airport_and_book_history_book_pingjia, "");
				break;
			default:
				break;
			}

		} else if (v.getId() == R.id.center) {
			int position = (Integer) v.getTag();
			Intent intent = new Intent(act, NewBookDetail.class);
			BookBean book = (BookBean) getItem(position);
			intent.putExtra("book", book);
			act.startActivity(intent);
		}
	}

	private void setItemEnabled(Holder h, boolean isEnabled) {
		h.iconStartAddr.setEnabled(isEnabled);
		h.iconEndAddr.setEnabled(isEnabled);
		h.iconBookState.setEnabled(isEnabled);
		h.iconBookTime.setEnabled(isEnabled);
	}

}
