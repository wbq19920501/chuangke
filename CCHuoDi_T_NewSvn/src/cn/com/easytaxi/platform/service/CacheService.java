package cn.com.easytaxi.platform.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 * 
 * @author magi
 * 
 */
public class CacheService extends TimerTask {

	// ��������
	private int capacity;

	// �������
	private HashMap<Class, HashMap<Long, CacheBean>> cacheObjectMap;

	// �������˳��
	private HashMap<String, TreeSet<Long>> cacheTreeMap;

	// ������д洢�Ķ�������
	private HashMap<String, Class> classMap;

	private Timer timer;

	public CacheService() {
		cacheObjectMap = new HashMap<Class, HashMap<Long, CacheBean>>();
		cacheTreeMap = new HashMap<String, TreeSet<Long>>();
		classMap = new HashMap<String, Class>();
	}

	/**
	 * �����������
	 * 
	 * @param capacity
	 *            ��������
	 */
	public void start(int capacity) {
		timer = new Timer();
		timer.schedule(this, 10000, 60000);
	}

	public void stop() {
		timer.cancel();
	}

	/**
	 * �������
	 * 
	 * @param cacheId
	 *            ����Ƭ��ID
	 * @param cacheBean
	 *            ����Ķ���
	 */
	public void put(String cacheId, CacheBean cacheBean) {
		if (cacheBean == null)
			return;
		HashMap<Long, CacheBean> cacheBeanMap = cacheObjectMap.get(cacheBean.getClass());
		if (cacheBeanMap == null) {
			cacheBeanMap = new HashMap<Long, CacheBean>();
			cacheObjectMap.put(cacheBean.getClass(), cacheBeanMap);
		}

		CacheBean old = cacheBeanMap.get(cacheBean.getCacheId());
		if (old == null) {
			cacheBean.setCacheRef(1);
		} else {
			cacheBean.setCacheRef(old.getCacheRef() + 1);
		}

		cacheBeanMap.put(cacheBean.getCacheId(), cacheBean);

		TreeSet<Long> treeSet = cacheTreeMap.get(cacheId);
		if (treeSet == null) {
			treeSet = new TreeSet<Long>(new Comparator<Long>() {
				public int compare(Long lhs, Long rhs) {
					if (lhs > rhs) {
						return -1;
					} else if (lhs < rhs) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			cacheTreeMap.put(cacheId, treeSet);
		}
		treeSet.add(cacheBean.getCacheId());

		classMap.put(cacheId, cacheBean.getClass());
	}

	public <T> List<T> gets(String cacheId, int start, int size) {
		TreeSet<Long> treeSet = cacheTreeMap.get(cacheId);
		HashMap<Long, CacheBean> cacheBeanMap = cacheObjectMap.get(classMap.get(cacheId));

		if (treeSet == null)
			return new ArrayList<T>();

		int count = 0;
		int index = 0;
		List<T> cacheBeans = new ArrayList<T>();
		for (Long id : treeSet) {
			if (index >= start) {
				cacheBeans.add((T) cacheBeanMap.get(id));
				count++;
			}
			index++;
			if (count == size)
				break;
		}
		return cacheBeans;
	}

	public CacheBean get(String cacheId, Long beanId) {
		HashMap<Long, CacheBean> cacheBeanMap = cacheObjectMap.get(classMap.get(cacheId));
		if (cacheBeanMap == null) {
			return null;
		}
		CacheBean bean = cacheBeanMap.get(beanId);
		return bean;
	}

	// �����߳���������
	@Override
	public void run() {
		// TODO
	}

}
