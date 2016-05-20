package cn.com.easytaxi.ui.view;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.expandable.Child;
import cn.com.easytaxi.expandable.ExpAdapter;
import cn.com.easytaxi.expandable.Group;
import cn.com.easytaxi.onetaxi.MainActivityNew;

/**
 * @ClassName: CancelBookDialog
 * @Description: TODO 评价对话框
 * @author Brook xu
 * @date 2013-8-20 下午1:17:41
 * @version 1.0
 */
public class SuggestDialog extends BaseDialog implements OnClickListener {
	private static final int SUGGEST_GOOD = 150;// 非常满意
	private static final int SUGGEST_NORMAL = 100;// 一般满意
	private static final int SUGGEST_BAD = 10;// 差评
	@Deprecated
	private static final int SUGGEST_CANCEL = 500; // 结束服务

	private Callback<Object> okBtnCallback;
	private Callback<Object> cancleBtnCallback;

	private RadioGroup radioGroup;
	private Button btnComfirm;
	private Button btnBack;
	private TextView tvTitle;
	private EditText etComment;
	private ExpandableListView epdList;
	private int reason;

	private ExpAdapter exAdapter;

	public SuggestDialog(Context context, Callback<Object> okBtnCallback, Callback<Object> cancleBtnCallback) {
		super(context);
		// TODO Auto-generated constructor stub
		this.okBtnCallback = okBtnCallback;
		this.cancleBtnCallback = cancleBtnCallback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub R.layout.dlg_close
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.stopserve_window);
		initView();
		initDataAndListener();
	}

	public void initView() {
		tvTitle = (TextView) this.findViewById(R.id.stopservice_title);
		epdList = (ExpandableListView) this.findViewById(R.id.stopwindow_expandableListView);
		etComment = (EditText) this.findViewById(R.id.stopservice_comment);
		btnComfirm = (Button) this.findViewById(R.id.stopservice_comfirm);
		btnBack = (Button) this.findViewById(R.id.stopservice_cancel);

	}

	public void initDataAndListener() {
		tvTitle.setText("评价");
		btnComfirm.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		exAdapter = new ExpAdapter(this.getContext(), MainActivityNew.getGroupData(3));
		epdList.setAdapter(exAdapter);
		epdList.setGroupIndicator(null);

		epdList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				// TODO Auto-generated method stub
				epdList.expandGroup(groupPosition);
				exAdapter.setSelectGroupIndex(((Group) exAdapter.getGroup(groupPosition)).getIndex());
				switch (groupPosition) {
				case 2:
					exAdapter.setSelectChildIndex(1);
					break;
				case 3:
					exAdapter.setSelectChildIndex(5);
					break;
				default:
					break;
				}
				return true;
			}
		});

		// 始终只展开一个组
		epdList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub

				int goupIndex = ((Group) exAdapter.getGroup(groupPosition)).getIndex();
				exAdapter.setSelectGroupIndex(goupIndex);

				switch (goupIndex) {
				case 3:
					exAdapter.setSelectChildIndex(1);
					break;
				case 4:
					exAdapter.setSelectChildIndex(5);
					break;
				default:
					break;
				}

				for (int i = 0; i < exAdapter.getGroupCount(); i++) {
					if (i != groupPosition && epdList.isGroupExpanded(i)) {
						epdList.collapseGroup(i);
					}
				}
			}
		});

		epdList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub

				Child child = (Child) ((Group) parent.getAdapter().getItem(groupPosition)).getChildrenList().get(childPosition);
				exAdapter.setSelectChildIndex(child.getIndex());
				exAdapter.notifyDataSetChanged();
				return true;
			}
		});
		// 默认选中第一项
		epdList.expandGroup(0);
	}

	private JSONObject getSuggestParams() {
		JSONObject object = new JSONObject();
		exAdapter.getSelectGroupIndex();
		int groupIndex = exAdapter.getSelectGroupIndex();
		int childIndex = exAdapter.getSelectChildIndex();
		String commentTxt = "";
		int commentValue = SUGGEST_NORMAL;
		switch (groupIndex) {
		// "非常满意"
		case 1:
			commentValue = SUGGEST_GOOD;
			commentTxt = "非常满意";
			break;
		// "一般满意"
		case 2:
			commentValue = SUGGEST_NORMAL;
			commentTxt = "一般满意";
			break;
		// "不太满意"
		case 3:
			commentValue = SUGGEST_BAD;
			commentTxt = MainActivityNew.childName[childIndex - 1];
			break;
		default:
			break;
		}

		String inputComment = etComment.getText().toString().trim();
		if (!StringUtils.isEmpty(inputComment)) {
			commentTxt = inputComment;
		}

		try {
			object.put("value", commentValue);
			object.put("comment", commentTxt);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object;
	}

	public void setOkBtnCallback(Callback<Object> okBtnCallback) {
		this.okBtnCallback = okBtnCallback;
	}

	public void setCancleBtnCallback(Callback<Object> cancleBtnCallback) {
		this.cancleBtnCallback = cancleBtnCallback;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.stopservice_comfirm:
			SuggestDialog.this.dismiss();
			if (okBtnCallback != null)
				okBtnCallback.handle(getSuggestParams());
			break;

		case R.id.stopservice_cancel:
			SuggestDialog.this.dismiss();
			if (cancleBtnCallback != null)
				cancleBtnCallback.handle(SuggestDialog.this);
			break;

		default:
			break;
		}
	}
}