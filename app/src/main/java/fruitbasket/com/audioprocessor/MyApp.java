package fruitbasket.com.audioprocessor;

import android.app.Application;
import android.util.Log;

import java.io.File;

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
	}
	
}
