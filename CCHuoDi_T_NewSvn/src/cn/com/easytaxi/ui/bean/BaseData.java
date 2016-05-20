package cn.com.easytaxi.ui.bean;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.platform.service.CacheBean;
import cn.com.easytaxi.util.DBManager;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;

/**
 * @author shiner
 */
public class BaseData<T extends CacheBean> {
	AbstractDao<T, Long> dao;
	Class<T> entityClass;

	private AbstractDao<T, Long> getDao() {
		if (dao == null) {
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			dao = (AbstractDao<T, Long>) DBManager.getSession(ETApp.getInstance()).getDao(entityClass);
		}
		return dao;
	}

	/**
	 * 插入
	 * 
	 * @param entities
	 */
	public void insert(List<T> entities) {
		getDao().insertOrReplaceInTx(entities, false);
	}

	/**
	 * 插入
	 * 
	 * @param entities
	 */
	public void insert(T entity) {
		getDao().insert(entity);
	}

	/**
	 * 查询
	 * 
	 * @param start
	 * @param size
	 * @return
	 */
	public List<T> getList(int start, int size, Property... orderProps) {
//		return getDao().queryBuilder().list();
		return getDao().queryBuilder().orderDesc(orderProps).offset(start).limit(size).list();
	}

	/**
	 * 清除表
	 */
	public void deleteAll() {
		getDao().deleteAll();
	}

	/**
	 * 清除历史订单
	 */
	public void delete(Long id) {
		getDao().deleteByKey(id);
	}

	/**
	 * 
	 * @return 数量
	 */
	public int getCount() {
		return (int) getDao().count();
	}
}
