package fruitbasket.com.audioprocessor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import fruitbasket.com.audioprocessor.modulate.AudioRecognition;
import fruitbasket.com.audioprocessor.modulate.MessageAudioPlayer;
import fruitbasket.com.audioprocessor.modulate.RecognitionTask;
import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.play.WavePlayTask;
import fruitbasket.com.audioprocessor.record.RecordTask;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * 执行录制音频和播放音频操作的服务
 */
///这里可能会引发多个线程同时播放声音的问题
///多次调用播放同一声音方法时可能能会出问题
public class AudioService extends Service {
	private static final String TAG=AudioService.class.toString();

	private AudioOutConfig audioOutConfig;

	private WavePlayTask wavePlayTask;
	private RecordTask recordTask;
	private RecognitionTask recognitionTask;

	private MessageAudioPlayer messageAudioPlayer;

	private Handler handler;

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
		stopSendingText();

		if(messageAudioPlayer !=null){
			messageAudioPlayer.releaseResource();
		}
		super.onDestroy();
	}

	public void setHandler(Handler handler){
		this.handler=handler;
	}

	public Handler getHandler(){
		return this.handler;
	}

	public void startPlayingWave(WaveType waveType,int waveRate,int sampleRate){
		wavePlayTask=new WavePlayTask(waveType,waveRate,sampleRate,audioOutConfig);
		new Thread(wavePlayTask).start();
	}

	public void stopPlayingWave(){
		if(wavePlayTask!=null){
			wavePlayTask.stopPlaying();
			wavePlayTask=null;
		}
	}

	public void startSendingText(){
		Log.i(TAG,"startSendingText()");
		if(messageAudioPlayer ==null){
			messageAudioPlayer =new MessageAudioPlayer();
		}
        messageAudioPlayer.play("12345",true,1000);
	}

	public void stopSendingText(){
		Log.i(TAG,"stopSendingText()");
		if(messageAudioPlayer !=null){
			messageAudioPlayer.stopPlaying();
		}
	}

	public void startRecord(){
		Log.i(TAG,"startRecord()");
		recordTask=new RecordTask();
		new Thread(recordTask).start();
	}

	public void stopRecord(){
		Log.i(TAG,"stopRecord()");
		if(recordTask!=null){
			recordTask.stopRecording();
		}
	}

	public void startRecognition(){
		Log.i(TAG,"startRecognition()");
		recognitionTask=new RecognitionTask();
        recognitionTask.setHandler(handler);
		recognitionTask.prepare();
		new Thread(recognitionTask).start();
	}

	public void stopRecognition(){
		Log.i(TAG,"stopRecognition()");
		if(recognitionTask!=null){
			recognitionTask.stop();
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
