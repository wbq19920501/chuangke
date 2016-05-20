package cn.com.easypay.upomppay.config;
/**
 * 银联支付参数
 * @author Administrator
 *
 */
public class UpompConfig {
	
	// 商户订单号，测试环境：建议输入9位；生产环境：最少8位，最多32
	public static String merchantOrderId="56789253546626";
	
	// 商户订单时间：建议与当前系统时间同步，且与现实时间相同。
	public static String merchantOrderTime="20130719034034";
	
	// 订单描述：对于该订单的描述
	public static String merchantOrderDesc = "充值30元";
	
	// 该URL与payurl相同，填写为商户服务器地址。银联回调URL
	public static String backEndUrl = "www.hao123.com";
	
	// 订单金额：单位为分。1为1分，10为1角；该处请不要出现特殊符号，例如“.”		
	public static String merchantOrderAmt = "10";			
	
	
	
	
	/**
	 * 以下参数不需要更改，直接使用
	 */
	
	// 商户名称，商户可以自行修改
	public static final String merchantName = "联通华建";
	
	// 商户ID，测试环境：898000000000002；生产环境：商户真实的商户证书ID号
	public static final String merchantId = "898000000000002";
	
	// 订单超时时间:建议传""。
	public static final String transTimeout = "";
	
	//签名原串
	public static String originalsign="merchantId=898000000000002&merchantOrderId=75696759&merchantOrderTime=20130415034034";
	
	//签名
	public static final String xmlSign = "r7TBBL+yPlOe7BhCoXPZBXyOmqUqxP9PnAAG6aySo8DRnTbtC6GE47RyXtgOFadMtazry9R2WVhcocfkZcFN7N/O/MHIlsZSY/OWf31PFP1xwDNPKAep+kw9Ln3v3yEZWZxQYJblcaLwZM9uSMsVdZ/vWFIpsQSXbcNFoJUWA6E=";
	
	// 商户公钥证书：这是商户公钥证书898000000000002.cer所读出公钥串。
	public static final String merchant_public_cer = "MIIDuDCCAyGgAwIBAgIQaOXUUCzukC6m5EAlw0LdZjANBgkqhkiG9w0BAQUFADAkMQswCQYDVQQGEwJDTjEVMBMGA1UEChMMQ0ZDQSBURVNUIENBMB4XDTExMDgxNzAyNDU1M1oXDTEyMDgxNzAyNDU1M1owfjELMAkGA1UEBhMCQ04xFTATBgNVBAoTDENGQ0EgVEVTVCBDQTERMA8GA1UECxMITG9jYWwgUkExFDASBgNVBAsTC0VudGVycHJpc2VzMS8wLQYDVQQDFCYwNDFAWjIwMTEwODE3QDg5ODAwMDAwMDAwMDAwMkAwMDAwMDAwMzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAzD7Xy03ptoXR7jx3BxGD5GN2Fsivu/QprnYZF+Axby8LjVNGs97tHn8CHfXzvFMqAvsd4dkKzKrTG+dOmrlunYLGFrntIHl8Mx3liFkGLYFuJUy1+HF/hIRAMPIkDux6AAhbbCZlawdx5faHkM5OQg2KGeBcD+8NUJA6IYOunIUCAwEAAaOCAY8wggGLMB8GA1UdIwQYMBaAFEZy3CVynwJOVYO1gPkL2+mTs/RFMB0GA1UdDgQWBBSrHyJHSuW1lYnmxBBh6ulilF4xSTALBgNVHQ8EBAMCBPAwDAYDVR0TBAUwAwEBADA7BgNVHSUENDAyBggrBgEFBQcDAQYIKwYBBQUHAwIGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwgwgfAGA1UdHwSB6DCB5TBPoE2gS6RJMEcxCzAJBgNVBAYTAkNOMRUwEwYDVQQKEwxDRkNBIFRFU1QgQ0ExDDAKBgNVBAsTA0NSTDETMBEGA1UEAxMKY3JsMTI3XzE1MzCBkaCBjqCBi4aBiGxkYXA6Ly90ZXN0bGRhcC5jZmNhLmNvbS5jbjozODkvQ049Y3JsMTI3XzE1MyxPVT1DUkwsTz1DRkNBIFRFU1QgQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDQYJKoZIhvcNAQEFBQADgYEARfg4YNGNETrJx+gy74UmPJ326T7H2hIE/lRcTyonq4NFpXmssau+TDV7btLUuhDxBGF1JysknFjeNAKMl9ZFGjKbOGGpQ7nfnEC8HIM7cp2n+gSlADRZbc8PHrqxLbjxsKoSUUFfh3PhfNXtWLfTxi5TT+hm6coV1K/EUX4t0AY=";		
	
	// 签名加密的私钥串和密码
	public static final String alias ="889ce7a52067a87f905c91f502c69644_d1cba47d-cbb1-4e29-9d77-8d1fe1b0dccd";
	
	//商户私钥898000000000002.p12的密码
	public final static String password = "898000000000002";
	
	//私钥路径
//	public final static String PrivatePath ="D:\\898000000000002.p12";
	
	//查询类型
	public final static Byte[] type = {01,31,04};
	
	//交易流水号
	public static String cupsQid;
	
	//日志标签
	public static String tag = "Sys";		
	
	// *****************以下为商户调起插件参数***********************//
	public static String CMD_PAY_PLUGIN = "cmd_pay_plugin";	
	
	//商户包名
	public static String MERCHANT_PACKAGE = "com.lthj.gq.merchant";		
	//判断是否提交了订单，先进行订单提交(submit)，再进行订单验证(Lanchpay)
}
