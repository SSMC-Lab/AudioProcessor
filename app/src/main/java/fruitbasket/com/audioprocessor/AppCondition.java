package fruitbasket.com.audioprocessor;

import android.os.Environment;

final public class AppCondition {
	private static final AppCondition APP_CONDITION =new AppCondition();
	
	public static final String APP_FILE_DIR=Environment.getExternalStorageDirectory()+/*File.separator+*/"/AudioProcessor";
	public static final String AUDIO_FILE_PATH =APP_FILE_DIR+"/audio.wav";

	///可能存在多处使用不一致的声音播放频率
	public static final int SIMPLE_RATE_CD=44100; //CD的采样频率
	
	private AppCondition(){}
	
	public AppCondition getInstance(){
		return APP_CONDITION;
	}
	
}
