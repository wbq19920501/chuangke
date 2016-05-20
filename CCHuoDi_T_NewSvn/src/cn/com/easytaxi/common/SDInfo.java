package cn.com.easytaxi.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class SDInfo {
	private static File mSdFile = Environment.getExternalStorageDirectory();
	private static StatFs mStatfs = new StatFs(mSdFile.getPath());
	private static File mInternalFile = Environment.getDataDirectory();
	private static StatFs mInternalStatfs = new StatFs(mInternalFile.getPath());

	public static long getAvailableInternalMemorySize() {
		long blockSize = mInternalStatfs.getBlockSize();
		long availableBlocks = mInternalStatfs.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static boolean hasEnoughAvailableSizeInternal() {
		long value = getAvailableInternalMemorySize() / 1024 / 1024; // MB
		return value > 10 ? true : false;
	}

	/*
	 * //获取block的SIZE private static long mBlocSize = mStatfs.getBlockSize();
	 * //获取BLOCK数量 private static long mTotalBlocks = mStatfs.getBlockCount();
	 * //空闲的Block的数量 private static long mAvailaBlocks=
	 * mStatfs.getAvailableBlocks();
	 */

	public static long getAvailaleSize() {
		long blockSize = mStatfs.getBlockSize();
		long blocks = mStatfs.getAvailableBlocks();
		return blockSize * blocks;
		// (availableBlocks * blockSize)/1024 KIB 单位
		// (availableBlocks * blockSize)/1024 /1024 MIB单位
	}

	public static long getAllSize() {
		return mStatfs.getBlockSize() * mStatfs.getBlockCount();
	}

	/**
	 * for checking record greeting
	 * 
	 * @return
	 */
	public static boolean hasEnoughAvailableSize() {
		long value = getAvailaleSize() / 1024 / 1024; // MB
		return value > 10 ? true : false;
	}

	/**
	 * for downloading skin apk
	 * 
	 * @return
	 */
	public static boolean hasDownloadSkinSize() {
		long value = getAvailaleSize() / 1024 / 1024; // MB
		return value > 1 ? true : false;
	}

	public static boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	public static boolean checkSdcard() {
		if (SDInfo.hasSDCard()) {
			if (SDInfo.hasEnoughAvailableSize()) {
				return true;
			}
		}
		return false;
	}

	public static FileOutputStream saveInMemory(Context context, String fileName) throws FileNotFoundException {
		// important: note the permission of the file in memory
		return context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
	}

}
