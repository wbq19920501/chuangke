package cn.com.easytaxi.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

public class FileUtil {
	/**
	 * 实例引用：单例模式
	 */
	private static FileUtil fileUtilInstance = null;
	
	
	/**
	 * 获取实例引用
	 * @return
	 */
	public static FileUtil getFileUtilInstance(){
		if(null == fileUtilInstance){
			fileUtilInstance = new FileUtil();
		}
		return fileUtilInstance;
	}
	
	
	/**
	 * 判断文件是否存在
	 * @param file 文件路径，包含文件名
	 */
   public boolean isFileExit(String file){
	   try{
		   File f = new File(file); 
		   if(f.exists()){
			  return true;
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return false;
   }
   
   
   /**
    * 如果文件不存在，则创建文件
    * @param file 文件路径，包含文件名
    */
   public void createFile(String file){
	   try{
		   File f = new File(file); 
		   if(!f.exists()){
			   f.createNewFile(); 
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
   
   /**
    * 如果文件或文件夹不存在，则创建它
    * @param file 文件
    */
   public void createFileOrDir(File file){
	   try{
		   if(!file.exists()){
			   if(file.isDirectory()){
				   file.mkdir();
			   }else if(file.isFile()){
				   file.createNewFile(); 
			   }
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
   
   
   /**
    * 删除文件
    * @param file 文件路径，包含文件名
    */
   public void deleteFile(String file){
	   try{
		   File f = new File(file); 
		   if(f.exists()){
			   f.delete(); 
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
   
   /**
    * 删除文件
    * @param file 文件
    */
   public void deleteFile(File f){
	   try{
		   if(f.exists()){
			   f.delete(); 
		   }
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
   
   
   /**
    * 获取/data/data目录下应用目录中的目录
    * 我们可以在该目录中创建文件   例如：File actionLogDir = fileUtil.getAppDataDir(bookParent.getBaseContext(),"actionLog");
    * @param context 上下文
    * @param fileName 文件名
    * @return
    */
   public File getAppDataDir(Context context,String fileName){
	   return context.getDir(fileName, Context.MODE_WORLD_WRITEABLE);
   }
   
   
   /**
    * 打开文件输出流
    * ：此方法在打开/data/data目录下应用目录中的文件时候会被用到，因为context会给于打开该文件为输出流的权限
    * @param context
    * @param fileName 文件名
    * @return
    * @throws FileNotFoundException
    */
   public FileOutputStream saveInMemory(Context context, String fileName) throws FileNotFoundException {
		// important: note the permission of the file in memory
		return context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
	}
   
   
   /**
    * 读取文件内容
    * @param file 文件
    * @return
    * @throws IOException
    */
   public byte[] readFileData(File file) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(file);// pathStr 文件路径
			byte[] b = new byte[1024];
			int n;
			while ((n = is.read(b)) != -1) {
				out.write(b, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return out.toByteArray();
	}
   
   
   /**
    * 把字符串写入文件
    * @param str 字串
    * @param file 文件
    * @return
    */
   public boolean writeFile(String str,File file){
		boolean bRet = false;
		try {
			byte[] strBts = str.getBytes();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(strBts, 0, strBts.length);
			fos.close();
			
			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bRet;
   }
   
   
   /*public boolean writeFile(File fromFile,File toFile){

		boolean bRet = false;

		try {
			InputStream is = new FileInputStream(fromFile);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	
   }*/
   
}
	   
