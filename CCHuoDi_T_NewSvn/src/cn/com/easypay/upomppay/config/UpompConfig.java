package cn.com.easypay.upomppay.config;
/**
 * ����֧������
 * @author Administrator
 *
 */
public class UpompConfig {
	
	// �̻������ţ����Ի�������������9λ����������������8λ�����32
	public static String merchantOrderId="56789253546626";
	
	// �̻�����ʱ�䣺�����뵱ǰϵͳʱ��ͬ����������ʵʱ����ͬ��
	public static String merchantOrderTime="20130719034034";
	
	// �������������ڸö���������
	public static String merchantOrderDesc = "��ֵ30Ԫ";
	
	// ��URL��payurl��ͬ����дΪ�̻���������ַ�������ص�URL
	public static String backEndUrl = "www.hao123.com";
	
	// ��������λΪ�֡�1Ϊ1�֣�10Ϊ1�ǣ��ô��벻Ҫ����������ţ����硰.��		
	public static String merchantOrderAmt = "10";			
	
	
	
	
	/**
	 * ���²�������Ҫ���ģ�ֱ��ʹ��
	 */
	
	// �̻����ƣ��̻����������޸�
	public static final String merchantName = "��ͨ����";
	
	// �̻�ID�����Ի�����898000000000002�������������̻���ʵ���̻�֤��ID��
	public static final String merchantId = "898000000000002";
	
	// ������ʱʱ��:���鴫""��
	public static final String transTimeout = "";
	
	//ǩ��ԭ��
	public static String originalsign="merchantId=898000000000002&merchantOrderId=75696759&merchantOrderTime=20130415034034";
	
	//ǩ��
	public static final String xmlSign = "r7TBBL+yPlOe7BhCoXPZBXyOmqUqxP9PnAAG6aySo8DRnTbtC6GE47RyXtgOFadMtazry9R2WVhcocfkZcFN7N/O/MHIlsZSY/OWf31PFP1xwDNPKAep+kw9Ln3v3yEZWZxQYJblcaLwZM9uSMsVdZ/vWFIpsQSXbcNFoJUWA6E=";
	
	// �̻���Կ֤�飺�����̻���Կ֤��898000000000002.cer��������Կ����
	public static final String merchant_public_cer = "MIIDuDCCAyGgAwIBAgIQaOXUUCzukC6m5EAlw0LdZjANBgkqhkiG9w0BAQUFADAkMQswCQYDVQQGEwJDTjEVMBMGA1UEChMMQ0ZDQSBURVNUIENBMB4XDTExMDgxNzAyNDU1M1oXDTEyMDgxNzAyNDU1M1owfjELMAkGA1UEBhMCQ04xFTATBgNVBAoTDENGQ0EgVEVTVCBDQTERMA8GA1UECxMITG9jYWwgUkExFDASBgNVBAsTC0VudGVycHJpc2VzMS8wLQYDVQQDFCYwNDFAWjIwMTEwODE3QDg5ODAwMDAwMDAwMDAwMkAwMDAwMDAwMzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAzD7Xy03ptoXR7jx3BxGD5GN2Fsivu/QprnYZF+Axby8LjVNGs97tHn8CHfXzvFMqAvsd4dkKzKrTG+dOmrlunYLGFrntIHl8Mx3liFkGLYFuJUy1+HF/hIRAMPIkDux6AAhbbCZlawdx5faHkM5OQg2KGeBcD+8NUJA6IYOunIUCAwEAAaOCAY8wggGLMB8GA1UdIwQYMBaAFEZy3CVynwJOVYO1gPkL2+mTs/RFMB0GA1UdDgQWBBSrHyJHSuW1lYnmxBBh6ulilF4xSTALBgNVHQ8EBAMCBPAwDAYDVR0TBAUwAwEBADA7BgNVHSUENDAyBggrBgEFBQcDAQYIKwYBBQUHAwIGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwgwgfAGA1UdHwSB6DCB5TBPoE2gS6RJMEcxCzAJBgNVBAYTAkNOMRUwEwYDVQQKEwxDRkNBIFRFU1QgQ0ExDDAKBgNVBAsTA0NSTDETMBEGA1UEAxMKY3JsMTI3XzE1MzCBkaCBjqCBi4aBiGxkYXA6Ly90ZXN0bGRhcC5jZmNhLmNvbS5jbjozODkvQ049Y3JsMTI3XzE1MyxPVT1DUkwsTz1DRkNBIFRFU1QgQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDQYJKoZIhvcNAQEFBQADgYEARfg4YNGNETrJx+gy74UmPJ326T7H2hIE/lRcTyonq4NFpXmssau+TDV7btLUuhDxBGF1JysknFjeNAKMl9ZFGjKbOGGpQ7nfnEC8HIM7cp2n+gSlADRZbc8PHrqxLbjxsKoSUUFfh3PhfNXtWLfTxi5TT+hm6coV1K/EUX4t0AY=";		
	
	// ǩ�����ܵ�˽Կ��������
	public static final String alias ="889ce7a52067a87f905c91f502c69644_d1cba47d-cbb1-4e29-9d77-8d1fe1b0dccd";
	
	//�̻�˽Կ898000000000002.p12������
	public final static String password = "898000000000002";
	
	//˽Կ·��
//	public final static String PrivatePath ="D:\\898000000000002.p12";
	
	//��ѯ����
	public final static Byte[] type = {01,31,04};
	
	//������ˮ��
	public static String cupsQid;
	
	//��־��ǩ
	public static String tag = "Sys";		
	
	// *****************����Ϊ�̻�����������***********************//
	public static String CMD_PAY_PLUGIN = "cmd_pay_plugin";	
	
	//�̻�����
	public static String MERCHANT_PACKAGE = "com.lthj.gq.merchant";		
	//�ж��Ƿ��ύ�˶������Ƚ��ж����ύ(submit)���ٽ��ж�����֤(Lanchpay)
}
