package cn.com.easytaxi.common;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.airport.AirportBookPublishFragement;
import cn.com.easytaxi.book.view.Datetime;
import cn.com.easytaxi.book.view.scrollwheel.ArrayWheelAdapter;
import cn.com.easytaxi.book.view.scrollwheel.WheelView;
import cn.com.easytaxi.expandable.Child;
import cn.com.easytaxi.expandable.ExpAdapter;
import cn.com.easytaxi.expandable.Group;
import cn.com.easytaxi.onetaxi.MainActivityNew;

import com.easytaxi.etpassengersx.R;

public final class Window {

	public static <T> void alert(Context context, String title, String content, final Callback<Object> callback) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setMessage(content);
			alert.setTitle(title);
			alert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (callback != null)
						callback.handle(which);
				}
			});
			alert.create().show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static <T> void alert(Context context, String title, String content) {
		alert(context, title, content, null);
	}

	public static <T> void info(Context context, String content) {
		alert(context, "��Ϣ��ʾ", content, null);
	}

	public static <T> void info(Context context, String content, Callback<Object> callback) {
		alert(context, "��Ϣ��ʾ", content, callback);
	}

	public static <T> void error(Context context, String content) {
		alert(context, "����", content, null);
	}

	public static <T> void error(Context context, String title, String content) {
		alert(context, title, content, null);
	}

	public static <T> void error(Context context, String title, String content, Callback<Object> callback) {
		alert(context, title, content, callback);
	}

	public static <T> void error(Context context, String content, Callback<Object> callback) {
		alert(context, "����", content, callback);
	}

	public static <T> void alert(Context context, String content) {
		alert(context, "����", content, null);
	}

	public static <T> AlertDialog confirm(Context context, String title, String content, final Callback<Object> callback1, final Callback<Object> callback2, String btn1Name, String btn2Name) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setCancelable(false);
			alert.setMessage(content);
			alert.setTitle(title);
			alert.setPositiveButton(btn1Name, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (callback1 != null) {

						dialog.dismiss();
						callback1.handle(which);
					}
				}
			});
			alert.setNegativeButton(btn2Name, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (callback2 != null) {

						callback2.handle(which);
						dialog.dismiss();
					}
				}
			});
			AlertDialog dialog = alert.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
			return dialog;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> AlertDialog confirm(boolean force, Context context, String title, String content, final Callback<Object> callback1, final Callback<Object> callback2, String btn1Name, String btn2Name) {
		try {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setCancelable(false);
			alert.setMessage(content);
			alert.setTitle(title);
			alert.setPositiveButton(btn1Name, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (callback1 != null) {

						dialog.dismiss();
						callback1.handle(which);
					}
				}
			});
			if (!force)
				alert.setNegativeButton(btn2Name, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (callback2 != null) {

							callback2.handle(which);
							dialog.dismiss();
						}
					}
				});
			AlertDialog dialog = alert.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
			return dialog;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> void confirm(Context context, String title, String content, final Callback<Object> callback1, final Callback<Object> callback2) {
		confirm(context, title, content, callback1, callback2, "ȷ��", "ȡ��");
	}

	public static <T> void confirm(Context context, String title, String content, final Callback<Object> callback1) {
		confirm(context, title, content, callback1, null);
	}

	public static <T> void confirm(Context context, String content, final Callback<Object> callback1) {
		confirm(context, "��ȷ��", content, callback1, null);
	}

	public static <T> void confirmOK(Context context, String content, final Callback<Object> callback1) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setCancelable(false);
		alert.setMessage(content);
		alert.setTitle("��ʾ");
		alert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callback1 != null)
					callback1.handle(which);
			}
		});

		AlertDialog dialog = alert.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}

	public static <T> AlertDialog input(Context context, String title, final Callback<Object> callback1, final Callback<Object> callback2, String btn1Name, String btn2Name) {
		final EditText editText = new EditText(context);
		editText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setView(editText);
		alert.setTitle(title);
		alert.setPositiveButton(btn1Name, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callback1 != null)
					callback1.handle(editText.getText().toString());
			}
		});
		alert.setNegativeButton(btn2Name, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callback2 != null)
					callback2.handle(which);
			}
		});
		AlertDialog dialog = alert.create();
		dialog.show();
		return dialog;
	}

	public static void selectHistory(final Context context, final List<String> rows, final Callback<String> callback) {
		LinearLayout l = new LinearLayout(context);
		l.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		l.setOrientation(LinearLayout.VERTICAL);

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setView(l);
		alert.setTitle("����ʷ��¼��ѡ��                         ");
		final AlertDialog dialog = alert.create();

		for (final String row : rows) {
			Button button = new Button(context);
			button.setText(row);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (callback != null) {
						callback.handle(row);
						dialog.dismiss();
					}
				}
			});
			l.addView(button, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

		dialog.show();
	}

	// ˾��ΥԼ��ʾ��
	public static AlertDialog breachTipWindow(final Context applicationContext, String title, String msg, final Callback<Object> callBack1, final Callback<Object> callBack2, final Callback<Object> callBack3) {

		AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);

		// builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(false);

		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (callBack1 != null) {
					callBack1.handle(null);
				}
			}
		});
		// builder.setNeutralButton("���´�", new
		// DialogInterface.OnClickListener() {
		//
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// dialog.dismiss();
		// if (callBack2 != null) {
		// callBack2.handle(null);
		// }
		// }
		// });
		builder.setPositiveButton("Ͷ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (callBack3 != null) {
					callBack3.handle(null);
				}
			}
		});

		builder.create();
		return builder.show();
	}

	// ����������ʾ��
	public static Dialog stopServeWindow(final Context context, String title, final Callback<Object> comfirmCallback, final Callback<Object> cancelCallback) {
		LayoutInflater factory = LayoutInflater.from(context);
		Dialog dlg = new Dialog(context, R.style.Customdialog);
		final View dialogView = factory.inflate(R.layout.onetaxi_stopserve_window_new, null);
		dlg.setContentView(dialogView);

		Button pop_good = (Button) dialogView.findViewById(R.id.pop_good);
		pop_good.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		Button pop_bad = (Button) dialogView.findViewById(R.id.pop_bad);
		pop_bad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		Button pop_cancel = (Button) dialogView.findViewById(R.id.pop_cancel);
		pop_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		return dlg;
	}

	// about����
	public static <T> void about(final Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.about, null);
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		String versionName = pi.versionName + " Ver.";
		TextView tv = (TextView) view.findViewById(R.id.about_version);
		tv.setText(versionName);
		view.findViewById(R.id.companysite).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse("http://www.easytaxi.com.cn");
				context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setView(view);
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
			}
		});
		alert.create().show();
	}

	public static void dateTime(Context context, String time, final Callback<String> callback) {
		final Datetime datetime = new Datetime(context, time);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("ѡ��ʱ��");
		builder.setView(datetime);
		builder.setNeutralButton("ȷ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.handle(datetime.getDatetimeStr());
				}
			}
		});
		builder.setNegativeButton("ȡ��", null);

		builder.create().show();

	}

	private static String getContent(int time) {
		// TODO Auto-generated method stub
		StringBuilder content = new StringBuilder("�ȴ�����ʱ��");
		if (time < 60) {
			content.append(time);
			content.append("��");
		} else {
			content.append(time / 60);
			content.append("��");
			content.append(time % 60);
			content.append("��");
		}
		return content.toString();
	}

	public static Dialog Tip(Context ctx, String tip) {

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("��Ϣ��ʾ");
		builder.setMessage(tip);
		builder.setCancelable(true);

		builder.setNegativeButton("֪����", null);
		Dialog d = builder.create();
		d.show();
		return d;
	}

	public static Dialog tipSystem(Context ctx, String tip) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("��Ϣ��ʾ");
		builder.setMessage(tip);
		builder.setCancelable(true);
		builder.setNegativeButton("֪����", null);
		Dialog d = builder.create();
		d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		d.show();
		return d;
	}

	public static void callTaxi(final Context ctx, String phone) throws Exception {
		if (phone == null || phone.equals("")) {
			throw new Exception("�绰����δ֪");
		}

		final String[] phoneList = phone.split(",");
		if (phoneList.length == 1) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneList[0]));
			ctx.startActivity(intent);
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		DialogInterface.OnClickListener clickLis = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String number = phoneList[which];
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
				ctx.startActivity(intent);
			}

		};
		builder.setTitle("ѡ��绰");
		builder.setSingleChoiceItems(phoneList, -1, clickLis);
		builder.setCancelable(true);
		final Dialog d = builder.create();
		d.show();
	}

	/**
	 * �µ�ѡ���ó�ʱ��ĵ�����
	 * 
	 * @param context
	 * @param callback
	 * @param timeLine
	 *            �ӷ�������ȡ��ʱ��������
	 * @param defaultMinUseCarTime
	 *            ����Ĭ�ϵ���С�ó�ʱ��
	 * @param defaultMaxUseCarTime
	 *            ����Ĭ�ϵ�����ó�ʱ��
	 */
	public static void selectDate(final Context context, final Callback<String[]> callback, final NewNetworkRequest.TimeLine timeLine, final int defaultMinUseCarTime, final int defaultMaxUseCarTime) {
		View view = LayoutInflater.from(context).inflate(R.layout.wheel_scroll_layout, null);
		final Dialog dialog = new Dialog(context, R.style.Customdialog);
		dialog.setContentView(view);

		Button btnok = (Button) view.findViewById(R.id.button_ok);
		Button btnCancel = (Button) view.findViewById(R.id.button_cancel);

		final WheelView wheelDay = (WheelView) view.findViewById(R.id.am_pm);
		final WheelView wheelHour = (WheelView) view.findViewById(R.id.clockadd_hour);
		final WheelView wheelMinutes = (WheelView) view.findViewById(R.id.clockadd_minutes);
		final Resources res = context.getResources();

		final String[] days = AirportBookPublishFragement.getTimeValues(0, 0, -1, 0, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime);
		final String[] hours = AirportBookPublishFragement.getTimeValues(1, 0, -1, 0, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime);
		final String[] mins = AirportBookPublishFragement.getTimeValues(2, 0, -1, 0, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime);

		wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(mins));
		wheelMinutes.setVisibleItems(5);
		wheelMinutes.setCyclic(false);
		wheelMinutes.setCurrentItem(0);

		wheelHour.setAdapter(new ArrayWheelAdapter<String>(hours));
		wheelHour.setVisibleItems(5);
		wheelHour.setCyclic(false);
		wheelHour.setCurrentItem(0);

		wheelDay.setAdapter(new ArrayWheelAdapter<String>(days));
		wheelDay.setVisibleItems(5);
		wheelDay.setCyclic(false);
		wheelDay.setCurrentItem(0);

		wheelDay.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				try {
					wheelHour.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(1, 0, -1, newValue, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime)));
					wheelHour.setCurrentItem(0);
					wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, 0, -1, newValue, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime)));
					wheelMinutes.setCurrentItem(0);
				} catch (Exception e) {
					e.printStackTrace();
					wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, 0, -1, newValue, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime)));
					wheelMinutes.setCurrentItem(0);
				}
			}
		});

		wheelHour.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int day = wheelDay.getCurrentItem();
				int hourIndex = wheel.getCurrentItem();
				int hour = Integer.parseInt(wheel.getAdapter().getItem(newValue));
				wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, hourIndex, hour, day, timeLine, defaultMinUseCarTime, defaultMaxUseCarTime)));
				wheelMinutes.setCurrentItem(0);
			}
		});

		wheelMinutes.addChangingListener(new WheelView.OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});

		btnok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String dayChoosedIndex = "" + wheelDay.getCurrentItem();
				String day = wheelDay.getAdapter().getItem(wheelDay.getCurrentItem());
				String hour = wheelHour.getAdapter().getItem(wheelHour.getCurrentItem());
				String min = wheelMinutes.getAdapter().getItem(wheelMinutes.getCurrentItem());

				boolean isChooseCorrect = checkChooseTime();
				if (isChooseCorrect) {
					if (day == null || hour == null || min == null) {
						Toast.makeText(context, res.getString(R.string.tip_book_time), Toast.LENGTH_LONG).show();
					} else {
						if (callback != null) {
							callback.handle(new String[] { dayChoosedIndex, day, hour, min });
						}
						dialog.dismiss();
					}
				} else {
					Toast.makeText(context, res.getString(R.string.tip_book_time), Toast.LENGTH_LONG).show();
				}

			}

			/**
			 * ���ѡ��ʱ���Ƿ�ȵ�ǰʱ���
			 */
			private boolean checkChooseTime() {
				String day = wheelDay.getAdapter().getItem(wheelDay.getCurrentItem());
				int chooseHour = Integer.parseInt(wheelHour.getAdapter().getItem(wheelHour.getCurrentItem()));
				int chooseMin = Integer.parseInt(wheelMinutes.getAdapter().getItem(wheelMinutes.getCurrentItem()));

				Calendar now = Calendar.getInstance();
				int hourNow = now.get(Calendar.HOUR_OF_DAY);
				int minNow = now.get(Calendar.MINUTE);

				if (day.equals("����")) {
					if (chooseHour > hourNow) {
						return true;
					} else if (chooseHour == hourNow && chooseMin > minNow) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * �ɵ�ѡ���ó�ʱ��ĵ�����
	 * 
	 * @param context
	 * @param callback
	 */
	public static void selectDate(final Context context, final Callback<String[]> callback) {
		View view = LayoutInflater.from(context).inflate(R.layout.wheel_scroll_layout, null);
		final Dialog dialog = new Dialog(context, R.style.Customdialog);
		dialog.setContentView(view);

		Button btnok = (Button) view.findViewById(R.id.button_ok);
		Button btnCancel = (Button) view.findViewById(R.id.button_cancel);

		final WheelView wheelDay = (WheelView) view.findViewById(R.id.am_pm);
		final WheelView wheelHour = (WheelView) view.findViewById(R.id.clockadd_hour);
		final WheelView wheelMinutes = (WheelView) view.findViewById(R.id.clockadd_minutes);
		final Resources res = context.getResources();

		final String[] days = AirportBookPublishFragement.getTimeValues(0, -1, 0);
		final String[] hours = AirportBookPublishFragement.getTimeValues(1, -1, 0);
		final String[] mins = AirportBookPublishFragement.getTimeValues(2, -1, 0);

		wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(mins));
		wheelMinutes.setVisibleItems(5);
		wheelMinutes.setCyclic(false);
		wheelMinutes.setCurrentItem(0);

		wheelHour.setAdapter(new ArrayWheelAdapter<String>(hours));
		wheelHour.setVisibleItems(5);
		wheelHour.setCyclic(false);
		wheelHour.setCurrentItem(0);

		wheelDay.setAdapter(new ArrayWheelAdapter<String>(days));
		wheelDay.setVisibleItems(5);
		wheelDay.setCyclic(false);
		wheelDay.setCurrentItem(0);

		wheelDay.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				try {
					wheelHour.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(1, -1, newValue)));
					wheelHour.setCurrentItem(0);
					wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, -1, newValue)));
					wheelMinutes.setCurrentItem(0);
				} catch (Exception e) {
					e.printStackTrace();
					wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, -1, newValue)));
					wheelMinutes.setCurrentItem(0);
				}
			}
		});

		wheelHour.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int day = wheelDay.getCurrentItem();
				int hour = Integer.parseInt(wheel.getAdapter().getItem(newValue));
				wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, hour, day)));
				wheelMinutes.setCurrentItem(0);
			}
		});

		wheelMinutes.addChangingListener(new WheelView.OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});

		btnok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String day = wheelDay.getAdapter().getItem(wheelDay.getCurrentItem());
				String hour = wheelHour.getAdapter().getItem(wheelHour.getCurrentItem());
				String min = wheelMinutes.getAdapter().getItem(wheelMinutes.getCurrentItem());

				boolean isChooseCorrect = checkChooseTime();
				if (isChooseCorrect) {
					if (day == null || hour == null || min == null) {
						Toast.makeText(context, res.getString(R.string.tip_book_time), Toast.LENGTH_LONG).show();
					} else {
						if (callback != null) {
							callback.handle(new String[] { day, hour, min });
						}
						dialog.dismiss();
					}
				} else {
					Toast.makeText(context, res.getString(R.string.tip_book_time), Toast.LENGTH_LONG).show();
				}

			}

			/**
			 * ���ѡ��ʱ���Ƿ�ȵ�ǰʱ���
			 */
			private boolean checkChooseTime() {
				String day = wheelDay.getAdapter().getItem(wheelDay.getCurrentItem());
				int chooseHour = Integer.parseInt(wheelHour.getAdapter().getItem(wheelHour.getCurrentItem()));
				int chooseMin = Integer.parseInt(wheelMinutes.getAdapter().getItem(wheelMinutes.getCurrentItem()));

				Calendar now = Calendar.getInstance();
				int hourNow = now.get(Calendar.HOUR_OF_DAY);
				int minNow = now.get(Calendar.MINUTE);

				if (day.equals("����")) {
					if (chooseHour > hourNow) {
						return true;
					} else if (chooseHour == hourNow && chooseMin > minNow) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public static void stopServiceWindow(final Context context, final int type, final Callback<JSONObject> callback) {
		final int commentGood = 150;// �ǳ�����
		final int commentNomal = 100;// һ������
		final int commentBad = 10;// ����
		final int commentCancelBook = 500; // ��������

		LayoutInflater factory = LayoutInflater.from(context);
		final Dialog dlg = new Dialog(context, R.style.Customdialog);
		final View dialogView = factory.inflate(R.layout.stopserve_window, null);
		dlg.setContentView(dialogView);

		final TextView tvTitle = (TextView) dialogView.findViewById(R.id.stopservice_title);
		final ExpandableListView epdList = (ExpandableListView) dialogView.findViewById(R.id.stopwindow_expandableListView);
		final EditText tvComment = (EditText) dialogView.findViewById(R.id.stopservice_comment);
		Button btnComfirm = (Button) dialogView.findViewById(R.id.stopservice_comfirm);
		Button btnBack = (Button) dialogView.findViewById(R.id.stopservice_cancel);

		if (type == 3) {
			tvTitle.setText("����");
		} else {
			tvTitle.setText("����");
		}

		final ExpAdapter exAdapter = new ExpAdapter(context, MainActivityNew.getGroupData(type));
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

		// ʼ��ֻչ��һ����
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

		btnComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exAdapter.getSelectGroupIndex();

				int groupIndex = exAdapter.getSelectGroupIndex();
				int childIndex = exAdapter.getSelectChildIndex();
				String commentTxt = "";
				int commentValue = 100;
				switch (groupIndex) {
				// "�ǳ�����"
				case 1:
					commentValue = commentGood;
					commentTxt = "";
					break;
				// "һ������"
				case 2:
					commentValue = commentNomal;
					commentTxt = "";
					break;
				// "��̫����"
				case 3:
					commentValue = commentBad;
					commentTxt = MainActivityNew.childName[childIndex - 1];
					break;
				// "ȡ������"
				case 4:
					commentValue = commentCancelBook;
					commentTxt = MainActivityNew.childName[childIndex - 1];
					break;
				default:
					break;
				}

				String inputComment = tvComment.getText().toString().trim();
				if (!StringUtils.isEmpty(inputComment)) {
					commentTxt = inputComment;
				}

				JSONObject object = new JSONObject();
				try {
					object.put("value", commentValue);
					object.put("comment", commentTxt);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Toast.makeText(context, object.toString(),
				// Toast.LENGTH_SHORT).show();

				callback.handle(object);
				dlg.dismiss();
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
			}
		});

		// Ĭ��ѡ�е�һ��
		epdList.expandGroup(0);

		dlg.show();
	}

	private static void keepDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void distoryDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showMessageDialog(final Context context, String title, String msg, String comfirmBtn, String cancelBtn, final Callback<Object> comfirmCallback, final Callback<Object> cancelCallback) {
		LayoutInflater factory = LayoutInflater.from(context);
		final Dialog dlg = new Dialog(context, R.style.Customdialog);
		final View dialogView = factory.inflate(R.layout.showmsg_window, null);
		final TextView tvTitle = (TextView) dialogView.findViewById(R.id.show_message_title);
		final TextView tvTxt = (TextView) dialogView.findViewById(R.id.show_message_txt);
		final TextView tvComfirm = (TextView) dialogView.findViewById(R.id.show_msg_comfirm_txt);
		final TextView tvCancel = (TextView) dialogView.findViewById(R.id.show_msg_cancel_txt);
		final View btnComfirm = dialogView.findViewById(R.id.show_msg_comfirm);
		final View btnBack = dialogView.findViewById(R.id.show_msg_cancel);
		dlg.setContentView(dialogView);

		if (StringUtils.isEmpty(title)) {
			tvTitle.setText("��Ϣ��ʾ");
		} else {
			tvTitle.setText(title);
		}

		if (StringUtils.isEmpty(msg)) {
			tvTxt.setText("");
		} else {
			tvTxt.setText(msg);
		}

		if (StringUtils.isEmpty(comfirmBtn)) {
			tvComfirm.setText("ȷ��");
		} else {
			tvComfirm.setText(comfirmBtn);
		}
		btnComfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				if (comfirmCallback != null) {
					comfirmCallback.handle(null);
				}
			}
		});

		if (!StringUtils.isEmpty(cancelBtn)) {
			tvCancel.setText(cancelBtn);
			btnBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dlg.dismiss();
					if (cancelCallback != null) {
						cancelCallback.handle(null);
					}
				}
			});
		} else {
			btnBack.setVisibility(View.GONE);
		}
		dlg.show();
	}

	public static void showDlgList(final Context context, final String title, final String[] items, final Callback<String> callback) {
		try {
			// �Զ���title��������ʾ
			TextView tvTitle = new TextView(context);
			tvTitle.setGravity(Gravity.CENTER);
			tvTitle.setTextSize(24);
			tvTitle.setPadding(0, 10, 0, 10);
			tvTitle.setText(title);
			new AlertDialog.Builder(context).setCustomTitle(tvTitle).setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					callback.handle(items[which]);
				}
			}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �µ�ѡ���ó�ʱ��ĵ�����
	 * 
	 * @param context
	 * @param callback
	 * @param timeLine
	 *            �ӷ�������ȡ��ʱ��������
	 * @param defaultMinUseCarTime
	 *            ����Ĭ�ϵ���С�ó�ʱ��
	 * @param defaultMaxUseCarTime
	 *            ����Ĭ�ϵ�����ó�ʱ��
	 */
	public static void selectCarInfos(final Context context, final Callback<WheelCallData[]> callback) {
		View view = LayoutInflater.from(context).inflate(R.layout.wheel_carinfo_layout, null);
		final Dialog dialog = new Dialog(context, R.style.Customdialog);
		dialog.setContentView(view);

		android.view.Window dlgWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dlgWindow.getAttributes();
		dlgWindow.setGravity(Gravity.BOTTOM);

		WindowManager m = dlgWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
		WindowManager.LayoutParams p = dlgWindow.getAttributes(); //
		// ��ȡ�Ի���ǰ�Ĳ���ֵ
		// p.height = (int) (d.getHeight() * 1); // �߶�����Ϊ��Ļ��1
		p.width = (int) (d.getWidth() * 1); // �������Ϊ��Ļ��1
		dlgWindow.setAttributes(p);

		Button btnok = (Button) view.findViewById(R.id.bj_button_ok);
		Button btnCancel = (Button) view.findViewById(R.id.bj_button_cancel);

		final WheelView wheelFirst = (WheelView) view.findViewById(R.id.bj_am_pm);
		final WheelView wheelTwo = (WheelView) view.findViewById(R.id.bj_clockadd_hour);
		final WheelView wheelThree = (WheelView) view.findViewById(R.id.bj_clockadd_minutes);
		final String[] persons = context.getResources().getStringArray(R.array.bj_persons);
		final String[] banches = context.getResources().getStringArray(R.array.bj_banche);
		final String[] floors = context.getResources().getStringArray(R.array.bj_floors);
		wheelThree.setAdapter(new ArrayWheelAdapter<String>(floors));
		wheelThree.setVisibleItems(5);
		wheelThree.setCyclic(false);
		wheelThree.setCurrentItem(0);

		wheelTwo.setAdapter(new ArrayWheelAdapter<String>(banches));
		wheelTwo.setVisibleItems(5);
		wheelTwo.setCyclic(false);
		wheelTwo.setCurrentItem(0);

		wheelFirst.setAdapter(new ArrayWheelAdapter<String>(persons));
		wheelFirst.setVisibleItems(5);
		wheelFirst.setCyclic(false);
		wheelFirst.setCurrentItem(0);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				wheelFirst.justify();
				wheelTwo.justify();
				wheelThree.justify();
			}
		}, 500);
		

		wheelFirst.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});

		wheelTwo.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});

		wheelThree.addChangingListener(new WheelView.OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});
		new WheelCallData(wheelFirst.getCurrentItem(), persons[wheelFirst.getCurrentItem()]);
		btnok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callback != null) {

					callback.handle(new WheelCallData[] { new WheelCallData(wheelFirst.getCurrentItem(), persons[wheelFirst.getCurrentItem()]),new WheelCallData(wheelTwo.getCurrentItem(), banches[wheelTwo.getCurrentItem()]),new WheelCallData(wheelThree.getCurrentItem(), floors[wheelThree.getCurrentItem()]) });
				}
				dialog.dismiss();

			}

			/**
			 * ���ѡ��ʱ���Ƿ�ȵ�ǰʱ���
			 */
			private boolean checkChooseTime() {
				String day = wheelFirst.getAdapter().getItem(wheelFirst.getCurrentItem());
				int chooseHour = Integer.parseInt(wheelTwo.getAdapter().getItem(wheelTwo.getCurrentItem()));
				int chooseMin = Integer.parseInt(wheelThree.getAdapter().getItem(wheelThree.getCurrentItem()));

				Calendar now = Calendar.getInstance();
				int hourNow = now.get(Calendar.HOUR_OF_DAY);
				int minNow = now.get(Calendar.MINUTE);

				if (day.equals("����")) {
					if (chooseHour > hourNow) {
						return true;
					} else if (chooseHour == hourNow && chooseMin > minNow) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * �ɵ�ѡ���ó�ʱ��ĵ�����
	 * 
	 * @param context
	 * @param callback
	 */
	public static void selectInfos(final Context context, final Callback<String[]> callback) {
		View view = LayoutInflater.from(context).inflate(R.layout.wheel_scroll_layout, null);
		final Dialog dialog = new Dialog(context, R.style.Customdialog);
		dialog.setContentView(view);

		Button btnok = (Button) view.findViewById(R.id.button_ok);
		Button btnCancel = (Button) view.findViewById(R.id.button_cancel);

		final WheelView wheelDay = (WheelView) view.findViewById(R.id.am_pm);
		final WheelView wheelHour = (WheelView) view.findViewById(R.id.clockadd_hour);
		final WheelView wheelMinutes = (WheelView) view.findViewById(R.id.clockadd_minutes);
		final Resources res = context.getResources();

		final String[] days = AirportBookPublishFragement.getTimeValues(0, -1, 0);
		final String[] hours = AirportBookPublishFragement.getTimeValues(1, -1, 0);
		final String[] mins = AirportBookPublishFragement.getTimeValues(2, -1, 0);

		wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(mins));
		wheelMinutes.setVisibleItems(5);
		wheelMinutes.setCyclic(false);
		wheelMinutes.setCurrentItem(0);

		wheelHour.setAdapter(new ArrayWheelAdapter<String>(hours));
		wheelHour.setVisibleItems(5);
		wheelHour.setCyclic(false);
		wheelHour.setCurrentItem(0);

		wheelDay.setAdapter(new ArrayWheelAdapter<String>(days));
		wheelDay.setVisibleItems(5);
		wheelDay.setCyclic(false);
		wheelDay.setCurrentItem(0);

		wheelDay.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				try {
					wheelHour.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(1, -1, newValue)));
					wheelHour.setCurrentItem(0);
					wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, -1, newValue)));
					wheelMinutes.setCurrentItem(0);
				} catch (Exception e) {
					e.printStackTrace();
					wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, -1, newValue)));
					wheelMinutes.setCurrentItem(0);
				}
			}
		});

		wheelHour.addChangingListener(new WheelView.OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int day = wheelDay.getCurrentItem();
				int hour = Integer.parseInt(wheel.getAdapter().getItem(newValue));
				wheelMinutes.setAdapter(new ArrayWheelAdapter<String>(AirportBookPublishFragement.getTimeValues(2, hour, day)));
				wheelMinutes.setCurrentItem(0);
			}
		});

		wheelMinutes.addChangingListener(new WheelView.OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		});

		btnok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String day = wheelDay.getAdapter().getItem(wheelDay.getCurrentItem());
				String hour = wheelHour.getAdapter().getItem(wheelHour.getCurrentItem());
				String min = wheelMinutes.getAdapter().getItem(wheelMinutes.getCurrentItem());

				boolean isChooseCorrect = checkChooseTime();
				if (isChooseCorrect) {
					if (day == null || hour == null || min == null) {
						Toast.makeText(context, res.getString(R.string.tip_book_time), Toast.LENGTH_LONG).show();
					} else {
						if (callback != null) {
							callback.handle(new String[] { day, hour, min });
						}
						dialog.dismiss();
					}
				} else {
					Toast.makeText(context, res.getString(R.string.tip_book_time), Toast.LENGTH_LONG).show();
				}

			}

			/**
			 * ���ѡ��ʱ���Ƿ�ȵ�ǰʱ���
			 */
			private boolean checkChooseTime() {
				String day = wheelDay.getAdapter().getItem(wheelDay.getCurrentItem());
				int chooseHour = Integer.parseInt(wheelHour.getAdapter().getItem(wheelHour.getCurrentItem()));
				int chooseMin = Integer.parseInt(wheelMinutes.getAdapter().getItem(wheelMinutes.getCurrentItem()));

				Calendar now = Calendar.getInstance();
				int hourNow = now.get(Calendar.HOUR_OF_DAY);
				int minNow = now.get(Calendar.MINUTE);

				if (day.equals("����")) {
					if (chooseHour > hourNow) {
						return true;
					} else if (chooseHour == hourNow && chooseMin > minNow) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * ������ϸ��Ϣ�ĵ�����
	 * 
	 * @param context
	 * @param type
	 * @param yOffset
	 */
	public static void showCarDetails(final Context context, int type, int yOffset) {
		View view = LayoutInflater.from(context).inflate(R.layout.dlg_carinfos, null);
		final Dialog dialog = new Dialog(context, R.style.Customdialog);
		dialog.setContentView(view);

		android.view.Window dlgWindow = dialog.getWindow();
		dlgWindow.setGravity(Gravity.BOTTOM);

		WindowManager m = dlgWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
		WindowManager.LayoutParams lp = dlgWindow.getAttributes(); //
		// ��ȡ�Ի���ǰ�Ĳ���ֵ
		// p.height = (int) (d.getHeight() * 1); // �߶�����Ϊ��Ļ��1
		lp.width = (int) (d.getWidth() * 1); // �������Ϊ��Ļ��1
		lp.y = yOffset;
		dlgWindow.setAttributes(lp);

		String[] strValuesOne = { "1T", "2950*1500*1590mm", "30Ԫ�𲽼ۣ�3�����ڣ�+3Ԫ/����" };
		String[] strValuesTwo = { "1.95T", "4200*2120*2420mm", "35Ԫ�𲽼ۣ�3�����ڣ�+5Ԫ/����" };

		ImageView btnCancel = (ImageView) view.findViewById(R.id.dlg_iv_cancel);
		TextView tvChiCun = (TextView) view.findViewById(R.id.dlg_tv_chicun);
		TextView tvPrice = (TextView) view.findViewById(R.id.dlg_tv_price);
		TextView tvTiji = (TextView) view.findViewById(R.id.dlg_tv_tiji);
		ImageView ivCar = (ImageView) view.findViewById(R.id.ivCar);
		TextView tvCarName = (TextView) view.findViewById(R.id.tvCarName);

		if (type == 1) {
			tvCarName.setText("�����");
			tvTiji.setText(strValuesOne[0]);
			tvChiCun.setText(strValuesOne[1]);
			tvPrice.setText(strValuesOne[2]);
			ivCar.setImageResource(R.drawable.pic_car_info1);
		} else {
			tvCarName.setText("��ʽ��");
			ivCar.setImageResource(R.drawable.pic_car_info2);
			tvTiji.setText(strValuesTwo[0]);
			tvChiCun.setText(strValuesTwo[1]);
			tvPrice.setText(strValuesTwo[2]);
		}

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public static void selectBanyungong(final Context context, int yOffset, int defaultPserson, final Callback<Integer> callback) {
		View view = LayoutInflater.from(context).inflate(R.layout.dlg_banyungong, null);
		final Dialog dialog = new Dialog(context, R.style.Customdialog);
		dialog.setContentView(view);

		android.view.Window dlgWindow = dialog.getWindow();
		dlgWindow.setGravity(Gravity.BOTTOM);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		WindowManager m = dlgWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������
		WindowManager.LayoutParams lp = dlgWindow.getAttributes(); //
		// ��ȡ�Ի���ǰ�Ĳ���ֵ
		// p.height = (int) (d.getHeight() * 1); // �߶�����Ϊ��Ļ��1
		lp.width = (int) (d.getWidth() * 1); // �������Ϊ��Ļ��1
		lp.y = yOffset;
		dlgWindow.setAttributes(lp);

		final TextView ivPcount0 = (TextView) view.findViewById(R.id.dlg_tv_pcount_0);
		final TextView ivPcount1 = (TextView) view.findViewById(R.id.dlg_tv_pcount_1);
		final TextView ivPcount2 = (TextView) view.findViewById(R.id.dlg_tv_pcount_2);
		final TextView ivPcount3 = (TextView) view.findViewById(R.id.dlg_tv_pcount_3);
		final TextView ivPcount4 = (TextView) view.findViewById(R.id.dlg_tv_pcount_4);
		final TextView ivPcount5 = (TextView) view.findViewById(R.id.dlg_tv_pcount_5);

		ImageView btnCancel = (ImageView) view.findViewById(R.id.dlg_iv_cancel);
		ImageView btnOk = (ImageView) view.findViewById(R.id.dlg_iv_ok);
		final SeekBar seekBar = (SeekBar) view.findViewById(R.id.dlg_sb_person);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				ivPcount0.setVisibility(View.INVISIBLE);
				ivPcount1.setVisibility(View.INVISIBLE);
				ivPcount2.setVisibility(View.INVISIBLE);
				ivPcount3.setVisibility(View.INVISIBLE);
				ivPcount4.setVisibility(View.INVISIBLE);
				ivPcount5.setVisibility(View.INVISIBLE);
				switch (progress) {
				case 0:
					ivPcount0.setVisibility(View.VISIBLE);
					break;
				case 1:
					ivPcount1.setVisibility(View.VISIBLE);
					break;
				case 2:
					ivPcount2.setVisibility(View.VISIBLE);
					break;
				case 3:
					ivPcount3.setVisibility(View.VISIBLE);
					break;
				case 4:
					ivPcount4.setVisibility(View.VISIBLE);
					break;
				case 5:
					ivPcount5.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			}
		});

		seekBar.setProgress(defaultPserson);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				callback.handle(seekBar.getProgress());
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public static class WheelCallData {
		public int index;
		public String name;

		public WheelCallData(int index, String name) {
			this.index = index;
			this.name = name;
		}
	}
}
