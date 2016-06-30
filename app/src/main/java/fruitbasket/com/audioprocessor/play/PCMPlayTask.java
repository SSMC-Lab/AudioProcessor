package fruitbasket.com.audioprocessor.play;

import android.util.Log;

/**
 * 播放原始音频的任务
 */
final public class PCMPlayTask implements Runnable{
	private static final String TAG=PCMPlayTask.class.toString();
	
	private AudioTrackWrapper audioTrackWrapper;
	
	private String audioPath;	//the full path and the name of the audio file
	private AudioOutConfig audioOutConfig;
	
	public PCMPlayTask(String audioPath){
		this(audioPath,null);
	}

	public PCMPlayTask(String audioPath,AudioOutConfig audioOutConfig){
		this.audioPath =audioPath;
		this.audioOutConfig=audioOutConfig;
	}
	
	@Override
	public void run(){
		Log.d(TAG,"run()");
		audioTrackWrapper =new AudioTrackWrapper(audioOutConfig);
		if(audioPath !=null){
			audioTrackWrapper.startPlaying(audioPath);
		}
		else{
			Log.e(TAG,"audioPath==null");
		}
	}

	public void stopPlaying(){
		if(audioTrackWrapper !=null){
			audioTrackWrapper.stopPlaying();
			audioTrackWrapper.releaseResource();
			audioTrackWrapper=null;
			audioPath =null;
		}
	}
	
	public void setParameters(String audioPath){
		this.audioPath =audioPath;
	}
}
