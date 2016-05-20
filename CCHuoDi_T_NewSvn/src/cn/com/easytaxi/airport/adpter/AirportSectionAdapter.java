package cn.com.easytaxi.airport.adpter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.com.easytaxi.airport.bean.AirportBean;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.PinyinConvertor;

public class AirportSectionAdapter extends ArrayAdapter<AirportBean> implements SectionIndexer {

	private static final int TYPE_SECTION_HEADER = 0; 
	private static final int TYPE_LIST_ITEM = 1;

	int defaultRowLayoutResID;
	Context context;
	LayoutInflater inflater;
	ArrayList<AirportBean> items;
	int resource;
    int lastChooseIndex = 0;
    /**
     * 所有机场的数据在列表中的开始位置
     */
    int startIndex=0;
    private String choosedAirportName="";
    
	public AirportSectionAdapter(Context context, int resourceId, ArrayList<AirportBean> items) {
		super(context, resourceId, items);
		this.context = context;
		this.resource = resourceId;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public AirportBean getItem(int position) {
		return items.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		AirportBean item = items.get(position);
		if (item.isHeader) {
			return TYPE_SECTION_HEADER;
		} else {
			return TYPE_LIST_ITEM;
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		AirportBean item = getItem(position);

		if (convertView == null) {
			if (item.isHeader) {

				convertView = inflater.inflate(R.layout.platform_city_select_item, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.row_item_title);

				// holder.subtitle =
				// (TextView)convertView.findViewById(R.id.row_item_subtitle);
				convertView.setTag(holder);
			} else {
				convertView = inflater.inflate(R.layout.platform_city_select_item_title, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.row_item_title);
				// holder.subtitle =
				// (TextView)convertView.findViewById(R.id.row_item_subtitle);
				convertView.setTag(holder);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(item.name);
		if(item.isHeader){
			holder.title.setTextColor(context.getResources().getColor(R.color.white));
		}else{
			if(item.name.equals(choosedAirportName)){
				holder.title.setTextColor(Color.RED);
			}else{
				holder.title.setTextColor(context.getResources().getColor(R.color.airport_item_color));
			}
		}

		return convertView;
	}

	
	
	// Sort the Airport 
	@Override
	public int getPositionForSection(int section) {
		String[] letters = (String[]) getSections();
		if (letters[section].equals("所选")) {
			return 0;
		}
		if (letters[section].equals("全部")) {
			return startIndex-1;
		}

		// 0 热门
		// 1 - A 2 -B , 3 - C ,4 - D
		for (int i = startIndex; i < items.size(); i++) {
			String le = PinyinConvertor.cn2py(items.get(i).name, 0);
			if (le != null && letters[section] != null)
				if (le.equalsIgnoreCase(letters[section])) {
					lastChooseIndex = i;
					return i;
				}
		}
		return lastChooseIndex;//当listView中没有和右侧列表匹配的项目时候，返回上次选中的位置，不能返回-1,这样防止listView获取焦点，导致右侧AlphabetBar失去焦点
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return new String[] {"所选","全部","A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
	}

	private class ViewHolder {
		public TextView title;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public String getChoosedAirportName() {
		return choosedAirportName;
	}

	public void setChoosedAirportName(String choosedAirportName) {
		this.choosedAirportName = choosedAirportName;
	}
}
