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
//			h.cancel.setText("��������");
//			h.cancel.setVisibility(View.INVISIBLE);
//		} else if (BookUtil.isNew(b)) {
//			h.cancel.setVisibility(View.VISIBLE);
//			h.cancel.setTag(1);
//			h.cancel.setText("ȡ������");
//		} else if (BookUtil.isActive(b)) {
//			h.cancel.setVisibility(View.VISIBLE);
//			h.cancel.setTag(2);
//			h.cancel.setText("��������");
//			// h.cancel.setEnabled(true);
//		} else {// ��ʷ����
//			if (b.getReplyerId() != null) {// ��˾���ӵ�
//				h.cancel.setTag(3);
//				h.cancel.setText("��Ҫ����");
//				h.cancel.setVisibility(View.VISIBLE);
//			} else {
//				h.cancel.setTag(3);
//				h.cancel.setText("��Ҫ����");
//				h.cancel.setVisibility(View.INVISIBLE);
//			}
//
//			// h.cancel.setEnabled(false);
//		}
		
		h.cancel.setVisibility(View.GONE);

		// ����״̬
		setState(v, b, h);
		return v;
	}

	// ����״̬,0.δ����,1.��ʾ�Ѵ���,2��ʾ�˿�ȡ����3��ʾ���⳵ȡ��
	private void setState(View v, BookBean b, Holder h) {
		switch (b.getState()) {
		// ˾���ӵ�ǰ
		case 0x01:
			h.head.setBackgroundResource(R.drawable.q13);
			h.number.setVisibility(View.INVISIBLE);
			h.dyTime.setTextColor(res.getColor(R.color.yellow_state));
//			h.dyTime.setText(ToolUtil.getTimeStr(b.getDyTime()));
			h.dyTime.setText("������");
			break;
		// ����ʧ��
		case 0x03:
			h.head.setBackgroundResource(R.drawable.q15);
			h.number.setVisibility(View.INVISIBLE);
			h.dyTime.setTextColor(res.getColor(R.color.text_gray));
			h.dyTime.setText("����ʧ��");
			break;
		// δ�ӵ��˿�ȡ��
		case 0x04:
			h.head.setBackgroundResource(R.drawable.q15);
			h.number.setVisibility(View.INVISIBLE);
			h.dyTime.setTextColor(res.getColor(R.color.text_gray));
			h.dyTime.setText("����ȡ��");
			break;

		// ִ�ж���-��˾���ӵ�
		case 0x05:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("�ѽӵ�");
			break;
		// ����������-�˿�ȷ���ϳ�/˾��ȷ���ϳ����˿���ֹ����/˾����ֹ������
		case 0x06:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("����������");
			break;
		// �������-˾��ȷ���ϳ�/�˿�ȷ���ϳ�
		case 0x07:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("�������");
			break;
		// ����ִ��ʧ��-�˿�ͬ����ֹ����/˾��ͬ����ֹ�����������ٲ�
		case 0x08:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("����ִ��ʧ��");
			break;
		// ����-�˿Ͳ�ͬ����ֹ����/˾����ͬ����ֹ������˾��δȷ���ϳ�/�˿�δȷ���ϳ�������ִ�г�ʱ
		case 0x09:
			h.head.setBackgroundResource(R.drawable.q12);
			h.number.setVisibility(View.VISIBLE);
			h.number.setText(b.getReplyerNumber());
			h.number.setTextColor(Color.BLACK);
			h.dyTime.setTextColor(res.getColor(R.color.green_state));
			h.dyTime.setText("����");
			break;
		default:
			try{
				h.head.setBackgroundResource(R.drawable.q12);
				h.number.setVisibility(View.GONE);
				h.dyTime.setTextColor(res.getColor(R.color.green_state));
				h.dyTime.setText("δ֪״̬");
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
			// ��˿͵绰
			// BookListActivity.callDriver(v.getContext(), detailTaxiNumber);

			// д����־
			switch (type) {
			case 1:// ȡ������
				ActionLogUtil.writeActionLog(act, R.array.airport_and_book_history_book_cancel, "");
				break;
			case 2:// ��������
				ActionLogUtil.writeActionLog(act, R.array.airport_and_book_history_book_endServer, "");
				break;
			case 3:// ����
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
