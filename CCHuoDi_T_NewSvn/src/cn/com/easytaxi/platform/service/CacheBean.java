package cn.com.easytaxi.platform.service;

import java.io.Serializable;

public class CacheBean implements Serializable {
	/**
	 * ����Ψһ��ʶ
	 */
	private Long _cacehe_id;
	
	/**
	 * �������ü���
	 */
	private int cacheRef=0;

	public Long getCacheId() {
		return _cacehe_id;
	}

	public void setCacheId(Long id) {
		this._cacehe_id = id;
	}

	public int getCacheRef() {
		return cacheRef;
	}

	public void setCacheRef(int cacheRef) {
		this.cacheRef = cacheRef;
	}
}
