package cn.com.easytaxi.mine.account;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AccountTotle {
	private int type;
	private int totle;
	private List<Account> accountList;
	private Gson gson;

	public AccountTotle(int type) {
		gson = new Gson();
		this.type = type;
	}

	public void setValues(JSONObject jsonObject) {
		try {
			Type typeToken = null;
			if (type == Account.TYPE_ET_MONEY) {
				typeToken = new TypeToken<List<EtMoneyBean>>() {
				}.getType();
			} else if (type == Account.TYPE_ET_COIN) {
				typeToken = new TypeToken<List<EtCoinBean>>() {
				}.getType();
			} else if (type == Account.TYPE_ET_SCORE) {
				typeToken = new TypeToken<List<EtScoreBean>>() {
				}.getType();
			}
			totle = jsonObject.getJSONObject("datas").getInt("money");
			accountList = gson.fromJson(jsonObject.getJSONObject("datas").getString("logs"), typeToken);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTotle() {
		return totle;
	}

	public void setTotle(int totle) {
		this.totle = totle;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}
}
