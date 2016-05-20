package cn.com.easypay.alipay.config;
/**
 * 支付宝支付参数
 * @author Administrator
 *
 */
public class PartnerConfig {

	//商品名称
	public static String subject = "魅力香水";
	//商品简介
	public static String body = "新年特惠 adidas 阿迪达斯走珠 香体止汗走珠 多种香型可选";
	//价格
	public static String price = "一口价:0.01";
	
	
	/**
	 * 以下参数不需要更改，直接使用
	 */
	
	//服务器URL
	public final static String server_url = "https://msp.alipay.com/x.htm";
	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088901069264137";
	// 商户收款的支付宝账号
	public static final String SELLER = "laiguiquan@gmail.com";
	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = "MIICXgIBAAKBgQDF4sHzSYzelkbHgy5d/K47YUu6Z0nissl8ZPdONM4oYbioMs6FbxAo7l3l0N+3eAX3zV9XJAXjj1dNzUG9365V/HhmE7JNxBhR21HCQdmDcgPNRDeuEf3ZvqiJ2YAQmloKjcaQmmlmDIm625Q/goTv5oaxfp2Ny4OjuUb0NN9t8QIDAQABAoGAfm++YMYz3dzC6KNmsuLKKGKoUjfAu7pkhri0UyHmP2MA62dbEWK/PoffMwHcj4t+tLBiJHl90cI6lBl70efjqSlJzEX2b0X0KhTKOdQ5FwcR5gohKQoo6KCAiHGy3B9l2PuL2Vn8zXb2HRUSOLour+YIU84HhFaqOAg5P1FknykCQQDqEUoWRKRMm26M9K6NZ+12+UcHr+2o9tsGlQo0D2ZI+UGtvB5rszyVlRKOyh+xgvD1ZludztPwVq82/dlQbKAjAkEA2G2ORDUBiV4NAdHXsPQN4RChk4HHEOTwA2zP7ahvOPLJbdimzANUtqyKl8uRfWbuORTts1nhi12UCLDeFTLQ2wJBAKBbtfkpj7JYLlXtcAS6tcpzn4EZu2WtsYKB6xqdjkLiWWfQYLsCcbg9CBjBSxNPFQva01t7DmIy0RmIsjBSKycCQQCifX52jyVcMRSq7RV3cYSKGaZ1zNXcRUY+DBAAapk2tHGu2x4/xbBwhhJ37QqKkCEDEkd0hGLl85gU/lV+rOxJAkEAzQ/+eu+EOb/xkhVZg7p57ywOWLEtgk52GlsV3RijbaMsUiWbZmecthYc5bMhMMKeAdcPzkGzrsQW09V+ATo8Kw==";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
//	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCu73pZxCq2u1f/a9J7ZjCYVAcoddGvlwzbAYtE GccrBHE8PXd49ZcbOAMkiL0/erQ47Vu99Bj+uudFENPwCzui880DqyD2Vl4EgRNN7arSGufR8MnU Bpx0RQ84Gfo2rvB+EIRof0vnWjY83w4umqHC6LaFKGoFro7w9QKDro7jZQIDAQAB";
	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLBxQQm+iWoxykWLshNsnAeCmlqov7m3hxQDgWAgco7Ch0qyJjVcn2igGQBi+Y1ym7pcS32fH4AsWCRFxGpkxPcM9ZhlKIlrbjlqMYMHoYjmaWRbK596mE0EK+6DoFaQNjA36wYG1qoyttDFMovjrUxNBgvs4vpcNPustTENepbQIDAQAB";
	// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
	public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_20120428msp.apk";
}
