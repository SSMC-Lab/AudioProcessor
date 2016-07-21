package fruitbasket.com.audioprocessor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.play.WavePlayTask;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * 执行录制音频和播放音频操作的服务
 */
///这里可能会引发多个线程同时播放声音的问题
///多次调用播放同一声音方法时可能能会出问题
public class AudioService extends Service {
	private static final String TAG=AudioService.class.toString();

	private Thread wavePlayThread;
	private WavePlayTask wavePlayTask;

	private AudioOutConfig audioOutConfig;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new RecordServiceBinder();
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG,"onCreate()");
	}
	
	@Override
	public void onDestroy(){
		Log.d(TAG,"onDestroy()");

		//在服务结束时，必须结束所有任务
		stopPlayingWave();
		super.onDestroy();
	}

	public void startPlayingWave(WaveType waveType,int waveRate,int sampleRate){
		wavePlayTask=new WavePlayTask(waveType,waveRate,sampleRate,audioOutConfig);
		wavePlayThread=new Thread(wavePlayTask);
		wavePlayThread.start();
	}

	public void stopPlayingWave(){
		if(wavePlayTask!=null){
			wavePlayTask.stopPlaying();
			wavePlayTask=null;
		}
	}

	public void setAudioOutConfig(AudioOutConfig audioOutConfig){
		this.audioOutConfig=audioOutConfig;
	}

	public void setChannelOut(int channelOut){
		if(audioOutConfig==null){
			this.audioOutConfig=new AudioOutConfig(channelOut);
		}
		else{
			audioOutConfig.setChannelOut(channelOut);
		}
	}




	public class RecordServiceBinder extends Binder{
		public AudioService getService(){
			return AudioService.this;
		}
	}
}
