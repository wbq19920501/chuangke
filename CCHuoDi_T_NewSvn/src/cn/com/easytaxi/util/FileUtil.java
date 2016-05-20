package cn.com.easytaxi.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

@SuppressLint("ParserError")
public class FileUtil {
	private static final int BUFFER_SIZE = 8 * 1024;
	private static final long MMS_MAX_SIZE = 1024000;

	public static boolean copyFileToFile(Context context, File src, File dst) {
		if (context != null && src != null && dst != null) {
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);

				// Transfer bytes from in to out
				byte[] buf = new byte[BUFFER_SIZE];
				int len = -1;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static boolean copyFileToFile(InputStream is, File dst) throws IOException {
		if (is != null && dst != null) {
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			try {
				in = new BufferedInputStream(is, BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
				// Transfer bytes from in to out
				byte[] buf = new byte[BUFFER_SIZE];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				return true;
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					Log.i("FileUtil:L62", e.getMessage());
				}
				try {
					out.close();
				} catch (Exception e) {
					Log.i("FileUtil:L67", e.getMessage());
				}
			}
		}
		return false;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 把uri转换成实际路径
	 * 
	 * @param uri
	 * @throws IllegalArgumentException
	 */
	public static String uriToPath(Activity activty, Uri uri) throws IllegalArgumentException {
		Log.e("FileUtil", "uri getPath = " + uri.getPath() + " getAuthority = " + uri.getAuthority());
		if (uri != null && "media".equals(uri.getAuthority())) {// 媒体文件
			Cursor actualCursor = activty.managedQuery(uri, null, null, null, null);
			int actual_column_index = actualCursor.getColumnIndexOrThrow("_data");
			actualCursor.moveToFirst();
			return actualCursor.getString(actual_column_index);
		}
		return uri.getPath();
	}

	/**
	 * And to convert the image URI to the direct file system path of the image
	 * file
	 * 
	 * @param contentResolver
	 * @param contentUri
	 * 
	 * @return bugs not suit for sdk4.4, it will be null
	 */
	public static String getRealPathFromURI(ContentResolver contentResolver, Uri contentUri) {

		if (contentUri == null) {
			return null;
		}

		// can post image
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = contentResolver.query(contentUri, proj, // Which columns
																// to return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int columnIndex = cursor.getColumnIndexOrThrow(proj[0]);
		cursor.moveToFirst();

		return cursor.getString(columnIndex);
	}

	/**
	 * 获取文件名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileName(String fileName) {
		return fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
	}

	// /**
	// * 根据文件后缀名获得对应的MIME类型。
	// *
	// * @param file
	// */
	// public static String getMIMEType(String file) {
	// String type = "*/*";
	// // String fName=file.getName();
	// String fName = file;
	// // 获取后缀名前的分隔符"."在fName中的位置。
	// int dotIndex = fName.lastIndexOf(".");
	// if (dotIndex < 0) {
	// return type;
	// }
	// /* 获取文件的后缀名 */
	// String end = fName.substring(dotIndex, fName.length()).toLowerCase();
	// if (end == "")
	// return type;
	// // 在MIME和文件类型的匹配表中找到对应的MIME类型。
	// for (int i = 0; i < MIME_MapTable.length; i++) {
	// if (end.equals(MIME_MapTable[i][0]))
	// type = MIME_MapTable[i][1];
	// }
	// return type;
	// }

	/**
	 * 常见类型
	 */
	// private static final String[][] MIME_MapTable = {
	// // {后缀名， MIME类型}
	// { ".3gp", "video/3gpp" }, { ".apk",
	// "application/vnd.android.package-archive" }, { ".asf", "video/x-ms-asf"
	// }, { ".avi", "video/x-msvideo" }, { ".bin", "application/octet-stream" },
	// { ".bmp", "image/bmp" }, { ".c", "text/plain" }, { ".class",
	// "application/octet-stream" }, { ".conf", "text/plain" }, { ".cpp",
	// "text/plain" }, { ".doc", "application/msword" }, { ".exe",
	// "application/octet-stream" }, { ".gif", "image/gif" }, { ".gtar",
	// "application/x-gtar" }, { ".gz", "application/x-gzip" }, { ".h",
	// "text/plain" }, { ".htm", "text/html" }, { ".html", "text/html" }, {
	// ".jar", "application/java-archive" }, { ".java", "text/plain" }, {
	// ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" }, { ".js",
	// "application/x-javascript" }, { ".log", "text/plain" }, { ".m3u",
	// "audio/x-mpegurl" }, { ".m4a", "audio/mp4a-latm" }, { ".m4b",
	// "audio/mp4a-latm" }, { ".m4p", "audio/mp4a-latm" }, { ".m4u",
	// "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" }, { ".mov",
	// "video/quicktime" },
	// { ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" }, { ".mp4",
	// "video/mp4" }, { ".mpc", "application/vnd.mpohun.certificate" }, {
	// ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" }, { ".mpg", "video/mpeg"
	// }, { ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" }, { ".msg",
	// "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" }, { ".pdf",
	// "application/pdf" }, { ".png", "image/png" }, { ".pps",
	// "application/vnd.ms-powerpoint" }, { ".ppt",
	// "application/vnd.ms-powerpoint" }, { ".prop", "text/plain" }, { ".rar",
	// "application/x-rar-compressed" }, { ".rc", "text/plain" }, { ".rmvb",
	// "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" }, { ".sh",
	// "text/plain" }, { ".tar", "application/x-tar" }, { ".tgz",
	// "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav",
	// "audio/x-wav" }, { ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv"
	// }, { ".wps", "application/vnd.ms-works" },
	// // {".xml", "text/xml"},
	// { ".xml", "text/plain" }, { ".z", "application/x-compress" }, { ".zip",
	// "application/zip" }, { ".amr", "audio/*" }, { "", "*/*" } };

	/*** 获取文件大小 ***/
	public static long getFileSizes(File f) throws Exception {
		long s = 0;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			s = fis.available();
		} catch (Exception e) {
			// 文件不存在
			throw new Exception();
		} finally {
			fis.close();
		}

		return s;
	}

	/*** 转换文件大小单位(b/kb/mb/gb) ***/
	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static boolean isOutofMmsMaxSize(File file) {
		try {
			return FileUtil.getFileSizes(file) > MMS_MAX_SIZE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@SuppressLint({ "ParserError", "ParserError" })
	public static String readTxt(String filePath) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String str = "";
			String r = br.readLine();
			while (r != null) {
				str += r;
				r = br.readLine();
			}
			return str;
		} finally {
			br.close();
		}
	}

	/**
	 * 自动跟据文本文件格式 转码，返回文件内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @return String型文件内容
	 * @throws IOException
	 */
	public static String getTextAutoCovertFromFile(String filePath) throws IOException {
		File file = new File(filePath);
		BufferedReader reader = null;
		String text = "";
		try {
			// FileReader f_reader = new FileReader(file);
			// BufferedReader reader = new BufferedReader(f_reader);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);// 找到文档的前三个字节并自动判断文档类型。
			in.reset();
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB && first3bytes[2] == (byte) 0xBF) {// utf-8

				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

			} else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {

				reader = new BufferedReader(new InputStreamReader(in, "unicode"));
			} else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {

				reader = new BufferedReader(new InputStreamReader(in, "utf-16be"));
			} else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {

				reader = new BufferedReader(new InputStreamReader(in, "utf-16le"));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, "GBK"));
			}
			String str = reader.readLine();
			while (str != null) {
				// text = text + str + "\n";
				text = text + str;
				str = reader.readLine();
			}
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				// TODO: handle exception
				throw new IOException();
			}
		}
		return text;
	}
}