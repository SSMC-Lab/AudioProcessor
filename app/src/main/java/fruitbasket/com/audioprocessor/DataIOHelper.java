package fruitbasket.com.audioprocessor;

import java.io.File;

final public class DataIOHelper {
	private static final DataIOHelper dataIOHelper=new DataIOHelper();
	
	private DataIOHelper(){}
	
	public DataIOHelper getInstance(){
		return dataIOHelper;
	}
	
	/**
	 * get a file name ends with ".pcm"
	 * @return 
	 */
	public static String getRecordedFileName(String extensionName){
		return Condition.APP_FILE_DIR+File.separator+DateHelper.getCurrentTime()+"."+extensionName;
	}
}
