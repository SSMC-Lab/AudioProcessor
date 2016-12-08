package fruitbasket.com.audioprocessor;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Condition;

import fruitbasket.com.audioprocessor.utilities.ExcelProcessor;
import fruitbasket.com.audioprocessor.utilities.Utilities;

/**
 * you can initialize the application in this class
 * @author Study
 *
 */
final public class MyApp extends Application {
	private static final String TAG=MyApp.class.toString();
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG,"onCreate()");
		
		//create the main directroy of the application
		File appDirectroy=new File(AppCondition.APP_FILE_DIR);
		appDirectroy.mkdirs();
		Utilities.createDirs(AppCondition.DATA_DIR);

		String [] dataLine;
		try {

			if(AppCondition.ACC_EXCEL.exists()==false){
				dataLine=new String[]{"Time","accX","accY","accZ"};
				ExcelProcessor.createFileWithHeader(AppCondition.ACC_EXCEL,dataLine);
			}
			if(AppCondition.GYR_EXCEL.exists()==false){
				dataLine=new String[]{"Time","gyrX","gyrY","gyrZ"};
				ExcelProcessor.createFileWithHeader(AppCondition.GYR_EXCEL,dataLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
