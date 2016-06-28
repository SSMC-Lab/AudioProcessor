package fruitbasket.com.audioprocessor.play;

import android.util.Log;

/**
 * 播放原始音频的任务
 */
final public class PCMPlayTask implements Runnable{
	private static final String TAG=PCMPlayTask.class.toString();
	
	private AudioTrackWrapper audioTrackWrapper;
	
	private String audioPath =null;	//the full path and the name of the music data
	private int sampleRate=0; //the sample rate of the music data
	private AudioOutConfig audioOutConfig;
	
	public PCMPlayTask(String audioPath, int sampleRate){
		this(audioPath,sampleRate,null);
	}

	public PCMPlayTask(String audioPath,int sampleRate,AudioOutConfig audioOutConfig){
		this.audioPath =audioPath;
		this.sampleRate=sampleRate;
		this.audioOutConfig=audioOutConfig;
	}
	
	@Override
	public void run(){
		Log.d(TAG,"run()");
		audioTrackWrapper =new AudioTrackWrapper(audioOutConfig);
		if(audioPath !=null&&sampleRate!=0){
			audioTrackWrapper.startPlaying(audioPath, sampleRate);
		}
		else{
			Log.e(TAG,"audioPath==null && sampleRate==0");
		}
	}

	public void stopPlaying(){
		if(audioTrackWrapper !=null){
			audioTrackWrapper.stopPlaying();
			audioTrackWrapper =null;
			
			audioPath =null;
			sampleRate=0;
		}
	}
	
	public void setParameters(String audioPath,int sampleRate){
		this.audioPath =audioPath;
		this.sampleRate=sampleRate;
	}
}
