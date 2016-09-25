package fruitbasket.com.audioprocessor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import fruitbasket.com.audioprocessor.modulate.MessageAudioPlayer;
import fruitbasket.com.audioprocessor.modulate.RecognitionTask;
import fruitbasket.com.audioprocessor.play.AudioOutConfig;
import fruitbasket.com.audioprocessor.play.PCMPlayTask;
import fruitbasket.com.audioprocessor.play.WavePlayTask;
import fruitbasket.com.audioprocessor.record.RecordTask;
import fruitbasket.com.audioprocessor.waveProducer.WaveType;

/**
 * 执行录制音频和播放音频操作的服务
 */
public class AudioService extends Service {
	private static final String TAG=AudioService.class.toString();

	private AudioOutConfig audioOutConfig;
	private Handler handler;

	private boolean isPlaying=false;
	private boolean isRecording=false;

	private WavePlayTask wavePlayTask;
	private RecordTask recordTask;
	private RecognitionTask recognitionTask;
	private MessageAudioPlayer messageAudioPlayer;
	private PCMPlayTask pcmPlayTask;

	@Override
	public IBinder onBind(Intent arg0) {
		return new RecordServiceBinder();
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d(TAG,"onCreate()");
		isPlaying=false;
		isRecording=false;
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
		stopRecognition();

		super.onDestroy();
	}

	public void setHandler(Handler handler){
		this.handler=handler;
	}

	public Handler getHandler(){
		return this.handler;
	}

	public void startPlayingWave(WaveType waveType,int waveRate,int sampleRate){
		Log.i(TAG,"startPlayingWave()");
		if(isPlaying){
			stopPlayingWave();
			stopSendingText();
			stopPlayPcm();
		}
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
		if(isPlaying){
			stopPlayingWave();
			stopSendingText();
			stopPlayPcm();
		}
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
		if(isRecording){
			stopRecord();
			stopRecognition();
		}
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
		if(isRecording){
			stopRecord();
			stopRecognition();
		}
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

	public void startPlayPcm(String pcmAudioPath){
		Log.i(TAG,"startPlayPcm()");
		if(isPlaying){
			stopPlayingWave();
			stopSendingText();
			stopPlayPcm();
		}
		pcmPlayTask=new PCMPlayTask(pcmAudioPath);
		new Thread(pcmPlayTask).start();
	}

	public void stopPlayPcm(){
		Log.i(TAG,"stopPlayPcm()");
		if(pcmPlayTask!=null){
			pcmPlayTask.stopPlaying();
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
