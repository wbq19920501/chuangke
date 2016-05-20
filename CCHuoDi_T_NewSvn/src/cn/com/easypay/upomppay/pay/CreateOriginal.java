package cn.com.easypay.upomppay.pay;

import cn.com.easypay.upomppay.config.UpompConfig;


/*
 * 创建签名原串
 */
public class CreateOriginal {
	
	public static  String CreateOriginal_Sign(int args){
		
		StringBuilder os3,os7,os4,os6,os8;
		
		switch(args){
		
		//SubmitOrder：订单提交
				case 7:
					os7 = new StringBuilder();
					os7.append("merchantName=").append(UpompConfig.merchantName).append("&merchantId=")
							.append(UpompConfig.merchantId).append("&merchantOrderId=")
							.append(UpompConfig.merchantOrderId).append("&merchantOrderTime=")
							.append(UpompConfig.merchantOrderTime).append("&merchantOrderAmt=")
							.append(UpompConfig.merchantOrderAmt).append("&merchantOrderDesc=")
							.append(UpompConfig.merchantOrderDesc).append("&transTimeout=")
							.append(UpompConfig.transTimeout);
					return os7.toString();
		
		//LanchPay：订单验证
		case 3:
			
			os3 = new StringBuilder();
			os3.append("merchantId=")
					.append(UpompConfig.merchantId).append("&merchantOrderId=")
					.append(UpompConfig.merchantOrderId).append("&merchantOrderTime=")
					.append(UpompConfig.merchantOrderTime);

			return os3.toString();
					
		
		//QueryOrder:订单查询
		case 4:	
			os4 = new StringBuilder();
			os4.append("transType=").append(UpompConfig.type[1]);
			os4.append("&merchantId=")
					.append(UpompConfig.merchantId).append("&merchantOrderId=")
					.append(UpompConfig.merchantOrderId).append("&merchantOrderTime=")
					.append(UpompConfig.merchantOrderTime);

			return os4.toString();
		
		//Cancel: 订单撤单
		case 6:
				os6 = new StringBuilder();
		
				os6.append("merchantId=")
				.append(UpompConfig.merchantId).append("&merchantOrderId=")
				.append(UpompConfig.merchantOrderId).append("&merchantOrderTime=")
				.append(UpompConfig.merchantOrderTime).append("&merchantOrderAmt=")
				.append(UpompConfig.merchantOrderAmt).append("&cupsQid=")
				.append(UpompConfig.cupsQid).append("&backEndUrl=").append(UpompConfig.backEndUrl);

		return os6.toString();
		//Cancel: 订单退货
		case 8:
			os8 = new StringBuilder();
			
			os8.append("merchantId=")
			.append(UpompConfig.merchantId).append("&merchantOrderId=")
			.append(UpompConfig.merchantOrderId).append("&merchantOrderTime=")
			.append(UpompConfig.merchantOrderTime).append("&merchantOrderAmt=")
			.append(UpompConfig.merchantOrderAmt).append("&cupsQid=")
			.append(UpompConfig.cupsQid).append("&backEndUrl=").append(UpompConfig.backEndUrl);
			
			return os8.toString();
		default:
			System.out.println("No Thing");
		}
		
		return null;
		
		
		
	}
	
	
}
