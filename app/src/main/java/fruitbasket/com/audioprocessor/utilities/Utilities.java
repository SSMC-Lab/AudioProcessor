package fruitbasket.com.audioprocessor.utilities;

import android.app.ActivityManager;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utilities {
	
	private static Utilities mUtilities=new Utilities();
	
	private Utilities(){}
	
	public static Utilities getInstance(){
		return mUtilities;
	}
	
	/**
	 * �����ļ��С��ļ������ڵ�·������ԭ�Ⱦʹ���
	 * @param dir
	 */
	public static void createDir(String dir){
		File appDir=new File(dir);
		if(appDir.exists()==false){
			appDir.mkdir();
		}
	}
	
	/**
	 * �����ļ���
	 * @param dir
	 */
	public static void createDirs(String dir){
		File appDir=new File(dir);
		if(appDir.exists()==false){
			appDir.mkdirs();
		}
	}
	
	/**
	 * �����ļ����ļ����ڵ�·������ԭ�Ⱦʹ���
	 * @param filePath
	 * @return true��ִ�д�����false��ûִ�д������ļ�ԭ���Ѿ�����
	 * @throws IOException
	 */
	public static boolean createFile(String filePath) 
			throws IOException{
		File file=new File(filePath);
		if(file.exists()==false){
			file.createNewFile();
			return true;
		}
		else{
			return false;
		}
	}

	
	public static String getTime(){
		return new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
	}
	
	
	/**
	 * @param context
	 * @param className ///
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
