package cn.com.easytaxi;

public class BookConfig {
	
	/**
	 * @ClassName: ClientType 
	 * @Description: TODO
	 * @author Brook xu
	 * @date 2013-8-9 下午2:06:51 
	 * @version 1.0
	 */
	public static class ClientType{
		public static final String CLIENT_TYPE_PASSENGER ="android.phone.easytaxi";
		public static final String CLIENT_TYPE_96106 ="android.phone.96106";
		public static final String CLIENT_TYPE_PAD ="pad";
	}

	public static class BookType{
		public static final int BOOK_TYPE_IMMEDIATE = 1;
		// public static final int BOOK_TYPE_IMMEDIATE_OLD = 2;
		// public static final int BOOK_TYPE_ORDER_IOS = 3;
		// public static final int BOOK_TYPE_ORDER_OLD = 4;
		public static final int BOOK_TYPE_ORDER = 5;
		public static final int BOOK_TYPE_PILOT = 6;
		public static final int BOOK_TYPE_AIRPORT = 7;
	}
	
	public static class BookState{
		public static final int BOOK_STATE_SCHEDULING = 0x01;
		public static final int BOOK_STATE_LOCKING = 0x02;
		public static final int BOOK_STATE_SCHEDULE_FAILED = 0x03;
		public static final int BOOK_STATE_PASSENGER_CANCELED = 0x04;
		public static final int BOOK_STATE_TAXI_ACCEPT = 0x05;
		public static final int BOOK_STATE_FINISHING = 0x06;
		public static final int BOOK_STATE_FINISHED = 0x07;
		public static final int BOOK_STATE_EXCUTE_FAILED = 0x08;
		public static final int BOOK_STATE_ARGUE = 0x09;
	}
	
}
