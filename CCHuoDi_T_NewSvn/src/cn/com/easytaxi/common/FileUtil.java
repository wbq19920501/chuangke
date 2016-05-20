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
	 * ʵ�����ã�����ģʽ
	 */
	private static FileUtil fileUtilInstance = null;
	
	
	/**
	 * ��ȡʵ������
	 * @return
	 */
	public static FileUtil getFileUtilInstance(){
		if(null == fileUtilInstance){
			fileUtilInstance = new FileUtil();
		}
		return fileUtilInstance;
	}
	
	
	/**
	 * �ж��ļ��Ƿ����
	 * @param file �ļ�·���������ļ���
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
    * ����ļ������ڣ��򴴽��ļ�
    * @param file �ļ�·���������ļ���
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
    * ����ļ����ļ��в����ڣ��򴴽���
    * @param file �ļ�
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
    * ɾ���ļ�
    * @param file �ļ�·���������ļ���
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
    * ɾ���ļ�
    * @param file �ļ�
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
    * ��ȡ/data/dataĿ¼��Ӧ��Ŀ¼�е�Ŀ¼
    * ���ǿ����ڸ�Ŀ¼�д����ļ�   ���磺File actionLogDir = fileUtil.getAppDataDir(bookParent.getBaseContext(),"actionLog");
    * @param context ������
    * @param fileName �ļ���
    * @return
    */
   public File getAppDataDir(Context context,String fileName){
	   return context.getDir(fileName, Context.MODE_WORLD_WRITEABLE);
   }
   
   
   /**
    * ���ļ������
    * ���˷����ڴ�/data/dataĿ¼��Ӧ��Ŀ¼�е��ļ�ʱ��ᱻ�õ�����Ϊcontext����ڴ򿪸��ļ�Ϊ�������Ȩ��
    * @param context
    * @param fileName �ļ���
    * @return
    * @throws FileNotFoundException
    */
   public FileOutputStream saveInMemory(Context context, String fileName) throws FileNotFoundException {
		// important: note the permission of the file in memory
		return context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
	}
   
   
   /**
    * ��ȡ�ļ�����
    * @param file �ļ�
    * @return
    * @throws IOException
    */
   public byte[] readFileData(File file) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(file);// pathStr �ļ�·��
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
    * ���ַ���д���ļ�
    * @param str �ִ�
    * @param file �ļ�
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
	   
