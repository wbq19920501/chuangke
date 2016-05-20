package cn.com.easytaxi.util;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

public class AndroidUtil {

	public static final int PROGRESS_MAX_VALUE = 100;

	/**
	 * 获取签名
	 * 
	 * @param applicationContext
	 * @param pkgName
	 * @return
	 * @return String 获取签名失败，会返回null
	 */
	private static String getPublicKey(Context applicationContext, String pkgName) {
		PackageInfo packageInfo;
		try {

			if (TextUtils.isEmpty(pkgName)) {
				pkgName = applicationContext.getPackageName();
			}

			packageInfo = applicationContext.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
			Signature sign = packageInfo.signatures[0];

			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(sign.toByteArray()));

			RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
			BigInteger big = publicKey.getModulus();

			return big.toString(16);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static String getPublicKey(Context applicationContext) {
		return getPublicKey(applicationContext, null);
	}
}
