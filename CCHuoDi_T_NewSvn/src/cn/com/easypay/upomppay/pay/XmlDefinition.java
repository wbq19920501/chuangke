package cn.com.easypay.upomppay.pay;

import cn.com.easypay.upomppay.config.UpompConfig;


/*
 * author: guoqiang
 * 该类主要用于生成，服务器相应操作的报文。
 */
public class XmlDefinition {

	public static String ReturnXml(String sign,int args){
		
		String SubmitOrder,lanchpay,QueryOrder,Cancel,Refund;
		switch (args){
		
		/*
		 * SubmitOrder：订单提交报文
//		 */
//		case 7:
//			SubmitOrder = "<?xml version="+"'1.0' "
//			+ "encoding=" + "'utf-8' " +"standalone='yes'"+ "?>"+"<upomp  application="+"'SubmitOrder.Req' " +"version="+ "'1.0.0'"+">"
//			+ "<merchantName>"
//			+ Upomp_Pay_Info.merchantName
//			+ "</merchantName>"
//			+ "<merchantId>"
//			+ Upomp_Pay_Info.merchantId
//			+ "</merchantId>"
//			+ "<merchantOrderId>"
//			+ Upomp_Pay_Info.merchantOrderId
//			+ "</merchantOrderId>"
//			+ "<merchantOrderTime>"
//			+ Upomp_Pay_Info.merchantOrderTime
//			+ "</merchantOrderTime>"
//			+ "<merchantOrderAmt>"
//			+ Upomp_Pay_Info.merchantOrderAmt
//			+ "</merchantOrderAmt>"
//			+ "<merchantOrderDesc>"
//			+ Upomp_Pay_Info.merchantOrderDesc
//			+ "</merchantOrderDesc>"
//			+ "<transTimeout>"
//			+ Upomp_Pay_Info.transTimeout
//			+ "</transTimeout>"
//			+ "<backEndUrl>"
//			+ Upomp_Pay_Info.backEndUrl
//			+ "</backEndUrl>"
//			+ "<sign>"
//			+ sign
//			+ "</sign>"
//			+ "<merchantPublicCert>"
//			+ Upomp_Pay_Info.merchant_public_cer
//			+ "</merchantPublicCert>"+ "</upomp>";
//
//			return SubmitOrder;
		/*
		 * LanchPay：订单验证	
		 */
		case 3:
			lanchpay ="<?xml version=" + "'1.0' "
			+ "encoding=" + "'UTF-8' " + "?>" + "<upomp  application="+"'LanchPay.Req' "+"version="+ "'1.0.0' "+">"				
			+ "<merchantId>"
			+ UpompConfig.merchantId
			+ "</merchantId>"
			
			+ "<merchantOrderId>"
			+ UpompConfig.merchantOrderId
			+ "</merchantOrderId>"
			
			+ "<merchantOrderTime>"
			+ UpompConfig.merchantOrderTime
			+ "</merchantOrderTime>"

			+ "<sign>"
			+ sign
			+ "</sign>"
			+ "</upomp>";

	return lanchpay;
		/*
		 * QueryOrder:订单查询报文
		 */
		case 4:
			QueryOrder = "<?xml version="+"'1.0' "
			+ "encoding=" + "'utf-8' " +"standalone='yes'"+ "?>"+"<upomp  application="+"'QueryOrder.Req' " +"version="+ "'1.0.0'"+">"
			+"<transType>"
			+UpompConfig.type[1]
			+"</transType>"
			+"<merchantId>"
			+UpompConfig.merchantId
			+"</merchantId>"
			+"<merchantOrderId>"
			+UpompConfig.merchantOrderId
			+"</merchantOrderId>"
			+"<merchantOrderTime>"
			+UpompConfig.merchantOrderTime
			+"</merchantOrderTime>"
			+ "<sign>"
			+ sign
			+ "</sign>"
			+ "<merchantPublicCert>"
			+ UpompConfig.merchant_public_cer
			+ "</merchantPublicCert>"
			+ "</upomp>";
		
		return QueryOrder;
		/*
		 * Cancel：撤单报文
		 */
		case 6:
			
			Cancel = "<?xml version="+"'1.0' "
			+ "encoding=" + "'utf-8' " +"standalone='yes'"+ "?>"+"<upomp  application="+"'Cancel.Req' " +"version="+ "'1.0.0'"+">"
			
			+ "<merchantId>"
			+ UpompConfig.merchantId
			+ "</merchantId>"
			
			+ "<merchantOrderId>"
			+ UpompConfig.merchantOrderId
			+ "</merchantOrderId>"
			
			+ "<merchantOrderTime>"
			+ UpompConfig.merchantOrderTime
			+ "</merchantOrderTime>"
			
			+ "<merchantOrderAmt>"
			+ UpompConfig.merchantOrderAmt
			+ "</merchantOrderAmt>"
			
			+"<cupsQid>"
			+UpompConfig.cupsQid
			+"</cupsQid>"
			
			+ "<backEndUrl>"
			+ UpompConfig.backEndUrl
			+ "</backEndUrl>"
			
			+ "<sign>"
			+ sign
			+ "</sign>"
			+ "<merchantPublicCert>"
			+ UpompConfig.merchant_public_cer
			+ "</merchantPublicCert>"
			
			+ "</upomp>";
		
		return Cancel;
		/*
		 * Refund：订单退货报文
		 */
		case 8:
			Refund = "<?xml version="+"'1.0' "
					+ "encoding=" + "'utf-8' " +"standalone='yes'"+ "?>"+"<upomp  application="+"'Cancel.Req' " +"version="+ "'1.0.0'"+">"
					
					+ "<merchantId>"
					+ UpompConfig.merchantId
					+ "</merchantId>"
					
					+ "<merchantOrderId>"
					+ UpompConfig.merchantOrderId
					+ "</merchantOrderId>"
					
					+ "<merchantOrderTime>"
					+ UpompConfig.merchantOrderTime
					+ "</merchantOrderTime>"
					
					+ "<merchantOrderAmt>"
					+ UpompConfig.merchantOrderAmt
					+ "</merchantOrderAmt>"
					
					+"<cupsQid>"
					+UpompConfig.cupsQid
					+"</cupsQid>"
					
					+ "<backEndUrl>"
					+ UpompConfig.backEndUrl
					+ "</backEndUrl>"
					
					+ "<sign>"
					+ sign
					+ "</sign>"
					+ "<merchantPublicCert>"
					+ UpompConfig.merchant_public_cer
					+ "</merchantPublicCert>"
					
					+ "</upomp>";
			return Refund;
		
		}
		return null;
	}

}