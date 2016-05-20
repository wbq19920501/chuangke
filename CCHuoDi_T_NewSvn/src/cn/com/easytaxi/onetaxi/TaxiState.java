package cn.com.easytaxi.onetaxi;

public enum TaxiState{
	
	    TAXI_STATE_EMPTY(0,"�ճ�"),	   
	    TAXI_STATE_FULL(1,"�ؿ�"),	    
	    TAXI_STATE_NOTGO(2,"���ӵ�"),
	    TAXI_STATE_GO(3,"�ӵ�"),	      
	    TAXI_STATE_LOADING(4,"������"),	
	    TAXI_STATE_NULL(5,"Ĭ��ֵ"),	 
	    TAXI_STATE_TIMEOUT(6,"Ӧ��ʱ");
	    
	    public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		private int type;
		private String value;
		
	    private TaxiState(int type, String value){
	    	this.type = type;
	    	this.value = value;
	    }
}
