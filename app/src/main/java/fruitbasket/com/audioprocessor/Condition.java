package fruitbasket.com.audioprocessor;

import android.os.Environment;

final public class Condition {
	private static final Condition condition=new Condition();
	
	public static final String APP_FILE_DIR=Environment.getExternalStorageDirectory()+/*File.separator+*/"/AudioProcessor";
	public static final String SOUND_FILE_PATH =APP_FILE_DIR+"/audio.wav";
	
	private Condition(){}
	
	public Condition getInstance(){
		return condition;
	}
	
}
