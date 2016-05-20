package cn.com.easytaxi.ui;

import android.content.Context;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.util.ToastUtil;
import cn.com.easytaxi.util.XGsonUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xc.lib.xutils.exception.HttpException;
import com.xc.lib.xutils.http.ResponseInfo;
import com.xc.lib.xutils.http.callback.RequestCallBack;

public class SimpleCallBack extends RequestCallBack<String> {

	private Context context;

	public SimpleCallBack(Context context) {
		this.context = context;
	}

	public void onResult(int error, String errormsg, JsonElement obj) {

	}

	@Override
	public void onSuccess(ResponseInfo<String> responseInfo) {
		onFinish();
		JsonObject object;
		try {
			object = XGsonUtil.getJsonObject(responseInfo.result);
			int error = object.get("error").getAsInt();
			String errormsg = object.get("errormsg").toString();
			onResult(error, errormsg, object.get("datas"));
		} catch (Exception e) {
			e.printStackTrace();
			onFailure(new HttpException("Ω‚Œˆ¥ÌŒÛ"), e.toString());
		}
	}

	@Override
	public void onSuccessPre(ResponseInfo<String> responseInfo) {
		AppLog.LogI("xxb", "result->" + responseInfo.result);

	}

	@Override
	public void onFailure(HttpException error, String msg) {
		ToastUtil.show(context, "Õ¯¬Á“Ï≥£");
		onFinish();
	}

	@Override
	public void onFinish() {

	}
}
