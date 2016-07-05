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
	private static final String TAG="MyAPP";
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG,"onCreate()");
		
		//makes the main directroy of the application
		File appDirectroy=new File(Condition.APP_FILE_DIR);
		appDirectroy.mkdirs();
		Log.e(TAG,"onCreate()");
	}
	
}
