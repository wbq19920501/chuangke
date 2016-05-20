package cn.com.easytaxi.book;
public class SpinnerPair {
		String value;
		String show;

		public String getValue() {
			return value;
		}

		public String getShow() {
			return show;
		}

		public SpinnerPair(String show, String value) {
			super();
			this.show = show;
			this.value = value;
		}

		@Override
		public String toString() {
			return show;
		}
	}