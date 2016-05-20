package cn.com.easytaxi.mine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.easytaxi.etpassengersx.R;

/**
 * @author shiner
 */
public class MeFunctionAdapter extends PagerAdapter {
	private Context ctx;
	private List<MenuBean> menuDatas;
	private List<View> gridDatas;

	public MeFunctionAdapter(Context ctx, List<MenuBean> menuDatas) {
		super();
		this.ctx = ctx;
		this.menuDatas = menuDatas;
		reCreateGrid();
	}

	@Override
	public void notifyDataSetChanged() {
//		reCreateGrid();
		super.notifyDataSetChanged();
	}

	private void reCreateGrid() {
		if (gridDatas == null) {
			gridDatas = new ArrayList<View>();
		}
		gridDatas.clear();
		int numColumn = Integer.valueOf(ctx.getResources().getString(R.string.me_function_numColumns));
		int numRow = Integer.valueOf(ctx.getResources().getString(R.string.me_function_numRows));
		int pageSize = numColumn * numRow;
		int pageTotle = menuDatas.size() / pageSize;
		if (pageSize * pageTotle < menuDatas.size()) {
			pageTotle += 1;
		}
		for (int i = 0; i < pageTotle; i++) {
			GridView grid = (GridView) LayoutInflater.from(ctx).inflate(R.layout.me_function_grid, null);
			int start = i * pageSize;
			int end = (i + 1) * pageSize;
			if (end >= menuDatas.size()) {
				end = menuDatas.size();
			}
			List<MenuBean> pageData = menuDatas.subList(start, end);
			final GridAdapter adapter = new GridAdapter(pageData, ctx);
			grid.setAdapter(adapter);
			gridDatas.add(grid);
		}
	}

	@Override
	public int getCount() {
		return gridDatas.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = gridDatas.get(position);
		container.addView(v);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
