package cn.com.easytaxi.onetaxi;

public enum TaxiState{
	
	    TAXI_STATE_EMPTY(0,"空车"),	   
	    TAXI_STATE_FULL(1,"载客"),	    
	    TAXI_STATE_NOTGO(2,"不接单"),
	    TAXI_STATE_GO(3,"接单"),	      
	    TAXI_STATE_LOADING(4,"调度中"),	
	    TAXI_STATE_NULL(5,"默认值"),	 
	    TAXI_STATE_TIMEOUT(6,"应答超时");
	    
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
