package cn.com.easytaxi.platform.service;

import java.io.Serializable;

public class CacheBean implements Serializable {
	/**
	 * 对象唯一标识
	 */
	private Long _cacehe_id;
	
	/**
	 * 对象引用计数
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
