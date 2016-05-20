package cn.com.easytaxi.mine.account;

import com.google.gson.annotations.SerializedName;

/**
 * »ý·Ö
 * @ClassName: EtScoreBean 
 * @Description: TODO
 * @author Brook xu
 * @date 2013-8-1 ÉÏÎç11:23:14 
 * @version 1.0
 */
public class EtScoreBean extends Account{

	public EtScoreBean() {
		super(Account.TYPE_ET_SCORE);
		// TODO Auto-generated constructor stub
	}

	@SerializedName("jyMoney")
	private int etScore;


	public int getEtScore() {
		return etScore;
	}

	public void setEtScore(int etScore) {
		this.etScore = etScore;
	}

	@Override
	public String toString() {
		return "EtScoreBean [etScore=" + etScore +  super.toString() + "]";
	}
}
