package cn.com.easytaxi.mine;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.ui.MoreWebviewActivity;

/**
 * @author shiner
 */
public class GridAdapter extends BaseAdapter {
	private List<MenuBean> datas;
	private Context ctx;

	public GridAdapter(List<MenuBean> datas, Context ctx) {
		super();
		this.datas = datas;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public MenuBean getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getCacheId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(R.layout.me_function_grid_item, null);
		}
		ImageButton btn = (ImageButton) convertView.findViewById(R.id.me_grid_btn);
		TextView text = (TextView) convertView.findViewById(R.id.me_grid_text);

		final MenuBean m = datas.get(position);
		btn.setImageResource(m.getImgRes());
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleMenuClick(m);
			}
		});

		text.setText(m.getName());
		return convertView;
	}

	protected void handleMenuClick(MenuBean menu) {
		try {
			String action = null;
			switch (menu.getActionType()) {
			case MenuBean.ACTION_TYPE_ACTIVITY:
				action = menu.getAction();
				if (action != null) {
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(ctx, action));
					ctx.startActivity(intent);
				}
				break;

			case MenuBean.ACTION_TYPE_URL:
				Intent intent = new Intent(ctx, MoreWebviewActivity.class);
				intent.putExtra("url", menu.getAction());
				intent.putExtra("title", menu.getName());
				ctx.startActivity(intent);
				break;

			case MenuBean.ACTION_TYPE_CALLBACK:
				if (menu.getCallback() != null) {
					menu.getCallback().handle(ctx);
				}
				break;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
