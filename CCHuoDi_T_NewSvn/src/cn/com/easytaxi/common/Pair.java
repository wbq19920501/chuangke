package cn.com.easytaxi.common;

public class Pair<K, V> {
	public K key;

	public V value;

	public Object data;

	public Pair(K key, V value) {
		this(key, value, null);
	}

	public Pair(K key, V value, Object data) {
		super();
		this.key = key;
		this.value = value;
		this.data = data;
	}

}
