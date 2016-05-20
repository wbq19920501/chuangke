package cn.com.easytaxi.bean;

import java.util.List;

/**
 * @author xxb 积分详细
 * @version 创建时间：2015年9月10日 下午4:00:55
 */
public class ScoreDetailBean {
	private int error;
	private int startId;
	private int totalScore;
	private List<ScoreItem> books;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public List<ScoreItem> getBooks() {
		return books;
	}

	public void setBooks(List<ScoreItem> books) {
		this.books = books;
	}

}
