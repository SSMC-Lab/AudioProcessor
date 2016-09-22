package fruitbasket.com.audioprocessor;

import android.os.Environment;

final public class AppCondition {
	private static final AppCondition APP_CONDITION =new AppCondition();

	/*
	App的根目录
	 */
	public static final String APP_FILE_DIR=Environment.getExternalStorageDirectory()+"/AudioProcessor";
	/*
	默认的声音样本频率
	 */
	public static final int DEFAULE_SIMPLE_RATE =44100;
	
	private AppCondition(){}
	
	public AppCondition getInstance(){
		return APP_CONDITION;
	}
	
}
